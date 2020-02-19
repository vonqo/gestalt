package mn.von.gestalt;

import mn.von.gestalt.utility.grimoire.ImageSupporter;
import mn.von.gestalt.utility.grimoire.ImageTransformer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LunarTear {

    public BufferedImage moodbarAndSpectogramCollection(BufferedImage spectogram, BufferedImage spectogramColorful, BufferedImage moodbar, BufferedImage circle, BufferedImage circleColorful, String title) {
        BufferedImage canvas = new BufferedImage(1000, 1300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        // ctx2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5));
        ctx2D.setPaint (ImageSupporter.backgroundColor);
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        ctx2D.setFont(new Font(ImageSupporter.fontName, Font.PLAIN, ImageSupporter.fontSize));
        ctx2D.setColor(ImageSupporter.fontColor);
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

    public BufferedImage vanilla4Bar(ArrayList<BufferedImage> moodbarList, ArrayList<String> moodbarTitle) {
        if(moodbarList.size() != moodbarTitle.size()) {
            throw new RuntimeException("parameter error");
        }
        BufferedImage canvas = new BufferedImage(1000, moodbarList.size() * 220, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.setPaint (ImageSupporter.backgroundColor);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        for(int i = 0; i < moodbarList.size(); i++) {
            ctx2D.setColor(ImageSupporter.fontColor);
            ctx2D.setFont(new Font(ImageSupporter.fontName, Font.BOLD, ImageSupporter.fontSize));
            ctx2D.drawString(moodbarTitle.get(i), 10, (i * 220)+55);
            ctx2D.drawImage(moodbarList.get(i),0, (i * 220)+68, null);
        }
        return canvas;
    }

}
