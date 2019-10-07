package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LunarTear {

    public static BufferedImage MoodbarAndSpectogramCollection(BufferedImage spectogram, BufferedImage spectogramColorful, BufferedImage moodbar, BufferedImage circle, BufferedImage circleColorful, String title) {
        BufferedImage canvas = new BufferedImage(1000, 1300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        // ctx2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5));
        ctx2D.setPaint (new Color(255, 255, 255));
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        ctx2D.setFont(new Font("SansSerif", Font.PLAIN, 22));
        ctx2D.setColor(Color.BLACK);
        ctx2D.drawString(title, 75, 30);

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

}
