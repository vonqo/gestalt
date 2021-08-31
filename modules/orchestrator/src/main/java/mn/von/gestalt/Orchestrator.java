package mn.von.gestalt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.utility.annotation.LoadOrchestrator;
import mn.von.gestalt.utility.config.dto.ParamDto;
import mn.von.gestalt.utility.grimoire.AudioUtils;
import mn.von.gestalt.utility.grimoire.ImageSupporter;
import mn.von.gestalt.utility.grimoire.ImageTransformer;
import mn.von.gestalt.utility.grimoire.NoiseGenerator;
import mn.von.gestalt.zenphoton.HQZUtils;
import mn.von.gestalt.zenphoton.dto.ZObject;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;

import java.util.ArrayList;

/**
 This is the place where all magic works

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Orchestrator {

    /* ============================================================================================ */
    /* ============================================================================================ */
    private enum ExportTypes {
        VANILLA,
        COLLECTION,
        BUBBLE_BAR_2DRT,
        WHIRLWIND_2DRT,
        DRAWING_2DRT,
        CARDIAC,
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    @LoadOrchestrator
    public static void main(String args[]) {

        ParamDto paramDto = Config.loadConfig();
        String type = paramDto.getExportType();

        if(type.equals(ExportTypes.VANILLA.name())) {

            int fontSize = 28;
            int moodbarWidth = 1000;
            int moodbarHeight = 150;

            renderVanillaMoodbars(paramDto, fontSize, moodbarHeight, moodbarWidth);

        } else if(type.equals(ExportTypes.COLLECTION.name())) {

            renderCollection(paramDto);

        } else if(type.equals(ExportTypes.BUBBLE_BAR_2DRT.name())) {



        } else if(type.equals(ExportTypes.WHIRLWIND_2DRT.name())) {



        } else if(type.equals(ExportTypes.DRAWING_2DRT.name())) {

            renderZenphotonDrawing();

        } else if(type.equals(ExportTypes.CARDIAC.name())) {

        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderVanillaMoodbars(ParamDto parameter, int fontSize, int height, int width) {

        ArrayList<String> audioFiles = parameter.getAudioFile();
        ArrayList<String> displayTexts = parameter.getDisplayText();

        String filename = "mood";
        String testPath = Config.RESOURCE_DIR;
        try{
            ArrayList<BufferedImage> moodbars = new ArrayList<>();

            for (String audioFile : audioFiles) {
                ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath + audioFile, testPath + "/tmp_moodbar");
                BufferedImage scaledImage = ImageTransformer.scaleImage(MoodbarAdapter.toBufferedImage(moodbar, height), width, height);
                moodbars.add(scaledImage);
            }

            ImageSupporter.setBackgroundColor(Color.WHITE);
            ImageSupporter.setFontColor(Color.BLACK);
            ImageSupporter.setFontSize(fontSize);
            ImageSupporter.setFontName("Roboto Mono");
            BufferedImage image = new LunarTear().vanilla4Bar(moodbars, displayTexts, height, width, fontSize);

            ImageIO.write(
                image, Config.OUTPUT_IMAGE_FORMAT,
                new File(testPath+"/"+filename+"_bars."+ Config.OUTPUT_IMAGE_FORMAT)
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderCollection(ParamDto parameter) {
        String sogname = parameter.getAudioFile().get(0);
        String displayText = parameter.getDisplayText().get(0);
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
            ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath + sogname + ".mp3", testPath + "/bar");
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

            ImageSupporter.setFontSize(38);
            BufferedImage hanzBar = ImageTransformer.hanzMoodbar(spectrumizer.getDATA(), moodbar, 40, "無謀侍"); // balmad samurai
            hanzBar = ImageSupporter.addTitle(hanzBar, displayText);

            ImageIO.write(bubbleBar, Config.OUTPUT_IMAGE_FORMAT,
                    new File(testPath+"/"+sogname+"_bubble."+ Config.OUTPUT_IMAGE_FORMAT)
            );

//            ImageIO.write(hanzBar, Config.OUTPUT_IMAGE_FORMAT,
//                    new File(testPath+"/"+sogname+"_hanz."+ Config.OUTPUT_IMAGE_FORMAT)
//            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static  void renderNoise() {
        String songname = "1982";
        String displayText = "\"Улаан Бүч\" Чуулга - Угтагчийн Дуу (1982)";
        String pathMp3 = Config.RESOURCE_DIR+songname+".mp3";
        String pathWav = Config.RESOURCE_DIR+songname+".wav";
        double audioDuration = 0;

        try {
            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
            audioDuration = AudioUtils.getDuration(pathWav);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(Config.RESOURCE_DIR+songname+".mp3",Config.RESOURCE_DIR+"/bar");
            BufferedImage image = NoiseGenerator.testNoise(moodbar);
            BufferedImage outputImage = ImageTransformer.scaleImage(image, 1500, 1500);
            ImageIO.write(
                outputImage, Config.OUTPUT_IMAGE_FORMAT,
                new File(Config.RESOURCE_DIR+"/noise_test."+ Config.OUTPUT_IMAGE_FORMAT)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderZenphotonDrawing() {
        String songname = "turing";
        String testPath = Config.RESOURCE_DIR;
        String pathMp3 = testPath+songname+".mp3";
        String pathWav = testPath+songname+".wav";
        double audioDuration = 0;

        try {
            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
            audioDuration = AudioUtils.getDuration(pathWav);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath+songname+".mp3",testPath+"/bar");

            final String objectFile = Config.RESOURCE_DIR+"data_1133295"+".json";
            Gson gson = new Gson();
            BufferedReader br = new BufferedReader(new FileReader(objectFile));
            ArrayList<ArrayList<Integer>> objects = gson.fromJson(br, new TypeToken<ArrayList<ArrayList<Integer>>>(){}.getType());
            ArrayList<ZObject> zObjects = new ArrayList<ZObject>();

            for(int i = 0; i < objects.size(); i++) {
                ZObject obj = new ZObject();
                zObjects.add(HQZUtils.buildObject(
                    objects.get(i).get(0),
                    objects.get(i).get(1),
                    objects.get(i).get(2),
                    objects.get(i).get(3),
                    objects.get(i).get(4)
                ));
            }

            LunarTearHqz hqz = new LunarTearHqz();
            File outputFile = new File(Config.RESOURCE_DIR+"/"+songname+"_drawing."+ Config.OUTPUT_IMAGE_FORMAT);
            hqz.buildDrawing(800*2, 670*2, zObjects, moodbar, 20000, outputFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderZenphoton() {
        // String songname = "fur_elise";
        String songname = "laura";
        String displayText = "renderZenphoton";
        String testPath = Config.RESOURCE_DIR;
        String pathMp3 = testPath+songname+".mp3";
        String pathWav = testPath+songname+".wav";
        double audioDuration = 0;

        try {
            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
            audioDuration = AudioUtils.getDuration(pathWav);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath+songname+".mp3",testPath+"/bar");
            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
            spectrumizer.applyMoodbar(moodbar);
            spectrumizer.build();

            int ray = 2500000;
            // int ray = 5000;
            File outputFile = new File(Config.RESOURCE_DIR+"/"+songname+"_"+ray+"."+ Config.OUTPUT_IMAGE_FORMAT);
            LunarTearHqz hqz = new LunarTearHqz();
            // hqz.build(LunarTearHqz.Types.BUBBLE2_PRINTABLE, moodbar, spectrumizer.getDATA(), ray, outputFile, audioDuration);

            hqz.build(LunarTearHqz.Types.BUBBLE2_PRINTABLE, moodbar, spectrumizer.getDATA(), ray, outputFile, audioDuration);

//            BufferedImage img = ImageIO.read(outputFile);
//            ImageSupporter.setBackgroundColor(Color.BLACK);
//            ImageSupporter.setFontColor(Color.WHITE);
//            ImageSupporter.setFontSize(32);
//            ImageSupporter.setFontName("Roboto Mono");
//            ImageIO.write(
//                    ImageSupporter.addTitle(img, displayText), Config.OUTPUT_IMAGE_FORMAT, outputFile
//            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderZenphotonFrames() {
        String sogname = "folk";
        String testPath = Config.RESOURCE_DIR;
        String pathMp3 = testPath+sogname+".mp3";
        String pathWav = testPath+sogname+".wav";
        double audioDuration = 0;

        try {
            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
            audioDuration = AudioUtils.getDuration(pathWav);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath+sogname+".mp3",testPath+"/bar");
            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
            spectrumizer.applyMoodbar(moodbar);
            spectrumizer.build();

            int ray = 2500000;
            LunarTearHqz hqz = new LunarTearHqz();

            hqz.buildFrames(LunarTearHqz.Types.TORNADO, moodbar, spectrumizer.getDATA(), ray, audioDuration, 30, "folk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
