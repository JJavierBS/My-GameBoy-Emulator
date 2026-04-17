package gpu;
public class OAMDumper {
    public static void dump(memory.Mmu mmu) {
        System.out.println("OAM DUMP:");
        for(int i=0; i<40; i++) {
            int addr = 0xFE00 + i*4;
            int y = mmu.readByte(addr) & 0xFF;
            int x = mmu.readByte(addr+1) & 0xFF;
            int tile = mmu.readByte(addr+2) & 0xFF;
            System.out.printf("OAM %d: Y=%d X=%d Tile=%d\n", i, y, x, tile);
        }
    }
}
