package mn.von.gestalt.moodbar;

// import org.apache.commons.io.IOUtils;

import mn.von.gestalt.utility.config.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 Moodbar executable's output adapter to java awt color

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class MoodbarAdapter {

    public static ArrayList<Color> buildMoodbar(String AUDIO_PATH, String OUTPUT) throws IOException {

        ProcessBuilder processBuilder;
        Process process;

        ArrayList<Color> moodbar = new ArrayList<Color>(1000);

        processBuilder = new ProcessBuilder(Config.MOODBAR_EXEC, "-o" ,OUTPUT, AUDIO_PATH);
        process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = null;

        while ((line = reader.readLine()) != null) {
            if(!"".equals(line)) moodbar.add(colorize(line));
        }

        try{
            int wait = process.waitFor();
            if (wait == 0) {
                Logger.getLogger(MoodbarAdapter.class.getName()).log(Level.INFO, "Moodbar build finished");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MoodbarAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("moodbar size: " + moodbar.size());
        return moodbar;
    }

    public static ArrayList<Color> groupColor(ArrayList<Color> colors, int grouping) {
        float group = colors.size() / grouping;
        ArrayList<Color> groupedColors = new ArrayList<>();
        Iterator<Color> itr = colors.iterator();

        for(int i = 0; i < group; i++) {
            int r = 0, g = 0, b = 0;

            for(int e = 0; e < grouping && itr.hasNext(); e++) {
                Color clr = itr.next();
                r += clr.getRed();
                g += clr.getGreen();
                b += clr.getBlue();
            }
            r /= grouping;
            g /= grouping;
            b /= grouping;
            groupedColors.add(new Color(r, g, b));
        }
        return  groupedColors;
    }

    private static Color colorize(String COLOR_STR) {
        String[] colors = COLOR_STR.split("\\s+");
        return new Color(
                Integer.parseInt(colors[0]),
                Integer.parseInt(colors[1]),
                Integer.parseInt(colors[2])
        );
    }

    public static BufferedImage toBufferedImage(ArrayList<Color> MOOD, int HEIGHT) {
        BufferedImage bar = new BufferedImage(MOOD.size(), HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics ctx = bar.getGraphics();
        Iterator<Color> itr = MOOD.iterator();
        for (int i = 0; itr.hasNext(); i++) {
            ctx.drawRect(i, 0, 0, HEIGHT);
            ctx.setColor(itr.next());
        }
        ctx.dispose();
        return bar;
    }

    // with gradient interpolation
    public static BufferedImage toBufferedImage(ArrayList<Color> MOOD, int HEIGHT, int WIDTH) {
        if(MOOD.size() > WIDTH) {
            throw new InvalidParameterException("mood.size > width");
        }

        BufferedImage bar = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D ctx = (Graphics2D)bar.getGraphics();
        ctx.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        float unit = WIDTH / MOOD.size();
        float pos1 = 0;
        float pos2 = 0;

        for(int i = 0; i < MOOD.size()-1; i++) {
            pos1 = pos2;
            pos2 = pos1 + unit;

            GradientPaint gradient = new GradientPaint(
                Math.round(pos1),0,MOOD.get(i),Math.round(pos2),0,MOOD.get(i+1)
            );

            ctx.setPaint(gradient);
            ctx.fillRect(Math.round(pos1),0,Math.round(pos2),HEIGHT);
        }

        ctx.dispose();
        return bar;
    }

    public static void moodToFile(ArrayList<Color> MOOD, int HEIGHT, File OUTPUT) throws IOException {
        BufferedImage bar = toBufferedImage(MOOD, HEIGHT);
        ImageIO.write(bar, "png", OUTPUT);
        Logger.getLogger(MoodbarAdapter.class.getName()).log(Level.INFO, "Mood Image Ready!");
    }

//    public static Vector<Color> getMoodbar() {
//        return MoodbarAdapter.moodbar;
//    }

}
