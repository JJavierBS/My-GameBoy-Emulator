package gpu;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import memory.Mmu;

public class GpuDebugger extends JPanel {
    private static final int TILE_SIZE = 8;
    private static final int TILE_SCALE = 2; // Ampliamos para verlo mejor
    private static final int TILES_PER_ROW = 16;

    private final Mmu mmu;

    public GpuDebugger(Mmu mmu) {
        this.mmu = mmu;

        JFrame frame = new JFrame("GPU Debugger");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.setSize(512, 512);
        frame.setVisible(true);

        new Timer(500, e -> repaint()).start(); // Actualiza cada 500 ms
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawTileset(g, 0, 0);
        drawTileMap(g, 0, 256); // debajo de los tiles
    }

    private void drawTileset(Graphics g, int offsetX, int offsetY) {
        for (int tileId = 0; tileId < 384; tileId++) {
            int x = tileId % TILES_PER_ROW;
            int y = tileId / TILES_PER_ROW;

            BufferedImage tileImage = getTileImage(tileId);
            g.drawImage(tileImage, offsetX + x * TILE_SIZE * TILE_SCALE, offsetY + y * TILE_SIZE * TILE_SCALE, TILE_SIZE * TILE_SCALE, TILE_SIZE * TILE_SCALE, null);
        }

        g.setColor(Color.BLACK);
        g.drawString("Tileset (VRAM @ 0x8000)", offsetX, offsetY - 4);
    }

    private BufferedImage getTileImage(int tileId) {
        BufferedImage img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        int baseAddr = 0x8000 + tileId * 16;

        for (int row = 0; row < TILE_SIZE; row++) {
            int low = mmu.readByte(baseAddr + row * 2) & 0xFF;
            int high = mmu.readByte(baseAddr + row * 2 + 1) & 0xFF;

            for (int col = 0; col < TILE_SIZE; col++) {
                int bitIndex = 7 - col;
                int colorId = ((high >> bitIndex) & 1) << 1 | ((low >> bitIndex) & 1);
                int rgb = mapColor(colorId);
                img.setRGB(col, row, rgb);
            }
        }

        return img;
    }

    private void drawTileMap(Graphics g, int offsetX, int offsetY) {
        int baseMap = ((mmu.readByte(0xFF40) & 0x08) != 0) ? 0x9C00 : 0x9800;

        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                int tileIndex = y * 32 + x;
                int tileId = mmu.readByte(baseMap + tileIndex) & 0xFF;
                BufferedImage tileImage = getTileImage(tileId);

                g.drawImage(tileImage, offsetX + x * TILE_SIZE, offsetY + y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
            }
        }

        g.setColor(Color.BLACK);
        g.drawString("Background Tilemap (@" + Integer.toHexString(baseMap).toUpperCase() + ")", offsetX+300, offsetY - 4);
    }

    private int mapColor(int color) {
        // Paleta básica Game Boy (BG Palette por defecto)
        return switch (color) {
            case 0 -> 0xFFFFFF; // blanco
            case 1 -> 0xAAAAAA; // gris claro
            case 2 -> 0x555555; // gris oscuro
            case 3 -> 0x000000; // negro
            default -> 0xFF00FF; // error
        };
    }
}

