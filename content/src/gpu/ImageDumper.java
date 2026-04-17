package gpu;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageDumper {
    public static void dump(int[][] frameBuffer, String filename) {
        int height = frameBuffer.length;
        int width = frameBuffer[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, frameBuffer[y][x]);
            }
        }
        try {
            ImageIO.write(image, "png", new File(filename));
            System.out.println("Image dumped to " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
