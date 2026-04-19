import apu.Apu;
import java.io.FileOutputStream;

public class ApuDebugger3 extends Apu {
    FileOutputStream fos;
    public ApuDebugger3() throws Exception {
        super();
        fos = new FileOutputStream("apu_out.raw");
    }
    public void pushSampleOverride(int left, int right) {
        // Not easily overridable if private. We'll just patch Apu.java temporarily.
    }
    public static void main(String[] args) throws Exception {
        Apu apu = new Apu();
        apu.writeRegister(0xFF26, 0x80); // Sound on
        apu.writeRegister(0xFF25, 0xFF); // Route all channels
        apu.writeRegister(0xFF24, 0x77); // Max vol
        
        apu.writeRegister(0xFF11, 0x80); // Duty 50%
        apu.writeRegister(0xFF12, 0xF1); // Envelope: Initial 15, decay 1
        apu.writeRegister(0xFF13, 0x00); // Freq LSB
        apu.writeRegister(0xFF14, 0x86); // Trigger + MSB
        
        System.out.println("Starting...");
        for (int i = 0; i < 4194304 * 2; i += 100) {
            apu.step(100);
        }
        System.out.println("Done. If it played a beep, Apu works.");
        System.exit(0);
    }
}
