package apu;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

public class Apu {
    private static final int SAMPLE_RATE = 44100;
    private static final int CYCLES_PER_SAMPLE = 4194304 / SAMPLE_RATE;
    
    private float cyclesAccumulated = 0;
    private static final float CYCLES_PER_SAMPLE_F = 4194304.0f / SAMPLE_RATE;
    
    private SourceDataLine line;
    private byte[] buffer = new byte[1024];
    private int bufferIndex = 0;
    

    private float hpLeft = 0;
    private float hpRight = 0;
    

    private int[] registers = new int[0x30];
    

    private int ch1Timer = 0;
    private int ch1DutyStep = 0;
    private int ch2Timer = 0;
    private int ch2DutyStep = 0;
    
    private static final int[][] DUTY_CYCLES = {
        {0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1},
        {0, 1, 1, 1, 1, 1, 1, 0} 
    };

    public Apu() {
        initAudio(loadMixerName());
    }
    
    private String loadMixerName() {
        File file = new File("settings.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                return props.getProperty("audioMixer");
            } catch (Exception e) {}
        }
        return null;
    }
    
    public void saveMixerName(String name) {
        Properties props = new Properties();
        File file = new File("settings.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (Exception e) {}
        }
        if (name != null) {
            props.setProperty("audioMixer", name);
        } else {
            props.remove("audioMixer");
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "Game Boy Emulator Controls");
        } catch (Exception e) {}
    }

    private void initAudio(String mixerName) {
        try {
            if (line != null) {
                line.stop();
                line.close();
            }

            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            
            Mixer targetMixer = null;
            if (mixerName != null && !mixerName.isEmpty()) {
                for (Mixer.Info mInfo : AudioSystem.getMixerInfo()) {
                    if (mInfo.getName().equals(mixerName)) {
                        Mixer m = AudioSystem.getMixer(mInfo);
                        if (m.isLineSupported(info)) {
                            targetMixer = m;
                            break;
                        }
                    }
                }
            }
            
            if (targetMixer != null) {
                line = (SourceDataLine) targetMixer.getLine(info);
            } else {
                line = (SourceDataLine) AudioSystem.getLine(info);
            }
            
            line.open(format, 8192);
            line.start();
        } catch (Exception e) {
            System.err.println("Audio initialization failed: " + e.getMessage());
        }
    }
    
    public List<String> getAvailableMixers() {
        List<String> list = new ArrayList<>();
        list.add("Default");
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        for (Mixer.Info mInfo : AudioSystem.getMixerInfo()) {
            try {
                Mixer m = AudioSystem.getMixer(mInfo);
                if (m.isLineSupported(info)) {
                    list.add(mInfo.getName());
                }
            } catch (Exception e) {}
        }
        return list;
    }
    
    public void setMixer(String name) {
        if ("Default".equals(name)) name = null;
        saveMixerName(name);
        initAudio(name);
    }
    
    public String getCurrentMixerName() {
        String name = loadMixerName();
        return (name == null || name.isEmpty()) ? "Default" : name;
    }

    public byte readRegister(int addr) {
        return (byte) (registers[addr - 0xFF10] & 0xFF);
    }

    public void writeRegister(int addr, int value) {
        int reg = addr - 0xFF10;
        registers[reg] = value;

        if (addr == 0xFF14 && (value & 0x80) != 0) {
            int freq = registers[0xFF13 - 0xFF10] | ((registers[0xFF14 - 0xFF10] & 0x07) << 8);
            ch1Timer = (2048 - freq) * 4;
        }
        if (addr == 0xFF19 && (value & 0x80) != 0) {
            int freq = registers[0xFF18 - 0xFF10] | ((registers[0xFF19 - 0xFF10] & 0x07) << 8);
            ch2Timer = (2048 - freq) * 4;
        }
    }

    public void step(int cycles) {
        cyclesAccumulated += cycles;
        while (cyclesAccumulated >= CYCLES_PER_SAMPLE_F) {
            cyclesAccumulated -= CYCLES_PER_SAMPLE_F;
            generateSample();
        }
    }

    private void generateSample() {
        int leftFinal = 0;
        int rightFinal = 0;


        if ((registers[0xFF26 - 0xFF10] & 0x80) != 0) {

            int freq1 = registers[0xFF13 - 0xFF10] | ((registers[0xFF14 - 0xFF10] & 0x07) << 8);
            ch1Timer -= CYCLES_PER_SAMPLE;
            while (ch1Timer <= 0) {
                ch1Timer += (2048 - freq1) * 4;
                ch1DutyStep = (ch1DutyStep + 1) % 8;
            }
            int ch1Duty = (registers[0xFF11 - 0xFF10] >> 6) & 0x03;
            int ch1Vol = (registers[0xFF12 - 0xFF10] >> 4) & 0x0F;
            int sample1 = DUTY_CYCLES[ch1Duty][ch1DutyStep] * ch1Vol;
    

            int freq2 = registers[0xFF18 - 0xFF10] | ((registers[0xFF19 - 0xFF10] & 0x07) << 8);
            ch2Timer -= CYCLES_PER_SAMPLE;
            while (ch2Timer <= 0) {
                ch2Timer += (2048 - freq2) * 4;
                ch2DutyStep = (ch2DutyStep + 1) % 8;
            }
            int ch2Duty = (registers[0xFF16 - 0xFF10] >> 6) & 0x03;
            int ch2Vol = (registers[0xFF17 - 0xFF10] >> 4) & 0x0F;
            int sample2 = DUTY_CYCLES[ch2Duty][ch2DutyStep] * ch2Vol;
    
            int leftOut = 0;
            int rightOut = 0;
            
            int nr51 = registers[0xFF25 - 0xFF10];

            if ((nr51 & 0x10) != 0) leftOut += sample1;
            if ((nr51 & 0x01) != 0) rightOut += sample1;

            if ((nr51 & 0x20) != 0) leftOut += sample2;
            if ((nr51 & 0x02) != 0) rightOut += sample2;
            

            int nr50 = registers[0xFF24 - 0xFF10];
            int leftVol = ((nr50 >> 4) & 0x07) + 1;
            int rightVol = (nr50 & 0x07) + 1;
            



            leftFinal = leftOut * leftVol * 80;
            rightFinal = rightOut * rightVol * 80;
        }


        hpLeft += (leftFinal - hpLeft) * 0.01f;
        hpRight += (rightFinal - hpRight) * 0.01f;
        
        int filteredLeft = (int)(leftFinal - hpLeft);
        int filteredRight = (int)(rightFinal - hpRight);
        


        pushSample(filteredLeft, filteredRight);
    }

    private void pushSample(int left, int right) {

        if (left < -32768) left = -32768;
        if (left > 32767) left = 32767;
        if (right < -32768) right = -32768;
        if (right > 32767) right = 32767;
        

        buffer[bufferIndex++] = (byte) (left & 0xFF);
        buffer[bufferIndex++] = (byte) ((left >> 8) & 0xFF);
        buffer[bufferIndex++] = (byte) (right & 0xFF);
        buffer[bufferIndex++] = (byte) ((right >> 8) & 0xFF);
        
        if (bufferIndex >= buffer.length) {
            if (line != null) {
                line.write(buffer, 0, buffer.length);
            }
            bufferIndex = 0;
        }
    }
}
