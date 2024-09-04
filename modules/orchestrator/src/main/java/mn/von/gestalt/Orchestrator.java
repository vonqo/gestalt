package mn.von.gestalt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.utility.annotation.LoadOrchestrator;
import mn.von.gestalt.utility.config.dto.AudioDto;
import mn.von.gestalt.utility.config.dto.ParamDto;
import mn.von.gestalt.utility.grimoire.*;
import mn.von.gestalt.utility.config.dto.VideoExportDto;
import mn.von.gestalt.zenphoton.HQZUtils;
import mn.von.gestalt.zenphoton.dto.ZObject;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;

import java.util.ArrayList;

/**
 This is the place where all magic works

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/PunkOwl/gestalt]
 **/
public class Orchestrator {

    /* ============================================================================================ */
    /* ============================================================================================ */
    private enum ExportTypes {
        ECLIPSE,
        VANILLA,
        COLLECTION,
        BUBBLE_BAR_2DRT,
        WHIRLWIND_2DRT,
        DRAWING_2DRT,
        CARDIAC,
        MOOD_RAIN,
        MEDIA_ART_2022,
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    @LoadOrchestrator
    public static void main(String[] args) {

        ParamDto paramDto = Config.loadConfig();
        VideoExportDto videoExportDto = paramDto.getVideoExportDto();

        if(videoExportDto.isVideoExport()) {
            for(AudioDto audio : paramDto.getAudioDtos()) {
                String type = audio.getExportType();
                if(type.equals(ExportTypes.DRAWING_2DRT.name())) {
                    renderDrawingZenphotonFrames(audio, videoExportDto);
                }
            }
        } else {
            for(AudioDto audio : paramDto.getAudioDtos()) {
                String type = audio.getExportType();
                if(type.equals(ExportTypes.VANILLA.name())) {

                    int fontSize = 28;
                    int moodbarWidth = 1000;
                    int moodbarHeight = 110;

                    renderVanillaMoodbars(audio, fontSize, moodbarHeight, moodbarWidth);

                } else if(type.equals(ExportTypes.COLLECTION.name())) {

                    renderCollection(audio);

                } else if(type.equals(ExportTypes.BUBBLE_BAR_2DRT.name())) {

                    renderBubbleBarZenphoton(audio);

                } else if(type.equals(ExportTypes.WHIRLWIND_2DRT.name())) {

                    renderWhirlwindZenphoton(audio);

                } else if(type.equals(ExportTypes.DRAWING_2DRT.name())) {

                    renderZenphotonDrawing(audio.getExtraDataFile(), audio.getRay());

                } else if(type.equals(ExportTypes.CARDIAC.name())) {

                    renderCardiacZenphoton(audio);
                  
                } else if(type.equals(ExportTypes.MOOD_RAIN.name())) {

                    renderRain(audio);

                } else if(type.equals(ExportTypes.ECLIPSE.name())) {

                    renderEclipse(audio);
                }
            }
        }
    }


    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderVanillaMoodbars(AudioDto audio, int fontSize, int height, int width) {
        ArrayList<String> audioFiles = audio.getAudioFile();
        ArrayList<String> displayTexts = audio.getDisplayText();
        String filename = audio.getAudioFile().get(0) + "_" + audio.getAudioFile().size();
        String testPath = Config.RESOURCE_DIR;
        try{
            ArrayList<BufferedImage> moodbars = new ArrayList<>();

            for (String audioFile : audioFiles) {
                System.out.println(audioFile);
                ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath + audioFile + ".mp3", testPath + "/tmp_moodbar");
                FileUtils.moodbarToFile(moodbar, testPath + audioFile + ".txt");
                BufferedImage scaledImage = ImageTransformer.scaleImage(MoodbarAdapter.toBufferedImage(moodbar, height), width, height);
                moodbars.add(scaledImage);
            }

            // ImageSupporter.setBackgroundColor(new Color(255,255,255, 0));
            ImageSupporter.setBackgroundColor(new Color(0,0,0, 255));
            ImageSupporter.setFontColor(Color.WHITE);
            ImageSupporter.setFontSize(fontSize);
            ImageSupporter.setFontName("JetBrains Mono");

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
    private static void renderEclipse(AudioDto audio) {
        String sogname = audio.getAudioFile().get(0);
        String displayText = audio.getDisplayText().get(0);
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
            // Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);

//            // idk
//            int len = spectrumizer.getDATA().length;
//            double[] data = new double[len];
//            System.out.println(len);
//
//            for(int i = 0; i < len; i++) {
//                double sum = 0;
//                for(int e = 0; e < spectrumizer.getDATA()[i].length; e++) {
//                    sum += spectrumizer.getDATA()[i][e];
//                }
//                data[i] = sum;
//            }

            final int innerCircleSize = 700;
            final int outerCircleSize = 2000;

            ArrayList<Color> colors1 = MoodbarAdapter.groupColor(moodbar, 75);
            ArrayList<Color> colors2 = MoodbarAdapter.groupColor(moodbar, 25);
            BufferedImage moodbarGradient1 = MoodbarAdapter.toBufferedImage(colors1, 700, 5000);
            BufferedImage moodbarGradient2 = MoodbarAdapter.toBufferedImage(colors2, 700, 5000);

            // ======================================================= //
            BufferedImage moodbarTransparent1 = ImageTransformer.gradientTransparentVertical(
                    moodbarGradient1,
                    0.0f,
                    1.0f,
                    ImageTransformer.EASING.Cubic
            );
            BufferedImage layer1 = ImageTransformer.rectangularToPolarCoordinate(
                    moodbarTransparent1,
                    innerCircleSize + (int)((outerCircleSize - innerCircleSize) * 0.13f),
                    innerCircleSize
            );
            RescaleOp rescaleOp = new RescaleOp(1.9f, 10, null);
            rescaleOp.filter(layer1, layer1);

            // ======================================================= //
            BufferedImage moodbarTransparent2 = ImageTransformer.gradientTransparentVertical(
                    moodbarGradient2,
                    0.0f,
                    1.0f,
                    ImageTransformer.EASING.Sine
            );
            BufferedImage layer2 = ImageTransformer.rectangularToPolarCoordinate(
                    moodbarTransparent2,
                    outerCircleSize,
                    innerCircleSize
            );
            rescaleOp = new RescaleOp(1.5f, 0, null);
            rescaleOp.filter(layer2, layer2);
            // ======================================================= //

            System.out.println("layer1:[" + layer1.getHeight() +"]["+ layer1.getWidth() + "]");
            System.out.println("layer2:[" + layer2.getHeight() +"]["+ layer2.getWidth() + "]");

            // gg
            BufferedImage image = new BufferedImage((int)(outerCircleSize * 1.05), (int)(outerCircleSize * 1.2), BufferedImage.TYPE_INT_ARGB);
            Graphics2D ctx = (Graphics2D) image.getGraphics();
            ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // ctx.setComposite(AlphaComposite.Clear);
            ctx.setPaint (new Color(232, 232, 232));
            ctx.fillRect(0,0,image.getWidth(),image.getHeight());
            ctx.setComposite(AlphaComposite.SrcOver);

            int centerX = image.getWidth() / 2;
            int centerY = image.getHeight() / 2;

            ctx.drawImage(layer2, centerX - layer2.getWidth()/2, centerY - layer2.getHeight()/2, null);
            ctx.drawImage(layer1, centerX - layer1.getWidth()/2, centerY - layer1.getHeight()/2, null);

            Ellipse2D.Double circle = new Ellipse2D.Double(
                    centerX - innerCircleSize/2 - 4,
                    centerY - innerCircleSize/2 - 4,
                    innerCircleSize + 8,
                    innerCircleSize + 8
            );
            ctx.setColor(Color.BLACK);
            ctx.fill(circle);
            ctx.dispose();

            ImageSupporter.setFontColor(Color.BLACK);
            ImageSupporter.setFontSize(72);
            image = ImageSupporter.addTitleOver(image, displayText, 100, 70);
            // image = ImageSupporter.addMark(image, 50);

            ImageIO.write(layer1, Config.OUTPUT_IMAGE_FORMAT,
                new File(testPath+"/"+sogname+"_1."+ Config.OUTPUT_IMAGE_FORMAT)
            );
            ImageIO.write(layer2, Config.OUTPUT_IMAGE_FORMAT,
                new File(testPath+"/"+sogname+"_2."+ Config.OUTPUT_IMAGE_FORMAT)
            );
            ImageIO.write(image, Config.OUTPUT_IMAGE_FORMAT,
                    new File(testPath+"/"+sogname+"_final."+ Config.OUTPUT_IMAGE_FORMAT)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderCollection(AudioDto audio) {
        String sogname = audio.getAudioFile().get(0);
        String displayText = audio.getDisplayText().get(0);
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

//            ImageSupporter.setBackgroundColor(Color.BLACK);
//            ImageSupporter.setFontColor(Color.WHITE);

            BufferedImage circle = ImageTransformer.rectangularToPolarCoordinate(
                spectrumizer.asBufferedImage(),
                1000,160
            );

            BufferedImage circleMood = ImageTransformer.rectangularToPolarCoordinate(
                spectrumizer.asBufferedMoodbar(),
                1000,100
            );

            ImageSupporter.setFontSize(32);

            ImageSupporter.setBackgroundColor(Color.WHITE);
            ImageSupporter.setFontColor(Color.BLACK);
            ImageSupporter.setFontSize(28);

            BufferedImage circle2 = ImageSupporter.addTitle(circle, displayText);
            ImageIO.write(circle2, Config.OUTPUT_IMAGE_FORMAT, new File(testPath+"/"+sogname+"_collection_circle."+ Config.OUTPUT_IMAGE_FORMAT));

            // circle = ImageTransformer.invert(circle);
            // BufferedImage circle2 = ImageSupporter.addTitle(circle, displayText);
            ImageTransformer.invert(circle2);
            ImageIO.write(circle2, Config.OUTPUT_IMAGE_FORMAT, new File(testPath+"/"+sogname+"_circle."+ Config.OUTPUT_IMAGE_FORMAT));

            ImageSupporter.setBackgroundColor(Color.WHITE);
            ImageSupporter.setFontColor(Color.BLACK);
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
    private static void renderRain(AudioDto audio) {
        String sogname = audio.getAudioFile().get(0);
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

//            LunarTear lunarTear = new LunarTear();
//            BufferedImage img = lunarTear.moodbarRain(spectrumizer.asBufferedMoodbar(), MoodbarAdapter.toBufferedImage(moodbar, 100));

            ImageIO.write(spectrumizer.asBufferedMoodbar(), Config.OUTPUT_IMAGE_FORMAT,
                    new File(testPath+"/"+sogname+"_moodrain."+ Config.OUTPUT_IMAGE_FORMAT)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderNoise() {
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
    private static void renderZenphotonDrawing(String extraDataFile, int ray) {
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

            final String objectFile = Config.RESOURCE_DIR + extraDataFile;
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
            hqz.buildDrawing(800*2, 670*2, zObjects, moodbar, ray, outputFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderBubbleBarZenphoton(AudioDto audio) {
        String path = Config.RESOURCE_DIR;

        for(int i = 0; i < audio.getAudioFile().size(); i++) {
            String songname = audio.getAudioFile().get(i);
            String pathMp3 = path + songname + ".mp3";
            String pathWav = path + songname + ".wav";
            double audioDuration = 0;

            System.out.println(pathMp3);
            System.out.println(pathWav);

            try {
                AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
                audioDuration = AudioUtils.getDuration(pathWav);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(path+songname+".mp3",path+"/bar");
                Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
                spectrumizer.applyMoodbar(moodbar);
                spectrumizer.build();

                int ray = audio.getRay();
                File outputFile = new File(Config.RESOURCE_DIR+"/"+songname+"_"+ray+"."+ Config.OUTPUT_IMAGE_FORMAT);
                LunarTearHqz hqz = new LunarTearHqz();
                hqz.build(LunarTearHqz.Types.BUBBLE2_PRINTABLE, moodbar, spectrumizer.getDATA(), ray, outputFile, audioDuration);

                BufferedImage img = ImageIO.read(outputFile);
                ImageSupporter.setFontName("JetBrains Mono");
                ImageSupporter.setBackgroundColor(Color.BLACK);
                ImageSupporter.setFontColor(Color.WHITE);
                ImageSupporter.setFontSize(120);

                img = ImageSupporter.addTitleOver(img, audio.getDisplayText().get(i), 260, 180);

                if(audio.isHasBanner()) {
                    BufferedImage bannerImg = ImageIO.read(new File("gestalt_banner.png"));
                    img = ImageSupporter.addMarkOver(img, bannerImg, 8350 - bannerImg.getHeight(), 5906-bannerImg.getWidth()-140);
                }

                ImageIO.write(img, Config.OUTPUT_IMAGE_FORMAT, outputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderMediaArt(AudioDto audio) {
        String path = Config.RESOURCE_DIR;

        for(int i = 0; i < audio.getAudioFile().size(); i++) {
            String songname = audio.getAudioFile().get(i);
            String pathMp3 = path + songname + ".mp3";
            String pathWav = path + songname + ".wav";
            double audioDuration = 0;

            System.out.println(pathMp3);
            System.out.println(pathWav);

            try {
                AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
                audioDuration = AudioUtils.getDuration(pathWav);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(path+songname+".mp3",path+"/bar");
                Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
                spectrumizer.applyMoodbar(moodbar);
                spectrumizer.build();

                int ray = audio.getRay();
                File outputFile = new File(Config.RESOURCE_DIR+"/"+songname+"_"+ray+"."+ Config.OUTPUT_IMAGE_FORMAT);
                LunarTearHqz hqz = new LunarTearHqz();
                hqz.build(LunarTearHqz.Types.MEDIA_ART, moodbar, spectrumizer.getDATA(), ray, outputFile, audioDuration);

                BufferedImage img = ImageIO.read(outputFile);
                ImageSupporter.setBackgroundColor(Color.BLACK);
                ImageSupporter.setFontColor(Color.WHITE);
                ImageSupporter.setFontSize(28);

                img = ImageSupporter.addTitleOver(img, audio.getDisplayText().get(i), 50, 108);

//                if(audio.isHasBanner()) {
//                    BufferedImage bannerImg = ImageIO.read(new File("gestalt_banner.png"));
//                    img = ImageSupporter.addMarkOver(img, bannerImg, 8350 - bannerImg.getHeight(), 5906-bannerImg.getWidth()-140);
//                }

                ImageIO.write(img, Config.OUTPUT_IMAGE_FORMAT, outputFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderCardiacZenphoton(AudioDto audio) {
        String path = Config.RESOURCE_DIR;

        String songname = audio.getAudioFile().get(0);
        String pathMp3 = path + songname + ".mp3";
        String pathWav = path + songname + ".wav";
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
            ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(path+songname+".mp3",path+"/bar");
            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
            spectrumizer.applyMoodbar(moodbar);
            spectrumizer.build();

            int ray = audio.getRay();
            File outputFile = new File(Config.RESOURCE_DIR+"/"+songname+"_"+ray+"."+ Config.OUTPUT_IMAGE_FORMAT);
            LunarTearHqz hqz = new LunarTearHqz();
            hqz.build(LunarTearHqz.Types.CARDIAC, moodbar, spectrumizer.getDATA(), ray, outputFile, audioDuration);

            BufferedImage img = ImageIO.read(outputFile);
            ImageSupporter.setBackgroundColor(Color.BLACK);
            ImageSupporter.setFontColor(Color.WHITE);
            ImageSupporter.setFontSize(32);
            ImageIO.write(
                ImageSupporter.addTitle(img, audio.getDisplayText().get(0)),
                Config.OUTPUT_IMAGE_FORMAT,
                outputFile
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderWhirlwindZenphoton(AudioDto audio) {
        String path = Config.RESOURCE_DIR;

        for(int i = 0; i < audio.getAudioFile().size(); i++) {
            String songname = audio.getAudioFile().get(i);
            String pathMp3 = path + songname + ".mp3";
            String pathWav = path + songname + ".wav";
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
                ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(path+songname+".mp3",path+"/bar");
                Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
                spectrumizer.applyMoodbar(moodbar);
                spectrumizer.build();

                BufferedImage moodbarImg = MoodbarAdapter.toBufferedImage(moodbar, 75);
                BufferedImage spectrum = spectrumizer.asBufferedMoodbar();

                LunarTearHqz hqz = new LunarTearHqz();
                int ray = audio.getRay();
                File outputFile = new File(Config.RESOURCE_DIR+"/"+songname+"_"+ray+"."+ Config.OUTPUT_IMAGE_FORMAT);
                hqz.build(LunarTearHqz.Types.TORNADO, moodbar, spectrumizer.getDATA(), ray, outputFile, audioDuration);

                BufferedImage tornadoHqz = ImageIO.read(outputFile);

                LunarTear lunarTear = new LunarTear();
                BufferedImage img = lunarTear.wirldwind(moodbarImg, spectrum, tornadoHqz);

                ImageSupporter.setBackgroundColor(Color.BLACK);
                ImageSupporter.setFontColor(Color.WHITE);
                ImageSupporter.setFontSize(32);
                ImageIO.write(
                        ImageSupporter.addTitle(img, audio.getDisplayText().get(i)),
                        Config.OUTPUT_IMAGE_FORMAT,
                        outputFile
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* ============================================================================================ */
    /* ============================================================================================ */
    private static void renderDrawingZenphotonFrames(AudioDto audioDto, VideoExportDto videoExportDto) {
        String sogname = audioDto.getAudioFile().get(0);
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
            ArrayList<Color> moodbar = MoodbarAdapter.buildMoodbar(pathMp3,testPath+"/bar");
            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
            spectrumizer.applyMoodbar(moodbar);
            spectrumizer.build();

            LunarTearHqz2 hqz = new LunarTearHqz2();
            hqz.buildFrames(
                    LunarTearHqz2.Types.DRAWING,
                    moodbar, spectrumizer.getDATA(), audioDuration,
                    audioDto, videoExportDto
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
