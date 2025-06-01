package emulator;

import cpu.Cpu;
import cpu.InstructionSet;
import cpu.InterruptionManager;
import cpu.Timer;
import gpu.Gpu;
import gpu.GpuDebugger;
import gpu.GpuDisplay;
import java.io.File;
import java.io.IOException;
import memory.Mmu;

public class Emulator {
	private static final int DEFAULT_CYCLES_WHEN_HALTED = 4;
	private final InterruptionManager iM;
	private final Cpu cpu;
	private Mmu mmu;
	private final Timer timer;
	private final InstructionSet instructionSet;
	private final Gpu gpu;
	private final GpuDisplay gpuD;
	private final GpuDebugger gpuDebugger;
	
	public Emulator() {
		mmu = new Mmu();
		try {
			mmu.loadROM(new File("C:\\Users\\josej\\eclipse-workspace\\myGameBoyEmulator\\romTest\\cpu_instrs.gb")); //Cargamos la rom
		}
		catch (IOException e){
			System.out.println("No se ha podido cargar la ROM corréctamente");
			System.exit(1);
		}
		iM = new InterruptionManager(mmu);
		timer = new Timer(iM,mmu);
		cpu = new Cpu(mmu,timer,iM);
		cpu.inicializateRegisters();
		instructionSet = new InstructionSet();
		gpu = new Gpu(iM,mmu);
		gpuD = new GpuDisplay(gpu);
		gpuDebugger = new GpuDebugger(mmu);
	}

	public Cpu getCpu() {
		return cpu;
	}

	public Mmu getMmu() {
		return mmu;
	}
	
	public InstructionSet getInstructionSet() {
		return instructionSet;
	}
	
	public Timer getTimer() {
		return timer;
	}
	
	public InterruptionManager getInterruptionManager(){
		return iM;
	}
	
	public GpuDisplay getGpuDisplay() {
		return this.gpuD;
	}

	
	//Función principal que se encarga de ejecutar las instrucciones de la ROM
	public void run() {
		boolean skipInterruptThisCycle = false;
		while(true) {
			int cycles;
			if(!cpu.isHalted() && !cpu.isStop()) {
				cycles = cpu.execute(instructionSet);
			}
			else{
				cycles = DEFAULT_CYCLES_WHEN_HALTED;
			}
			int interruptCycles = 0;
			if(skipInterruptThisCycle){
				skipInterruptThisCycle = false;
			}
			else{
				if(iM.handleInterrupt(cpu)) {
					interruptCycles = 5; 
				}
			}
			timer.step(cycles + interruptCycles);
			gpu.step(cycles + interruptCycles);
			if(cpu.isPendingIME()) {
				iM.setIME(true);
				cpu.setPendingIME(false);
				skipInterruptThisCycle = true; // Evitar interrupciones en este ciclo
			}
			//cpu.volcarAFichero(cpu.log);
		}
	}
	
	
	//Función para hacer tests cargando bytes concretos en zonas de memoria arbitrarias
	public void loadTest() {
		for (int tile = 0; tile < 256; tile++) {
		    int baseAddr = 0x8000 + tile * 16;
		    for (int row = 0; row < 8; row++) {
		        // Patrón simple: líneas diagonales
		        byte low = (byte)(0b10101010 >> (tile % 8));
		        byte high = (byte)(0b01010101 << (tile % 8));
		        mmu.writeByte(baseAddr + row * 2, low);
		        mmu.writeByte(baseAddr + row * 2 + 1, high);
		    }
		}
		// Tile map: 32x32 = 1024 bytes
		for (int i = 0; i < 1024; i++) {
		    mmu.writeByte(0x9800 + i, (byte)(i % 256)); // cada tile ID del 0 al 255
		}
		mmu.writeByte(0xFF40, (byte)10010001); // LCD on, bg on, tilemap at 0x9800, tiledata at 0x8000
		mmu.writeByte(0xFF42, (byte)0); // SCY = 0
		mmu.writeByte(0xFF43, (byte)0); // SCX = 0
		mmu.writeByte(0xFF47, (byte)0b11100100); // Paleta BG: blanco, claro, oscuro, negro

	}
	
	
	
}
