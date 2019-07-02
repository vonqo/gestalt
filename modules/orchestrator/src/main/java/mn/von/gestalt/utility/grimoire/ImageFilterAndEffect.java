package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 Image playing utility functions

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class ImageFilterAndEffect {

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
        int red   = (COLOR & 0x00ff0000) >> 16;
        int green = (COLOR & 0x0000ff00) >> 8;
        int blue  =  COLOR & 0x000000ff;
        return new Color(red, green, blue);
    }

}
