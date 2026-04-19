package cpu;
import java.util.HashMap;
import java.util.Map;
import memory.Mmu;


public class InstructionSet {

	private final Map<Byte, Instruction> instructions;
	private final Map<Byte, Instruction> instructionsCB;
	
	public InstructionSet() {
		instructions = new HashMap<>();
		instructionsCB = new HashMap<>();
		
		instructions.put((byte)0x00, cpu -> {
			return 4;
		}); 
		

		instructions.put((byte)0xDD, cpu -> {
			return 4;
		});  

		instructions.put((byte)0xFD, cpu -> {
			return 4;
		});

		
		instructions.put((byte)0x01, cpu -> {
			cpu.setBC(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x02, cpu -> {
			cpu.getMmu().writeByte(cpu.getBC(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0x03, cpu -> {
			cpu.setBC(cpu.getBC()+1);
			return 8;
		});
		
		instructions.put((byte)0x04, cpu ->{
			int reg = cpu.getB();
			int value = (reg + 1) & 0xFF;
			cpu.setB(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x05, cpu -> {
			int value = (cpu.getB() - 1) & 0xFF;
			cpu.setB(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x06, cpu -> {
			cpu.setB(cpu.fetchByte() & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0x07, cpu -> {
    		int a = cpu.getA();
    		int bit7 = (a >> 7) & 1;
    		int result = ((a << 1) | bit7) & 0xFF;
    		cpu.setA(result);

    		cpu.updateZeroFlag(false); 
    		cpu.updateSubstractFlag(false);
    		cpu.updateHalfCarryFlag(false);
    		cpu.updateCarryFlag(bit7 == 1);

    		return 4;
		});

		instructions.put((byte)0x08, cpu -> {
			int addr = cpu.fetchWord();
			int value = cpu.getSp() & 0xFFFF; 
			Mmu mmu = cpu.getMmu();
			mmu.writeWord(addr&0xFFFF, value & 0xFFFF);
			return 20;
		});
		
		instructions.put((byte)0x09, cpu -> {
			int hl = cpu.getHL() & 0xFFFF;
			int bc = cpu.getBC() & 0xFFFF;
			int result = hl + bc;

			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((hl & 0xFFF) + (bc & 0xFFF)) > 0xFFF);
			cpu.updateCarryFlag(result > 0xFFFF);

			cpu.setHL(result & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x0A, cpu -> {
			cpu.setA(cpu.getMmu().readByte(cpu.getBC()));
			return 8;
		});
		
		instructions.put((byte)0x0B, cpu -> {
			cpu.setBC(cpu.getBC()-1);
			return 8;
		});
		
		
		instructions.put((byte)0x0C, cpu ->{
			int reg = cpu.getC();
			int value = (reg + 1) & 0xFF;
			cpu.setC(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x0D, cpu -> {
			int value = (cpu.getC() - 1) & 0xFF;
			cpu.setC(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		instructions.put((byte)0x0E, cpu -> {
			cpu.setC(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x0F, cpu -> {
			int value = cpu.getA();
			int result = ((value & 0xFF) >> 1) | ((value & 0x01) << 7);
			cpu.setA(result);
			
			cpu.updateZeroFlag(false);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag((value & 0x01)==1);
			return 4;
		});

		instructions.put((byte)0x10, cpu -> {
			cpu.setStop(true);
			return 4;
		});
		
		instructions.put((byte)0x11, cpu -> {
			cpu.setDE(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x12, cpu -> {
			cpu.getMmu().writeByte(cpu.getDE(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0x13, cpu -> {
			cpu.setDE(cpu.getDE()+1);
			return 8;
		});
		
		
		instructions.put((byte)0x14, cpu ->{
			int reg = cpu.getD();
			int value = (reg + 1) & 0xFF;
			cpu.setD(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x15, cpu -> {
			int value = (cpu.getD() - 1) & 0xFF;
			cpu.setD(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x16, cpu -> {
			cpu.setD(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x17, cpu -> {
    		int a = cpu.getA();
    		int carry = cpu.isCarryFlag() ? 1 : 0;
    		int bit7 = (a >> 7) & 1;
    
    		int result = ((a << 1) | carry) & 0xFF;
    		cpu.setA(result);

    		cpu.updateZeroFlag(false);
   			cpu.updateSubstractFlag(false);
    		cpu.updateHalfCarryFlag(false);
    		cpu.updateCarryFlag(bit7 == 1);

    		return 4;
		});
		
		instructions.put((byte)0x18, cpu -> {
			int despl = (byte)cpu.fetchByte();
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x19, cpu -> {
			int hl = cpu.getHL() & 0xFFFF;
			int de = cpu.getDE() & 0xFFFF;
			int result = hl + de;

			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((hl & 0xFFF) + (de & 0xFFF)) > 0xFFF);
			cpu.updateCarryFlag(result > 0xFFFF);

			cpu.setHL(result & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x1A, cpu -> {
			cpu.setA(cpu.getMmu().readByte(cpu.getDE()));
			return 8;
		});
		
		instructions.put((byte)0x1B, cpu -> {
			cpu.setDE(cpu.getDE()-1);
			return 8;
		});
		
		
		instructions.put((byte)0x1C, cpu ->{
			int reg = cpu.getE();
			int value = (reg + 1) & 0xFF;
			cpu.setE(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x1D, cpu -> {
			int value = (cpu.getE() - 1) & 0xFF;
			cpu.setE(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x1E, cpu -> {
			cpu.setE(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x1F, cpu -> {
			int a = cpu.getA();
			int carry = cpu.isCarryFlag() ? 1 : 0;
			int bit0 = a & 0x01;

			int result = ((a >> 1) | (carry << 7)) & 0xFF;
			cpu.setA(result);

			cpu.updateZeroFlag(false); 
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bit0 == 1);

			return 4;
		});
		
		instructions.put((byte)0x20, cpu -> {
			int despl = (byte)cpu.fetchByte();
			if(cpu.isZeroFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x21, cpu -> {
			cpu.setHL(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x22, cpu -> {
			int hl = cpu.getHL();
			cpu.getMmu().writeByte(hl, cpu.getA());
			cpu.setHL((hl+1) & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x23, cpu -> {
			cpu.setHL(cpu.getHL()+1);
			return 8;
		});
		
		instructions.put((byte)0x24, cpu ->{
			int reg = cpu.getH();
			int value = (reg + 1) & 0xFF;
			cpu.setH(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x25, cpu -> {
			int value = (cpu.getH() - 1) & 0xFF;
			cpu.setH(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x26, cpu -> {
			cpu.setH(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x27, cpu -> {
			int a = cpu.getA();
			int result = a;
			boolean carry = cpu.isCarryFlag();

			if (!cpu.isSubstractFlag()) {
				if ((a & 0x0F) > 0x09 || cpu.isHalfCarryFlag()) result += 0x06;
				if (a > 0x99 || carry) {
					result += 0x60;
					carry = true;
				}
			} else {
				if (cpu.isHalfCarryFlag()) result -= 0x06;
				if (carry) result -= 0x60;
			}

			result &= 0xFF;
			cpu.setA(result);
			cpu.updateZeroFlag((result&0xFF) == 0);
			cpu.updateHalfCarryFlag(false); 
			cpu.updateCarryFlag(carry);

			return 4;
		});
		
		instructions.put((byte)0x28, cpu -> {
			int despl = (byte)cpu.fetchByte();
			if(!cpu.isZeroFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x29, cpu -> {
			int hl = cpu.getHL() & 0xFFFF;
			int result = hl + hl;

			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((hl & 0xFFF) + (hl & 0xFFF)) > 0xFFF);
			cpu.updateCarryFlag(result > 0xFFFF);

			cpu.setHL(result & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x2A, cpu -> {
			int hl = cpu.getHL();
			cpu.setA(cpu.getMmu().readByte(hl));
			cpu.setHL((hl+1) & 0xFFFF);
			return 8;
		});
		
		
		instructions.put((byte)0x2B, cpu -> {
			cpu.setHL((cpu.getHL()-1) & 0xFFFF);
			return 8;
		});

		
		
		instructions.put((byte)0x2C, cpu ->{
			int reg = cpu.getL();
			int value = (reg + 1) & 0xFF;
			cpu.setL(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x2D, cpu -> {
			int value = (cpu.getL() - 1) & 0xFF;
			cpu.setL(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x2E, cpu -> {
			cpu.setL(cpu.fetchByte() & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0x2F, cpu -> {
			cpu.setA(~cpu.getA());
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag(true);
			return 4;
		});
		
		instructions.put((byte)0x30, cpu -> {
			int despl = (byte)cpu.fetchByte();
			if(cpu.isCarryFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x31, cpu -> {
			cpu.setSp(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x32, cpu -> {
			int hl = cpu.getHL();
			cpu.getMmu().writeByte(hl, cpu.getA());
			cpu.setHL((hl-1) & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x33, cpu -> {
			cpu.setSp(cpu.getSp()+1);
			return 8;
		});
		
		instructions.put((byte)0x34, cpu -> {
			Mmu aux = cpu.getMmu();
			int hl = cpu.getHL();
			int value = aux.readByte(hl) & 0xFF;
			int result = (value+1) & 0xFF;

			cpu.updateZeroFlag((result&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((value & 0xF) + 1) > 0xF);

			aux.writeByte(hl, result & 0xFF);
			return 12;
			
		});
		
		instructions.put((byte)0x35, cpu -> {
			Mmu aux = cpu.getMmu();
			int hl = cpu.getHL();
			int value = aux.readByte(hl) & 0xFF;
			int result = (value-1) & 0xFF;

			cpu.updateZeroFlag((result&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0xF) == 0);
			
			aux.writeByte(hl, result & 0xFF);
			return 12;
			
		});
		
		instructions.put((byte)0x36, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.fetchByte() & 0xFF);
			return 12;
		});
		
		instructions.put((byte)0x37, cpu -> {
			cpu.updateCarryFlag(true);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			return 4;
		});
		
		instructions.put((byte)0x38, cpu -> {
			int despl = (byte)cpu.fetchByte();
			if(!cpu.isCarryFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x39, cpu -> {
			int hl = cpu.getHL() & 0xFFFF;
			int sp = cpu.getSp() & 0xFFFF;
			int result = hl + sp;

			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((hl & 0xFFF) + (sp & 0xFFF)) > 0xFFF);
			cpu.updateCarryFlag(result > 0xFFFF);

			cpu.setHL(result & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x3A, cpu -> {
			int hl = cpu.getHL();
			cpu.setA(cpu.getMmu().readByte(hl));
			cpu.setHL((hl-1 ) & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x3B, cpu -> {
			cpu.setSp(cpu.getSp()-1);
			return 8;
		});
		
		instructions.put((byte)0x3C, cpu ->{
			int reg = cpu.getA();
			int value = (reg + 1) & 0xFF;
			cpu.setA(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x3D, cpu -> {
			int reg = cpu.getA();
			int value = (reg - 1) & 0xFF;
			cpu.setA(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((reg & 0x0F)==0);
			return 4;
		});
		
		instructions.put((byte)0x3E, cpu -> {
			cpu.setA(cpu.fetchByte() & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0x3F, cpu -> {
			cpu.updateCarryFlag(!cpu.isCarryFlag());
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			return 4;
		});
		
		instructions.put((byte)0x40, cpu -> {
			cpu.setB(cpu.getB());
			return 4;
		});

		instructions.put((byte)0x41, cpu -> {
			cpu.setB(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x42, cpu -> {
			cpu.setB(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x43, cpu -> {
			cpu.setB(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x44, cpu -> {
			cpu.setB(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x45, cpu -> {
			cpu.setB(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x46, cpu -> {
			cpu.setB(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x47, cpu -> {
			cpu.setB(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x48, cpu -> {
			cpu.setC(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x49, cpu -> {
			cpu.setC(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x4A, cpu -> {
			cpu.setC(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x4B, cpu -> {
			cpu.setC(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x4C, cpu -> {
			cpu.setC(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x4D, cpu -> {
			cpu.setC(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x4E, cpu -> {
			cpu.setC(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x4F, cpu -> {
			cpu.setC(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x50, cpu -> {
			cpu.setD(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x51, cpu -> {
			cpu.setD(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x52, cpu -> {
			cpu.setD(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x53, cpu -> {
			cpu.setD(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x54, cpu -> {
			cpu.setD(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x55, cpu -> {
			cpu.setD(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x56, cpu -> {
			cpu.setD(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x57, cpu -> {
			cpu.setD(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x58, cpu -> {
			cpu.setE(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x59, cpu -> {
			cpu.setE(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x5A, cpu -> {
			cpu.setE(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x5B, cpu -> {
			cpu.setE(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x5C, cpu -> {
			cpu.setE(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x5D, cpu -> {
			cpu.setE(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x5E, cpu -> {
			cpu.setE(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x5F, cpu -> {
			cpu.setE(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x60, cpu -> {
			cpu.setH(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x61, cpu -> {
			cpu.setH(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x62, cpu -> {
			cpu.setH(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x63, cpu -> {
			cpu.setH(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x64, cpu -> {
			cpu.setH(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x65, cpu -> {
			cpu.setH(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x66, cpu -> {
			cpu.setH(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x67, cpu -> {
			cpu.setH(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x68, cpu -> {
			cpu.setL(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x69, cpu -> {
			cpu.setL(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x6A, cpu -> {
			cpu.setL(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x6B, cpu -> {
			cpu.setL(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x6C, cpu -> {
			cpu.setL(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x6D, cpu -> {
			cpu.setL(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x6E, cpu -> {
			cpu.setL(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x6F, cpu -> {
			cpu.setL(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x70, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getB());
			return 8;
		});
		
		instructions.put((byte)0x71, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getC());
			return 8;
		});
		
		instructions.put((byte)0x72, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getD());
			return 8;
		});
		
		instructions.put((byte)0x73, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getE());
			return 8;
		});
		
		instructions.put((byte)0x74, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getH());
			return 8;
		});
		
		instructions.put((byte)0x75, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getL());
			return 8;
		});
		
		instructions.put((byte)0x76, cpu ->{

			if(!cpu.getInterruptionManager().isIME() && (cpu.getInterruptionManager().getIF() & cpu.getInterruptionManager().getIE() & 0x1F) != 0) {
				cpu.setHaltBug(true);
			}
			else{
				cpu.setHalted(true); 
			}
			return 4;
		});
		
		instructions.put((byte)0x77, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0x78, cpu -> {
			cpu.setA(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x79, cpu -> {
			cpu.setA(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x7A, cpu -> {
			cpu.setA(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x7B, cpu -> {
			cpu.setA(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x7C, cpu -> {
			cpu.setA(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x7D, cpu -> {
			cpu.setA(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x7E, cpu -> {
			cpu.setA(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x7F, cpu -> {
			cpu.setA(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x80, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getB() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 4;
		});
		
		instructions.put((byte)0x81, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getC() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 4;
		});
		
		instructions.put((byte)0x82, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getD() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 4;
		});
		
		instructions.put((byte)0x83, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getE() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 4;
		});
		
		
		instructions.put((byte)0x84, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getH() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x85, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getL() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 4;
		});
		
		instructions.put((byte)0x86, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0x87, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.getA() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 4;
		});
		
		instructions.put((byte)0x88, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getB() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 4;
		});
		
		instructions.put((byte)0x89, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getC() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x8A, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getD() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x8B, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getE() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 4;
		});
				
		instructions.put((byte)0x8C, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getH() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x8D, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getL() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x8E, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 8;
		});
		
		
		instructions.put((byte)0x8F, cpu -> {

			int a = cpu.getA() & 0xFF;
			int b = cpu.getA() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x90, cpu -> {
			int original = cpu.getA() & 0XFF;
			int value = ((original-cpu.getB()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getB()&0x0F));
			cpu.updateCarryFlag(original < cpu.getB());
			
			cpu.setA(value & 0xFF);
			return 4;
		});
		
		instructions.put((byte)0x91, cpu -> {
			int original = cpu.getA() & 0XFF;
			int value = ((original-cpu.getC()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getC()&0x0F));
			cpu.updateCarryFlag(original < cpu.getC());
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x92, cpu -> {
			int original = cpu.getA() & 0XFF;
			int value = ((original-cpu.getD()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getD()&0x0F));
			cpu.updateCarryFlag(original < cpu.getD());
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x93, cpu -> {
			int original = cpu.getA() & 0XFF;
			int value = ((original-cpu.getE()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getE()&0x0F));
			cpu.updateCarryFlag(original < cpu.getE());
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x94, cpu -> {
			int original = cpu.getA() & 0XFF;
			int value = ((original-cpu.getH()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getH()&0x0F));
			cpu.updateCarryFlag(original < cpu.getH());
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x95, cpu -> {
			int original = cpu.getA() & 0XFF;
			int value = ((original-cpu.getL()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getL()&0x0F));
			cpu.updateCarryFlag(original < cpu.getL());
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x96, cpu -> {
			int original = cpu.getA() & 0XFF;
			int operand = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int value = ((original-operand));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F));
			cpu.updateCarryFlag(original < operand);
			
			cpu.setA(value & 0xFF);
		 return 8;
		});
		
		instructions.put((byte)0x97, cpu -> {
			int original = cpu.getA() & 0XFF;
			int value = ((original-cpu.getA()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getA()&0x0F));
			cpu.updateCarryFlag(original < cpu.getA());
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x98, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getB() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x99, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getC() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x9A, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getD() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x9B, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getE() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x9C, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getH() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x9D, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getL() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		instructions.put((byte)0x9E, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 8;
		});
		
		instructions.put((byte)0x9F, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.getA() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
		 return 4;
		});
		
		
		
		instructions.put((byte)0xA0, cpu -> {
			int value = cpu.getA()&cpu.getB();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA1, cpu -> {
			int value = cpu.getA()&cpu.getC();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA2, cpu -> {
			int value = cpu.getA()&cpu.getD();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA3, cpu -> {
			int value = cpu.getA()&cpu.getE();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA4, cpu -> {
			int value = cpu.getA()&cpu.getH();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA5, cpu -> {
			int value = cpu.getA()&cpu.getL();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA6, cpu -> {
			int value = cpu.getA()&(cpu.getMmu().readByte(cpu.getHL()));
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xA7, cpu -> {
			int value = cpu.getA()&cpu.getA();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA8, cpu -> {
			int value = (cpu.getA()^cpu.getB()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA9, cpu -> {
			int value = (cpu.getA()^cpu.getC()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAA, cpu -> {
			int value = (cpu.getA()^cpu.getD()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAB, cpu -> {
			int value = (cpu.getA()^cpu.getE()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAC, cpu -> {
			int value = (cpu.getA()^cpu.getH()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAD, cpu -> {
			int value = (cpu.getA()^cpu.getL()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAE, cpu -> {
			int value = (cpu.getA()^cpu.getMmu().readByte(cpu.getHL())) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xAF, cpu ->{
			cpu.setA(0);
			cpu.updateZeroFlag(true);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB0, cpu -> {
			int value = cpu.getA()|cpu.getB();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB1, cpu -> {
			int value = cpu.getA()|cpu.getC();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB2, cpu -> {
			int value = cpu.getA()|cpu.getD();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB3, cpu -> {
			int value = cpu.getA()|cpu.getE();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB4, cpu -> {
			int value = cpu.getA()|cpu.getH();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB5, cpu -> {
			int value = cpu.getA()|cpu.getL();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB6, cpu -> {
			int value = cpu.getA()|cpu.getMmu().readByte(cpu.getHL());
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xB7, cpu -> {
			int value = cpu.getA()|cpu.getA();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB8, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getB()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getB()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getB()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xB9, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getC()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getC()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getC()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBA, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getD()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getD()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getD()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBB, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getE()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getE()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getE()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBC, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getH()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getH()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getH()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBD, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getL()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getL()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getL()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBE, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getMmu().readByte(cpu.getHL())));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getMmu().readByte(cpu.getHL())&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getMmu().readByte(cpu.getHL())&0xFF));
		 return 8;
		});
		
		instructions.put((byte)0xBF, cpu -> {
			int original = cpu.getA();
			int value = ((original-cpu.getA()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getA()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getA()&0xFF));
		 return 4;
		});
		
		
		
		instructions.put((byte)0xC0, cpu -> {
			if(!cpu.isZeroFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});

		instructions.put((byte)0xC1, cpu -> {
			cpu.setBC(cpu.popWord());
			return 12;
		});

		instructions.put((byte)0xC2, cpu -> {
			int addr = cpu.fetchWord();
			if(!cpu.isZeroFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return 12;
		});
		
		instructions.put((byte)0xC3, cpu -> {
			cpu.setPc(cpu.fetchWord());
			return 16;
		});
		
		instructions.put((byte)0xC4, cpu -> {
			if(!cpu.isZeroFlag()) {
				int addr = cpu.fetchWord();
				cpu.pushWord(cpu.getPc());
				cpu.setPc(addr);
				return 24;
			}
			else {
				cpu.setPc(cpu.getPc()+2);
				return 12;
			}
		});

		instructions.put((byte)0xC5, cpu -> {
			cpu.pushWord(cpu.getBC());
			return 16;
		});
		
		instructions.put((byte)0xC6, cpu -> {
			int a = cpu.getA() & 0xFF;
			int b = cpu.fetchByte() & 0xFF;
			int value = a + b;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF)) > 0xF);
			cpu.updateCarryFlag(value>0xFF);

			cpu.setA(value & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0xC7, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0000);
			return 16;
		});
		
		instructions.put((byte)0xC8, cpu -> {
			if(cpu.isZeroFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});
		
		instructions.put((byte)0XC9, cpu -> { 

			cpu.setPc(cpu.popWord());
			return 16;
		});
		
		instructions.put((byte)0xCA, cpu -> {
			int addr = cpu.fetchWord();
			if(cpu.isZeroFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return 12;
		});
		
		instructions.put((byte)0xCB, cpu -> {
			Instruction inst = instructionsCB.get(cpu.fetchByte());
			return inst.execute(cpu);
		});

		instructions.put((byte)0xCC, cpu -> {
			if(cpu.isZeroFlag()) {
				int addr = cpu.fetchWord();
				cpu.pushWord(cpu.getPc());
				cpu.setPc(addr);
				return 24;
			}
			else {
				cpu.setPc(cpu.getPc()+2);
				return 12;
			}
		});
		
		instructions.put((byte)0xCD, cpu -> {
			int addr = cpu.fetchWord();
			cpu.pushWord(cpu.getPc());
			cpu.setPc(addr);
			return 24;
		});
		
		instructions.put((byte)0xCE, cpu -> {

			int a = cpu.getA();
			int b = cpu.fetchByte() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = a + b + carry;

			cpu.updateCarryFlag(value>0xFF);
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((a & 0xF) + (b & 0xF) + carry) > 0xF);

			cpu.setA(value & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0xCF, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0008);
			return 16;
		});
		
		instructions.put((byte)0xD0, cpu -> {
			if(!cpu.isCarryFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});

		instructions.put((byte)0xD1, cpu -> {
			cpu.setDE(cpu.popWord());
			return 12;
		});
		
		instructions.put((byte)0xD2, cpu -> {
			int addr = cpu.fetchWord();
			if(!cpu.isCarryFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return 12;
		});
		
		instructions.put((byte)0xD4, cpu -> {
			if(!cpu.isCarryFlag()) {
				int addr = cpu.fetchWord();
				cpu.pushWord(cpu.getPc());
				cpu.setPc(addr);
				return 24;
			}
			else {
				cpu.setPc(cpu.getPc()+2);
				return 12;
			}
		});

		instructions.put((byte)0xD5, cpu -> {
			cpu.pushWord(cpu.getDE()); 
			return 16;
		});
		
		instructions.put((byte)0xD6, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.fetchByte() & 0xFF;
			int value = (original - operand);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F));
			cpu.updateCarryFlag(original < operand); 

			cpu.setA(value & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0xD7, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0010);
			return 16;
		});
		
		instructions.put((byte)0xD8, cpu -> {
			if(cpu.isCarryFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});

		instructions.put((byte)0xD9, cpu -> {
			cpu.setPc(cpu.popWord());
			cpu.getInterruptionManager().setIME(true);
			return 16;
		});


		
		instructions.put((byte)0xDA, cpu -> {
			int addr = cpu.fetchWord();
			if(cpu.isCarryFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return  12;
		});
		
		instructions.put((byte)0xDC, cpu -> {
			if(cpu.isCarryFlag()) {
				int addr = cpu.fetchWord();
				cpu.pushWord(cpu.getPc());
				cpu.setPc(addr);
				return 24;
			}
			else {
				cpu.setPc(cpu.getPc()+2);
				return 12;
			}
		});
		
		instructions.put((byte)0xDE, cpu -> {
			int original = cpu.getA();
			int operand = cpu.fetchByte() & 0xFF;
			int carry = (cpu.isCarryFlag()) ? 1 : 0;
			int value = (original-operand-carry);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F)+carry);
			cpu.updateCarryFlag(original < operand + carry);
			
			cpu.setA(value & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0xDF, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0018);
			return 16;
		});

		instructions.put((byte)0xE0, cpu -> {
			int inm = cpu.fetchByte() & 0xFF;
			cpu.getMmu().writeByte(0xFF00 + (inm&0xFF), cpu.getA());
			return 12;
		});

		instructions.put((byte)0xE1, cpu -> {
			cpu.setHL(cpu.popWord());
			return 12;
		});

		instructions.put((byte)0xE2, cpu -> {
			cpu.getMmu().writeByte(0xFF00 + cpu.getC(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0xE5, cpu -> {
			cpu.pushWord(cpu.getHL());
			return 16;
		});

		instructions.put((byte)0xE6, cpu -> {
			int value = cpu.getA()&(cpu.fetchByte() & 0xFF);
			
			cpu.setA(value & 0xFF);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xE7, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0020);
			return 16;
		});

		instructions.put((byte)0xE8, cpu -> {
			int offset = (byte)cpu.fetchByte();
			int value = cpu.getSp() + offset;
			
			cpu.updateZeroFlag(false);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((cpu.getSp() & 0xF) + (offset & 0xF)) > 0xF);
			cpu.updateCarryFlag(((cpu.getSp() & 0xFF) + (offset & 0xFF)) > 0xFF);
			
			cpu.setSp(value & 0xFFFF);
			return 16;
		});

		instructions.put((byte)0xE9, cpu -> {
			cpu.setPc(cpu.getHL());
			return 4;
		});

		instructions.put((byte)0xEA, cpu -> {

			int addr = cpu.fetchWord();
			cpu.getMmu().writeByte(addr&0xFFFF, cpu.getA());
			return 16;
		});
		
		instructions.put((byte)0xEE, cpu -> {
			int value = (cpu.getA()^cpu.fetchByte()) & 0xFF;

			cpu.setA(value & 0xFF);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xEF, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0028);
			return 16;
		});

		instructions.put((byte)0xF0, cpu -> {
			int inm = cpu.fetchByte() & 0xFF;
			cpu.stepHardware(4);
			cpu.setA(cpu.getMmu().readByte(0xFF00 + (inm & 0xFF)));
			return 8;
		});

		instructions.put((byte)0xF1, cpu -> {
			int value = cpu.popWord();

    		cpu.setA((value >> 8) & 0xFF);
    		cpu.setF(value & 0xF0);

    		return 12;
		});

		instructions.put((byte)0xF2, cpu -> {
			cpu.setA(cpu.getMmu().readByte(0xFF00 + cpu.getC()));
			return 8;
		});

		instructions.put((byte)0xF3, cpu -> {
			cpu.getInterruptionManager().setIME(false);
			return 4;
		});

		instructions.put((byte)0xF5, cpu -> {
			cpu.pushWord((cpu.getA() << 8) | (cpu.getF() & 0xF0)); 
			return 16; 
		}); 
		
		instructions.put((byte)0xF6, cpu -> {
			int value = cpu.getA()|(cpu.fetchByte()& 0xFF);
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xF7, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0030);
			return 16;
		});

		instructions.put((byte)0xF8, cpu -> {
			
			int sp = cpu.getSp();
			byte offsetByte = (byte) cpu.fetchByte();
			int offset = offsetByte;
			int result = sp + offset;

			cpu.updateZeroFlag(false);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((sp & 0xF) + (offset & 0xF)) > 0xF);
			cpu.updateCarryFlag(((sp & 0xFF) + (offset & 0xFF)) > 0xFF);

			cpu.setHL(result & 0xFFFF);

			return 12;
		});

		instructions.put((byte)0xF9, cpu -> {
			cpu.setSp(cpu.getHL());
			return 8;
		});

		instructions.put((byte)0xFA, cpu -> {
			int addr = cpu.fetchWord();
			cpu.stepHardware(8);
			cpu.setA(cpu.getMmu().readByte(addr));
			return 8;
		});

		instructions.put((byte)0xFB, cpu -> {
			cpu.setPendingIME(true);
			return 4;
		});
		
		
		instructions.put((byte)0xFE, cpu -> {
			int original = cpu.getA() & 0xFF;
			int operand = cpu.fetchByte() & 0xFF; 
			int value = (original-operand);
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (operand&0x0F));
			cpu.updateCarryFlag(original < operand);
		 	return 8;
		});
		
		instructions.put((byte)0xFF, cpu -> {
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0038);
			return 16;
		});
		
		
		instructionsCB.put((byte) 0x00, cpu -> {
			int topbit = (cpu.getB() & 0x80)>>7;
			int value = (cpu.getB()<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x01, cpu -> {
			int topbit = (cpu.getC() & 0x80)>>7;
			int value = (cpu.getC()<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x02, cpu -> {
			int topbit = (cpu.getD() & 0x80)>>7;
			int value = (cpu.getD()<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setD(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x03, cpu -> {
			int topbit = (cpu.getE() & 0x80)>>7;
			int value = (cpu.getE()<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x04, cpu -> {
			int topbit = (cpu.getH() & 0x80)>>7;
			int value = (cpu.getH()<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setH(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x05, cpu -> {
			int topbit = (cpu.getL() & 0x80)>>7;
			int value = (cpu.getL()<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setL(value);
		 return 8;
		});
		
		instructionsCB.put((byte) 0x06, cpu -> {
			int original = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int topbit = (original & 0x80) >> 7;
			int value = ((original << 1) | topbit) & 0xFF;

			cpu.updateZeroFlag(value == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit == 1);

			cpu.getMmu().writeByte(cpu.getHL(), value);
			return 16;
		});
		
		instructionsCB.put((byte) 0x07, cpu -> {
			int topbit = (cpu.getA() & 0x80)>>7;
			int value = (cpu.getA()<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x08, cpu -> {
			int original = cpu.getB() & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);

			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x09, cpu -> {
			int original = cpu.getC() & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);

			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0A, cpu -> {
			int original = cpu.getD() & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);

			cpu.setD(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0B, cpu -> {
			int original = cpu.getE() & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);

			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0C, cpu -> {
			int original = cpu.getH() & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);

			cpu.setH(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0D, cpu -> {
			int original = cpu.getL() & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);

			cpu.setL(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0E, cpu -> {
			int original = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte) 0x0F, cpu -> {
			int original = cpu.getA() & 0xFF;
			int bottombit = original & 0x01;
			int value = (original>>1) | (bottombit<<7);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);

			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x10, cpu -> {
			int topbit = (cpu.getB() & 0x80)>>7;
			int value = (cpu.getB()<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x11, cpu -> {
			int topbit = (cpu.getC() & 0x80)>>7;
			int value = (cpu.getC()<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x12, cpu -> {
			int topbit = (cpu.getD() & 0x80)>>7;
			int value = (cpu.getD()<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setD(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x13, cpu -> {
			int topbit = (cpu.getE() & 0x80)>>7;
			int value = (cpu.getE()<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x14, cpu -> {
			int topbit = (cpu.getH() & 0x80)>>7;
			int value = (cpu.getH()<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setH(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x15, cpu -> {
			int topbit = (cpu.getL() & 0x80)>>7;
			int value = (cpu.getL()<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setL(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x16, cpu -> {
			int original = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int topbit = (original & 0x80) >> 7;
			int value = ((original << 1) | (cpu.isCarryFlag() ? 1 : 0)) & 0xFF;

			cpu.updateZeroFlag(value == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit == 1);

			cpu.getMmu().writeByte(cpu.getHL(), value);
			return 16;
		});
		
		instructionsCB.put((byte) 0x17, cpu -> {
			int topbit = (cpu.getA() & 0x80)>>7;
			int value = (cpu.getA()<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x18, cpu -> {

			int value = cpu.getB();
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.setB(result);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x19, cpu -> {
			int value = cpu.getC();
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.setC(result);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x1A, cpu -> {
			int value = cpu.getD();
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.setD(result);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x1B, cpu -> {
			int value = cpu.getE();
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.setE(result);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x1C, cpu -> {
			int value = cpu.getH();
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.setH(result);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x1D, cpu -> {
			int value = cpu.getL();
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.setL(result);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x1E, cpu -> {

			int value = cpu.getMmu().readByte(cpu.getHL()) & 0xFF; 
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.getMmu().writeByte(cpu.getHL(), result & 0xFF);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 16;
		});
		
		instructionsCB.put((byte) 0x1F, cpu -> {
			int value = cpu.getA();
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.setA(result);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x20, cpu -> {
			int topbit = (cpu.getB() & 0x80)>>7;
			int value = (cpu.getB()<<1) & 0xFF;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x21, cpu -> {
			int topbit = (cpu.getC() & 0x80)>>7;
			int value = (cpu.getC()<<1) & 0xFF;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x22, cpu -> {
			int topbit = (cpu.getD() & 0x80)>>7;
			int value = (cpu.getD()<<1) & 0xFF;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setD(value);
		 return 8;
		});
		
		instructionsCB.put((byte) 0x23, cpu -> {
			int topbit = (cpu.getE() & 0x80)>>7;
			int value = (cpu.getE()<<1) & 0xFF;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x24, cpu -> {
			int topbit = (cpu.getH() & 0x80)>>7;
			int value = (cpu.getH()<<1) & 0xFF;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setH(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x25, cpu -> {
			int topbit = (cpu.getL() & 0x80)>>7;
			int value = (cpu.getL()<<1) & 0xFF;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setL(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x26, cpu -> {

			int original = cpu.getMmu().readByte(cpu.getHL());
			int result = (original << 1) & 0xFF;
			int topbit = (original & 0x80) >> 7;

			cpu.updateZeroFlag(result == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit == 1);

			cpu.getMmu().writeByte(cpu.getHL(), result);
			return 16;
		});
		
		instructionsCB.put((byte) 0x27, cpu -> {
			int topbit = (cpu.getA() & 0x80)>>7;
			int value = (cpu.getA()<<1) & 0xFF;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x28, cpu -> {
			int original = cpu.getB()	
			;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x29, cpu -> {
			int original = cpu.getC()	
			;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x2A, cpu -> {
			int original = cpu.getD()	
			;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setD(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x2B, cpu -> {
			int original = cpu.getE()	
			;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x2C, cpu -> {
			int original = cpu.getH()	
			;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setH(value);
			return 8; 
		});
		
		instructionsCB.put((byte)0x2D, cpu -> {
			int original = cpu.getL()	
			;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setL(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x2E, cpu -> {
			int original = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte)0x2F, cpu -> {
			int original = cpu.getA()	
			;
			int bottombit = original & 0x01;
			int topbit = original & 0x80;
			int value = (original>>1) | topbit;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x30, cpu -> {
			int original = cpu.getB();
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x31, cpu -> {
			int original = cpu.getC();
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x32, cpu -> {
			int original = cpu.getD();
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.setD(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x33, cpu -> {
			int original = cpu.getE();
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x34, cpu -> {
			int original = cpu.getH();
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.setH(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x35, cpu -> {
			int original = cpu.getL();
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.setL(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x36, cpu -> {
			int original = cpu.getMmu().readByte(cpu.getHL());
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte)0x37, cpu -> {
			int original = cpu.getA() & 0xFF;
			int top4 = (original&0xF0)>>4;
			int bottom4 = (original&0x0F)<<4;
			int value = top4 | bottom4;

			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateCarryFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateSubstractFlag(false);
			
			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x38, cpu -> {
			int original = cpu.getB() & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x39, cpu -> {
			int original = cpu.getC() & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x3A, cpu -> {
			int original = cpu.getD() & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setD(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x3B, cpu -> {
			int original = cpu.getE() & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x3C, cpu -> {
			int original = cpu.getH() & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setH(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x3D, cpu -> {
			int original = cpu.getL() & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setL(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x3E, cpu -> {
			int original = cpu.getMmu().readByte(cpu.getHL()) & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte)0x3F, cpu -> {
			int original = cpu.getA() & 0xFF;
			int bottombit = original&0x01;
			int value = (original>>1);
			
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte)0x40, cpu -> {
			BITgenericOperation(cpu, 0, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x41, cpu -> {
			BITgenericOperation(cpu, 0, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x42, cpu -> {
			BITgenericOperation(cpu, 0, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x43, cpu -> {
			BITgenericOperation(cpu, 0, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x44, cpu -> {
			BITgenericOperation(cpu, 0, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x45, cpu -> {
			BITgenericOperation(cpu, 0, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x46, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 0, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x47, cpu -> {
			BITgenericOperation(cpu, 0, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x48, cpu -> {
			BITgenericOperation(cpu, 1, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x49, cpu -> {
			BITgenericOperation(cpu, 1, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x4A, cpu -> {
			BITgenericOperation(cpu, 1, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x4B, cpu -> {
			BITgenericOperation(cpu, 1, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x4C, cpu -> {
			BITgenericOperation(cpu, 1, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x4D, cpu -> {
			BITgenericOperation(cpu, 1, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x4E, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 1, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x4F, cpu -> {
			BITgenericOperation(cpu, 1, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x50, cpu -> {
			BITgenericOperation(cpu, 2, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x51, cpu -> {
			BITgenericOperation(cpu, 2, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x52, cpu -> {
			BITgenericOperation(cpu, 2, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x53, cpu -> {
			BITgenericOperation(cpu, 2, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x54, cpu -> {
			BITgenericOperation(cpu, 2, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x55, cpu -> {
			BITgenericOperation(cpu, 2, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x56, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 2, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x57, cpu -> {
			BITgenericOperation(cpu, 2, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x58, cpu -> {
			BITgenericOperation(cpu, 3, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x59, cpu -> {
			BITgenericOperation(cpu, 3, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x5A, cpu -> {
			BITgenericOperation(cpu, 3, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x5B, cpu -> {
			BITgenericOperation(cpu, 3, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x5C, cpu -> {
			BITgenericOperation(cpu, 3, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x5D, cpu -> {
			BITgenericOperation(cpu, 3, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x5E, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 3, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x5F, cpu -> {
			BITgenericOperation(cpu, 3, cpu.getA());
		 return 8;
		});
		
		instructionsCB.put((byte)0x60, cpu -> {
			BITgenericOperation(cpu, 4, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x61, cpu -> {
			BITgenericOperation(cpu, 4, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x62, cpu -> {
			BITgenericOperation(cpu, 4, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x63, cpu -> {
			BITgenericOperation(cpu, 4, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x64, cpu -> {
			BITgenericOperation(cpu, 4, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x65, cpu -> {
			BITgenericOperation(cpu, 4, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x66, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 4, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x67, cpu -> {
			BITgenericOperation(cpu, 4, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x68, cpu -> {
			BITgenericOperation(cpu, 5, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x69, cpu -> {
			BITgenericOperation(cpu, 5, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x6A, cpu -> {
			BITgenericOperation(cpu, 5, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x6B, cpu -> {
			BITgenericOperation(cpu, 5, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x6C, cpu -> {
			BITgenericOperation(cpu, 5, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x6D, cpu -> {
			BITgenericOperation(cpu, 5, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x6E, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 5, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x6F, cpu -> {
			BITgenericOperation(cpu, 5, cpu.getA());
			return 8;
		});

		instructionsCB.put((byte)0x70, cpu -> {
			BITgenericOperation(cpu, 6, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x71, cpu -> {
			BITgenericOperation(cpu, 6, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x72, cpu -> {
			BITgenericOperation(cpu, 6, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x73, cpu -> {
			BITgenericOperation(cpu, 6, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x74, cpu -> {
			BITgenericOperation(cpu, 6, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x75, cpu -> {
			BITgenericOperation(cpu, 6, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x76, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 6, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x77, cpu -> {
			BITgenericOperation(cpu, 6, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x78, cpu -> {
			BITgenericOperation(cpu, 7, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x79, cpu -> {
			BITgenericOperation(cpu, 7, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x7A, cpu -> {
			BITgenericOperation(cpu, 7, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x7B, cpu -> {
			BITgenericOperation(cpu, 7, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x7C, cpu -> {
			BITgenericOperation(cpu, 7, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x7D, cpu -> {
			BITgenericOperation(cpu, 7, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x7E, cpu -> {
			cpu.stepHardware(4);
			BITgenericOperation(cpu, 7, cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x7F, cpu -> {
			BITgenericOperation(cpu, 7, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x80, cpu -> {
			cpu.setB(RESgenericOperation(0,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x81, cpu -> {
			cpu.setC(RESgenericOperation(0,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x82, cpu -> {
			cpu.setD(RESgenericOperation(0,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x83, cpu -> {
			cpu.setE(RESgenericOperation(0,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x84, cpu -> {
			cpu.setH(RESgenericOperation(0,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x85, cpu -> {
			cpu.setL(RESgenericOperation(0,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x86, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(0,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x87, cpu -> {
			cpu.setA(RESgenericOperation(0,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0x88, cpu -> {
			
			cpu.setB(RESgenericOperation(1,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x89, cpu -> {
			cpu.setC(RESgenericOperation(1,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8A, cpu -> {
			cpu.setD(RESgenericOperation(1,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8B, cpu -> {
			cpu.setE(RESgenericOperation(1,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8C, cpu -> {
			cpu.setH(RESgenericOperation(1,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8D, cpu -> {
			cpu.setL(RESgenericOperation(1,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8E, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(1,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x8F, cpu -> {
			cpu.setA(RESgenericOperation(1,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0x90, cpu -> {
			cpu.setB(RESgenericOperation(2,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x91, cpu -> {
			cpu.setC(RESgenericOperation(2,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x92, cpu -> {
			cpu.setD(RESgenericOperation(2,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x93, cpu -> {
			cpu.setE(RESgenericOperation(2,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x94, cpu -> {
			cpu.setH(RESgenericOperation(2,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x95, cpu -> {
			cpu.setL(RESgenericOperation(2,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x96, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(2,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x97, cpu -> {
			cpu.setA(RESgenericOperation(2,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0x98, cpu -> {
			cpu.setB(RESgenericOperation(3,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x99, cpu -> {
			cpu.setC(RESgenericOperation(3,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9A, cpu -> {
			cpu.setD(RESgenericOperation(3,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9B, cpu -> {
			cpu.setE(RESgenericOperation(3,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9C, cpu -> {
			cpu.setH(RESgenericOperation(3,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9D, cpu -> {
			cpu.setL(RESgenericOperation(3,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9E, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(3,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x9F, cpu -> {
			cpu.setA(RESgenericOperation(3,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA0, cpu -> {
			cpu.setB(RESgenericOperation(4,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA1, cpu -> {
			cpu.setC(RESgenericOperation(4,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA2, cpu -> {
			cpu.setD(RESgenericOperation(4,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA3, cpu -> {
			cpu.setE(RESgenericOperation(4,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA4, cpu -> {
			cpu.setH(RESgenericOperation(4,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA5, cpu -> {
			cpu.setL(RESgenericOperation(4,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA6, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(4,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xA7, cpu -> {
			cpu.setA(RESgenericOperation(4,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA8, cpu -> {
			cpu.setB(RESgenericOperation(5,cpu.getB()));
		
			return 8;
		});
		
		instructionsCB.put((byte)0xA9, cpu -> {
			cpu.setC(RESgenericOperation(5,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAA, cpu -> {
			cpu.setD(RESgenericOperation(5,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAB, cpu -> {
			cpu.setE(RESgenericOperation(5,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAC, cpu -> {
			cpu.setH(RESgenericOperation(5,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAD, cpu -> {
			cpu.setL(RESgenericOperation(5,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAE, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(5,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xAF, cpu -> {
			cpu.setA(RESgenericOperation(5,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB0, cpu -> {
			cpu.setB(RESgenericOperation(6,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB1, cpu -> {
			cpu.setC(RESgenericOperation(6,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB2, cpu -> {
			cpu.setD(RESgenericOperation(6,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB3, cpu -> {
			cpu.setE(RESgenericOperation(6,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB4, cpu -> {
			cpu.setH(RESgenericOperation(6,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB5, cpu -> {
			cpu.setL(RESgenericOperation(6,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB6, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(6,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xB7, cpu -> {
			cpu.setA(RESgenericOperation(6,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB8, cpu -> {
			cpu.setB(RESgenericOperation(7,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB9, cpu -> {
			cpu.setC(RESgenericOperation(7,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBA, cpu -> {
			cpu.setD(RESgenericOperation(7,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBB, cpu -> {
			cpu.setE(RESgenericOperation(7,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBC, cpu -> {
			cpu.setH(RESgenericOperation(7,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBD, cpu -> {
			cpu.setL(RESgenericOperation(7,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBE, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(7,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xBF, cpu -> {
			cpu.setA(RESgenericOperation(7,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC0, cpu -> {
			cpu.setB(SETgenericOperation(0,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC1, cpu -> {
			cpu.setC(SETgenericOperation(0,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC2, cpu -> {
			cpu.setD(SETgenericOperation(0,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC3, cpu -> {
			cpu.setE(SETgenericOperation(0,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC4, cpu -> {
			cpu.setH(SETgenericOperation(0,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC5, cpu -> {
			cpu.setL(SETgenericOperation(0,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC6, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(0,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xC7, cpu -> {
			cpu.setA(SETgenericOperation(0,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC8, cpu -> {
			
			cpu.setB(SETgenericOperation(1,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC9, cpu -> {
			cpu.setC(SETgenericOperation(1,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCA, cpu -> {
			cpu.setD(SETgenericOperation(1,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCB, cpu -> {
			cpu.setE(SETgenericOperation(1,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCC, cpu -> {
			cpu.setH(SETgenericOperation(1,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCD, cpu -> {
			cpu.setL(SETgenericOperation(1,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCE, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(1,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xCF, cpu -> {
			cpu.setA(SETgenericOperation(1,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD0, cpu -> {
			cpu.setB(SETgenericOperation(2,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD1, cpu -> {
			cpu.setC(SETgenericOperation(2,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD2, cpu -> {
			cpu.setD(SETgenericOperation(2,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD3, cpu -> {
			cpu.setE(SETgenericOperation(2,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD4, cpu -> {
			cpu.setH(SETgenericOperation(2,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD5, cpu -> {
			cpu.setL(SETgenericOperation(2,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD6, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(2,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xD7, cpu -> {
			cpu.setA(SETgenericOperation(2,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD8, cpu -> {
			cpu.setB(SETgenericOperation(3,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD9, cpu -> {
			cpu.setC(SETgenericOperation(3,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDA, cpu -> {
			cpu.setD(SETgenericOperation(3,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDB, cpu -> {
			cpu.setE(SETgenericOperation(3,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDC, cpu -> {
			cpu.setH(SETgenericOperation(3,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDD, cpu -> {
			cpu.setL(SETgenericOperation(3,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDE, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(3,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xDF, cpu -> {
			cpu.setA(SETgenericOperation(3,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE0, cpu -> {
			cpu.setB(SETgenericOperation(4,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE1, cpu -> {
			cpu.setC(SETgenericOperation(4,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE2, cpu -> {
			cpu.setD(SETgenericOperation(4,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE3, cpu -> {
			cpu.setE(SETgenericOperation(4,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE4, cpu -> {
			cpu.setH(SETgenericOperation(4,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE5, cpu -> {
			cpu.setL(SETgenericOperation(4,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE6, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(4,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xE7, cpu -> {
			cpu.setA(SETgenericOperation(4,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE8, cpu -> {
			cpu.setB(SETgenericOperation(5,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE9, cpu -> {
			cpu.setC(SETgenericOperation(5,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEA, cpu -> {
			cpu.setD(SETgenericOperation(5,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEB, cpu -> {
			cpu.setE(SETgenericOperation(5,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEC, cpu -> {
			cpu.setH(SETgenericOperation(5,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xED, cpu -> {
			cpu.setL(SETgenericOperation(5,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEE, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(5,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xEF, cpu -> {
			cpu.setA(SETgenericOperation(5,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF0, cpu -> {
			cpu.setB(SETgenericOperation(6,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF1, cpu -> {
			cpu.setC(SETgenericOperation(6,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF2, cpu -> {
			cpu.setD(SETgenericOperation(6,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF3, cpu -> {
			cpu.setE(SETgenericOperation(6,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF4, cpu -> {
			cpu.setH(SETgenericOperation(6,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF5, cpu -> {
			cpu.setL(SETgenericOperation(6,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF6, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(6,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xF7, cpu -> {
			cpu.setA(SETgenericOperation(6,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF8, cpu -> {
			cpu.setB(SETgenericOperation(7,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF9, cpu -> {
			cpu.setC(SETgenericOperation(7,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFA, cpu -> {
			cpu.setD(SETgenericOperation(7,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFB, cpu -> {
			cpu.setE(SETgenericOperation(7,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFC, cpu -> {
			cpu.setH(SETgenericOperation(7,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFD, cpu -> {
			cpu.setL(SETgenericOperation(7,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFE, cpu -> {
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(7,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xFF, cpu -> {
			cpu.setA(SETgenericOperation(7,cpu.getA()));
			return 8;
		});
		
	}
	
	
	public Instruction get(byte opCode) {
		return this.instructions.get(opCode);
	}
	
	private void BITgenericOperation(Cpu cpu, int bit, int reg_value) {
		int value = (reg_value >> bit) & 0x01;
		cpu.updateZeroFlag((value & 0xFF) == 0);
		cpu.updateSubstractFlag(false);
		cpu.updateHalfCarryFlag(true);
	}
	
	private int RESgenericOperation(int bit, int reg_value) {
		int value = reg_value & ~(1<<bit);
		return value & 0xFF;
	}
	
	private int SETgenericOperation(int bit, int reg_value) {
		int value = reg_value | (1<<bit);
		return value & 0xFF;
	}
	
}
