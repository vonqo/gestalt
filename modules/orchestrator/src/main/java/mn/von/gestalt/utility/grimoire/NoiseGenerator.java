package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class NoiseGenerator {

    public static BufferedImage testNoise(ArrayList<Color> colors) {

        int width = 1000;
        int height = 1000;
        int featureSize = 1;
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        OpenSimplexNoise noise = new OpenSimplexNoise();
        Graphics2D ctx = (Graphics2D) canvas.getGraphics();
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                double value = noise.eval(x / featureSize, y / featureSize, 0.0);
                int rgb = 0x010101 * (int)((value + 1) * 127.5);
                canvas.setRGB(x, y, rgb);
            }
        }

        return canvas;
    }

    private Color[][] distributeNoise(Color color, Polygon polygon, double density, int distributionRadius) {
        OpenSimplexNoise noise = new OpenSimplexNoise();



        return null;
    }
}
