package mn.von.gestalt;

import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;
import mn.von.gestalt.utility.grimoire.AudioUtils;
import mn.von.gestalt.utility.grimoire.ImageTransformer;
import mn.von.gestalt.utility.grimoire.LunarTear;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 This is the place where all magic works

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Orchestrator {

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        nu.pattern.OpenCV.loadShared();
//    }

    public static void main(String args[]) {



        String testPath = "/home/enkh-amar/Desktop/mood_test/awlnpl/";
        String pathMp3 = testPath+"falling.mp3";
        String pathWav = testPath+"alling.wav";
        try {
            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {

            Vector<Color> moodbar = MoodbarAdapter.buildMoodbar(pathMp3,"/home/enkh-amar/Desktop/mood_test/bar");
            MoodbarAdapter.moodToImage(moodbar,150,
                    new File(testPath+"/moodbar.png"));

            // initialize spectogram
            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
            spectrumizer.applyMoodbar(moodbar);
            spectrumizer.build();

            // save with rotation
            BufferedImage circle = ImageTransformer.rectangularToPolarCoordinate(
                    spectrumizer.asBufferedImage(),
                    1000,100
            );

            BufferedImage circleMood = ImageTransformer.rectangularToPolarCoordinate(
                    spectrumizer.asBufferedMoodbar(),
                    1000,100
            );

            BufferedImage lunarTear = LunarTear.MoodbarAndSpectogramCollection(
                    spectrumizer.asBufferedImage(),
                    spectrumizer.asBufferedMoodbar(),
                    MoodbarAdapter.convertToBufferedImage(),
                    circle, circleMood,
                    "Test Music - Test Title"
            );
            ImageIO.write(lunarTear, "png", new File(testPath+"/collection.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
