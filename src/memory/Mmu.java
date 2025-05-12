package memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cpu.Timer;

public class Mmu {
	//Memoria de la mmu
	byte[] memory;
	Timer timer; //referencia al timer
	
	
	public Mmu(Timer timer) {
		super();
		this.memory = new byte[0xFFFFF]; //64KB
		this.timer=timer;
	}
	
	
	//funciones de lectura/escritura
	
	//Leer byte
	public byte readByte(int addr) {
		byte value;
		switch(addr) {
			case 0xFF04:
				value = (byte) timer.getDIV();
				break;
			case 0xFF05:
				value = (byte) timer.getTIMA();
				break;
			case 0xFF06:
				value=(byte) timer.getTMA();
				break;
			case 0xFF07:
				value=(byte) (timer.getTAC() & 0xF8); //los bits 0-2 estan reservados
				break;
			default:
				value = memory[addr & 0xFFFF];
		};
		return value;
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
		};
	};
	
	public void loadROM(File romFile) throws IOException{
		FileInputStream fis = new FileInputStream(romFile);
		byte[] romData = fis.readAllBytes();
		fis.close();
		for(int i = 0; i<romData.length && i<0x8000; i++) {
			memory[i]=romData[i];
		}
	}
	
	
}
