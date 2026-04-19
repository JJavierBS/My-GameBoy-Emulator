import javax.sound.sampled.*;

public class TestAudio {
    public static void main(String[] args) throws Exception {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format, 8192);
        line.start();
        
        System.out.println("Playing 440Hz tone for 2 seconds...");
        byte[] buffer = new byte[1024];
        double angle = 0;
        for (int i = 0; i < 44100 * 2 * 4; i += 4) {
            angle += 2.0 * Math.PI * 440.0 / 44100.0;
            short sample = (short) (Math.sin(angle) * 16000);
            buffer[(i % 1024)] = (byte) (sample & 0xFF);
            buffer[(i % 1024) + 1] = (byte) ((sample >> 8) & 0xFF);
            buffer[(i % 1024) + 2] = (byte) (sample & 0xFF);
            buffer[(i % 1024) + 3] = (byte) ((sample >> 8) & 0xFF);
            
            if (i % 1024 == 1020) {
                line.write(buffer, 0, buffer.length);
            }
        }
        line.drain();
        line.close();
        System.out.println("Done.");
    }
}
