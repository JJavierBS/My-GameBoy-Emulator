import javax.sound.sampled.*;
public class MixerTest {
    public static void main(String[] args) {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        System.out.println("Available Mixers for SourceDataLine (Playback):");
        for (Mixer.Info mInfo : AudioSystem.getMixerInfo()) {
            try {
                Mixer m = AudioSystem.getMixer(mInfo);
                if (m.isLineSupported(info)) {
                    System.out.println(" - " + mInfo.getName() + " | " + mInfo.getDescription());
                }
            } catch (Exception e) {}
        }
    }
}
