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

    public static BufferedImage rectangularToPolarCoordinate(BufferedImage source, int OUTER_SIZE, int INNER_SIZE) {
        int r = OUTER_SIZE / 2;
        int rr = INNER_SIZE / 2;
        int cicularRadius = r - rr;
        double x, y;
        double unitSpace = 0.0005;
        double fullCircle = Math.PI * 4;
        int xScalar = (int)Math.ceil((Math.PI * 3) / unitSpace);

        BufferedImage destination = new BufferedImage(OUTER_SIZE, OUTER_SIZE, BufferedImage.TYPE_INT_ARGB);
        BufferedImage scaled = ImageTransformer.scaleImage(source, xScalar, cicularRadius);

        int scaledX = 0;
        for(double theta = Math.PI; theta < fullCircle; theta+= unitSpace, scaledX++) {
            for(int e = 0; e < cicularRadius; e++) {
                x = Math.cos(theta) * (r-e) + r;
                y = Math.sin(theta) * (r-e) + r;
                destination.setRGB((int)x, (int)y, scaled.getRGB(scaledX, e));
            }
        }

        return destination;
    }


    public static BufferedImage tint(BufferedImage loadImg, Color COLOR, int h1) {

        for(int x = 0; x < loadImg.getWidth(); x++)
            // for (int y = 0; y < loadImg.getHeight(); y++) {
            for (int y = 0; y < 150; y++) {
                Color pixel = ImageTransformer.getColorFromInt(loadImg.getRGB(x, y));

                int r = Math.abs(pixel.getRed() - COLOR.getRed());
                int g = Math.abs(pixel.getGreen() - COLOR.getGreen());
                int b = Math.abs(pixel.getBlue() - COLOR.getBlue());

                loadImg.setRGB(x, y + h1, ImageTransformer.getIntFromColor(new Color(r, g, b)));
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
