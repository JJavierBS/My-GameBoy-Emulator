package cpu;
import memory.Mmu;

//Esta clase modela el comportamiento de la cpu
public class Cpu {
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
	private boolean halted, stop;
	
	public Cpu(Mmu mmu, Timer timer, InterruptionManager iM) {
		super();
		this.mmu = mmu;
		this.timer=timer;
		this.iM=iM;
		halted=false;
		stop=false;
		a = 0;
		f = 0;
		b = 0;
		c = 0;
		d = 0;
		e = 0;
		h = 0;
		l = 0;
		//La pila comienza en 0xFFFE y avanza hacia abajo
		sp = 0xFFFE;
		pc = 0x0100;
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
		this.f = word & 0xFF;
	}
	
	public int getAF() {
		return ((a<<8) | f) & 0xFFFF;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
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
		this.d = d;
	}

	public int getE() {
		return e;
	}

	public void setE(int e) {
		this.e = e;
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
		this.h = h;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public int getHL() {
		return ((h<<8) | l) & 0xFFFF;
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
	}
	
	public void pushWord(int value) {
		this.pushByte(value>>8 & 0xFF); //highByte
		this.pushByte(value & 0xFF); //lowByte
	}
	
	//saca el byte del final de la pila
	public byte popByte() {
		byte value = mmu.readByte(sp);
		sp++;
		return value;
	}
	
	public int popWord() {
		byte lowByte = this.popByte();
		byte highByte = this.popByte();
		return (lowByte & 0xFF) + (highByte<<8); 
	}
	
	public void pushPC() {
		this.pushWord(this.pc);
	}
	
	//Funciones independientes
	
	//Función para ejeutar
	public int execute(InstructionSet ins) {
		if(this.stop) return 0;
		byte op = fetchByte();
		//System.out.println("Opcode: " + Integer.toHexString(op) + " Integer -> " + op);
		Instruction instruction = ins.get(op);
		if(instruction==null) {
			System.out.println("Instruction is null on pc = " + (this.pc-1));
			System.out.println("Operation code = " + Integer.toHexString(op) + " Integer -> " + op);
			System.exit(1);
		}
		int cycles = instruction.execute(this);
		//System.out.println(this.toString());
		return cycles;
	}
	

	public void inicializateRegisters() {
		this.setAF(0x01B0);
		this.setBC(0x0013);
		this.setDE(0x00D8);
		this.setHL(0x014D);
		this.setSp(0xFFFE);
		this.setPc(0x0100);
	}
	
	
	@Override
	public String toString() {
		return "Cpu [a=" + a + ", f=" + f + ", b=" + b + ", c=" + c + ", d=" + d + ", e=" + e + ", h=" + h + ", l=" + l
				+ ", sp=" + sp + ", pc=" + pc + ", lcdcontrol=" + Integer.toHexString(mmu.readByte(0xFF40)) + "]";
	}
	
	
}
