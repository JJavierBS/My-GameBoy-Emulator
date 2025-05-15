package cpu;

public class InterruptionManager {
	private final int[] INTERRUPTIONS = {1,2,4,8,16};
	private final int[] interruptionsAddr = {0x40,0x48,0x50,0x58,0x60};
	//Indexación:
	//0 -> Vblank
	//1 -> LCD
	//2 -> Timer
	//3 -> Serial
	//4 -> JoyPad
	private int IF;	//Flag para solicitar interrupciones
	private int IE;	//Selección de interrupciones con flags
	private boolean IME; //Permite o no las interrupciones en el sistema (Interrupt Master Enable)

	
	
	public InterruptionManager() {
		super();
		IF=0;
		IE=0;
		IME=false;
	}
	
	//Función que permite a otros módulos solicitar interrupciones
	//Se comprueba después de la ejecución de cada instrucción**
	public void requestInterrupt(int type) {
		IF |= INTERRUPTIONS[type];
	}
	
	
	//Función para activar y desactivar interrupciones
	public void setIME(boolean value) {
		IME=value;
	}
	
	//Función para realizar las interrupciones
	//Es llamada después de cada instrucción del procesador
	public boolean handleInterrupt(Cpu cpu) {
		if(!IME) return false; //las interrupciones están desactivadas		
		int interruptions=IF & IE;
		if(interruptions==0) return false; //Comprobar si hay alguna interrupción posible a ejecutar
		
		for(int i=0; i<5; i++) {
			int flag = INTERRUPTIONS[i];
			if((interruptions & flag)!=0) { //Ejecutar ESA interrupción
				//Quitamos esta interrupción pendiente
				IF &= ~flag;
				IME=false; //Deshabilitamos temporalemente el resto de interrupciones
				cpu.pushPC();
				cpu.setPc(interruptionsAddr[i]);
				cpu.setStop(false);
				//System.out.println("Interrupción manejada: " + i);
				return true;
			}
		}
		return false;
	}

	public int getIF() {
		return IF;
	}

	public void setIF(int iF) {
		IF = iF;
	}

	public int getIE() {
		return IE;
	}

	public void setIE(int iE) {
		IE = iE;
	}

	public boolean isIME() {
		return IME;
	}
	
	
	
	
	
}
