package mn.von.gestalt;

import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;
import mn.von.gestalt.utility.grimoire.AudioUtils;
import mn.von.gestalt.utility.grimoire.ImageTransformer;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
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



        String testPath = "/home/enkh-amar/Desktop/mood_test";
        String pathMp3 = testPath+"/yuno/toosontsor.mp3";
        String pathWav = testPath+"/toosontsor.wav";
//        try {
//            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        try {
            // initialize moodbar
//            Vector<Color> moodbar = MoodbarAdapter.buildMoodbar(pathMp3,"/home/enkh-amar/Desktop/mood_test/bar");
//            MoodbarAdapter.moodToImage(moodbar,150,
//                    new File(testPath+"/moodbar.png"));
//
//            // initialize spectogram
//            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
//
//            // save as black and white
//            spectrumizer.asImageRange(0,spectrumizer.getSize(),
//                    new File(testPath+"/spectogram.png"));
//
//            // save as moodbar color
//            spectrumizer.ApplyMoodbar(moodbar);
//            spectrumizer.asImageRange(0,spectrumizer.getSize(),
//                    new File(testPath+"/spectogram_color.png"));

            // save with rotation
            ImageTransformer.circularTransform(
                    ImageIO.read(new File(testPath+"/moodbar.png")),
                    1000,300,
                    new File((testPath+"/circle.png"))
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
