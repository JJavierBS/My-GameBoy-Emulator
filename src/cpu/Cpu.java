package cpu;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import memory.Mmu;

//Esta clase modela el comportamiento de la cpu
public class Cpu {
	private int cont = 0;
	//Registros de 8 bits
	private int a, f, b, c, d, e, h, l; //Siendo f un marcador de flags y a un acumulador
		
	//Registros de 16 bits
	private int sp, pc; //sp=stack pointer && pc=program counter
	
	//Referencia a la mmu
	private final Mmu mmu;
	
	//Referencia al timer
	private final Timer timer;

	//Referencia al interruption manager
	private final InterruptionManager iM;
	
	//Flags de control
	private boolean halted, stop, pendingIME;
	
	public Cpu(Mmu mmu, Timer timer, InterruptionManager iM) {
		super();
		this.mmu = mmu;
		this.timer=timer;
		this.iM=iM;
		halted=false;
		stop=false;
		pendingIME=false;
	}
	

	//getters and setters
	
	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}
	
	public void setAF(int word) {
		//aplicamos un and bit a bit con 8 bits
		this.a = (word>>8) & 0xFF;
		this.f = word & 0xF0; //f solo tiene 4 bits significativos 
	}
	
	public int getAF() {
		return ((a<<8) | f) & 0xFFFF;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c & 0xFF;
	}
	
	public int getBC() {
		return ((b<<8) | c) & 0xFFFF;
	}
	
	public void setBC(int word) {
		//aplicamos un and bit a bit con 8 bits
		this.b = word>>8 & 0xFF;
		this.c = word & 0xFF;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d & 0xFF;
	}

	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.e = e & 0xFF;
	}
	
	public int getDE() {
		return ((d<<8) | e) & 0xFFFF;
	}
	
	public void setDE(int word) {
		//aplicamos un and bit a bit con 8 bits
		this.d = word>>8 & 0xFF;
		this.e = word & 0xFF;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h & 0xFF;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l & 0xFF;
	}

	public int getHL() {
		return (((h & 0Xff)<<8) | (l & 0xFF));
	}
	
	public void setHL(int word) {
		//aplicamos un and bit a bit con 8 bits
		this.h = word>>8 & 0xFF;
		this.l = word & 0xFF;
	}

	public int getSp() {
		return sp;
	}

	public void setSp(int sp) {
		this.sp = sp & 0xFFFF;
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
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

	//funciones
	
	
	//funciones de flags
	public void updateZeroFlag(boolean value) {
		if(value) f = f | 0x80; //or lógico
		else f = f & ~0x80; //and lógico con el complementario
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
	
	
	
	
	//funciones de fetch
	
	//función para obtener el siguiente byte a leer
	public byte fetchByte() {
		byte value = (byte)(mmu.readByte(pc) & 0xFF); //leemos de memoria
		pc++;  //avanzamos pc
		pc &= 0xFFFF; //protegemos pc
		return value;
	}
	
	//función para obtener los siguientes 16bits
	public int fetchWord() {
		int value = mmu.readWord(pc);
		//2 veces ya que un word son 2 bytes
		pc = (pc + 2) & 0xFFFF;
		return value;
	}
	
	
	//funciones de stack/pila
	
	//añade el byte al final de la pila
	public void pushByte(int value) {
		sp--;
		mmu.writeByte(sp,(byte)(value & 0xFF));
		if(sp>0xFFFE || sp<0xC000) {
			System.out.println("sp=" + sp);
			throw new RuntimeException("Stack overflow");
		}
	}
	
	public void pushWord(int value) {
		this.pushByte((value >> 8) & 0xFF); // high byte
		this.pushByte(value & 0xFF);        // low byte
	}
	
	//saca el byte del final de la pila
	public byte popByte() {
		byte value = mmu.readByte(sp);
		sp++;
		if(sp>0xFFFE || sp<0xC000){
			System.out.println("sp=" + sp);
			throw new RuntimeException("Stack underflow");
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
	
	//Funciones independientes
	
	//Función para ejeutar
	public int execute(InstructionSet ins) {
		//log
		System.out.println(cont++);
		String log = this.toString();
		//System.out.println(log);
		volcarAFichero(log);
		//endlog
		if(this.stop || this.halted) return 0;
		byte op = fetchByte();
		//System.out.println("Opcode: " + Integer.toHexString(op) + " Integer -> " + op);
		Instruction instruction = ins.get(op);
		if(pc>=0x8000 && (pc<0xC000 || pc>0xDFFF)) {
			System.out.println("pc out of ROM range: ");
		}
		if(instruction==null) {
			System.out.println("Instruction is null on pc = " + (this.pc-1));
			System.out.println("Operation code = " + Integer.toHexString(op) + " Integer -> " + op);
			System.exit(1);
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
	}
	
	
	@Override
	public String toString() {
		return "A:" + String.format("%02X", a&0xFF) + " F:" + String.format("%02X", f) + " B:" + String.format("%02X", b & 0xFF) + " C:" + String.format("%02X", c&0xFF) +
				" D:" + String.format("%02X", d) + " E:" + String.format("%02X", e) + " H:" + String.format("%02X", h) + " L:" + String.format("%02X", l) +
				" SP:" + String.format("%04X", sp) + " PC:" + String.format("%04X", pc) + " PCMEM:" + String.format("%02X", mmu.readByte(pc)) + "," + String.format("%02X", mmu.readByte(pc+1)) + "," + String.format("%02X", mmu.readByte(pc+2)) + "," + String.format("%02X", mmu.readByte(pc+3));
	}
	
	public void volcarAFichero(String log){
		try {
			File file = new File("log.txt");
			FileWriter fw = new FileWriter(file, true); // true para append
			fw.write(log);
			fw.write("\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//A:00 F:11 B:22 C:33 D:44 E:55 H:66 L:77 SP:8888 PC:9999 PCMEM:AA,BB,CC,DD
}
