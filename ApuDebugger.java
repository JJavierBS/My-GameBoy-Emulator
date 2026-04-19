import apu.Apu;
public class ApuDebugger {
    public static void main(String[] args) {
        Apu apu = new Apu();
        apu.writeRegister(0xFF26, 0x80); // Master on
        apu.writeRegister(0xFF25, 0xFF); // Route all channels to both L & R
        apu.writeRegister(0xFF24, 0x77); // Master volume max
        
        apu.writeRegister(0xFF11, 0x80); // 50% duty
        apu.writeRegister(0xFF12, 0xF0); // Initial volume 15
        apu.writeRegister(0xFF13, 0xFF); // Freq LSB
        apu.writeRegister(0xFF14, 0x87); // Freq MSB & trigger
        
        System.out.println("Starting emulation steps...");
        for (int i = 0; i < 4194304 * 2; i += 100) {
            apu.step(100);
        }
        System.out.println("Done.");
    }
}
