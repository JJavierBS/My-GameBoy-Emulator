package gpu;

import cpu.InterruptionManager;
import memory.Mmu;




public class Gpu {
	private static final int WIDTH = 160;
	private static final int HEIGHT = 144;
	
	private final InterruptionManager iM;
	private final Mmu mmu;
	private GpuDisplay display;
	
	private int mode;
	private int modeClock;
	

	private final int[][] frameBuffer;
	private final int[] bgPriority;
	
	private boolean windowEnable;
	private int wx, wy;
	private int windowLineCounter;
	private boolean renderedWindow;
	
	public Gpu(InterruptionManager iM, Mmu mmu) {
		super();
		this.iM=iM;
		this.mmu=mmu;
		frameBuffer=new int[HEIGHT][WIDTH];
		bgPriority=new int[WIDTH];
		display=null;
		mode=0;
		inicializateRegisters();
	}
	
	private void inicializateRegisters(){
		mmu.writeByte(0xFF40, 0x91);
		mmu.writeByte(0xFF41, 0x85);
		mmu.writeByte(0xFF42, 0x00);
		mmu.writeByte(0xFF43, 0x00);
		mmu.writeByte(0xFF44, 0x00);
		mmu.writeByte(0xFF45, 0x00);
		mmu.writeByte(0xFF46, 0xFF);
		mmu.writeByte(0xFF47, (byte) 0b11100100);
		mmu.writeByte(0xFF48, 0xFC);
		mmu.writeByte(0xFF49, 0xFC);
		mmu.writeByte(0xFF4A, 0x00);
		mmu.writeByte(0xFF4B, 0x00);
	}

	public void setDisplay(GpuDisplay display) {
		this.display=display;
	}
	

	public void step(int cycles) {
 		boolean lcdAvailable = (mmu.readByte(0xFF40) & 0x80) != 0;
		if(!lcdAvailable) {
			modeClock = 0;
			mode = 0;
			mmu.writeByte(0xFF44, 0);
			return;
		}
		modeClock+=cycles;
		int stat = mmu.readByte(0xFF41) & 0xFF;
		switch(mode) {
		case 2:

			if(modeClock>=80) {
				modeClock-=80;
				mode=3;
			}

			if((stat & 0x20) != 0) iM.requestInterrupt(1);
			break;
		case 3:

			if(modeClock>=172) {
				modeClock-=172;
				mode=0;
				drawLine();
			}
			break;
		case 0:

			if(modeClock>=204) {
				modeClock-=204;
				int ln = mmu.readByte(0xFF44) & 0xFF;
				ln++;
				mmu.writeByte(0XFF44, ln);

				if((stat & 0x08) != 0) iM.requestInterrupt(1);

				if(ln == (mmu.readByte(0xFF45) & 0xFF) && (stat & 0x40) != 0) iM.requestInterrupt(1);
				if(ln>=144) {
					mode=1;
					iM.requestInterrupt(0);
					if (display != null) display.vBlankOccurred();

					windowLineCounter = 0;
				}
				else {
					mode=2;
				}
			}
			break;
		case 1:
			if(modeClock>=456) {
				modeClock-=456;
				int ln = mmu.readByte(0xFF44) & 0xFF;
				ln++;
				mmu.writeByte(0xFF44, ln & 0xFF);

				if((stat & 0x10) != 0) iM.requestInterrupt(1);

				if(ln == (mmu.readByte(0xFF45) & 0xFF) && (stat & 0x40) != 0) iM.requestInterrupt(1);
				if(ln>153) {
					ln=0;
					mmu.writeByte(0xFF44, ln);
					mode=2;
				}
			}
			break;
		}
	}
	
