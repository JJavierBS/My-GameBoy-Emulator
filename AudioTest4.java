import javax.sound.sampled.*;

public class AudioTest4 {
    public static void main(String[] args) throws Exception {
        System.out.println("Checking all sample rates for Headset...");
        int[] rates = {44100, 48000, 8000, 11025, 16000, 22050, 32000, 96000};
        for (Mixer.Info mInfo : AudioSystem.getMixerInfo()) {
            if (mInfo.getName().contains("Headset")) {
                Mixer m = AudioSystem.getMixer(mInfo);
                System.out.println("Mixer: " + mInfo.getName());
                for (int rate : rates) {
                    for (int bits : new int[]{8, 16}) {
                        for (int channels : new int[]{1, 2}) {
                            AudioFormat f = new AudioFormat(rate, bits, channels, true, false);
                            DataLine.Info info = new DataLine.Info(SourceDataLine.class, f);
                            if (m.isLineSupported(info)) {
                                System.out.println("Supported: rate=" + rate + " bits=" + bits + " channels=" + channels);
                            }
                        }
                    }
                }
            }
        }
    }
}
