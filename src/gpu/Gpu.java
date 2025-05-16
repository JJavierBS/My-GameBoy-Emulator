package gpu;

import cpu.InterruptionManager;
import memory.Mmu;


//Esta clase se encarga de la lógica de la gpu, tiles, sprites, scroll...
//Esta clase no imprime como tal la imagen, de ello se encarga GpuDisplay
public class Gpu {
	private static final int WIDTH = 160;
	private static final int HEIGHT = 144;
	
	private final InterruptionManager iM;
	private final Mmu mmu;
	private GpuDisplay display;
	
	private int mode; //0-3, son los modos LCD
	private int modeClock;	//va a contar los ciclos del modo LCD actual
	private int line; 
	
	//Importante -> El buffer de dibujado
	private final int[][] frameBuffer;
	
	
	public Gpu(InterruptionManager iM, Mmu mmu) {
		super();
		this.iM=iM;
		this.mmu=mmu;
		line=0;
		frameBuffer=new int[HEIGHT][WIDTH];
		display=null;
		inicializateRegisters();
	}
	
	private void inicializateRegisters(){
		mmu.writeByte(0xFF40, 0x91); //LCDC
		mmu.writeByte(0xFF41, 0x85); //STAT
		mmu.writeByte(0xFF42, 0x00); //SCY
		mmu.writeByte(0xFF43, 0x00); //SCX
		mmu.writeByte(0xFF44, 0x00); //LY -> línea actual
		mmu.writeByte(0xFF45, 0x00); //LYC -> línea comparativa
		mmu.writeByte(0xFF46, 0xFF); //DMA -> transferencia de sprites
		mmu.writeByte(0xFF47, (byte) 0b11100100); //BG Palette (fondo)
		mmu.writeByte(0xFF48, 0xFF); //OBP0 -> sprite palette 0
		mmu.writeByte(0xFF49, 0xFF); //OBP1 -> sprite palette 1
		mmu.writeByte(0xFF4A, 0x00); //WY -> x de la ventana
		mmu.writeByte(0xFF4B, 0x00); //WX -> y de la ventana
	}

	public void setDisplay(GpuDisplay display) {
		this.display=display;
	}
	
	//Método principal de la GPU
	public void step(int cycles) {
		modeClock+=cycles;
		
		int stat = mmu.readByte(0xFF41) & 0xFF;
		switch(mode) {
		case 2: //OAM Scan -> Lee los sprites que hay que renderizar
			//Dura 80 ciclos
			if(modeClock>=80) {
				modeClock-=80;
				mode=3;
			}
			// STAT interrupt: modo 2
			if((stat & 0x20) != 0) iM.requestInterrupt(1);
			break;
		case 3: //VRAM read -> Lee los tiles y dibuja línea
			//Dura 182 ciclos
			if(modeClock>=172) {
				modeClock-=172;
				mode=0;
				drawLine();
			}
			break;
		case 0: //HBlank -> Acaba de terminar de dibujar una línea, espera
			//Dura 204 ciclos
			if(modeClock>=204) {
				modeClock-=204;
				line++;
				mmu.writeByte(0XFF44, line);
				// STAT interrupt: modo 0
				if((stat & 0x08) != 0) iM.requestInterrupt(1);
				// STAT interrupt: LY==LYC
				if(line == (mmu.readByte(0xFF45) & 0xFF) && (stat & 0x40) != 0) iM.requestInterrupt(1);
				if(line==144) {
					mode=1;
					iM.requestInterrupt(0);
					display.vBlankOccurred();
					//printFrameBuffer(); //depuración
					//printTileInfo(); //depuración
				}
				else {
					mode=2;
				}
			}
			break;
		case 1: //VBlank -> Espera entre frames y debe lanzar una interrupción
			if(modeClock>=456) {
				modeClock-=456;
				line++;
				mmu.writeByte(0xFF44, line);
				// STAT interrupt: modo 1
				if((stat & 0x10) != 0) iM.requestInterrupt(1);
				// STAT interrupt: LY==LYC
				if(line == (mmu.readByte(0xFF45) & 0xFF) && (stat & 0x40) != 0) iM.requestInterrupt(1);
				if(line>153) {
					line=0;
					mmu.writeByte(0xFF44, line);
					mode=2;
				}
			}
			break;
		}
	}
	
	
	
	private void drawLine() {
		int lcdControl = mmu.readByte(0xFF40);
		boolean lcdAvailable = (lcdControl & 0x80) != 0;
		boolean bgAvailable = (lcdControl & 0x01) !=0;
		//Comprobamos que podemos dibujar
		if(!lcdAvailable || !bgAvailable) return;
		
		int scy = mmu.readByte(0xFF42);
		int scx = mmu.readByte(0xFF43);
		int ln = mmu.readByte(0xFF44);
		int bgPalete = mmu.readByte(0xFF47) & 0xFF;
		
		int tileMapBase = (lcdControl & 0x08)!=0 ? 0x9C00 : 0x9800;
		int tileDataBase = (lcdControl & 0x10)!=0 ? 0x8000 : 0x8800;
		
		
		//recorremos todos los píxeles de nuestra linea actual
		for(int x = 0; x<160; x++) {
			int pixelX = (x+scx) & 0xFF;
			int pixelY = (ln + scy) & 0xFF;
			//los tiles son 8x8 píxeles*, calculamos cual usar
			int tileX = pixelX/8;
			int tileY = pixelY/8;
			int tileIndex = tileY*32 + tileX;
			
			
			int tileId = mmu.readByte(tileMapBase + tileIndex) & 0xFF;
			
			if(tileDataBase==0x8800) {	//Si usamos 0x8800, el tileID lleva signo
				tileId=(byte)tileId;
				tileId+=128;
			}
			
			//Cada tile ocupa 16 bytes
			//2 bytes cada fila x 8 filas
			int tileAddress = tileDataBase + (tileId*16); 
			int row = pixelY%8;
			int low = mmu.readByte(tileAddress + row*2); //primer byte de la fila
			int high = mmu.readByte(tileAddress + row*2 + 1); //segundo byte de la fila
			
			int bitIndex = 7 - (pixelX % 8);
			int color = ((high>>bitIndex) & 1) << 1 | ((low >> bitIndex) & 1);
		
			int rgb = mapColor(color, bgPalete);
			//System.out.println("tileId: " + tileId + " low: " + low + " high: " + high + " color: " + color);
			frameBuffer[line][x] = rgb;
			
		}
	}
	
	private int mapColor(int color, int bgPalette) {
		int shade = (bgPalette >> (color * 2) & 0x03);
		switch (shade){
		case 0:
			return 0xFFFFFF; //blanco
		case 1:
			return 0xAAAAAA; //gris claro
		case 2:
			return 0x555555; //gris oscuro
		case 3:
			return 0x00000000; //negro
		default:
			return 0xFF00FF; //error
		}
	}
	
	public int[][] getFrameBuffer(){
		return this.frameBuffer;
	}
	
	private void printFrameBuffer() {
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				System.out.print(frameBuffer[y][x] + " ");
			}
			System.out.println();
		}
	}

	private void printTileInfo(){
		System.out.println("\nPrimeros 64 bytes del tilemap:");
		for (int i = 0x9800; i < 0x9800 + 64; i++) {
			System.out.printf("%02X ", mmu.readByte(i) & 0xFF);
		}
		System.out.println("\nPrimeros 16 bytes de VRAM:");
		for (int i = 0x8000; i < 0x8000 + 16; i++) {
			System.out.printf("%02X ", mmu.readByte(i) & 0xFF);
		}
	}

}
