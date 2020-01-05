package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Vector;
import java.util.ArrayList;

public class LunarTear {

    public static int fontSize = 26;
    public static String fontName = "Roboto Mono";
    public static Color fontColor = new Color(255,255,255);
    public static Color backgroundColor = new Color(0,0,0);

    public static void RGB2WV_Generate_LossyExhaustingTable() {
        float[][][] colorTable = new float[255][255][255];
        boolean[][][] fillTable = new boolean[255][255][255];
        int rate = 0;
        // Generate discrete wvs
        for(short i = 360; i <= 780; i++) {
            float gamma = 0.05f;
            for(short e = 0; e < 20; e++, gamma += 0.05f) {
                Color clr = Wavelength.wvColor(i, gamma);
                short r = (short)clr.getRed();
                short g = (short)clr.getGreen();
                short b = (short)clr.getBlue();
                colorTable[r][g][b] = (float)i;
                fillTable[r][g][b] = true;
                rate++;
            }
        }
        System.out.println(rate);
    }

    public static BufferedImage MakeFun(int width, int height, String title ) {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        return null;
    }

    public static BufferedImage MoodbarAndSpectogramCollection(BufferedImage spectogram, BufferedImage spectogramColorful, BufferedImage moodbar, BufferedImage circle, BufferedImage circleColorful, String title) {
        BufferedImage canvas = new BufferedImage(1000, 1300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        // ctx2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5));
        ctx2D.setPaint (backgroundColor);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        ctx2D.setFont(new Font(fontName, Font.PLAIN, fontSize));
        ctx2D.setColor(LunarTear.fontColor);
        ctx2D.drawString(title, 25  , 40);

        moodbar = ImageTransformer.scaleImage(moodbar, 1000, 75);
        ctx2D.drawImage(moodbar, 0, 50, null);

        spectogram = ImageTransformer.scaleImage(spectogram, 1000, 500);
        ctx2D.drawImage(spectogram, 0, 125, null);

        spectogramColorful = ImageTransformer.scaleImage(spectogramColorful, 1000, 500);
        ctx2D.drawImage(spectogramColorful, 0 , 425, null);
        ctx2D.setColor(Color.WHITE);
        ctx2D.fillRect(0,850,1000,100);

        circle = ImageTransformer.scaleImage(circle, 450, 450);
        circleColorful = ImageTransformer.scaleImage(circleColorful, 450, 450);
        ctx2D.drawImage(circle, 25, 850, null);
        ctx2D.drawImage(circleColorful, 525, 850, null);

        return canvas;
    }

    public static BufferedImage addTitle (BufferedImage image, String title) {
        BufferedImage canvas = new BufferedImage(image.getWidth(), image.getHeight() + 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.setPaint (LunarTear.backgroundColor);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        ctx2D.setFont(new Font(fontName, Font.PLAIN, fontSize));
        ctx2D.setColor(LunarTear.fontColor);
        ctx2D.drawString(title, 25, 40);
        ctx2D.drawImage(image, 0 , 50, null);
        return canvas;
    }

    public static BufferedImage legacy4Bar(ArrayList<BufferedImage> moodbarList, ArrayList<String> moodbarTitle) {
        if(moodbarList.size() != moodbarTitle.size()) {
            throw new RuntimeException("parameter error");
        }
        BufferedImage canvas = new BufferedImage(1000, moodbarList.size() * 220, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.setPaint (backgroundColor);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(int i = 0; i < moodbarList.size(); i++) {
            ctx2D.setColor(LunarTear.fontColor);
            ctx2D.setFont(new Font(fontName, Font.BOLD, fontSize));
            ctx2D.drawString(moodbarTitle.get(i), 10, (i * 220)+55);
            ctx2D.drawImage(moodbarList.get(i),0, (i * 220)+68, null);
        }
        return canvas;
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static void setFontSize(int fontSize) {
        LunarTear.fontSize = fontSize;
    }

    public static String getFontName() {
        return fontName;
    }

    public static void setFontName(String fontName) {
        LunarTear.fontName = fontName;
    }

    public static Color getFontColor() {
        return fontColor;
    }

    public static void setFontColor(Color fontColor) {
        LunarTear.fontColor = fontColor;
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static void setBackgroundColor(Color backgroundColor) {
        LunarTear.backgroundColor = backgroundColor;
    }

}
