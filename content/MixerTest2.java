import javax.sound.sampled.*;
public class MixerTest2 {
    public static void main(String[] args) throws Exception {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format, 8192);
        line.start();
        System.out.println("Audio started on Default line. Playing tone...");
        byte[] buf = new byte[1024];
        for (int i = 0; i < 44100; i++) {
            short val = (short)(Math.sin(i * 440.0 * 2.0 * Math.PI / 44100.0) * 16000);
            buf[(i % 256) * 4] = (byte)(val & 0xFF);
            buf[(i % 256) * 4 + 1] = (byte)((val >> 8) & 0xFF);
            buf[(i % 256) * 4 + 2] = (byte)(val & 0xFF);
            buf[(i % 256) * 4 + 3] = (byte)((val >> 8) & 0xFF);
            if (i % 256 == 255) {
                line.write(buf, 0, 1024);
            }
        }
        line.drain();
        line.stop();
        line.close();
        System.out.println("Done.");
    }
}
