package memory;

import cpu.Timer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Mmu {
	//Memoria de la mmu
	byte[] memory;
	Timer timer; //referencia al timer
	
	
	public Mmu(Timer timer) {
		super();
		this.memory = new byte[0x10000]; //64KB
		this.timer=timer;
	}
	
	
	//funciones de lectura/escritura
	
	//Leer byte
	public byte readByte(int addr) {
		return memory[addr & 0xFFFF];

	}
	
	
	//Escribir byte
	public void writeByte(int addr, int value) {
		memory[addr & 0xFFFF] = (byte)(value & 0xFF);
	}
	
	//Leer word
	public int readWord(int addr) {
		return memory[addr & 0xFFFF] + (memory[(addr+1) & 0xFFFF]<<8);
	}
	
	//Escribir word
	public void writeWord(int addr, int value) {
		//primero el lowByte ya que las roms de gamboy funcionan con little-endian
		memory[addr & 0xFFFF] = (byte)(value & 0xF);
		memory[(addr + 1) & 0xFFFF] = (byte)((value>>8) & 0xF);
	}
	
	//funciones de carga
	public void loadROM(byte[] rom, int index) {
		for(byte b : rom) {
			memory[index++]=b;
		}
	};
	
	public void loadROM(File romFile) throws IOException{
		try (FileInputStream fis = new FileInputStream(romFile)) {
			byte[] romData = fis.readAllBytes();
			System.out.println("ROM size: " + romData.length + " bytes");
			for (int i = 0; i < romData.length && i < 0x8000; i++) {
				memory[i] = romData[i];
			}
		}
	}
	
	
}
