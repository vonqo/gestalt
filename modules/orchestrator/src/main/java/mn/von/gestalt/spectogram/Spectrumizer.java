package mn.von.gestalt.spectogram;

import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 Spectrum to image

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Spectrumizer {

    private Wave waveFile;
    private double[][] DATA;
    private BufferedImage SPECTOGRAM;
    private int HEIGHT = 500;

    public Spectrumizer(String WAVE_PATH, Integer fftSampleSize) {
        waveFile = new Wave(WAVE_PATH);
        Spectrogram spectrogram = new Spectrogram(waveFile, 2048, 0);
        double[][] data = spectrogram.getNormalizedSpectrogramData();
    }

    public BufferedImage asBufferedImageRange(Integer left, Integer right) {
        Integer width = right - left;
        SPECTOGRAM = new BufferedImage(width, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics ctx = SPECTOGRAM.getGraphics();
        for(int i = left; i < right; i++) {
            for(int e = 0; e < HEIGHT; e++) {
                int colorValue = ((Double) (DATA[i][e]*255*5)).intValue();
                ctx.drawRect(i,e,1,1);
                ctx.setColor(new Color(0,colorValue,colorValue));
            }
        }
        ctx.dispose();
        return SPECTOGRAM;
    }

    public void asImageRange(Integer left, Integer right, File OUTPUT) throws IOException {
        ImageIO.write(this.asBufferedImageRange(left, right), "png", OUTPUT);
    }

    public Integer getSize() {
        return DATA.length;
    }

}
