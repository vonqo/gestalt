package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LunarTear {

    public static int fontSize = 26;
    public static String fontName = "SansSerif";
    public static Color fontColor = new Color(255,255,255);
    public static Color backgroundColor = new Color(0,0,0);


    public static BufferedImage MoodbarAndSpectogramCollection(BufferedImage spectogram, BufferedImage spectogramColorful, BufferedImage moodbar, BufferedImage circle, BufferedImage circleColorful, String title) {
        BufferedImage canvas = new BufferedImage(1000, 1300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        // ctx2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5));
        ctx2D.setPaint (new Color(255, 255, 255));
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        ctx2D.setFont(new Font(fontName, Font.PLAIN, fontSize));
        ctx2D.setColor(LunarTear.fontColor);
        ctx2D.drawString(title, 25, 40);

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
        ctx2D.setPaint (LunarTear.backgroundColor);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        ctx2D.setFont(new Font(fontName, Font.PLAIN, fontSize));
        ctx2D.setColor(LunarTear.fontColor);
        ctx2D.drawString(title, 25, 40);
        ctx2D.drawImage(image, 0 , 50, null);
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
