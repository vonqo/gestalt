package mn.von.gestalt.utility.grimoire;

import com.google.zxing.common.BitMatrix;
import mn.von.gestalt.moodbar.MoodbarAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Vector;
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
        int circularRadius = r - rr;
        double x, y;
        double unitSpace = 0.0005;
        double fullCircle = Math.PI * 4;
        int xScalar = (int)Math.ceil((Math.PI * 3) / unitSpace);

        BufferedImage destination = new BufferedImage(OUTER_SIZE, OUTER_SIZE, BufferedImage.TYPE_INT_ARGB);
        BufferedImage scaled = ImageTransformer.scaleImage(source, xScalar, circularRadius);

        int scaledX = 0;
        for(double theta = Math.PI; theta < fullCircle; theta+= unitSpace, scaledX++) {
            for(int e = 0; e < circularRadius; e++) {
                x = Math.cos(theta) * (r-e) + r;
                y = Math.sin(theta) * (r-e) + r;
                destination.setRGB((int)x, (int)y, scaled.getRGB(scaledX, e));
            }
        }
        return destination;
    }

    public static BufferedImage bubbleMoodbar(double[][] spectogramData, Vector<Color> moodbar, int bubbleSize) {
        Vector<Double> bubbleSizeList = new Vector<Double>(moodbar.size());
        int unitRegion = spectogramData.length / moodbar.size();
        double max = 0, min = Double.MAX_VALUE;
        for(int i = 0, g = 0, e = 0; i < moodbar.size(); i++) {
            double size = 0.0;
            for(e = 0; e < unitRegion; e++) {
                for(int k = 0; k < spectogramData[g+e].length; k++) {
                    size += spectogramData[g+e][k];
                }
            }
            // System.out.println(size);
            g += e;
            if(max < size) max = size;
            if(i > 10 && min > size && size != 0.0) min = size;
            bubbleSizeList.add(size);
        }
        double wtf = max - min;
        for(int i = 0; i < bubbleSizeList.size(); i++) {
            double percent = 0;
            if((bubbleSizeList.get(i) - min) > 0) {
                percent = (bubbleSizeList.get(i) - min) / wtf;
                // System.out.println(percent);
            }
            bubbleSizeList.set(i, percent);
        }

        BufferedImage destination = new BufferedImage((bubbleSize * 40 / 2) + bubbleSize, bubbleSize * 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx = (Graphics2D) destination.getGraphics();
        ctx.setColor(Color.BLACK);
        ctx.fillRect(0, 0, destination.getWidth(), destination.getHeight());
        ctx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(int y = 0, i = 0; y < 25; y++) {
            for(int x = 0; x < 40; x++, i++) {
                Color clr = moodbar.get(i);
                ctx.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 230));
                int pixelSize = (int)(bubbleSizeList.get(i) * bubbleSize);
                int gap = (bubbleSize - pixelSize) / 2;
                Ellipse2D.Double circle = new Ellipse2D.Double(
                        (x*(bubbleSize/2)) + gap,(y*bubbleSize) + gap, pixelSize, pixelSize);
                ctx.fill(circle);
            }
        }

        ctx.dispose();
        return destination;
    }

    public static BufferedImage qrStringWithMoodbar(Vector<Color> moodbar, BitMatrix qrMatrix) {
        BufferedImage destination = new BufferedImage(100,100, BufferedImage.TYPE_INT_ARGB);
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
