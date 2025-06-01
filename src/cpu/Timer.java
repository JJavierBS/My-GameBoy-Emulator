package cpu;

import memory.Mmu;

public class Timer {
	private final int[] PERIODS = {1024, 16, 64, 256}; //Tabla de frecuencias según TAC
	private int divCont;
	private int timerCont;
	private final InterruptionManager interruptionManager;
	private final Mmu mmu;
	private boolean pendingInterrupt = false;
	//Registros:
	//DIV -> 0xFF04
	//TIMA -> 0xFF05
	//TMA -> 0xFF06
	//TAC -> 0xFF07
	
	public Timer(InterruptionManager interruptionManager, Mmu mmu) {
		super();
		divCont=0;
		timerCont=0;
		this.interruptionManager=interruptionManager;
		this.mmu=mmu;
		this.inicializateRegisters();
	}
	
	
	//Esta función es llamada en cada instrucción para simular los ciclos que tardaría
	public void step(int cycles) {
		//DIV aumenta cada 256 ciclos
		divCont+=cycles;
		if(divCont>=256) {
			divCont-=256;
			setDIV((getDIV()+1)&0xFF);
		}
		
		if((getTAC()&0X04)!=0) {
			//si el timer esta activado accedemos a la tabla de frecuencias para comprobar cual es la oportuna
			int period = PERIODS[getTAC() & 0x03]; 
			timerCont+=cycles;
			//Ojo, puede que hayamos incrementado demasiados ciclos hasta el punto que haya que incrementar TIMA más de 1 vez, se soluciona con el siguiente while
			while(timerCont>=period) {
				timerCont-=period;
				if((getTIMA()&0xFF) == 0xFF) {
					//Si TIMA es 0xFF, se pone a TMA y se solicita una interrupción para la instruccion siguiente
					setTIMA(getTMA());
					setPendingInterrupt();
				} else if(this.pendingInterrupt){
					setTIMA(getTMA());
					interruptionManager.requestInterrupt(2);
					this.pendingInterrupt=false;
				}
				else { 
					setTIMA((getTIMA()+1)&0xFF);
				}
			}
		}
	}

	private int getDIV(){
		return mmu.readByte(0xFF04);
	}

	private void setDIV(int value){
		mmu.writeByte(0xFF04, value);
	}

	private int getTIMA(){
		return mmu.readByte(0xFF05);
	}

	private void setTIMA(int value){
		mmu.writeByte(0xFF05, value);
	}

	private int getTMA(){
		return mmu.readByte(0xFF06);
	}

	private void setTMA(int value){
		mmu.writeByte(0xFF06, value);
	}

	private int getTAC(){
		return mmu.readByte(0xFF07);
	}

	private void setTAC(int value){
		mmu.writeByte(0xFF07, value);
	}

	private void setPendingInterrupt() {
		this.pendingInterrupt = true;
	}

	
	private void inicializateRegisters(){
		mmu.writeByte(0xFF04, 0x00); //DIV
		mmu.writeByte(0xFF05, 0x00); //TIMA
		mmu.writeByte(0xFF06, 0x00); //TMA
		mmu.writeByte(0xFF07, 0x05); //TAC
	}
	
}
