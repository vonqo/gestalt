package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class NoiseGenerator {

    public static BufferedImage testNoise(ArrayList<Color> colors) {

        int width = 3000;
        int height = 3000;
        int featureSize = 1;
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        OpenSimplexNoise noise = new OpenSimplexNoise(1234);
        OpenSimplexNoise noise2 = new OpenSimplexNoise(1000);

        Graphics2D ctx = (Graphics2D) canvas.getGraphics();
        GradientPaint background = new GradientPaint(0, 0, Color.red, width, height, Color.blue);
        // ctx.setColor(Color.BLACK);
        ctx.setPaint(background);
        ctx.fillRect(0, 0, width, height);
        ctx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                double value = (noise.eval(x / featureSize, y / featureSize) + 1) / 2;
                double value2 = (noise2.eval(x / featureSize, y / featureSize) + 1) / 2;

                if(value2 > 0) {
                    ctx.setColor(new Color(50 / 255.0f,139 / 255.0f,168 / 255.0f, (float)value2));
                    ctx.fillRect(x,y,1,1);
                }
            }
        }

        ctx.dispose();
        return canvas;
    }

    private Color[][] distributeNoise(Color color, Polygon polygon, double density, int distributionRadius) {
        OpenSimplexNoise noise = new OpenSimplexNoise();


        return null;
    }
}
