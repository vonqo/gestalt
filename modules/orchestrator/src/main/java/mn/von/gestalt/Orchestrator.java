package mn.von.gestalt;

import com.google.gson.Gson;
import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;
import mn.von.gestalt.utility.grimoire.AudioUtils;
import mn.von.gestalt.utility.grimoire.ImageTransformer;
import mn.von.gestalt.utility.grimoire.LunarTear;
import mn.von.gestalt.utility.grimoire.PhysicsUtils;
import mn.von.gestalt.zenphoton.HQZAdapter;
import mn.von.gestalt.zenphoton.dto.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
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
        PhysicsUtils.simulateB2T();

//        renderPhotonbar();
//        renderCollection();
//        renderVanillaMoodbars();
    }

    private static void renderPhotonbar() {
        String sogname = "heretic";
        String displayText = "Slipknot - The Heretic Anthem";
        String testPath = "/Users/von/Desktop/mood_test/";
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
            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
            spectrumizer.applyMoodbar(moodbar);
            spectrumizer.build();

            // ==============================================================
            // LunarTear.RGB2WV_Generate_LossyExhaustingTable();
            int ray = 5000000;
            File outputFile = new File(sogname+"_"+ray+".png");
            HQZAdapter hqz = new HQZAdapter();
            hqz.buildHQZ(HQZAdapter.Types.TORNADO, moodbar, spectrumizer.getDATA(), ray, outputFile);
            BufferedImage img = ImageIO.read(outputFile);
            LunarTear.setBackgroundColor(Color.BLACK);
            LunarTear.setFontColor(Color.WHITE);
            LunarTear.setFontSize(55);
            LunarTear.setFontName("Roboto Mono");
            ImageIO.write(
                    LunarTear.addTitle(img, displayText), "png", outputFile
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderVanillaMoodbars() {
        String sogname = "emil";
        String testPath = "/Users/von/Desktop/mood_test/tsatsral/";
        try{
            Vector<Color> moodbar1 = MoodbarAdapter.buildMoodbar(testPath+"az_jargaltai_tugsdug.mp3",testPath+"/bar1");
            Vector<Color> moodbar2 = MoodbarAdapter.buildMoodbar(testPath+"minii_nirvana.mp3",testPath+"/bar2");
            Vector<Color> moodbar3 = MoodbarAdapter.buildMoodbar(testPath+"setgel_hudulnu.mp3",testPath+"/bar3");
            ArrayList<BufferedImage> moodbarList = new ArrayList<BufferedImage>();
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar1, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar2, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar3, 150));
            ArrayList<String> names = new ArrayList<String>();
            names.add("NISVANIS - Аз жаргалтай төгсдөг");
            names.add("NISVANIS - Миний Нирвана");
            names.add("NISVANIS - Сэтгэл хөдөлнө");

            LunarTear.setBackgroundColor(Color.WHITE);
            LunarTear.setFontColor(Color.BLACK);
            LunarTear.setFontSize(28);
            LunarTear.setFontName("Roboto Mono");
            BufferedImage moodbars = LunarTear.vanilla4Bar(moodbarList, names);

            // ==================== LOGO MARK =================== //
            LunarTear.setFontSize(28);
            LunarTear.setFontColor(Color.black);
            LunarTear.setFontName("Ubuntu");
            moodbars = LunarTear.addMark(moodbars, "# Gereltuul Art & Music Fest vol5 ", 50);
            // ==================== LOGO MARK - END ============= //

            ImageIO.write(
                    moodbars, "png",
                    new File(testPath+"/"+sogname+"_bars.png")
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void renderCollection() {
        String sogname = "heretic";
        String displayText = "Slipknot - The Heretic Anthem";
        String footerText = "# Gereltuul Art & Music Fest vol5 ";
        String testPath = "/Users/von/Desktop/mood_test/";
        String pathMp3 = testPath+sogname+".mp3";
        String pathWav = testPath+sogname+".wav";
        try {
            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            Vector<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath + sogname + ".mp3", testPath + "/bar");
            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
            spectrumizer.applyMoodbar(moodbar);
            spectrumizer.build();

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
            LunarTear.setFontSize(32);
            BufferedImage lunarTear = LunarTear.MoodbarAndSpectogramCollection(
                    spectrumizer.asBufferedImage(),
                    spectrumizer.asBufferedMoodbar(),
                    MoodbarAdapter.toBufferedImage(moodbar, 150),
                    circle, circleMood,
                    displayText
            );
            // ==================== LOGO MARK =================== //
//            LunarTear.setFontSize(28);
//            LunarTear.setFontColor(Color.black);
//            LunarTear.setFontName("Ubuntu");
//            lunarTear = LunarTear.addMark(lunarTear, footerText, 0);
            // ==================== LOGO MARK - END ============= //
            ImageIO.write(lunarTear, "png", new File(testPath+"/"+sogname+"_collection.png"));


            LunarTear.setBackgroundColor(Color.BLACK);
            LunarTear.setFontColor(Color.WHITE);
            BufferedImage bubble = ImageTransformer.bubbleMoodbar(spectrumizer.getDATA(), moodbar, 50);
            bubble = LunarTear.addTitle(bubble, displayText);
            // ==================== LOGO MARK =================== //
//            LunarTear.setFontSize(28);
//            LunarTear.setFontColor(Color.black);
//            LunarTear.setFontName("Ubuntu");
//            bubble = LunarTear.addMark(bubble, footerText, 0);
            // ==================== LOGO MARK - END ============= //


            ImageIO.write(bubble,"png",
                    new File(testPath+"/"+sogname+"_bubble.png")
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
