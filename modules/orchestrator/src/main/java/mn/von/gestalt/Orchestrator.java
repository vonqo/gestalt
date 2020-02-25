package mn.von.gestalt;

import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.utility.annotation.LoadOrchestrator;
import mn.von.gestalt.utility.grimoire.AudioUtils;
import mn.von.gestalt.utility.grimoire.ImageSupporter;
import mn.von.gestalt.utility.grimoire.ImageTransformer;

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

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Orchestrator {

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        nu.pattern.OpenCV.loadShared();
//    }

    @LoadOrchestrator
    public static void main(String args[]) {
        Config.loadConfig();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores);
//        renderZenphoton();
//        renderCollection();
//        renderVanillaMoodbars();
    }

    private static void renderZenphoton() {
        String sogname = "fall";
        String displayText = "Even Tide - Fall";
        String testPath = Config.RESOURCE_DIR;
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

            int ray = 5000;
            File outputFile = new File(sogname+"_"+ray+"."+ Config.OUTPUT_IMAGE_FORMAT);
            LunarTearHqz hqz = new LunarTearHqz();
            hqz.build(LunarTearHqz.Types.TORNADO, moodbar, spectrumizer.getDATA(), ray, outputFile);
            BufferedImage img = ImageIO.read(outputFile);
            ImageSupporter.setBackgroundColor(Color.BLACK);
            ImageSupporter.setFontColor(Color.WHITE);
            ImageSupporter.setFontSize(55);
            ImageSupporter.setFontName("Roboto Mono");
            ImageIO.write(
                    ImageSupporter.addTitle(img, displayText), Config.OUTPUT_IMAGE_FORMAT, outputFile
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderVanillaMoodbars() {
        String filename = "col3";
        String testPath = Config.RESOURCE_DIR;
        try{
            Vector<Color> moodbar1 = MoodbarAdapter.buildMoodbar(testPath+"lemons.mp3",testPath+"/bar1");
            Vector<Color> moodbar2 = MoodbarAdapter.buildMoodbar(testPath+"molboyz.mp3",testPath+"/bar2");
            Vector<Color> moodbar3 = MoodbarAdapter.buildMoodbar(testPath+"haraatsai.mp3",testPath+"/bar3");
            Vector<Color> moodbar4 = MoodbarAdapter.buildMoodbar(testPath+"huduu.mp3",testPath+"/bar3");
            ArrayList<BufferedImage> moodbarList = new ArrayList<BufferedImage>();
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar1, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar2, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar3, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar4, 150));
            ArrayList<String> names = new ArrayList<String>();
            names.add("The Lemons - Сүүлчийн уянга");
            names.add("MOLBOYZ - Өвлийн уянга");
            names.add("Хараацай - Усны гуталтай залуу");
            names.add("Соёл Эрдэнэ - Хөдөөгийн сайхан талд зорино");

            ImageSupporter.setBackgroundColor(Color.WHITE);
            ImageSupporter.setFontColor(Color.BLACK);
            ImageSupporter.setFontSize(28);
            ImageSupporter.setFontName("Roboto Mono");
            BufferedImage moodbars = new LunarTear().vanilla4Bar(moodbarList, names);

            ImageIO.write(
                    moodbars, Config.OUTPUT_IMAGE_FORMAT,
                    new File(testPath+"/"+filename+"_bars."+ Config.OUTPUT_IMAGE_FORMAT)
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void renderCollection() {
        String sogname = "fall";
        String displayText = "Even Tide - Fall";
        String testPath = Config.RESOURCE_DIR;
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

            ImageSupporter.setBackgroundColor(Color.WHITE);
            ImageSupporter.setFontColor(Color.BLACK);
            ImageSupporter.setFontSize(32);

            LunarTear lunarTear = new LunarTear();

            BufferedImage collectionImage = lunarTear.moodbarAndSpectogramCollection(
                    spectrumizer.asBufferedImage(),
                    spectrumizer.asBufferedMoodbar(),
                    MoodbarAdapter.toBufferedImage(moodbar, 150),
                    circle, circleMood,
                    displayText
            );
            ImageIO.write(collectionImage, Config.OUTPUT_IMAGE_FORMAT, new File(testPath+"/"+sogname+"_collection."+ Config.OUTPUT_IMAGE_FORMAT));

            ImageSupporter.setBackgroundColor(Color.BLACK);
            ImageSupporter.setFontColor(Color.WHITE);
            BufferedImage bubbleBar = ImageTransformer.bubbleMoodbar(spectrumizer.getDATA(), moodbar, 50);
            bubbleBar = ImageSupporter.addTitle(bubbleBar, displayText);

            ImageIO.write(bubbleBar, Config.OUTPUT_IMAGE_FORMAT,
                    new File(testPath+"/"+sogname+"_bubble."+ Config.OUTPUT_IMAGE_FORMAT)
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
