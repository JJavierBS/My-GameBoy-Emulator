package cpu;
import memory.Mmu;

public class Cpu {
	private int cont = 0;
	private int a, f, b, c, d, e, h, l;
		
	private int sp, pc;
	
	private final Mmu mmu;
	
	private final Timer timer;

	private final InterruptionManager iM;
	
	private boolean halted, stop, pendingIME, haltBug;
	
	public Cpu(Mmu mmu, Timer timer, InterruptionManager iM) {
		super();
		this.mmu = mmu;
		this.timer=timer;
		this.iM=iM;
		halted=false;
		stop=false;
		pendingIME=false;
		haltBug=false;
	}
	

	
	public int getA() {
		return a&0xFF;
	}

	public void setA(int a) {
		this.a = a & 0xFF;
	}

	public int getF() {
		return f & 0xF0;
	}

	public void setF(int f) {
		this.f = f & 0xF0;
	}

	public int getB() {
		return b&0xFF;
	}

	public void setB(int b) {
		this.b = b & 0xFF;
	}
	
	public void setAF(int word) {
		this.a = (word>>8) & 0xFF;
		this.f = word & 0xF0;
	}
	
	public int getAF() {
		return ((a<<8) | f) & 0xFFFF;
	}

	public int getC() {
		return c & 0xFF;
	}

	public void setC(int c) {
		this.c = c & 0xFF;
	}
	
	public int getBC() {
		return ((b<<8) | c) & 0xFFFF;
	}
	
	public void setBC(int word) {
		this.b = word>>8 & 0xFF;
		this.c = word & 0xFF;
	}

	public int getD() {
		return d & 0xFF;
	}

	public void setD(int d) {
		this.d = d & 0xFF;
	}

	public int getE() {
		return e & 0xFF;
	}

	public void setE(int e) {
		this.e = e & 0xFF;
	}
	
	public int getDE() {
		return ((d<<8) | e) & 0xFFFF;
	}
	
	public void setDE(int word) {
		this.d = word>>8 & 0xFF;
		this.e = word & 0xFF;
	}

	public int getH() {
		return h & 0xFF;
	}

	public void setH(int h) {
		this.h = h & 0xFF;
	}

	public int getL() {
		return l & 0xFF;
	}

	public void setL(int l) {
		this.l = l & 0xFF;
	}

	public int getHL() {
		return (((h & 0Xff)<<8) | (l & 0xFF));
	}
	
	public void setHL(int word) {
		this.h = word>>8 & 0xFF;
		this.l = word & 0xFF;
	}

	public int getSp() {
		return sp & 0xFFFF;
	}

	public void setSp(int sp) {
		this.sp = sp & 0xFFFF;
	}

	public int getPc() {
		return pc & 0xFFFF;
	}

	public void setPc(int pc) {
		this.pc = pc & 0xFFFF;
	}
	
	public Mmu getMmu() {
		return this.mmu;
	}

	public Timer getTimer() {
		return this.timer;
	}

	public InterruptionManager getInterruptionManager() {
		return this.iM;
	}
	
	public void setHalted(boolean value) {
		this.halted=value;
	}
	
	public boolean isHalted() {
		return this.halted;
	}

	public void setStop(boolean value) {
		this.stop=value;
	}

	public boolean isStop() {
		return this.stop;
	}

	public void setPendingIME(boolean value) { 
		this.pendingIME=value;
	}

	public boolean isPendingIME() {
		return this.pendingIME;
	}

	public int getCont() {
		return cont;
	}

	public boolean isHaltBug() {
		return haltBug;
	}

	public void setHaltBug(boolean haltBug) {
		this.haltBug = haltBug;
	}

	
	
	private int extraGpuCycles = 0;

	public void stepHardware(int cycles) {
		this.timer.step(cycles);
		this.extraGpuCycles += cycles;
	}

	public int consumeExtraGpuCycles() {
		int e = this.extraGpuCycles;
		this.extraGpuCycles = 0;
		return e;
	}

	public void updateZeroFlag(boolean value) {
		if(value) f = f | 0x80;
		else f = f & ~0x80;
	}
	
	public boolean isZeroFlag() {
		return (f & 0x80)!=0;
	}
	
