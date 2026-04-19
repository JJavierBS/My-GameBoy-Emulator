package cpu;

import memory.Mmu;

public class InterruptionManager {
	private final int[] INTERRUPTIONS = {1,2,4,8,16};
	private final int[] interruptionsAddr = {0x40,0x48,0x50,0x58,0x60};






	private boolean IME;
	private final Mmu mmu;
	
	
	public InterruptionManager(Mmu mmu) {
		super();
		this.mmu = mmu;
		IME=false;
	}
	


	public void requestInterrupt(int type) {
		setIF(getIF() | INTERRUPTIONS[type]);
	}
	
	

	public void setIME(boolean value) {
		IME=value;
	}
	


	public boolean handleInterrupt(Cpu cpu) {
		int interruptions = getIF() & getIE();

		if(interruptions != 0 && cpu.isHalted()) {
			cpu.setHalted(false);
		}

		if(interruptions!=0 && cpu.isStop()) {
			cpu.setStop(false);
		}
		if(!IME) {
			return false;
		}
		if(interruptions == 0) return false;
		for(int i=0; i<5; i++) {
			int flag = INTERRUPTIONS[i];
			if((interruptions & flag)!=0) {

				setIF((getIF() & ~flag) & 0xFF);
				IME=false;
				cpu.pushPC();
				cpu.setPc(interruptionsAddr[i]);
				cpu.setStop(false);
				cpu.setHalted(false);
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
