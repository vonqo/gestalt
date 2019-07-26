package mn.von.gestalt.moodbar;

import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private static ProcessBuilder processBuilder;
    private static Process process;

    private static Vector<Color> moodbar;

    public static Vector<Color> buildMoodbar(String AUDIO_PATH, String OUTPUT) throws IOException {
        moodbar = new Vector<Color>(1000);
        processBuilder = new ProcessBuilder("moodbar", "-o" ,OUTPUT, AUDIO_PATH);
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

        return moodbar;
    }

    private static Color colorize(String COLOR_STR) {
        String[] colors = COLOR_STR.split("\\s+");
        return new Color(
                Integer.parseInt(colors[0]),
                Integer.parseInt(colors[1]),
                Integer.parseInt(colors[2])
        );
    }

    public static BufferedImage convertToBufferedImage() {
        BufferedImage bar = new BufferedImage(moodbar.size(), 150, BufferedImage.TYPE_INT_RGB);
        Graphics ctx = bar.getGraphics();
        Iterator<Color> itr = moodbar.iterator();
        for (int i = 0; itr.hasNext(); i++) {
            ctx.drawRect(i, 0, 1, 150);
            ctx.setColor(itr.next());
        }
        ctx.dispose();
        return bar;
    }

    public static void moodToImage(Vector<Color> MOOD, int HEIGHT, File OUTPUT) throws IOException {
        BufferedImage bar = new BufferedImage(MOOD.size(),HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics ctx = bar.getGraphics();

        Iterator<Color> itr = MOOD.iterator();
        for(int i = 0; itr.hasNext(); i++){
            ctx.drawRect(i, 0, 1, HEIGHT);
            ctx.setColor(itr.next());
        }
        ctx.dispose();
        ImageIO.write(bar, "png", OUTPUT);
        Logger.getLogger(MoodbarAdapter.class.getName()).log(Level.INFO, "Mood Image Ready!");
    }

    public static Vector<Color> getMoodbar() {
        return MoodbarAdapter.moodbar;
    }

}
