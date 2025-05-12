package emulator;

public class Main {

	public static void main(String[] args) {
		
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
	        System.err.println("Uncaught exception in thread " + t);
	        e.printStackTrace();
	    });
		
		byte[] rom = new byte[0xFFFF];
		rom[0x0100]=(byte)0x06;	//LD B d8
		rom[0x0101]=(byte)0x00;	//0
		rom[0x0102]=(byte)0x04;	//inc B
		rom[0x0103]=(byte)0xC3;	//jp a16
		rom[0x0104]=0x02;
		rom[0x0105]=0x00;
		
		
		Emulator emulator = new Emulator();
		emulator.run();
		
		
	}

}
