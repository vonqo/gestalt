package mn.von.gestalt;

import mn.von.gestalt.utility.grimoire.ImageSupporter;
import mn.von.gestalt.utility.grimoire.ImageTransformer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
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

    public BufferedImage moodbarRain(BufferedImage spectogramColorful, BufferedImage moodbar) {
        BufferedImage canvas = new BufferedImage(1000, 1300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();

        // ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        moodbar = ImageTransformer.scaleImage(moodbar, 1000, 75);
        ctx2D.drawImage(moodbar, -1, 0, null);

        spectogramColorful = ImageTransformer.scaleImage(spectogramColorful, 1000, 500);
        ctx2D.drawImage(spectogramColorful, 0, 75, null);

        return canvas;
    }

    public BufferedImage wirldwind(BufferedImage moodbar, BufferedImage spectrum, BufferedImage tornadoHqz) {
        int spectrumHeight = (int) Math.round(spectrum.getHeight() * 0.5);
        BufferedImage canvas = new BufferedImage(
                tornadoHqz.getWidth(),
                tornadoHqz.getHeight() + moodbar.getHeight() + spectrumHeight,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setPaint (ImageSupporter.backgroundColor);
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        moodbar = ImageTransformer.scaleImage(moodbar, tornadoHqz.getWidth(), moodbar.getHeight());
        ctx2D.drawImage(moodbar,0,0,null);

        spectrum = ImageTransformer.scaleImage(spectrum, tornadoHqz.getWidth(), spectrumHeight);
        ctx2D.drawImage(spectrum,1,moodbar.getHeight(),null);

        ctx2D.drawImage(tornadoHqz,0,moodbar.getHeight() + spectrumHeight, null);

        return canvas;
    }

    public BufferedImage vanilla4Bar(ArrayList<BufferedImage> moodbarList, ArrayList<String> moodbarTitle, int height, int width, int fontSize) {
        if(moodbarList.size() != moodbarTitle.size()) {
            throw new InvalidParameterException("parameter error");
        }

        int additionalSpace = 0;
        int newHeight = height + fontSize + (int)Math.round(fontSize * 1.4) + additionalSpace;
        BufferedImage canvas = new BufferedImage(width, moodbarList.size() * newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx2D = canvas.createGraphics();
        ctx2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ctx2D.setComposite(AlphaComposite.Clear);
        ctx2D.setPaint (ImageSupporter.backgroundColor);
        ctx2D.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        ctx2D.setComposite(AlphaComposite.Src);
        for(int i = 0; i < moodbarList.size(); i++) {
            ctx2D.setColor(ImageSupporter.fontColor);
            ctx2D.setFont(new Font(ImageSupporter.fontName, Font.PLAIN, ImageSupporter.fontSize));
            ctx2D.drawString(moodbarTitle.get(i), 10, (i * newHeight) + (newHeight - height - (fontSize / 2)));
            ctx2D.drawImage(moodbarList.get(i),0, (i * newHeight) + (newHeight - height - 2), null);
        }
        return canvas;
    }

    public BufferedImage neuralStyle() {
        return null;
    }

}
