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
			//nop
			return 4;
		}); 
		
		//Instrucciones ignoradas (depuracion)

		instructions.put((byte)0xDD, cpu -> {
			//nop
			return 4;
		});  

		instructions.put((byte)0xFD, cpu -> {
			//nop
			return 4;
		});

		
		instructions.put((byte)0x01, cpu -> {
			//LD BC,d16
			cpu.setBC(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x02, cpu -> {
			//LD (BC), A
			cpu.getMmu().writeByte(cpu.getBC(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0x03, cpu -> {
			//INC BC
			cpu.setBC(cpu.getBC()+1);
			return 8;
		});
		
		instructions.put((byte)0x04, cpu ->{
			//INC B
			int reg = cpu.getB();
			int value = (reg + 1) & 0xFF;
			cpu.setB(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x05, cpu -> {
			//DEC B
			int value = (cpu.getB() - 1) & 0xFF;
			cpu.setB(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x06, cpu -> {
			//LD B, d8
			cpu.setB(cpu.fetchByte() & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0x07, cpu -> {
			//RLCA
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
			//LD (a16), SP
			int addr = cpu.fetchWord();
			int value = cpu.getSp() & 0xFFFF; 
			Mmu mmu = cpu.getMmu();
			mmu.writeWord(addr&0xFFFF, value & 0xFFFF);
			return 20;
		});
		
		instructions.put((byte)0x09, cpu -> {
			//ADD HL, BC
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
			//LD A, (BC)
			cpu.setA(cpu.getMmu().readByte(cpu.getBC()));
			return 8;
		});
		
		instructions.put((byte)0x0B, cpu -> {
			//DEC BC
			cpu.setBC(cpu.getBC()-1);
			return 8;
		});
		
		
		instructions.put((byte)0x0C, cpu ->{
			//INC C
			int reg = cpu.getC();
			int value = (reg + 1) & 0xFF;
			cpu.setC(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x0D, cpu -> {
			//DEC C
			int value = (cpu.getC() - 1) & 0xFF;
			cpu.setC(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		instructions.put((byte)0x0E, cpu -> {
			//LD C, d8
			cpu.setC(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x0F, cpu -> {
			//RRC A
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
			//STOP 0
			cpu.setStop(true);
			return 4;
		});
		
		instructions.put((byte)0x11, cpu -> {
			//LD DE,d16
			cpu.setDE(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x12, cpu -> {
			//LD (DE), A
			cpu.getMmu().writeByte(cpu.getDE(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0x13, cpu -> {
			//INC DE
			cpu.setDE(cpu.getDE()+1);
			return 8;
		});
		
		
		instructions.put((byte)0x14, cpu ->{
			//INC D
			int reg = cpu.getD();
			int value = (reg + 1) & 0xFF;
			cpu.setD(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x15, cpu -> {
			//DEC D
			int value = (cpu.getD() - 1) & 0xFF;
			cpu.setD(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x16, cpu -> {
			//LD D, d8
			cpu.setD(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x17, cpu -> {
			//RLA
    		int a = cpu.getA();
    		int carry = cpu.isCarryFlag() ? 1 : 0;
    		int bit7 = (a >> 7) & 1;
    
    		int result = ((a << 1) | carry) & 0xFF;
    		cpu.setA(result);

    		cpu.updateZeroFlag(false); // Siempre 0
   			cpu.updateSubstractFlag(false);
    		cpu.updateHalfCarryFlag(false);
    		cpu.updateCarryFlag(bit7 == 1);

    		return 4;
		});
		
		instructions.put((byte)0x18, cpu -> {
			//JR r8
			int despl = (byte)cpu.fetchByte();
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x19, cpu -> {
			//ADD HL, DE
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
			//LD A, (DE)
			cpu.setA(cpu.getMmu().readByte(cpu.getDE()));
			return 8;
		});
		
		instructions.put((byte)0x1B, cpu -> {
			//DEC DE
			cpu.setDE(cpu.getDE()-1);
			return 8;
		});
		
		
		instructions.put((byte)0x1C, cpu ->{
			//INC E
			int reg = cpu.getE();
			int value = (reg + 1) & 0xFF;
			cpu.setE(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x1D, cpu -> {
			//DEC E
			int value = (cpu.getE() - 1) & 0xFF;
			cpu.setE(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x1E, cpu -> {
			//LD E, d8
			cpu.setE(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x1F, cpu -> {
			//RRA
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
			//JR NZ,r8
			int despl = (byte)cpu.fetchByte(); //importante hace el casting a byte para que sea con símbolo!!!
			if(cpu.isZeroFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x21, cpu -> {
			//LD HL,d16
			cpu.setHL(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x22, cpu -> {
			//LD (HL+),A
			int hl = cpu.getHL();
			cpu.getMmu().writeByte(hl, cpu.getA());
			cpu.setHL((hl+1) & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x23, cpu -> {
			//INC HL
			cpu.setHL(cpu.getHL()+1);
			return 8;
		});
		
		instructions.put((byte)0x24, cpu ->{
			//INC H
			int reg = cpu.getH();
			int value = (reg + 1) & 0xFF;
			cpu.setH(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x25, cpu -> {
			//DEC H
			int value = (cpu.getH() - 1) & 0xFF;
			cpu.setH(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x26, cpu -> {
			//LD H, d8
			cpu.setH(cpu.fetchByte() & 0xFF);
			return 8;
		});

		instructions.put((byte)0x27, cpu -> {
			//DAA
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
			//JR Z,r8
			int despl = (byte)cpu.fetchByte();
			if(!cpu.isZeroFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x29, cpu -> {
			//ADD HL, HL
			int hl = cpu.getHL() & 0xFFFF;
			int result = hl + hl;

			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((hl & 0xFFF) + (hl & 0xFFF)) > 0xFFF);
			cpu.updateCarryFlag(result > 0xFFFF);

			cpu.setHL(result & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x2A, cpu -> {
			//LD A,(HL+)
			int hl = cpu.getHL();
			cpu.setA(cpu.getMmu().readByte(hl));
			cpu.setHL((hl+1) & 0xFFFF);
			return 8;
		});
		
		
		instructions.put((byte)0x2B, cpu -> {
			//DEC HL
			cpu.setHL((cpu.getHL()-1) & 0xFFFF);
			return 8;
		});

		
		
		instructions.put((byte)0x2C, cpu ->{
			//INC L
			int reg = cpu.getL();
			int value = (reg + 1) & 0xFF;
			cpu.setL(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x2D, cpu -> {
			//DEC L
			int value = (cpu.getL() - 1) & 0xFF;
			cpu.setL(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((value & 0x0F)==0x0F);
			return 4;
		});
		
		
		instructions.put((byte)0x2E, cpu -> {
			//LD L, d8
			cpu.setL(cpu.fetchByte() & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0x2F, cpu -> {
			//CPL
			cpu.setA(~cpu.getA());
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag(true);
			return 4;
		});
		
		instructions.put((byte)0x30, cpu -> {
			//JR NC,r8
			int despl = (byte)cpu.fetchByte();
			if(cpu.isCarryFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x31, cpu -> {
			//LD SP,d16
			cpu.setSp(cpu.fetchWord());
			return 12;
		});
		
		instructions.put((byte)0x32, cpu -> {
			//LD (HL-),A
			int hl = cpu.getHL();
			cpu.getMmu().writeByte(hl, cpu.getA());
			cpu.setHL((hl-1) & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x33, cpu -> {
			//INC SP
			cpu.setSp(cpu.getSp()+1);
			return 8;
		});
		
		instructions.put((byte)0x34, cpu -> {
			//INC (HL)
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
			//DEC (HL)
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
			//LD (HL),d8
			cpu.getMmu().writeByte(cpu.getHL(), cpu.fetchByte() & 0xFF);
			return 12;
		});
		
		instructions.put((byte)0x37, cpu -> {
			//SCF
			cpu.updateCarryFlag(true);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			return 4;
		});
		
		instructions.put((byte)0x38, cpu -> {
			//JR C,r8
			int despl = (byte)cpu.fetchByte();
			if(!cpu.isCarryFlag()) return 8;
			cpu.setPc(cpu.getPc()+despl);
			return 12;
		});
		
		instructions.put((byte)0x39, cpu -> {
			//ADD HL, SP
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
			//LD A,(HL-)
			int hl = cpu.getHL();
			cpu.setA(cpu.getMmu().readByte(hl));
			cpu.setHL((hl-1 ) & 0xFFFF);
			return 8;
		});
		
		instructions.put((byte)0x3B, cpu -> {
			//DEC SP
			cpu.setSp(cpu.getSp()-1);
			return 8;
		});
		
		instructions.put((byte)0x3C, cpu ->{
			//INC A
			int reg = cpu.getA();
			int value = (reg + 1) & 0xFF;
			cpu.setA(value);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((reg & 0xF) + 1) > 0xF);
			return 4;
		});
		
		instructions.put((byte)0x3D, cpu -> {
			//DEC A
			int reg = cpu.getA();
			int value = (reg - 1) & 0xFF;
			cpu.setA(value);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((reg & 0x0F)==0);
			return 4;
		});
		
		instructions.put((byte)0x3E, cpu -> {
			//LD A, d8
			cpu.setA(cpu.fetchByte() & 0xFF);
			return 8;
		});
		
		instructions.put((byte)0x3F, cpu -> {
			//CCF
			cpu.updateCarryFlag(!cpu.isCarryFlag());
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			return 4;
		});
		
		instructions.put((byte)0x40, cpu -> {
			//LD B, B
			cpu.setB(cpu.getB());
			return 4;
		});

		instructions.put((byte)0x41, cpu -> {
			//LD B, C
			cpu.setB(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x42, cpu -> {
			//LD B, D
			cpu.setB(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x43, cpu -> {
			//LD B, E
			cpu.setB(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x44, cpu -> {
			//LD B, H
			cpu.setB(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x45, cpu -> {
			//LD B, L
			cpu.setB(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x46, cpu -> {
			//LD B, (HL)
			cpu.setB(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x47, cpu -> {
			//LD B,A
			cpu.setB(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x48, cpu -> {
			//LD C,B
			cpu.setC(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x49, cpu -> {
			//LD C,C
			cpu.setC(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x4A, cpu -> {
			//LD C,D
			cpu.setC(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x4B, cpu -> {
			//LD C,E
			cpu.setC(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x4C, cpu -> {
			//LD C,H
			cpu.setC(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x4D, cpu -> {
			//LD C,L
			cpu.setC(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x4E, cpu -> {
			//LD C,(HL)
			cpu.setC(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x4F, cpu -> {
			//LD C,A
			cpu.setC(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x50, cpu -> {
			//LD D,B
			cpu.setD(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x51, cpu -> {
			//LD D,C
			cpu.setD(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x52, cpu -> {
			//LD D,D
			cpu.setD(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x53, cpu -> {
			//LD D,E
			cpu.setD(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x54, cpu -> {
			//LD D,H
			cpu.setD(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x55, cpu -> {
			//LD D,L
			cpu.setD(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x56, cpu -> {
			//LD D,(HL)
			cpu.setD(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x57, cpu -> {
			//LD D,A
			cpu.setD(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x58, cpu -> {
			//LD E,B
			cpu.setE(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x59, cpu -> {
			//LD E,C
			cpu.setE(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x5A, cpu -> {
			//LD E,D
			cpu.setE(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x5B, cpu -> {
			//LD E,E
			cpu.setE(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x5C, cpu -> {
			//LD E,H
			cpu.setE(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x5D, cpu -> {
			//LD E,L
			cpu.setE(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x5E, cpu -> {
			//LD E,(HL)
			cpu.setE(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x5F, cpu -> {
			//LD E,A
			cpu.setE(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x60, cpu -> {
			//LD H,B
			cpu.setH(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x61, cpu -> {
			//LD H,C
			cpu.setH(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x62, cpu -> {
			//LD H,D
			cpu.setH(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x63, cpu -> {
			//LD H,E
			cpu.setH(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x64, cpu -> {
			//LD H,H
			cpu.setH(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x65, cpu -> {
			//LD H,L
			cpu.setH(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x66, cpu -> {
			//LD H,(HL)
			cpu.setH(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x67, cpu -> {
			//LD H,A
			cpu.setH(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x68, cpu -> {
			//LD L,B
			cpu.setL(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x69, cpu -> {
			//LD L,C
			cpu.setL(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x6A, cpu -> {
			//LD L,D
			cpu.setL(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x6B, cpu -> {
			//LD L,E
			cpu.setL(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x6C, cpu -> {
			//LD L,H
			cpu.setL(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x6D, cpu -> {
			//LD L,L
			cpu.setL(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x6E, cpu -> {
			//LD L,(HL)
			cpu.setL(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x6F, cpu -> {
			//LD L,A
			cpu.setL(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x70, cpu -> {
			//LD (HL),B
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getB());
			return 8;
		});
		
		instructions.put((byte)0x71, cpu -> {
			//LD (HL),C
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getC());
			return 8;
		});
		
		instructions.put((byte)0x72, cpu -> {
			//LD (HL),D
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getD());
			return 8;
		});
		
		instructions.put((byte)0x73, cpu -> {
			//LD (HL),E
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getE());
			return 8;
		});
		
		instructions.put((byte)0x74, cpu -> {
			//LD (HL),H
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getH());
			return 8;
		});
		
		instructions.put((byte)0x75, cpu -> {
			//LD (HL),L
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getL());
			return 8;
		});
		
		instructions.put((byte)0x76, cpu ->{
			//HALT
			cpu.setHalted(true);
			return 4;
		});
		
		instructions.put((byte)0x77, cpu -> {
			//LD (HL), A
			cpu.getMmu().writeByte(cpu.getHL(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0x78, cpu -> {
			//LD A, B
			cpu.setA(cpu.getB());
			return 4;
		});
		
		instructions.put((byte)0x79, cpu -> {
			//LD A, C
			cpu.setA(cpu.getC());
			return 4;
		});
		
		instructions.put((byte)0x7A, cpu -> {
			//LD A, D
			cpu.setA(cpu.getD());
			return 4;
		});
		
		instructions.put((byte)0x7B, cpu -> {
			//LD A, E
			cpu.setA(cpu.getE());
			return 4;
		});
		
		instructions.put((byte)0x7C, cpu -> {
			//LD A, H
			cpu.setA(cpu.getH());
			return 4;
		});
		
		instructions.put((byte)0x7D, cpu -> {
			//LD A, L
			cpu.setA(cpu.getL());
			return 4;
		});
		
		instructions.put((byte)0x7E, cpu -> {
			//LD A, (HL)
			cpu.setA(cpu.getMmu().readByte(cpu.getHL()));
			return 8;
		});
		
		instructions.put((byte)0x7F, cpu -> {
			//LD A, A
			cpu.setA(cpu.getA());
			return 4;
		});
		
		instructions.put((byte)0x80, cpu -> {
			//ADD A, B
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
			//ADD A, C
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
			//ADD A, D
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
			//ADD A, E
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
			//ADD A, H
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
			//ADD A, L
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
			//ADD A, (HL)
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
			//ADD A, A
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
			//ADC A, B
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
			//ADC A, C
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
			//ADC A, D
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
			//ADC A, E
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
			//ADC A, H
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
			//ADC A, L
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
			//ADC A, (HL)
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
			//ADC A, A
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
			//SUB B
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
			//SUB C
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
			//SUB D
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
			//SUB E
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
			//SUB H
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
			//SUB L
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
			//SUB (HL)
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
			//SUB A
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
			//SBC B
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
			//SBC C
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
			//SBC D
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
			//SBC E
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
			//SBC H
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
			//SBC L
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
			//SBC (HL)
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
			//SBC A
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
			//AND B
			int value = cpu.getA()&cpu.getB();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA1, cpu -> {
			//AND C
			int value = cpu.getA()&cpu.getC();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA2, cpu -> {
			//AND D
			int value = cpu.getA()&cpu.getD();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA3, cpu -> {
			//AND E
			int value = cpu.getA()&cpu.getE();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA4, cpu -> {
			//AND H
			int value = cpu.getA()&cpu.getH();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA5, cpu -> {
			//AND L
			int value = cpu.getA()&cpu.getL();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA6, cpu -> {
			//AND (HL)
			int value = cpu.getA()&(cpu.getMmu().readByte(cpu.getHL()));
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xA7, cpu -> {
			//AND A
			int value = cpu.getA()&cpu.getA();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA8, cpu -> {
			//XOR B
			int value = (cpu.getA()^cpu.getB()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xA9, cpu -> {
			//XOR C
			int value = (cpu.getA()^cpu.getC()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAA, cpu -> {
			//XOR D
			int value = (cpu.getA()^cpu.getD()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAB, cpu -> {
			//XOR E
			int value = (cpu.getA()^cpu.getE()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAC, cpu -> {
			//XOR H
			int value = (cpu.getA()^cpu.getH()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAD, cpu -> {
			//XOR L
			int value = (cpu.getA()^cpu.getL()) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xAE, cpu -> {
			//XOR (HL)
			int value = (cpu.getA()^cpu.getMmu().readByte(cpu.getHL())) & 0xFF;
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xAF, cpu ->{
			//XOR A (limpia el registro)
			cpu.setA(0);
			cpu.updateZeroFlag(true);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB0, cpu -> {
			//OR B
			int value = cpu.getA()|cpu.getB();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB1, cpu -> {
			//OR C
			int value = cpu.getA()|cpu.getC();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB2, cpu -> {
			//OR D
			int value = cpu.getA()|cpu.getD();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB3, cpu -> {
			//OR E
			int value = cpu.getA()|cpu.getE();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB4, cpu -> {
			//OR H
			int value = cpu.getA()|cpu.getH();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB5, cpu -> {
			//OR L
			int value = cpu.getA()|cpu.getL();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB6, cpu -> {
			//OR (HL)
			int value = cpu.getA()|cpu.getMmu().readByte(cpu.getHL());
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xB7, cpu -> {
			//OR A
			int value = cpu.getA()|cpu.getA();
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 4;
		});
		
		instructions.put((byte)0xB8, cpu -> {
			//CP B
			int original = cpu.getA();
			int value = ((original-cpu.getB()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getB()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getB()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xB9, cpu -> {
			//CP C
			int original = cpu.getA();
			int value = ((original-cpu.getC()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getC()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getC()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBA, cpu -> {
			//CP D
			int original = cpu.getA();
			int value = ((original-cpu.getD()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getD()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getD()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBB, cpu -> {
			//CP E
			int original = cpu.getA();
			int value = ((original-cpu.getE()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getE()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getE()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBC, cpu -> {
			//CP H
			int original = cpu.getA();
			int value = ((original-cpu.getH()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getH()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getH()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBD, cpu -> {
			//CP L
			int original = cpu.getA();
			int value = ((original-cpu.getL()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getL()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getL()&0xFF));
		 return 4;
		});
		
		instructions.put((byte)0xBE, cpu -> {
			//CP (HL)
			int original = cpu.getA();
			int value = ((original-cpu.getMmu().readByte(cpu.getHL())));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getMmu().readByte(cpu.getHL())&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getMmu().readByte(cpu.getHL())&0xFF));
		 return 8;
		});
		
		instructions.put((byte)0xBF, cpu -> {
			//CP A
			int original = cpu.getA();
			int value = ((original-cpu.getA()));
	
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(true);
			cpu.updateHalfCarryFlag((original&0x0F) < (cpu.getA()&0x0F));
			cpu.updateCarryFlag((original&0xFF) < (cpu.getA()&0xFF));
		 return 4;
		});
		
		
		
		instructions.put((byte)0xC0, cpu -> {
			//RET NZ
			if(!cpu.isZeroFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});

		instructions.put((byte)0xC1, cpu -> {
			//POP BC
			cpu.setBC(cpu.popWord());
			return 12;
		});

		instructions.put((byte)0xC2, cpu -> {
			//JP NZ, a16
			int addr = cpu.fetchWord();
			if(!cpu.isZeroFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return 12;
		});
		
		instructions.put((byte)0xC3, cpu -> {
			//JP a16
			cpu.setPc(cpu.fetchWord());
			return 16;
		});
		
		instructions.put((byte)0xC4, cpu -> {
			//CALL NZ, a16
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
			//PUSH BC
			cpu.pushWord(cpu.getBC());
			return 16;
		});
		
		instructions.put((byte)0xC6, cpu -> {
			//ADD A, d8
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
			//RST 00H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0000);
			return 16;
		});
		
		instructions.put((byte)0xC8, cpu -> {
			//RET Z
			if(cpu.isZeroFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});
		
		instructions.put((byte)0XC9, cpu -> { 
			//RET
			cpu.setPc(cpu.popWord());
			return 16;
		});
		
		instructions.put((byte)0xCA, cpu -> {
			//JP Z, a16
			int addr = cpu.fetchWord();
			if(cpu.isZeroFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return 12;
		});
		
		instructions.put((byte)0xCB, cpu -> {
			//Prefijo CB, se ejecutará la instrucción correspondiente.
			Instruction inst = instructionsCB.get(cpu.fetchByte());
			return 4 + inst.execute(cpu);
		});

		instructions.put((byte)0xCC, cpu -> {
			//CALL Z, a16
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
			//CALL a16
			int addr = cpu.fetchWord();
			cpu.pushWord(cpu.getPc());
			cpu.setPc(addr);
			return 24;
		});
		
		instructions.put((byte)0xCE, cpu -> {
			//ADC A, d8
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
			//RST 08H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0008);
			return 16;
		});
		
		instructions.put((byte)0xD0, cpu -> {
			//RET NC
			if(!cpu.isCarryFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});

		instructions.put((byte)0xD1, cpu -> {
			//POP DE
			cpu.setDE(cpu.popWord());
			return 12;
		});
		
		instructions.put((byte)0xD2, cpu -> {
			//JP NC, a16
			int addr = cpu.fetchWord();
			if(!cpu.isCarryFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return 12;
		});
		
		instructions.put((byte)0xD4, cpu -> {
			//CALL NC, a16
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
			//PUSH DE
			cpu.pushWord(cpu.getDE()); 
			return 16;
		});
		
		instructions.put((byte)0xD6, cpu -> {
			//SUB d8
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
			//RST 10H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0010);
			return 16;
		});
		
		instructions.put((byte)0xD8, cpu -> {
			//RET C
			if(cpu.isCarryFlag()) {
				cpu.setPc(cpu.popWord());
				return 20;
			}
			return 8;
		});

		instructions.put((byte)0xD9, cpu -> {
			//RETI
			cpu.setPc(cpu.popWord());
			cpu.getInterruptionManager().setIME(true);
			return 16;
		});


		
		instructions.put((byte)0xDA, cpu -> {
			//JP C, a16
			int addr = cpu.fetchWord();
			if(cpu.isCarryFlag()) {
				cpu.setPc(addr);
				return 16;
			}
			return  12;
		});
		
		instructions.put((byte)0xDC, cpu -> {
			//CALL C, a16
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
			//SBC d8
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
			//RST 18H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0018);
			return 16;
		});

		instructions.put((byte)0xE0, cpu -> {
			//LDH (a8), A
			int inm = cpu.fetchByte() & 0xFF;
			cpu.getMmu().writeByte(0xFF00 + (inm&0xFF), cpu.getA());
			return 12;
		});

		instructions.put((byte)0xE1, cpu -> {
			//POP HL
			cpu.setHL(cpu.popWord());
			return 12;
		});

		instructions.put((byte)0xE2, cpu -> {
			//LD (C), A
			cpu.getMmu().writeByte(0xFF00 + cpu.getC(), cpu.getA());
			return 8;
		});
		
		instructions.put((byte)0xE5, cpu -> {
			//PUSH HL
			cpu.pushWord(cpu.getHL());
			return 16;
		});

		instructions.put((byte)0xE6, cpu -> {
			//AND d8
			int value = cpu.getA()&(cpu.fetchByte() & 0xFF);
			
			cpu.setA(value & 0xFF);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(true);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xE7, cpu -> {
			//RST 20H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0020);
			return 16;
		});

		instructions.put((byte)0xE8, cpu -> {
			//ADD SP, r8
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
			//JP HL
			cpu.setPc(cpu.getHL());
			return 4;
		});

		instructions.put((byte)0xEA, cpu -> {
			//LD(a16), A
			int addr = cpu.fetchWord();
			cpu.getMmu().writeByte(addr&0xFFFF, cpu.getA());
			return 16;
		});
		
		instructions.put((byte)0xEE, cpu -> {
			//XOR d8
			int value = (cpu.getA()^cpu.fetchByte()) & 0xFF;

			cpu.setA(value & 0xFF);

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xEF, cpu -> {
			//RST 28H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0028);
			return 16;
		});

		instructions.put((byte)0xF0, cpu -> {
			//LDH (A), a8
			int inm = cpu.fetchByte() & 0xFF;
			cpu.setA(cpu.getMmu().readByte(0xFF00 + (inm & 0xFF)));
			return 12;
		});

		instructions.put((byte)0xF1, cpu -> {
    		//POP AF
			int value = cpu.popWord();

    		cpu.setA((value >> 8) & 0xFF);
    		cpu.setF(value & 0xF0); // solo los 4 bits superiores son válidos

    		return 12;
		});

		instructions.put((byte)0xF2, cpu -> {
			//LD A, (C)
			cpu.setA(cpu.getMmu().readByte(0xFF00 + cpu.getC()));
			return 8;
		});

		instructions.put((byte)0xF3, cpu -> {
			//DI
			cpu.getInterruptionManager().setIME(false);
			return 4;
		});

		instructions.put((byte)0xF5, cpu -> {
			//PUSH AF 
			cpu.pushWord((cpu.getA() << 8) | (cpu.getF() & 0xF0)); 
			return 16; 
		}); 
		
		instructions.put((byte)0xF6, cpu -> {
			//OR d8
			int value = cpu.getA()|(cpu.fetchByte()& 0xFF);
			
			cpu.setA(value & 0xFF);
			
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(false);
		 return 8;
		});
		
		instructions.put((byte)0xF7, cpu -> {
			//RST 30H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0030);
			return 16;
		});

		instructions.put((byte)0xF8, cpu -> {
			//LD HL, SP+r8
			
			int sp = cpu.getSp();
			byte offsetByte = (byte) cpu.fetchByte(); // signed
			int offset = offsetByte; // signed extension
			int result = sp + offset;

			cpu.updateZeroFlag(false);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(((sp & 0xF) + (offset & 0xF)) > 0xF);
			cpu.updateCarryFlag(((sp & 0xFF) + (offset & 0xFF)) > 0xFF);

			cpu.setHL(result & 0xFFFF);

			return 12;
		});

		instructions.put((byte)0xF9, cpu -> {
			//LD SP, HL
			cpu.setSp(cpu.getHL());
			return 8;
		});

		instructions.put((byte)0xFA, cpu -> {
			//LD A, (a16)
			int addr = cpu.fetchWord();
			cpu.setA(cpu.getMmu().readByte(addr));
			return 16;
		});

		instructions.put((byte)0xFB, cpu -> {
			//EI
			cpu.setPendingIME(true);
			System.out.println("Pending IME set to true");
			return 4;
		});
		
		
		instructions.put((byte)0xFE, cpu -> {
			//CP d8
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
			//RST 38H
			cpu.pushWord(cpu.getPc());
			cpu.setPc(0x0038);
			return 16;
		});
		
		//Comienza la parte de las instrucciones compuestas
		//Estas instrucciones siempre comienzan por el prefijo CB
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		//-------------------------------------------------
		
		instructionsCB.put((byte) 0x00, cpu -> {
			//RLC B
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
			//RLC C
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
			//RLC D
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
			//RLC E
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
			//RLC H
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
			//RLC L
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
			//RLC (HL)
			int topbit = (cpu.getMmu().readByte(cpu.getHL()) & 0x80)>>7;
			int value = (cpu.getMmu().readByte(cpu.getHL())<<1) & 0xFF;
			value |= topbit;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte) 0x07, cpu -> {
			//RLC A
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
			//RRC B
			int bottombit = cpu.getB() & 0x01;
			int value = (cpu.getB()>>1) & 0xFF;
			value |= bottombit<<7;
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setB(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x09, cpu -> {
			//RRC C
			int bottombit = cpu.getC() & 0x01;
			int value = (cpu.getC()>>1) & 0xFF;
			value |= bottombit<<7;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setC(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0A, cpu -> {
			//RRC D
			int bottombit = cpu.getD() & 0x01;
			int value = (cpu.getD()>>1) & 0xFF;
			value |= bottombit<<7;
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setD(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0B, cpu -> {
			//RRC E
			int bottombit = cpu.getE() & 0x01;
			int value = (cpu.getE()>>1) & 0xFF;
			value |= bottombit<<7;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setE(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0C, cpu -> {
			//RRC H
			int bottombit = cpu.getH() & 0x01;
			int value = (cpu.getH()>>1) & 0xFF;
			value |= bottombit<<7;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setH(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0D, cpu -> {
			//RRC L
			int bottombit = cpu.getL() & 0x01;
			int value = (cpu.getL()>>1) & 0xFF;
			value |= bottombit<<7;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setL(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x0E, cpu -> {
			//RRC (HL)
			int bottombit = cpu.getMmu().readByte(cpu.getHL()) & 0x01;
			int value = (cpu.getMmu().readByte(cpu.getHL())>>1) & 0xFF;
			value |= bottombit<<7;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte) 0x0F, cpu -> {
			//RRC A
			int bottombit = cpu.getA() & 0x01;
			int value = (cpu.getA()>>1) & 0xFF;
			value |= bottombit<<7;

			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(bottombit==1);
			
			cpu.setA(value);
			return 8;
		});
		
		instructionsCB.put((byte) 0x10, cpu -> {
			//RL B
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
			//RL C
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
			//RL D
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
			//RL E
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
			//RL H
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
			//RL L
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
			//RL (HL)
			int topbit = (cpu.getMmu().readByte(cpu.getHL()) & 0x80)>>7;
			int value = (cpu.getMmu().readByte(cpu.getHL())<<1) & 0xFF;
			value |= (cpu.isCarryFlag() ? 1 : 0);
						
			cpu.updateZeroFlag((value&0xFF)==0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte) 0x17, cpu -> {
			//RL A
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
			// RR B
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
			//RR C
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
			//RR D
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
			//RR E
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
			//RR H
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
			//RR L
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
			//RR (HL)
			int value = cpu.getMmu().readByte(cpu.getHL());
			boolean oldCarry = cpu.isCarryFlag(); 
			int newCarry = value & 0x01;

			int result = (value >> 1) | (oldCarry ? 0x80 : 0x00);
			cpu.getMmu().writeByte(cpu.getHL(), result & 0xFF);

			cpu.updateZeroFlag((result & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(newCarry == 1);

			return 8;
		});
		
		instructionsCB.put((byte) 0x1F, cpu -> {
			//RR A
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
			//SLA B
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
			//SLA C
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
			//SLA D
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
			//SLA E
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
			//SLA H
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
			//SLA L
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
			//SLA (HL)
			int topbit = (cpu.getMmu().readByte(cpu.getHL()) & 0x80)>>7;
			int value = (cpu.getMmu().readByte(cpu.getHL())<<1) & 0xFF;
						
			cpu.updateZeroFlag((value & 0xFF) == 0);
			cpu.updateSubstractFlag(false);
			cpu.updateHalfCarryFlag(false);
			cpu.updateCarryFlag(topbit==1);
			
			cpu.getMmu().writeByte(cpu.getHL(), value & 0xFF);
			return 16;
		});
		
		instructionsCB.put((byte) 0x27, cpu -> {
			//SLA A
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
			//SRA B
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
			//SRA C
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
			//SRA D
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
			//SRA E
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
			//SRA H
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
			//SRA L
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
			//SRA (HL)
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
			//SRA A
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
			//SWAP B
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
			//SWAP C
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
			//SWAP D
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
			//SWAP E
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
			//SWAP H
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
			//SWAP L
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
			//SWAP (HL)
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
			//SWAP A
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
			//SRL B
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
			//SRL C
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
			//SRL D
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
			//SRL E
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
			//SRL H
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
			//SRL L
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
			//SRL (HL)
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
			//SRL A
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
			//BIT 0, B
			BITgenericOperation(cpu, 0, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x41, cpu -> {
			//BIT 0, C
			BITgenericOperation(cpu, 0, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x42, cpu -> {
			//BIT 0, D
			BITgenericOperation(cpu, 0, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x43, cpu -> {
			//BIT 0, E
			BITgenericOperation(cpu, 0, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x44, cpu -> {
			//BIT 0, H
			BITgenericOperation(cpu, 0, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x45, cpu -> {
			//BIT 0, L
			BITgenericOperation(cpu, 0, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x46, cpu -> {
			//BIT 0, (HL)
			BITgenericOperation(cpu, 0, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x47, cpu -> {
			//BIT 0, A
			BITgenericOperation(cpu, 0, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x48, cpu -> {
			//BIT 1, B
			BITgenericOperation(cpu, 1, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x49, cpu -> {
			//BIT 1, C
			BITgenericOperation(cpu, 1, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x4A, cpu -> {
			//BIT 1, D
			BITgenericOperation(cpu, 1, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x4B, cpu -> {
			//BIT 1, E
			BITgenericOperation(cpu, 1, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x4C, cpu -> {
			//BIT 1, H
			BITgenericOperation(cpu, 1, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x4D, cpu -> {
			//BIT 1, L
			BITgenericOperation(cpu, 1, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x4E, cpu -> {
			//BIT 1, (HL)
			BITgenericOperation(cpu, 1, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x4F, cpu -> {
			//BIT 1, A
			BITgenericOperation(cpu, 1, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x50, cpu -> {
			//BIT 2, B
			BITgenericOperation(cpu, 2, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x51, cpu -> {
			//BIT 2, C
			BITgenericOperation(cpu, 2, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x52, cpu -> {
			//BIT 2, D
			BITgenericOperation(cpu, 2, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x53, cpu -> {
			//BIT 2, E
			BITgenericOperation(cpu, 2, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x54, cpu -> {
			//BIT 2, H
			BITgenericOperation(cpu, 2, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x55, cpu -> {
			//BIT 2, L
			BITgenericOperation(cpu, 2, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x56, cpu -> {
			//BIT 2, (HL)
			BITgenericOperation(cpu, 2, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x57, cpu -> {
			//BIT 2, A
			BITgenericOperation(cpu, 2, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x58, cpu -> {
			//BIT 3, B
			BITgenericOperation(cpu, 3, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x59, cpu -> {
			//BIT 3, C
			BITgenericOperation(cpu, 3, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x5A, cpu -> {
			//BIT 3, D
			BITgenericOperation(cpu, 3, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x5B, cpu -> {
			//BIT 3, E
			BITgenericOperation(cpu, 3, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x5C, cpu -> {
			//BIT 3, H
			BITgenericOperation(cpu, 3, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x5D, cpu -> {
			//BIT 3, L
			BITgenericOperation(cpu, 3, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x5E, cpu -> {
			//BIT 3, (HL)
			BITgenericOperation(cpu, 3, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x5F, cpu -> {
			//BIT 3, A
			BITgenericOperation(cpu, 3, cpu.getA());
		 return 8;
		});
		
		instructionsCB.put((byte)0x60, cpu -> {
			//BIT 4, B
			BITgenericOperation(cpu, 4, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x61, cpu -> {
			//BIT 4, C
			BITgenericOperation(cpu, 4, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x62, cpu -> {
			//BIT 4, D
			BITgenericOperation(cpu, 4, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x63, cpu -> {
			//BIT 4, E
			BITgenericOperation(cpu, 4, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x64, cpu -> {
			//BIT 4 H
			BITgenericOperation(cpu, 4, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x65, cpu -> {
			//BIT 4, L
			BITgenericOperation(cpu, 4, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x66, cpu -> {
			//BIT 4, (HL)
			BITgenericOperation(cpu, 4, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x67, cpu -> {
			//BIT 4, A
			BITgenericOperation(cpu, 4, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x68, cpu -> {
			//BIT 5, B
			BITgenericOperation(cpu, 5, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x69, cpu -> {
			//BIT 5, C
			BITgenericOperation(cpu, 5, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x6A, cpu -> {
			//BIT 5, D
			BITgenericOperation(cpu, 5, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x6B, cpu -> {
			//BIT 5, E
			BITgenericOperation(cpu, 5, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x6C, cpu -> {
			//BIT 5, H
			BITgenericOperation(cpu, 5, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x6D, cpu -> {
			//BIT 5, L
			BITgenericOperation(cpu, 5, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x6E, cpu -> {
			//BIT 5, (HL)
			BITgenericOperation(cpu, 5, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x6F, cpu -> {
			//BIT 5, A
			BITgenericOperation(cpu, 5, cpu.getA());
			return 8;
		});

		instructionsCB.put((byte)0x70, cpu -> {
			//BIT 6, B
			BITgenericOperation(cpu, 6, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x71, cpu -> {
			//BIT 6, C
			BITgenericOperation(cpu, 6, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x72, cpu -> {
			//BIT 6, D
			BITgenericOperation(cpu, 6, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x73, cpu -> {
			//BIT 6, E
			BITgenericOperation(cpu, 6, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x74, cpu -> {
			//BIT 6, H
			BITgenericOperation(cpu, 6, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x75, cpu -> {
			//BIT 6, L
			BITgenericOperation(cpu, 6, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x76, cpu -> {
			//BIT 6, (HL)
			BITgenericOperation(cpu, 6, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x77, cpu -> {
			//BIT 6, A
			BITgenericOperation(cpu, 6, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x78, cpu -> {
			//BIT 7, B
			BITgenericOperation(cpu, 7, cpu.getB());
			return 8;
		});
		
		instructionsCB.put((byte)0x79, cpu -> {
			//BIT 7, C
			BITgenericOperation(cpu, 7, cpu.getC());
			return 8;
		});
		
		instructionsCB.put((byte)0x7A, cpu -> {
			//BIT 7, D
			BITgenericOperation(cpu, 7, cpu.getD());
			return 8;
		});
		
		instructionsCB.put((byte)0x7B, cpu -> {
			//BIT 7, E
			BITgenericOperation(cpu, 7, cpu.getE());
			return 8;
		});
		
		instructionsCB.put((byte)0x7C, cpu -> {
			//BIT 7, H
			BITgenericOperation(cpu, 7, cpu.getH());
			return 8;
		});
		
		instructionsCB.put((byte)0x7D, cpu -> {
			//BIT 7, L
			BITgenericOperation(cpu, 7, cpu.getL());
			return 8;
		});
		
		instructionsCB.put((byte)0x7E, cpu -> {
			//BIT 7, (HL)
			BITgenericOperation(cpu, 7, cpu.getMmu().readByte(cpu.getHL()));
			return 16;
		});
		
		instructionsCB.put((byte)0x7F, cpu -> {
			//BIT 7, A
			BITgenericOperation(cpu, 7, cpu.getA());
			return 8;
		});
		
		instructionsCB.put((byte)0x80, cpu -> {
			//RES 0, B
			cpu.setB(RESgenericOperation(0,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x81, cpu -> {
			//RES 0, C
			cpu.setC(RESgenericOperation(0,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x82, cpu -> {
			//RES 0, D
			cpu.setD(RESgenericOperation(0,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x83, cpu -> {
			//RES 0, E
			cpu.setE(RESgenericOperation(0,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x84, cpu -> {
			//RES 0, H
			cpu.setH(RESgenericOperation(0,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x85, cpu -> {
			//RES 0, L
			cpu.setL(RESgenericOperation(0,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x86, cpu -> {
			//RES 0, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(0,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x87, cpu -> {
			//RES 0, A
			cpu.setA(RESgenericOperation(0,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0x88, cpu -> {
			
			//RES 1, B
			cpu.setB(RESgenericOperation(1,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x89, cpu -> {
			//RES 1, C
			cpu.setC(RESgenericOperation(1,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8A, cpu -> {
			//RES 1, D
			cpu.setD(RESgenericOperation(1,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8B, cpu -> {
			//RES 1, E
			cpu.setE(RESgenericOperation(1,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8C, cpu -> {
			//RES 1, H
			cpu.setH(RESgenericOperation(1,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8D, cpu -> {
			//RES 1, L
			cpu.setL(RESgenericOperation(1,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x8E, cpu -> {
			//RES 1, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(1,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x8F, cpu -> {
			//RES 1, A
			cpu.setA(RESgenericOperation(1,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0x90, cpu -> {
			//RES 2, B
			cpu.setB(RESgenericOperation(2,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x91, cpu -> {
			//RES 2, C
			cpu.setC(RESgenericOperation(2,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x92, cpu -> {
			//RES 2, D
			cpu.setD(RESgenericOperation(2,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x93, cpu -> {
			//RES 2, E
			cpu.setE(RESgenericOperation(2,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x94, cpu -> {
			//RES 2, H
			cpu.setH(RESgenericOperation(2,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x95, cpu -> {
			//RES 2, L
			cpu.setL(RESgenericOperation(2,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x96, cpu -> {
			//RES 2, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(2,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x97, cpu -> {
			//RES 2, A
			cpu.setA(RESgenericOperation(2,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0x98, cpu -> {
			//RES 3, B
			cpu.setB(RESgenericOperation(3,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0x99, cpu -> {
			//RES 3, C
			cpu.setC(RESgenericOperation(3,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9A, cpu -> {
			//RES 3, D
			cpu.setD(RESgenericOperation(3,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9B, cpu -> {
			//RES 3, E
			cpu.setE(RESgenericOperation(3,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9C, cpu -> {
			//RES 3, H
			cpu.setH(RESgenericOperation(3,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9D, cpu -> {
			//RES 3, L
			cpu.setL(RESgenericOperation(3,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0x9E, cpu -> {
			//RES 3, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(3,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0x9F, cpu -> {
			//RES 3, A
			cpu.setA(RESgenericOperation(3,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA0, cpu -> {
			//RES 4, B
			cpu.setB(RESgenericOperation(4,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA1, cpu -> {
			//RES 4, C
			cpu.setC(RESgenericOperation(4,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA2, cpu -> {
			//RES 4, D
			cpu.setD(RESgenericOperation(4,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA3, cpu -> {
			//RES 4, E
			cpu.setE(RESgenericOperation(4,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA4, cpu -> {
			//RES 4, H
			cpu.setH(RESgenericOperation(4,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA5, cpu -> {
			//RES 4, L
			cpu.setL(RESgenericOperation(4,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA6, cpu -> {
			//RES 4, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(4,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xA7, cpu -> {
			//RES 4, A
			cpu.setA(RESgenericOperation(4,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xA8, cpu -> {
			//RES 5, B
			cpu.setB(RESgenericOperation(5,cpu.getB()));
		
			return 8;
		});
		
		instructionsCB.put((byte)0xA9, cpu -> {
			//RES 5, C
			cpu.setC(RESgenericOperation(5,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAA, cpu -> {
			//RES 5, D
			cpu.setD(RESgenericOperation(5,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAB, cpu -> {
			//RES 5, E
			cpu.setE(RESgenericOperation(5,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAC, cpu -> {
			//RES 5, H
			cpu.setH(RESgenericOperation(5,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAD, cpu -> {
			//RES 5, L
			cpu.setL(RESgenericOperation(5,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xAE, cpu -> {
			//RES 5, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(5,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xAF, cpu -> {
			//RES 5, A
			cpu.setA(RESgenericOperation(5,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB0, cpu -> {
			//RES 6, B
			cpu.setB(RESgenericOperation(6,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB1, cpu -> {
			//RES 6, C
			cpu.setC(RESgenericOperation(6,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB2, cpu -> {
			//RES 6, D
			cpu.setD(RESgenericOperation(6,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB3, cpu -> {
			//RES 6, E
			cpu.setE(RESgenericOperation(6,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB4, cpu -> {
			//RES 6, H
			cpu.setH(RESgenericOperation(6,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB5, cpu -> {
			//RES 6, L
			cpu.setL(RESgenericOperation(6,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB6, cpu -> {
			//RES 6, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(6,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xB7, cpu -> {
			//RES 6, A
			cpu.setA(RESgenericOperation(6,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB8, cpu -> {
			//RES 7, B
			cpu.setB(RESgenericOperation(7,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xB9, cpu -> {
			//RES 7, C
			cpu.setC(RESgenericOperation(7,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBA, cpu -> {
			//RES 7, D
			cpu.setD(RESgenericOperation(7,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBB, cpu -> {
			//RES 7, E
			cpu.setE(RESgenericOperation(7,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBC, cpu -> {
			//RES 7, H
			cpu.setH(RESgenericOperation(7,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBD, cpu -> {
			//RES 7, L
			cpu.setL(RESgenericOperation(7,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xBE, cpu -> {
			//RES 7, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), RESgenericOperation(7,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xBF, cpu -> {
			//RES 7, A
			cpu.setA(RESgenericOperation(7,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC0, cpu -> {
			//SET 0, B
			cpu.setB(SETgenericOperation(0,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC1, cpu -> {
			//SET 0, C
			cpu.setC(SETgenericOperation(0,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC2, cpu -> {
			//SET 0, D
			cpu.setD(SETgenericOperation(0,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC3, cpu -> {
			//SET 0, E
			cpu.setE(SETgenericOperation(0,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC4, cpu -> {
			//SET 0, H
			cpu.setH(SETgenericOperation(0,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC5, cpu -> {
			//SET 0, L
			cpu.setL(SETgenericOperation(0,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC6, cpu -> {
			//SET 0, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(0,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xC7, cpu -> {
			//SET 0, A
			cpu.setA(SETgenericOperation(0,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC8, cpu -> {
			
			//SET 1, B
			cpu.setB(SETgenericOperation(1,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xC9, cpu -> {
			//SET 1, C
			cpu.setC(SETgenericOperation(1,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCA, cpu -> {
			//SET 1, D
			cpu.setD(SETgenericOperation(1,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCB, cpu -> {
			//SET 1, E
			cpu.setE(SETgenericOperation(1,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCC, cpu -> {
			//SET 1, H
			cpu.setH(SETgenericOperation(1,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCD, cpu -> {
			//SET 1, L
			cpu.setL(SETgenericOperation(1,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xCE, cpu -> {
			//SET 1, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(1,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xCF, cpu -> {
			//SET 1, A
			cpu.setA(SETgenericOperation(1,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD0, cpu -> {
			//SET 2, B
			cpu.setB(SETgenericOperation(2,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD1, cpu -> {
			//SET 2, C
			cpu.setC(SETgenericOperation(2,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD2, cpu -> {
			//SET 2, D
			cpu.setD(SETgenericOperation(2,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD3, cpu -> {
			//SET 2, E
			cpu.setE(SETgenericOperation(2,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD4, cpu -> {
			//SET 2, H
			cpu.setH(SETgenericOperation(2,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD5, cpu -> {
			//SET 2, L
			cpu.setL(SETgenericOperation(2,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD6, cpu -> {
			//SET 2, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(2,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xD7, cpu -> {
			//SET 2, A
			cpu.setA(SETgenericOperation(2,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD8, cpu -> {
			//SET 3, B
			cpu.setB(SETgenericOperation(3,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xD9, cpu -> {
			//SET 3, C
			cpu.setC(SETgenericOperation(3,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDA, cpu -> {
			//SET 3, D
			cpu.setD(SETgenericOperation(3,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDB, cpu -> {
			//SET 3, E
			cpu.setE(SETgenericOperation(3,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDC, cpu -> {
			//SET 3, H
			cpu.setH(SETgenericOperation(3,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDD, cpu -> {
			//SET 3, L
			cpu.setL(SETgenericOperation(3,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xDE, cpu -> {
			//SET 3, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(3,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xDF, cpu -> {
			//SET 3, A
			cpu.setA(SETgenericOperation(3,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE0, cpu -> {
			//SET 4, B
			cpu.setB(SETgenericOperation(4,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE1, cpu -> {
			//SET 4, C
			cpu.setC(SETgenericOperation(4,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE2, cpu -> {
			//SET 4, D
			cpu.setD(SETgenericOperation(4,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE3, cpu -> {
			//SET 4, E
			cpu.setE(SETgenericOperation(4,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE4, cpu -> {
			//SET 4, H
			cpu.setH(SETgenericOperation(4,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE5, cpu -> {
			//SET 4, L
			cpu.setL(SETgenericOperation(4,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE6, cpu -> {
			//SET 4, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(4,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xE7, cpu -> {
			//SET 4, A
			cpu.setA(SETgenericOperation(4,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE8, cpu -> {
			//SET 5, B
			cpu.setB(SETgenericOperation(5,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xE9, cpu -> {
			//SET 5, C
			cpu.setC(SETgenericOperation(5,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEA, cpu -> {
			//SET 5, D
			cpu.setD(SETgenericOperation(5,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEB, cpu -> {
			//SET 5, E
			cpu.setE(SETgenericOperation(5,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEC, cpu -> {
			//SET 5, H
			cpu.setH(SETgenericOperation(5,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xED, cpu -> {
			//SET 5, L
			cpu.setL(SETgenericOperation(5,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xEE, cpu -> {
			//SET 5, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(5,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xEF, cpu -> {
			//SET 5, A
			cpu.setA(SETgenericOperation(5,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF0, cpu -> {
			//SET 6, B
			cpu.setB(SETgenericOperation(6,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF1, cpu -> {
			//SET 6, C
			cpu.setC(SETgenericOperation(6,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF2, cpu -> {
			//SET 6, D
			cpu.setD(SETgenericOperation(6,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF3, cpu -> {
			//SET 6, E
			cpu.setE(SETgenericOperation(6,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF4, cpu -> {
			//SET 6, H
			cpu.setH(SETgenericOperation(6,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF5, cpu -> {
			//SET 6, L
			cpu.setL(SETgenericOperation(6,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF6, cpu -> {
			//SET 6, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(6,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xF7, cpu -> {
			//SET 6, A
			cpu.setA(SETgenericOperation(6,cpu.getA()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF8, cpu -> {
			//SET 7, B
			cpu.setB(SETgenericOperation(7,cpu.getB()));
			return 8;
		});
		
		instructionsCB.put((byte)0xF9, cpu -> {
			//SET 7, C
			cpu.setC(SETgenericOperation(7,cpu.getC()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFA, cpu -> {
			//SET 7, D
			cpu.setD(SETgenericOperation(7,cpu.getD()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFB, cpu -> {
			//SET 7, E
			cpu.setE(SETgenericOperation(7,cpu.getE()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFC, cpu -> {
			//SET 7, H
			cpu.setH(SETgenericOperation(7,cpu.getH()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFD, cpu -> {
			//SET 7, L
			cpu.setL(SETgenericOperation(7,cpu.getL()));
			return 8;
		});
		
		instructionsCB.put((byte)0xFE, cpu -> {
			//SET 7, (HL)
			cpu.getMmu().writeByte(cpu.getHL(), SETgenericOperation(7,cpu.getMmu().readByte(cpu.getHL())));
			return 16;
		});
		
		instructionsCB.put((byte)0xFF, cpu -> {
			//SET 7, A
			cpu.setA(SETgenericOperation(7,cpu.getA()));
			return 8;
		});
		
	}
	
	
	public Instruction get(byte opCode) {
		return this.instructions.get(opCode);
	}
	
	//Esta operación ayuda a agilizar las instrucciones BIT al ser las 64 muy similares
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
