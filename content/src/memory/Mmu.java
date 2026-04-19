package memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class Mmu {
	byte[] memory;
	byte[] romData;
	byte[] ramData;
	int romBank = 1;
	int ramBank = 0;
	boolean isMBC1 = false;
	boolean isMBC3 = false;
	boolean isMBC5 = false;
	boolean isMBC = false;
	boolean ramEnabled = false;
	boolean romBankingMode = true;
	File saveFile;
	cpu.Timer timer;
	emulator.Joypad joypad;
	apu.Apu apu;

	public void setTimer(cpu.Timer timer) {
		this.timer = timer;
	}

	public void setJoypad(emulator.Joypad joypad) {
		this.joypad = joypad;
	}

	public void setApu(apu.Apu apu) {
		this.apu = apu;
	}

	public Mmu() {
		super();
		this.memory = new byte[0x10000];
	}

	public byte readByte(int addr) {
		addr &= 0xFFFF;
		if (addr == 0xFF00) {
			if (joypad != null) {
				return (byte) joypad.readByte();
			}
			return (byte)((memory[0xFF00] & 0x30) | 0x0F);
		}
		if (addr >= 0xFF10 && addr <= 0xFF3F) {
			if (apu != null) return apu.readRegister(addr);
		}
		addr = addr & 0xFFFF;
		if(addr>=0xE000 && addr<=0xFDFF){
			addr -= 0x2000;
		}
		if (addr < 0x4000) {
			if (romData != null && addr < romData.length) return romData[addr];
			return memory[addr];
		} else if (addr >= 0x4000 && addr < 0x8000) {
			if (romData != null) {
				int offset = (romBank * 0x4000) + (addr - 0x4000);
				if (offset < romData.length) {
					return romData[offset];
				} else {
					return (byte)0xFF;
				}
			}
			return memory[addr];
		} else if (addr >= 0xA000 && addr < 0xC000) {
			if (isMBC && ramEnabled && ramData != null) {
				int offset = (ramBank * 0x2000) + (addr - 0xA000);
				if (offset < ramData.length) return ramData[offset];
			}
			return memory[addr];
		}
		return memory[addr];

	}

	public void forceWriteByte(int addr, int value) {
		memory[addr & 0xFFFF] = (byte)(value & 0xFF);
	}

	public void writeByte(int addr, int value) {
		addr &= 0xFFFF;
		if (addr == 0xFF00) {
			if (joypad != null) {
				joypad.writeByte(value);
			}
			memory[0xFF00] = (byte) ((value & 0x30) | (memory[0xFF00] & 0xCF));
			return;
		}
		if (addr >= 0xFF10 && addr <= 0xFF3F) {
			if (apu != null) apu.writeRegister(addr, value);
			memory[addr] = (byte)(value & 0xFF);
			return;
		}
		if (addr < 0x8000) {
			if (isMBC) {
				if (addr < 0x2000) {
					boolean wasEnabled = ramEnabled;
					ramEnabled = ((value & 0x0F) == 0x0A);
					if (wasEnabled && !ramEnabled) {
						try {
							saveRAM();
						} catch (IOException e) {
							throw new RuntimeException("Failed to save RAM during bank switch", e);
						}
					}
				} else if (addr >= 0x2000 && addr < 0x3000) {
					if (isMBC5) {
						romBank = (romBank & 0x100) | (value & 0xFF);
					} else {
						int lower = value & (isMBC3 ? 0x7F : 0x1F);
						if (lower == 0) lower = 1;
						romBank = (romBank & 0xE0) | lower;
					}
				} else if (addr >= 0x3000 && addr < 0x4000) {
					if (isMBC5) {
						romBank = (romBank & 0xFF) | ((value & 0x01) << 8);
					} else {
						int lower = value & (isMBC3 ? 0x7F : 0x1F);
						if (lower == 0) lower = 1;
						romBank = (romBank & 0xE0) | lower;
					}
				} else if (addr >= 0x4000 && addr < 0x6000) {
					if (!romBankingMode) {
						ramBank = value & 0x03;
						if (isMBC3 && value >= 0x08 && value <= 0x0C) {

							ramBank = value;
						}
					} else {
						if (isMBC1) {
							romBank = (romBank & 0x1F) | ((value & 0x03) << 5);
						} else {
							ramBank = value & 0x0F;
						}
					}
				} else if (addr >= 0x6000 && addr < 0x8000) {
					romBankingMode = (value & 0x01) == 0;
					if (romBankingMode) {
						ramBank = 0;
					}
				}
			}
			return;
		}
		if (addr == 0xFF04) {
			value = 0;
			if (timer != null) timer.resetInternalCounters();
		}
		if(addr==0xFF46){
			int sourceHigh = value & 0xFF;
			int sourceAddress = sourceHigh << 8;

			for(int i = 0; i<160; i++){
				byte data = readByte(sourceAddress + i);
				writeByte(0xFE00 + i, data);
			}
			return;
		}
		if(addr>=0xE000 && addr<=0xFDFF){
			addr -= 0x2000;
		}
		if (addr >= 0xA000 && addr < 0xC000) {
			if (isMBC && ramEnabled && ramData != null) {
				int offset = (ramBank * 0x2000) + (addr - 0xA000);
				if (offset < ramData.length) {
					ramData[offset] = (byte)(value & 0xFF);
				}
				return;
			}
		}
		if (addr == 0xFF02 && value == 0x81) {
		}
		memory[addr] = (byte)(value & 0xFF);
	}

	public int readWord(int addr) {
		int lowByte = readByte(addr) & 0xFF;
		int highByte = readByte((addr + 1) & 0xFFFF) & 0xFF;
		return (highByte << 8) | lowByte;
	}

	public void writeWord(int addr, int value) {
		writeByte(addr, value & 0xFF);
		writeByte((addr + 1) & 0xFFFF, (value >> 8) & 0xFF);
	}

	public void saveRAM() throws IOException {
		if (saveFile != null && ramData != null) {
			Files.write(saveFile.toPath(), ramData);
		}
	}

	public void loadROM(byte[] rom, int index) {
		for(byte b : rom) {
			if (index < memory.length) memory[index++] = b;
		}
	};

	public void loadROM(File romFile) throws IOException{
		String romPath = romFile.getAbsolutePath();
		String romName = romFile.getName();
		int dotIndex = romName.lastIndexOf('.');
		String savePath = (dotIndex == -1) ? romPath + ".sav" : romPath.substring(0, romPath.length() - (romName.length() - dotIndex)) + ".sav";
		saveFile = new File(savePath);

		try (FileInputStream fis = new FileInputStream(romFile)) {
			romData = fis.readAllBytes();

			int limit = Math.min(romData.length, 0x8000);
			for (int i = 0; i < limit; i++) {
				memory[i] = romData[i];
			}

			if (romData.length > 0x147) {
				int cartType = romData[0x147] & 0xFF;

				if (cartType >= 1 && cartType <= 3) {
					isMBC1 = true;
					isMBC = true;
				}

				if (cartType >= 0x0F && cartType <= 0x13) {
					isMBC3 = true;
					isMBC = true;
				}

				if (cartType >= 0x19 && cartType <= 0x1E) {
					isMBC5 = true;
					isMBC = true;
				}
				if (isMBC) {
					int ramSizeCode = romData[0x149] & 0xFF;
					int ramSize = 0x8000;
					switch (ramSizeCode) {
						case 0: ramSize = 0; break;
						case 1: ramSize = 2048; break;
						case 2: ramSize = 8192; break;
						case 3: ramSize = 32768; break;
						case 4: ramSize = 131072; break;
						case 5: ramSize = 65536; break;
					}
					if (ramSize > 0) {
						ramData = new byte[ramSize];
						if (saveFile.exists()) {
							byte[] saved = Files.readAllBytes(saveFile.toPath());
							System.arraycopy(saved, 0, ramData, 0, Math.min(saved.length, ramData.length));
						}
						Runtime.getRuntime().addShutdownHook(new Thread(() -> {
							if (ramData != null) {
								try {
									saveRAM();
								} catch (IOException e) {
									throw new RuntimeException("Failed to save RAM on shutdown", e);
								}
							}
						}));
					}
				}
			}

			for(int i = 0xFF80; i<0xFFFE; i++) {
				memory[i] = (byte)0xFF;
			}
		}
	}

}
