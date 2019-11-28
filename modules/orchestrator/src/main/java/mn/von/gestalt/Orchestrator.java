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
import java.util.ArrayList;
import java.util.Vector;

/**
 This is the place where all magic works

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amagit r.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Orchestrator {

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        nu.pattern.OpenCV.loadShared();
//    }

    public static void main(String args[]) {

        String sogname = "games";
        String displayText = "Tessa Violet - Games";
        String testPath = "/home/enkh-amar/Desktop/MUZ/moodbar/";
        String pathMp3 = testPath+sogname+".mp3";
        String pathWav = testPath+sogname+".wav";
        try {
            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Vector<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath+sogname+".mp3",testPath+"/bar");
//            Vector<Color> moodbar1 = MoodbarAdapter.buildMoodbar(testPath+"divine.mp3",testPath+"/bar1");
//            Vector<Color> moodbar2 = MoodbarAdapter.buildMoodbar(testPath+"shootingstar.mp3",testPath+"/bar2");
//            Vector<Color> moodbar3 = MoodbarAdapter.buildMoodbar(testPath+"manaach.mp3",testPath+"/bar3");
//            Vector<Color> moodbar0 = MoodbarAdapter.buildMoodbar(testPath+"agaar.mp3",testPath+"/bar4");
//            ArrayList<BufferedImage> moodbarList = new ArrayList<BufferedImage>();
//            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar0, 150));
//            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar1, 150));
//            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar2, 150));
//            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar3, 150));
//            ArrayList<String> names = new ArrayList<String>();
//            names.add("Бадар-Ууган, Дуламсүрэн - Улаанбаатрын агаар");
//            names.add("Wondha Mountain, Yung Lean - Divine Madness");
//            names.add("ANDROMEDA - Сүүлт од");
//            names.add("Моханик - Манангийн манаач");
//
//            LunarTear.setBackgroundColor(Color.WHITE);
//            LunarTear.setFontColor(Color.BLACK);
//            LunarTear.setFontSize(28);
//            LunarTear.setFontName("Roboto Mono");
//            ImageIO.write(
//                    LunarTear.legacy4Bar(moodbarList, names), "png",
//                    new File(testPath+"/"+sogname+"_bars.png")
//            );



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

            LunarTear.setBackgroundColor(Color.WHITE);
            LunarTear.setFontColor(Color.BLACK);
            LunarTear.setFontSize(38);
            BufferedImage lunarTear = LunarTear.MoodbarAndSpectogramCollection(
                    spectrumizer.asBufferedImage(),
                    spectrumizer.asBufferedMoodbar(),
                    MoodbarAdapter.toBufferedImage(moodbar, 150),
                    circle, circleMood,
                    displayText
            );
            ImageIO.write(lunarTear, "png", new File(testPath+"/"+sogname+"_collection.png"));

            LunarTear.setBackgroundColor(Color.BLACK);
            LunarTear.setFontColor(Color.WHITE);
            BufferedImage bubble = ImageTransformer.bubbleMoodbar(spectrumizer.getDATA(), moodbar, 50);
            ImageIO.write(
                    LunarTear.addTitle(bubble, displayText), "png",
                    new File(testPath+"/"+sogname+"_bubble.png")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
