package memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Mmu {
	//Memoria de la mmu
	byte[] memory;
	
	public Mmu() {
		super();
		this.memory = new byte[0x10000]; //64KB
	}
	
	//funciones de lectura/escritura
	
	//Leer byte
	public byte readByte(int addr) {
		addr = addr & 0xFFFF;
		if(addr>=0xE000 && addr<=0xFDFF){ //echo RAM
			addr -= 0x2000; 
		}
		return memory[addr];
		
	}
	
	
	//Escribir byte
	public void writeByte(int addr, int value) {
		addr &= 0xFFFF;
		if(addr==0xFF46){
			int sourceHigh = value & 0xFF;
			int sourceAddress = sourceHigh << 8; //DMA -> transferencia de sprites

			for(int i = 0; i<160; i++){
				byte data = readByte(sourceAddress + i);
				writeByte(0xFE00 + i, data);
			}
			return; //No se escribe en la memoria, solo se hace la transferencia
		}
		if(addr>=0xE000 && addr<=0xFDFF){ //echo RAM
			addr -= 0x2000; 
		}
		memory[addr] = (byte)(value & 0xFF);
		//Debugging v
		if(addr==0xFFFF){
			int wdawd = 0;
		}
	}
	
	//Leer word
	public int readWord(int addr) {
		int lowByte = memory[addr & 0xFFFF] & 0xFF;
		int highByte = memory[(addr + 1) & 0xFFFF] & 0xFF;
		return (highByte << 8) | lowByte;
	}
	
	//Escribir word
	public void writeWord(int addr, int value) {
		//primero el lowByte ya que las roms de gamboy funcionan con little-endian
		memory[addr & 0xFFFF] = (byte)(value & 0xFF);
		memory[(addr + 1) & 0xFFFF] = (byte)((value>>8) & 0xFF);
	}
	
	//funciones de carga
	public void loadROM(byte[] rom, int index) {
		for(byte b : rom) {
			memory[index++]=b;
		}
	};
	
	public void loadROM(File romFile) throws IOException{
		try (FileInputStream fis = new FileInputStream(romFile)) {
			byte[] rom = fis.readAllBytes();
			for (int i = 0; i < rom.length; i++) {
				memory[i] = rom[i];
			}
			for(int i = 0xFF80; i<0xFFFE; i++) {
				memory[i] = (byte)0xFF;
			}
		}
	}
	
}
