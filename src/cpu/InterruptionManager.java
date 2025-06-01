package cpu;

import memory.Mmu;

public class InterruptionManager {
	private final int[] INTERRUPTIONS = {1,2,4,8,16};
	private final int[] interruptionsAddr = {0x40,0x48,0x50,0x58,0x60};
	//Indexación:
	//0 -> Vblank
	//1 -> LCD
	//2 -> Timer
	//3 -> Serial
	//4 -> JoyPad
	private boolean IME; //Permite o no las interrupciones en el sistema (Interrupt Master Enable)
	private final Mmu mmu;
	
	
	public InterruptionManager(Mmu mmu) {
		super();
		this.mmu = mmu;
		IME=false;
	}
	
	//Función que permite a otros módulos solicitar interrupciones
	//Se comprueba después de la ejecución de cada instrucción**
	public void requestInterrupt(int type) {
		System.out.println("Interrupción solicitada: " + type);
		setIF(getIF() | INTERRUPTIONS[type]);
	}
	
	
	//Función para activar y desactivar interrupciones
	public void setIME(boolean value) {
		IME=value;
	}
	
	//Función para realizar las interrupciones
	//Es llamada después de cada instrucción del procesador
	public boolean handleInterrupt(Cpu cpu) {
		int interruptions = getIF() & getIE();
		// Si hay interrupción pendiente y la CPU está en HALT, salir de HALT aunque IME=0
		if(interruptions != 0 && cpu.isHalted()) {
			cpu.setHalted(false);
		}
		if(!IME) {
			return false; // las interrupciones están desactivadas
		}
		//System.out.println("Handeling interrupt");
		if(interruptions == 0) return false; // Comprobar si hay alguna interrupción posible a ejecutar
		for(int i=0; i<5; i++) {
			int flag = INTERRUPTIONS[i];
			if((interruptions & flag)!=0) { //Ejecutar ESA interrupción
				//Quitamos esta interrupción pendiente
				setIF((getIF() & ~flag) & 0xFF);
				IME=false; //Deshabilitamos temporalemente el resto de interrupciones
				cpu.pushPC();
				cpu.setPc(interruptionsAddr[i]);
				cpu.setStop(false);
				System.out.println("Interrupción manejada: " + i);
				cpu.setHalted(false); //La cpu deja el modo suspensión
				return true;
			}
		}
		return false;
	}

	public int getIF() {
		return mmu.readByte(0xFF0F);
	}

	public void setIF(int iF) {
		mmu.writeByte(0xFF0F, iF);
	}

	public int getIE() {
		return mmu.readByte(0xFFFF);
	}

	public void setIE(int iE) {
		mmu.writeByte(0xFFFF, iE);
	}

	public boolean isIME() {
		return IME;
	}
	
	
	
	
	
}
