package mn.von.gestalt.utility.grimoire;

import mn.von.gestalt.moodbar.MoodbarAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;


/**
 Image playing utility functions

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/

public class ImageTransformer {

    public static BufferedImage scaleImage(BufferedImage source, double scaleFactor) {
        int width = (int) (source.getWidth() * scaleFactor);
        int height = (int) (source.getHeight() * scaleFactor);
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final AffineTransform affineTransform = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
        final AffineTransformOp ato = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC);
        scaled = ato.filter(source, scaled);
        return scaled;
    }

    public static BufferedImage scaleImage(BufferedImage source, int width, int height) {
        double scaleFactorX = (double) width / source.getWidth();
        double scaleFactorY = (double) height / source.getHeight();
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final AffineTransform affineTransform = AffineTransform.getScaleInstance(scaleFactorX, scaleFactorY);
        final AffineTransformOp ato = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC);
        scaled = ato.filter(source, scaled);
        return scaled;
    }

    public static BufferedImage circularTransform(BufferedImage source, int OUTER_SIZE, int INNER_SIZE) {
        BufferedImage destination = new BufferedImage(OUTER_SIZE+3, OUTER_SIZE+3, BufferedImage.TYPE_INT_ARGB);
        double scaleFactor = 2.0;
        BufferedImage scaled = scaleImage(source, scaleFactor);
        Graphics ctxDestination = destination.getGraphics();

        int r = OUTER_SIZE / 2;
        int rr = INNER_SIZE / 2;
        int cicularLineLength = r - rr;
        double x1, y1 ,x2, y2;
        double unitSpace = Math.PI / (scaled.getWidth() / 2.0);
        // double unitSpace = 0.001;
        double unitIndicator = Math.PI;

        for(int i = 0; i < scaled.getWidth(); i++, unitIndicator += unitSpace) {
            x1 = Math.cos(unitIndicator) * r;
            y1 = Math.sin(unitIndicator) * r;
            x2 = Math.cos(unitIndicator) * rr;
            y2 = Math.sin(unitIndicator) * rr;

            ctxDestination.drawLine((int)x1+r, (int)y1+r, (int)x2+r, (int)y2+r);
            ctxDestination.setColor(getColorFromInt(scaled.getRGB(i,0)));
        }
        ctxDestination.dispose();
        return destination;
    }


    public static BufferedImage tint(BufferedImage loadImg, Color COLOR, int h1) {

        for(int x = 0; x < loadImg.getWidth(); x++)
            // for (int y = 0; y < loadImg.getHeight(); y++) {
            for (int y = 0; y < 150; y++) {
                Color pixel = getColorFromInt(loadImg.getRGB(x, y));

                int r = Math.abs(pixel.getRed() - COLOR.getRed());
                int g = Math.abs(pixel.getGreen() - COLOR.getGreen());
                int b = Math.abs(pixel.getBlue() - COLOR.getBlue());

                loadImg.setRGB(x, y + h1, getIntFromColor(new Color(r, g, b)));
            }

        return loadImg;
    }

    public static int getIntFromColor(Color COLOR){
        int red = (COLOR.getRed() << 16) & 0x00FF0000;
        int green = (COLOR.getGreen() << 8) & 0x0000FF00;
        int blue = COLOR.getBlue() & 0x000000FF;
        return 0xFF000000 | red | green | blue;
    }

    public static Color getColorFromInt(int COLOR) {
        int red = (COLOR & 0x00ff0000) >> 16;
        int green = (COLOR & 0x0000ff00) >> 8;
        int blue = COLOR & 0x000000ff;
        return new Color(red, green, blue);
    }

}
