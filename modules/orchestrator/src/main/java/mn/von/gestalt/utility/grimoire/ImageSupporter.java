package mn.von.gestalt.utility.grimoire;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSupporter {

    public static int fontSize = 26;
    public static String fontName = "Roboto Mono";
    public static Color fontColor = new Color(255,255,255);
    public static Color backgroundColor = new Color(0,0,0);

    //=======================================================================================
    //=======================================================================================
    //=======================================================================================

    public static int getFontSize() {
        return fontSize;
    }

    public static void setFontSize(int fontSize) {
        ImageSupporter.fontSize = fontSize;
    }

    public static String getFontName() {
        return fontName;
    }

    public static void setFontName(String fontName) {
        ImageSupporter.fontName = fontName;
    }

    public static Color getFontColor() {
        return fontColor;
    }

    public static void setFontColor(Color fontColor) {
        ImageSupporter.fontColor = fontColor;
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static void setBackgroundColor(Color backgroundColor) {
        ImageSupporter.backgroundColor = backgroundColor;
    }

    //=======================================================================================
    //=======================================================================================
    //=======================================================================================

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

    public static BufferedImage addTitle (BufferedImage image, String title) {
        BufferedImage canvas = new BufferedImage(image.getWidth(), image.getHeight() + fontSize+20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.setPaint (ImageSupporter.backgroundColor);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        ctx2D.setFont(new Font(fontName, Font.PLAIN, fontSize));
        ctx2D.setColor(ImageSupporter.fontColor);
        ctx2D.drawString(title, fontSize+5, fontSize+10);
        ctx2D.drawImage(image, 0 , fontSize+20, null);
        return canvas;
    }

    public static BufferedImage addMark(BufferedImage image, String text, int topPadding) throws IOException {
        BufferedImage canvas = new BufferedImage(image.getWidth(), image.getHeight()+50+topPadding, BufferedImage.TYPE_INT_ARGB);
        BufferedImage logo = ImageIO.read(new File("logo_smoll.png"));
        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.setPaint (Color.WHITE);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        ctx2D.drawImage(image, 0 , 0, null);
        ctx2D.drawImage(logo, 0 , image.getHeight()+topPadding, null);

        ctx2D.setFont(new Font(fontName, Font.PLAIN, fontSize));
        ctx2D.setColor(ImageSupporter.fontColor);
        ctx2D.drawString(text, 410, image.getHeight()+35+topPadding);
        return canvas;
    }

    private static BufferedImage addFootermark(BufferedImage image, String text) throws IOException {
        ImageSupporter.setFontSize(28);
        ImageSupporter.setFontColor(Color.black);
        ImageSupporter.setFontName("Ubuntu");
        return ImageSupporter.addMark(image, text, 0);
    }

    public static BufferedImage fillBlack(int width, int height) {
        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setPaint(Color.BLACK);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        return canvas;
    }
}
