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
		if(addr==0xFF44){
			return (byte)0x90;
		}
		//if(addr==0xFFFF) System.out.println("MMU: read IE = " + Integer.toHexString(memory[addr & 0xFFFF] & 0xFF));
		return memory[addr];
		
	}
	
	
	//Escribir byte
	public void writeByte(int addr, int value) {
		addr &= 0xFFFF;
		memory[addr] = (byte)(value & 0xFF);
		if(addr>=0x8000 && addr<=0x9FFF) {
			//System.out.println("Escribiendo en la VRAM: " + Integer.toHexString(addr) + " valor: " + Integer.toHexString(value));
			if(addr>=0x9800){
			//	System.out.println("Escribiendo en el tilemap: " + Integer.toHexString(addr) + " valor: " + Integer.toHexString(value));
			}
			else {
			//	System.out.println("Escribiendo en el tile data: " + Integer.toHexString(addr) + " valor: " + Integer.toHexString(value));
			}
		}
		if(addr==0xFFFF) System.out.println("MMU: write IE = " + Integer.toHexString(value));
		if(addr==0xFF07){
			System.out.println("MMU: write TAC = " + Integer.toHexString(value));
		}
		if(addr==0xFF83){
			System.out.println("");
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