	public void updateSubstractFlag(boolean value) {
		if(value) f = f | 0x40;
		else f = f & ~0x40;
	}
	
	public boolean isSubstractFlag() {
		return (f & 0x40)!=0;
	}
	
	public void updateHalfCarryFlag(boolean value) {
		if(value) f = f | 0x20;
		else f = f & ~0x20;
	}
	
	public boolean isHalfCarryFlag() {
		return (f & 0x20)!=0;
	}
	
	public void updateCarryFlag(boolean value) {
		if(value) f = f | 0x10;
		else f = f & ~0x10;
	}
	
	public boolean isCarryFlag() {
		return (f & 0x10)!=0;
	}
	
	
	
	
	
	public byte fetchByte() {
		byte value = (byte)(mmu.readByte(pc) & 0xFF);
		pc++;
		pc &= 0xFFFF;
		return value;
	}
	
	public int fetchWord() {
		int value = mmu.readWord(pc);
		pc = (pc + 2) & 0xFFFF;
		return value;
	}
	
	
	
	public void pushByte(int value) {
		sp--;
		mmu.writeByte(sp,(byte)(value & 0xFF));
		if(sp>0xFFFE || sp<0x8000) { // Gameboy allows stack in WRAM and sometimes HRAM
			// Overflow check
		}
	}
	
	public void pushWord(int value) {
		this.pushByte((value >> 8) & 0xFF); // high byte
		this.pushByte(value & 0xFF);        // low byte
	}
	
	public byte popByte() {
		byte value = mmu.readByte(sp);
		sp++;
		if(sp>0xFFFE){
			// Some games might use WRAM for stack and occasionally pop past it temporarily or simply start there. We only care if it goes past FFFE.
		}
		return value;
	}
	
	public int popWord() {
		int lowByte = this.popByte() & 0xFF;
		int highByte = this.popByte() & 0xFF;
		return (highByte << 8) | lowByte;
	}
	
	public void pushPC() {
		this.pushWord(this.pc);
	}
	
	
	public int execute(InstructionSet ins) {
		cont++;
		if(this.stop || this.halted) return 0;
		byte op;
		if(!haltBug){
			op = fetchByte();
		}
		else{
			pc--;
			op=fetchByte();
			haltBug=false;
		}
		Instruction instruction = ins.get(op);
		if(instruction==null) {
			System.out.println("Unknown instruction " + String.format("%02X", op) + " at " + String.format("%04X", pc - 1));
		}
		int cycles = instruction.execute(this);

		return cycles;
	}
	

	public void inicializateRegisters() {
		this.setA(0x01);
		this.setF(0xB0);
		this.setB(0x00);
		this.setC(0x13);
		this.setD(0x00);
		this.setE(0xD8);
		this.setH(0x01);
		this.setL(0x4D);
		this.setSp(0xFFFE); 
		this.setPc(0x0100);
 		mmu.writeByte(0xFF40, 0x91);
		mmu.writeByte(0xFF42, 0x00);
		mmu.writeByte(0xFF43, 0x00);
		mmu.writeByte(0xFF45, 0x00);
		mmu.writeByte(0xFF47, 0xFC);
		mmu.writeByte(0xFF48, 0xFF);
		mmu.writeByte(0xFF49, 0xFF);
		mmu.writeByte(0xFF4A, 0x00);
		mmu.writeByte(0xFF4B, 0x00);
		mmu.writeByte(0xFFFF, 0x00);
	}
	
	
	@Override
	public String toString() {
		return "A:" + String.format("%02X", a&0xFF) + " F:" + String.format("%02X", f) + " B:" + String.format("%02X", b & 0xFF) + " C:" + String.format("%02X", c&0xFF) +
				" D:" + String.format("%02X", d) + " E:" + String.format("%02X", e) + " H:" + String.format("%02X", h) + " L:" + String.format("%02X", l) +
				" SP:" + String.format("%04X", sp) + " PC:" + String.format("%04X", pc) + " PCMEM:" + String.format("%02X", mmu.readByte(pc)) + "," + String.format("%02X", mmu.readByte(pc+1)) + "," + String.format("%02X", mmu.readByte(pc+2)) + "," + String.format("%02X", mmu.readByte(pc+3));
	}
	
}
