package mn.von.gestalt.utility.grimoire;

import com.google.zxing.common.BitMatrix;
import mn.von.gestalt.moodbar.MoodbarAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.cos;
import static java.lang.Math.floorMod;
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

    public static BufferedImage bubbleMoodbar(double[][] spectogramData, ArrayList<Color> moodbar, int bubbleSize) {
        ArrayList<Double> bubbleSizeList = DataUtils.spectogramMinMaxToPercent(spectogramData, moodbar.size());

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

    /**
     * Specially designed for B.L.M.D - Samurai
     * */
    public static BufferedImage hanzMoodbar(double[][] spectogramData, ArrayList<Color> moodbar, int squareSize, String characters) {
        ArrayList<Double> bubbleSizeList = DataUtils.spectogramMinMaxToPercent(spectogramData, moodbar.size());

        BufferedImage destination = new BufferedImage(squareSize * 30, squareSize * 33, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx = (Graphics2D) destination.getGraphics();
        ctx.setColor(Color.BLACK);
        ctx.fillRect(0, 0, destination.getWidth(), destination.getHeight());
        ctx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(int y = 0, i = 0 ,c = 0; y < 33; y++) {
            for(int x = 0; x < 30; x++, i++, c++) {
                Color clr = moodbar.get(i);
                ctx.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), (int)(150 * bubbleSizeList.get(i))));
                int pixelSize = (int)(bubbleSizeList.get(i) * squareSize);
                int gap = (squareSize - pixelSize) / 2;
                Rectangle2D.Double square = new Rectangle2D.Double((x * squareSize)+gap, (y * squareSize)+gap, pixelSize, pixelSize);
                ctx.fill(square);

                int fontSize = 32;
                ctx.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 255));
                // ctx.setColor(new Color(255,255,255,(int)(150 * bubbleSizeList.get(i))));
                ctx.setFont(new Font("Roboto Mono", Font.PLAIN, fontSize));

                if(c >= characters.length()) c = 0;
                ctx.drawString(String.valueOf(characters.charAt(c)),(x * squareSize) + 4,(y * squareSize) + fontSize);
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
