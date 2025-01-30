package mn.von.gestalt.spectogram;

import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private BufferedImage SPECTOGRAM = null;
    private BufferedImage SPECTOGRAM_WITH_MOODBAR = null;
    private int HEIGHT = 500;
    private ArrayList<Color> MOODBAR;
    private boolean isMoodbarApplied = false;

    public Spectrumizer(String WAVE_PATH, Integer fftSampleSize) {
        waveFile = new Wave(WAVE_PATH);
        Spectrogram spectrogram = new Spectrogram(waveFile, fftSampleSize, 1);
        DATA = spectrogram.getNormalizedSpectrogramData();
    }

    public void applyMoodbar(ArrayList<Color> moodbar) {
        this.MOODBAR = moodbar;
        this.isMoodbarApplied = true;
    }

    public BufferedImage asBufferedImage() {
        this.buildImage();
        return SPECTOGRAM;
    }

    public void build() {
        this.buildImage();
    }

    public BufferedImage asBufferedMoodbar() {
        if(!this.isMoodbarApplied) return null;
        this.buildImage();
        return SPECTOGRAM_WITH_MOODBAR;
    }

    private void clear() {
        this.SPECTOGRAM = null;
        this.SPECTOGRAM_WITH_MOODBAR = null;
        this.MOODBAR = null;
        this.isMoodbarApplied = false;
        this.DATA = null;
        this.waveFile = null;
    }

    private void buildImage() {
        if(SPECTOGRAM == null) {
            SPECTOGRAM = new BufferedImage(DATA.length, HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctx = SPECTOGRAM.createGraphics();
            for(int i = 0; i < DATA.length; i++) {
                for(int e = 0; e < HEIGHT; e++) {
                    int colorValue = 255-((Double)(noiseFilter(DATA[i][e])*255)).intValue();
                    ctx.drawRect(i,e,1,1);
                    ctx.setColor(new Color(colorValue,colorValue,colorValue));
                }
            }
            ctx.dispose();
        }
        if(SPECTOGRAM_WITH_MOODBAR == null) {
            SPECTOGRAM_WITH_MOODBAR = new BufferedImage(DATA.length, HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctx = SPECTOGRAM_WITH_MOODBAR.createGraphics();
            ctx.setComposite(AlphaComposite.Clear);
            ctx.fillRect(0, 0, SPECTOGRAM_WITH_MOODBAR.getWidth(), SPECTOGRAM_WITH_MOODBAR.getHeight());
            ctx.setComposite(AlphaComposite.Src);

            float spectogramSize = DATA.length;
            float moodbarSize = this.MOODBAR.size();
            float percent = moodbarSize / spectogramSize;

            for(int i = 0; i < DATA.length; i++) {
                for(int e = 0; e < HEIGHT; e++) {
                    ctx.drawRect(i,e,1,1);
                    int idx = Math.round(percent*i);
                    if(idx >= 1000) idx = 999;
                    Color temp = MOODBAR.get(idx);
//                    if(min > DATA[i][e]) min = DATA[i][e];
//                    if(max < DATA[i][e]) max = DATA[i][e];
                     DATA[i][e] = noiseFilter(DATA[i][e]);

                    if(DATA[i][e] != 0) {
                        ctx.setColor(new Color(
                            ((Double)Math.floor(temp.getRed()*DATA[i][e])).intValue(),
                            ((Double)Math.floor(temp.getGreen()*DATA[i][e])).intValue(),
                            ((Double)Math.floor(temp.getBlue()*DATA[i][e])).intValue(),
                            ((Double)Math.floor(255*DATA[i][e])).intValue()
                        ));
                    } else {
                        ctx.setColor(new Color(0, 0, 0, 0));
                    }
                }
            }
            ctx.dispose();
        }
    }
    private Double noiseFilter(Double threshold) {
        threshold -= 0.55;
        if(threshold < 0) threshold = 0.0;
        threshold *= 6.0;
        if(threshold > 1) threshold = 1.0;
        return threshold;
    }

    public Integer getSize() {
        return DATA.length;
    }

    public double[][] getDATA() {
        return DATA;
    }

    public void setDATA(double[][] DATA) {
        this.DATA = DATA;
    }
}
