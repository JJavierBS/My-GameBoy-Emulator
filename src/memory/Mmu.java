package memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cpu.InterruptionManager;

public class Mmu {
	//Memoria de la mmu
	byte[] memory;
	private final InterruptionManager interruptionManager;
	
	public Mmu(InterruptionManager interruptionManager) {
		super();
		this.memory = new byte[0x10000]; //64KB
		this.interruptionManager = interruptionManager;
	}
	
	//funciones de lectura/escritura
	
	//Leer byte
	public byte readByte(int addr) {
		addr = addr & 0xFFFF;
		if (addr == 0xFF0F) {
			return (byte)(interruptionManager.getIF() & 0xFF);
		}
		if (addr == 0xFFFF) {
			return (byte)(interruptionManager.getIE() & 0xFF);
		}
		return memory[addr];

	}
	
	
	//Escribir byte
	public void writeByte(int addr, int value) {
		addr = addr & 0xFFFF;
		if (addr == 0xFF0F) {
			interruptionManager.setIF(value & 0xFF);
			return;
		}
		if (addr == 0xFFFF) {
			interruptionManager.setIE(value & 0xFF);
			return;
		}
		memory[addr] = (byte)(value & 0xFF);
		if(addr>=0x8000 && addr<=0x9FFF) {
			System.out.println("Escribiendo en la VRAM: " + Integer.toHexString(addr) + " valor: " + Integer.toHexString(value));
			if(addr>=0x9800){
				System.out.println("Escribiendo en el tilemap: " + Integer.toHexString(addr) + " valor: " + Integer.toHexString(value));
			}
			else {
				System.out.println("Escribiendo en el tile data: " + Integer.toHexString(addr) + " valor: " + Integer.toHexString(value));
			}
		}
	}
	
	//Leer word
	public int readWord(int addr) {
		return memory[addr & 0xFFFF] + (memory[(addr+1) & 0xFFFF]<<8);
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
			byte[] romData = fis.readAllBytes();
			System.out.println("ROM size: " + romData.length + " bytes");
			for (int i = 0; i < romData.length && i < 0x8000; i++) {
				memory[i] = romData[i];
			}
		}
	}
	
	
}
