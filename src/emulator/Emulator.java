package emulator;

import java.io.File;
import java.io.IOException;

import cpu.Cpu;
import cpu.InstructionSet;
import cpu.InterruptionManager;
import cpu.Timer;
import gpu.Gpu;
import gpu.GpuDisplay;
import memory.Mmu;

public class Emulator {
	private InterruptionManager iM;
	private Cpu cpu;
	private Mmu mmu;
	private Timer timer;
	private InstructionSet instructionSet;
	private Gpu gpu;
	private GpuDisplay gpuD;
	
	public Emulator() {
		iM = new InterruptionManager();
		timer = new Timer(iM);
		mmu = new Mmu(timer);
		try {
			mmu.loadROM(new File("C:\\Users\\josej\\eclipse-workspace\\myGameBoyEmulator\\romTest\\tetris.gb"));
		}
		catch (IOException e){
			System.out.println("No se ha podido cargar la ROM corréctamente");
			System.exit(1);
		}
		cpu = new Cpu(mmu,timer);
		instructionSet = new InstructionSet();
		gpu = new Gpu(iM,mmu);
		gpuD = new GpuDisplay(gpu);
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
		while(true) {
			int cycles = cpu.execute(instructionSet);
			timer.step(cycles);
			gpu.step(cycles);
			iM.handleInterrupt(cpu);
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
