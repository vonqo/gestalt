package mn.von.gestalt.spectogram;

import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

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
    private int HEIGHT = 700;
    private Vector<Color> MOODBAR;
    private boolean isMoodbarApplied = false;

    public Spectrumizer(String WAVE_PATH, Integer fftSampleSize) {
        waveFile = new Wave(WAVE_PATH);
        Spectrogram spectrogram = new Spectrogram(waveFile, fftSampleSize, 1);
        DATA = spectrogram.getNormalizedSpectrogramData();
    }

    public void ApplyMoodbar(Vector<Color> moodbar) {
        this.MOODBAR = moodbar;
        this.isMoodbarApplied = true;
    }

    public BufferedImage asBufferedImageRange(Integer left, Integer right) {
        Integer width = right - left;
        SPECTOGRAM = new BufferedImage(width, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics ctx = SPECTOGRAM.getGraphics();

        /**
         *
         * moodbar: X
         * size: Y
         * Y - 100
         * X - 100
         * */

        if (this.isMoodbarApplied) {
            float spectogramSize = right - left;
            float moodbarSize = this.MOODBAR.size();
            float percent = moodbarSize / spectogramSize;

//            Double min = 255.0;
//            Double max = 0.0;

            for(int i = left; i < right; i++) {
                for(int e = 0; e < HEIGHT; e++) {
                    ctx.drawRect(i,e,1,1);
                    int idx = Math.round(percent*i);
                    if(idx >= 1000) idx = 999;
                    Color temp = MOODBAR.get(idx);
//                    if(min > DATA[i][e]) min = DATA[i][e];
//                    if(max < DATA[i][e]) max = DATA[i][e];
                    DATA[i][e] = shitter(DATA[i][e]);
                    ctx.setColor(new Color(
                            ((Double)Math.floor(temp.getRed()*DATA[i][e])).intValue(),
                            ((Double)Math.floor(temp.getGreen()*DATA[i][e])).intValue(),
                            ((Double)Math.floor(temp.getBlue()*DATA[i][e])).intValue()
                    ));
//                    ctx.setColor(new Color(
//                            ((float)temp.getRed()/255),
//                            ((float)temp.getGreen()/255),
//                            ((float)temp.getBlue()/255),
//                            ((float)DATA[i][e])
//                    ));
                }
            }
//            System.out.println("min: "+min);
//            System.out.println("max: "+max);
        } else {
            for(int i = left; i < right; i++) {
                for(int e = 0; e < HEIGHT; e++) {
                    int colorValue = 0;
                    colorValue = 255-((Double)(shitter(DATA[i][e])*255)).intValue();
                    // colorValue = ((Double)(colorValue * 2.55)).intValue();
                    ctx.drawRect(i-left,e,1,1);
                    ctx.setColor(new Color(colorValue,colorValue,colorValue));
                }
            }
        }
        ctx.dispose();
        return SPECTOGRAM;
    }

    public void asImageRange(Integer left, Integer right, File OUTPUT) throws IOException {
        ImageIO.write(this.asBufferedImageRange(left, right), "png", OUTPUT);
    }

    private Double shitter(Double sda) {
        sda -= 0.85;
        if(sda < 0) sda = 0.0;
        sda *= 8;
        if(sda > 1) sda = 1.0;
        return sda;
    }

    public Integer getSize() {
        return DATA.length;
    }

}
