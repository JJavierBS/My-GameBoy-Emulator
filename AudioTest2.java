import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class AudioTest2 {
    public static void main(String[] args) throws Exception {
        AudioFormat format = new AudioFormat(44100, 8, 2, false, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        System.out.println("Mixers supporting SourceDataLine:");
        for (Mixer.Info mInfo : AudioSystem.getMixerInfo()) {
            Mixer m = AudioSystem.getMixer(mInfo);
            if (m.isLineSupported(info)) {
                System.out.println(mInfo.getName() + " - " + mInfo.getDescription());
            }
        }
    }
}
