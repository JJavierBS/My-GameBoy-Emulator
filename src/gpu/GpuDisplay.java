package gpu;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GpuDisplay extends JPanel{
	private static final int SCALE=3; //Aumentamos el tamaño ya que la pantalla de la gamboy es muy pequeña
	private static final int FPS=60;
	private final Gpu gpu;
	private final BufferedImage screen;
	
	public GpuDisplay(Gpu gpu) {
		super();
		this.gpu=gpu;
		gpu.setDisplay(this);
		this.screen = new BufferedImage(160,144,BufferedImage.TYPE_INT_RGB);
		
		JFrame frame = new JFrame("My Game Boy emulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(this);
		frame.setSize(160*SCALE, 144*SCALE);
		frame.setVisible(true);
		
		
		new Timer(1000/FPS, e -> repaint()).start(); //Este timer es de swing, no del emulador
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int[][] frameBuffer = gpu.getFrameBuffer();
		for(int y = 0; y<144; y++) {
			for(int x = 0; x<160; x++) {
				screen.setRGB(x, y, frameBuffer[y][x]);
			}
		}
		g.drawImage(screen, 0, 0, 160 * SCALE, 144 * SCALE, null);
	}
	
	public void vBlankOccurred() {
		repaint();
		//System.out.println("Repintando");
	}
	
}
