package cpu;

import memory.Mmu;

public class Timer {
	private final int[] PERIODS = {1024, 16, 64, 256};
	private int divCont;
	private int timerCont;
	private final InterruptionManager interruptionManager;
	private final Mmu mmu;
	private boolean TIMAOverflow;
	private int TIMADelayCounter=0;





	
	public Timer(InterruptionManager interruptionManager, Mmu mmu) {
		super();
		divCont=0;
		timerCont=0;
		this.interruptionManager=interruptionManager;
		this.mmu=mmu;
		this.inicializateRegisters();
	}
	
	
	public void resetInternalCounters() {
		divCont = 0;
		timerCont = 0;
		TIMAOverflow = false;
		TIMADelayCounter = 0;
	}
	

	public void step(int cycles) {

		divCont+=cycles;
		if(divCont>=256) {
			divCont-=256;
			setDIV((getDIV()+1)&0xFF);
		}
		
		if((getTAC()&0X04)!=0) {

			int period = PERIODS[getTAC() & 0x03]; 
			timerCont+=cycles;

			while(timerCont>=period) {
				timerCont-=period;
				if((getTIMA()&0xFF) == 0xFF) {
					if(!TIMAOverflow) {
						TIMAOverflow = true;
						TIMADelayCounter = 0;
						setTIMA(0x00);
					}
				} 
				else { 
					setTIMA((getTIMA()+1)&0xFF);
				}
			}
		} else {
			TIMAOverflow = false;
			TIMADelayCounter = 0;
		}
		
		if (TIMAOverflow) {
			TIMADelayCounter += cycles;
			if (TIMADelayCounter >= 4) {
				TIMAOverflow = false;
				TIMADelayCounter = 0;
				setTIMA(getTMA());
				interruptionManager.requestInterrupt(2);
			}
		}
	}

	private int getDIV(){
		return mmu.readByte(0xFF04);
	}

	private void setDIV(int value){
		mmu.forceWriteByte(0xFF04, value);
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

	
	private void inicializateRegisters(){
		mmu.writeByte(0xFF04, 0x00);
		mmu.writeByte(0xFF05, 0x00);
		mmu.writeByte(0xFF06, 0x00);
		mmu.writeByte(0xFF07, 0x05);
	}
	
}
