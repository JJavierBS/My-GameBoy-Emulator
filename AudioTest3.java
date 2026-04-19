import javax.sound.sampled.*;

public class AudioTest3 {
    public static void main(String[] args) throws Exception {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        System.out.println("Mixers supporting 16-bit signed SourceDataLine:");
        for (Mixer.Info mInfo : AudioSystem.getMixerInfo()) {
            Mixer m = AudioSystem.getMixer(mInfo);
            if (m.isLineSupported(info)) {
                System.out.println(mInfo.getName() + " - " + mInfo.getDescription());
            }
        }
    }
}