	private void drawLine() {
		int lcdControl = mmu.readByte(0xFF40);
		boolean lcdAvailable = (lcdControl & 0x80) != 0;
		if(!lcdAvailable) return;
		
		boolean bgAvailable = (lcdControl & 0x01) !=0;
		windowEnable = (lcdControl & 0x20) != 0;
		
		int scy = mmu.readByte(0xFF42) & 0xFF;
		int scx = mmu.readByte(0xFF43) & 0xFF;
		int ln = mmu.readByte(0xFF44) & 0xFF;
		int bgPalete = mmu.readByte(0xFF47) & 0xFF;
		
		wy = mmu.readByte(0xFF4A) & 0xFF;
		wx = (mmu.readByte(0xFF4B) & 0xFF) - 7;
		
		if (ln == 0) windowLineCounter = 0;
		
		renderedWindow = false;
		
		int bgTileMapBase = (lcdControl & 0x08)!=0 ? 0x9C00 : 0x9800;
		int winTileMapBase = (lcdControl & 0x40)!=0 ? 0x9C00 : 0x9800;
		int tileDataBase = (lcdControl & 0x10)!=0 ? 0x8000 : 0x8800;
		
		for(int x = 0; x<160; x++) {
			int rgb = mapColor(0, bgPalete);
			int color = 0;
			
		if (bgAvailable) {
			boolean usingWindow = windowEnable && wy <= ln && wx <= x;
				if (usingWindow) renderedWindow = true;
				
				int pixelX = usingWindow ? (x - wx) : ((x + scx) & 0xFF);
				int pixelY = usingWindow ? windowLineCounter : ((ln + scy) & 0xFF);
				
				int tileMapBase = usingWindow ? winTileMapBase : bgTileMapBase;
				
				int tileX = pixelX/8;
				int tileY = pixelY/8;
				int tileIndex = tileY*32 + tileX;
				
				int tileId = mmu.readByte(tileMapBase + tileIndex) & 0xFF;
				
				if(tileDataBase==0x8800) {
					tileId=(byte)tileId;
					tileId+=128;
				}
				
				int tileAddress = tileDataBase + (tileId*16); 
				int row = pixelY%8;
				int low = mmu.readByte(tileAddress + row*2) & 0xFF;
				int high = mmu.readByte(tileAddress + row*2 + 1) & 0xFF;
				
				int bitIndex = 7 - (pixelX % 8);
				color = ((high>>bitIndex) & 1) << 1 | ((low >> bitIndex) & 1);
			
				rgb = mapColor(color, bgPalete);
			}
			
			if(ln<HEIGHT){
				frameBuffer[ln][x] = rgb;
			}
			bgPriority[x] = color;
		}
		if (renderedWindow) {
			windowLineCounter++;
		}
		this.drawSprites(ln);
	}
	
	private void drawSprites(int ln) {
		int lcdControl = mmu.readByte(0xFF40);
		if ((lcdControl & 0x02) == 0) return;
		
		int spriteHeight = ((lcdControl & 0x04) != 0) ? 16 : 8;
		
		java.util.List<int[]> lineSprites = new java.util.ArrayList<>();
		for (int i = 0; i < 40; i++) {
			int spriteAddr = 0xFE00 + i * 4;
			int y = (mmu.readByte(spriteAddr) & 0xFF) - 16;
			if (ln < y || ln >= y + spriteHeight) continue;
			lineSprites.add(new int[]{i, y, mmu.readByte(spriteAddr + 1) & 0xFF, mmu.readByte(spriteAddr + 2) & 0xFF, mmu.readByte(spriteAddr + 3) & 0xFF});
			if (lineSprites.size() == 10) break;
		}
		

		lineSprites.sort((s1, s2) -> {
			if (s1[2] != s2[2]) return Integer.compare(s1[2], s2[2]);
			return Integer.compare(s1[0], s2[0]);
		});
		
		boolean[] spriteDrawn = new boolean[WIDTH];

		for (int[] sprite : lineSprites) {
			int y = sprite[1];
			int x = sprite[2] - 8;
			int tile = sprite[3];
			int attr = sprite[4];
			
			int row = ln - y;
			if ((attr & 0x40) != 0) row = spriteHeight - 1 - row;
			if (spriteHeight == 16) {
			    tile &= 0xFE;
			}
			int tileAddr = 0x8000 + tile * 16 + row * 2;
			int low = mmu.readByte(tileAddr) & 0xFF;
			int high = mmu.readByte(tileAddr + 1) & 0xFF;
			
			for (int col = 0; col < 8; col++) {
				int bit = (attr & 0x20) != 0 ? col : 7 - col;
				int colorId = ((high >> bit) & 1) << 1 | ((low >> bit) & 1);
				if (colorId == 0) continue;
				int px = x + col;
				if (px < 0 || px >= WIDTH) continue;
				
				if (spriteDrawn[px]) continue;
				spriteDrawn[px] = true;
				
				int palette = (attr & 0x10) != 0 ? mmu.readByte(0xFF49) : mmu.readByte(0xFF48);
				int rgb = mapColor(colorId, palette);
				
				boolean spriteAboveBg = (attr & 0x80) == 0 || bgPriority[px] == 0;
				if (spriteAboveBg) {
					frameBuffer[ln][px] = rgb;
				}
			}
		}
	}

	private int mapColor(int color, int bgPalette) {
		int shade = (bgPalette >> (color * 2) & 0x03);
		switch (shade){
		case 0:
			return 0xFFFFFFFF;
		case 1:
			return 0xFFAAAAAA;
		case 2:
			return 0xFF555555;
		case 3:
			return 0xFF000000;
		default:
			return 0xFF00FF;
		}
	}
	
	public int[][] getFrameBuffer(){
		return this.frameBuffer;
	}
}
