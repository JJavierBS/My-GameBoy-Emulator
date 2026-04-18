package gpu;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GpuDisplay extends JPanel{
	private static final int SCALE=3; 
	private static final int FPS=60;
	private final Gpu gpu;
	private final BufferedImage screen;
	private final JFrame frame;
	private boolean isFullscreen = false;
	
	public GpuDisplay(Gpu gpu) {
		super();
		this.gpu=gpu;
		gpu.setDisplay(this);
		this.screen = new BufferedImage(160,144,BufferedImage.TYPE_INT_RGB);
		this.setPreferredSize(new Dimension(160*SCALE, 144*SCALE));
		
		frame = new JFrame("My Game Boy emulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public JFrame getFrame() {
		return this.frame;
	}
	
	public void toggleFullscreen() {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		isFullscreen = !isFullscreen;
		frame.dispose();
		frame.setUndecorated(isFullscreen);
		
		if (isFullscreen) {
			device.setFullScreenWindow(frame);
		} else {
			device.setFullScreenWindow(null);
			frame.pack();
			frame.setLocationRelativeTo(null);
		}
		frame.setVisible(true);
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
		
		int panelW = getWidth();
		int panelH = getHeight();
		float ratio = 160.0f / 144.0f;
		int drawW = panelW;
		int drawH = (int)(panelW / ratio);
		
		if (drawH > panelH) {
			drawH = panelH;
			drawW = (int)(panelH * ratio);
		}
		
		int drawX = (panelW - drawW) / 2;
		int drawY = (panelH - drawH) / 2;
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, panelW, panelH);
		g.drawImage(screen, drawX, drawY, drawW, drawH, null);
	}
	
	public void vBlankOccurred() {
		repaint();
	}
}
