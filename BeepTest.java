import javax.sound.sampled.*;

public class BeepTest {
    public static void main(String[] args) throws Exception {
        AudioFormat format = new AudioFormat(44100, 8, 2, false, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format, 4096);
        line.start();
        byte[] buf = new byte[2048];
        for (int i = 0; i < 44100; i++) {
            int t = i % 100;
            byte val = (byte) (t < 50 ? 200 : 50); // Square wave
            buf[(i % 1024) * 2] = val;
            buf[(i % 1024) * 2 + 1] = val;
            if (i % 1024 == 1023) {
                line.write(buf, 0, buf.length);
            }
        }
        line.drain();
        line.close();
        System.out.println("Beep done");
    }
}
