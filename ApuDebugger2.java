import apu.Apu;
public class ApuDebugger2 extends Apu {
    int nonSilence = 0;
    public void pushSampleOverride(int left, int right) {
        if (left != 128 || right != 128) nonSilence++;
    }
    public static void main(String[] args) {
        ApuDebugger2 apu = new ApuDebugger2();
        apu.writeRegister(0xFF26, 0x80);
        apu.writeRegister(0xFF25, 0xFF);
        apu.writeRegister(0xFF24, 0x77);
        apu.writeRegister(0xFF11, 0x80);
        apu.writeRegister(0xFF12, 0xF0);
        apu.writeRegister(0xFF13, 0xFF);
        apu.writeRegister(0xFF14, 0x87);
        for (int i = 0; i < 4194304; i += 100) {
            apu.step(100);
        }
    }
}
