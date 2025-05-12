package cpu;

public class Timer {
	private final int[] PERIODS = {1024, 16, 64, 256}; //Tabla de frecuencias según TAC
	private int DIV; //Se incrementa a un ratio de 16384Hz
	private int TIMA;
	private int TMA;
	private int TAC; //control (bit 2 -> activado; bits0,1 -> frecuencia)
	private int divCont;
	private int timerCont;
	private InterruptionManager interruptionManager;
	
	public Timer(InterruptionManager interruptionManager) {
		super();
		DIV=0;
		TIMA=0;
		TMA=0;
		TAC=0;		
		divCont=0;
		timerCont=0;
		this.interruptionManager=interruptionManager;
	}
	
	
	//Esta función es llamada en cada instrucción para simular los ciclos que tardaría
	public void step(int cycles) {
		//DIV aumenta cada 256 ciclos
		divCont+=cycles;
		if(divCont>=256) {
			divCont-=256;
			DIV = (DIV + 1) &0XFF; //registro de 8 bits
		}
		
		if((TAC&0X04)!=0) {
			//si el timer esta activado accedemos a la tabla de frecuencias para comprobar cual es la oportuna
			int period = PERIODS[TAC & 0x03]; 
			timerCont+=cycles;
			//Ojo, puede que hayamos incrementado demasiados ciclos hasta el punto que haya que incrementar TIMA más de 1 vez, se soluciona con el siguiente while
			while(timerCont>=period) {
				timerCont-=period;
				TIMA++;
				
				//TIMA overflow
				if(TIMA>=0xFF) {
					TIMA=TMA;
					//TODO -> LLAMAR A LA INTERRUPCIÓN DEL TIMER -> TIMA OVERFLOW
				}
			}
		}
	}


	public int getDIV() {
		return DIV;
	}


	public void setDIV(int dIV) {
		DIV = dIV;
	}


	public int getTIMA() {
		return TIMA;
	}


	public void setTIMA(int tIMA) {
		TIMA = tIMA;
	}


	public int getTMA() {
		return TMA;
	}


	public void setTMA(int tMA) {
		TMA = tMA;
	}


	public int getTAC() {
		return TAC;
	}


	public void setTAC(int tAC) {
		TAC = tAC;
	}
	
	
	
	
}
