package mn.von.gestalt;

import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.utility.annotation.LoadOrchestrator;
import mn.von.gestalt.utility.grimoire.AudioUtils;
import mn.von.gestalt.utility.grimoire.ImageSupporter;
import mn.von.gestalt.utility.grimoire.ImageTransformer;
import mn.von.gestalt.utility.grimoire.NoiseGenerator;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 This is the place where all magic works

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Orchestrator {

    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        nu.pattern.OpenCV.loadShared();
    }

    @LoadOrchestrator
    public static void main(String args[]) {
        Config.loadConfig();
//        try {
//            opencvTest();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        renderNoise();
//        renderCollection();
//        renderZenphoton();
//        renderZenphotonFrames();
//        renderVanillaMoodbars();
    }

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

    private static void renderZenphoton() {
        // String songname = "fur_elise";
        String songname = "1982";
        String displayText = "\"Улаан Бүч\" Чуулга - Угтагчийн Дуу (1982)";
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

            // int ray = 2500000;
            int ray = 2500000;
            File outputFile = new File(Config.RESOURCE_DIR+"/"+songname+"_"+ray+"."+ Config.OUTPUT_IMAGE_FORMAT);
            LunarTearHqz hqz = new LunarTearHqz();
            hqz.build(LunarTearHqz.Types.BUBBLE2_PRINTABLE, moodbar, spectrumizer.getDATA(), ray, outputFile, audioDuration);

            BufferedImage img = ImageIO.read(outputFile);
            ImageSupporter.setBackgroundColor(Color.BLACK);
            ImageSupporter.setFontColor(Color.WHITE);
            ImageSupporter.setFontSize(32);
            ImageSupporter.setFontName("Roboto Mono");
            ImageIO.write(
                    ImageSupporter.addTitle(img, displayText), Config.OUTPUT_IMAGE_FORMAT, outputFile
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            hqz.buildFrames(LunarTearHqz.Types.TORNADO_WIDE, moodbar, spectrumizer.getDATA(), ray, audioDuration, 30, "folk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderVanillaMoodbars() {
        String filename = "col3";
        String testPath = Config.RESOURCE_DIR;
        try{
            ArrayList<Color> moodbar1 = MoodbarAdapter.buildMoodbar(testPath+"fire_boroo.mp3",testPath+"/bar1");
            ArrayList<Color> moodbar2 = MoodbarAdapter.buildMoodbar(testPath+"tatar_boroo.mp3",testPath+"/bar2");
            ArrayList<Color> moodbar3 = MoodbarAdapter.buildMoodbar(testPath+"rokitbay_boroo.mp3",testPath+"/bar3");
            ArrayList<Color> moodbar4 = MoodbarAdapter.buildMoodbar(testPath+"guys_boroo.mp3",testPath+"/bar4");
            ArrayList<BufferedImage> moodbarList = new ArrayList<BufferedImage>();
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar1, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar2, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar3, 150));
            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar4, 150));
            ArrayList<String> names = new ArrayList<String>();
            names.add("Fire - Бороонд урсах нулимс");
            names.add("Татар - Бороо");
            names.add("Rokit Bay - Бороо");
            names.add("Guys - Бороо");

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
        String sogname = "1982";
        String displayText = "\"Улаан Бүч\" Чуулга - Угтагчийн Дуу (1982)";
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

    private static void opencvTest() throws IOException {
//        String sourceImg = "alan_turing_1.jpg";
//        String tempImg = "edge.png";
//
//        Mat img = Highgui.imread(sourceImg);
//
//        Mat gray = new Mat();
//
//        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.GaussianBlur(img, gray, new Size(0, 0), 5);
//        Core.addWeighted(img, 1.5, gray, -0.5, 0, gray);
//
//        Mat edges = new Mat();
//        int lowThreshold = 50;
//        int ratio = 3;
//        Imgproc.Canny(gray, edges, lowThreshold, lowThreshold * ratio);
//
//        Highgui.imwrite(tempImg, edges);
//        Mat edge = Highgui.imread(tempImg);
//
//        Mat lines = new Mat();
//        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 10, 10, 10);
//
//        int dist = 3;
//
//        for (int i = 0; i < lines.cols(); i++) {
//            double[] val = lines.get(0, i);
//            Core.line(edge, new Point(val[0], val[1]), new Point(val[2], val[3]),
//                    new Scalar(randColor(0, 100), randColor(0, 100), randColor(0, 100)), dist * 2);
//        }
//
//        for (int i = 0; i < lines.cols(); i++) {
//            double[] val = lines.get(0, i);
//
//            Core.line(edge, new Point(val[0] - dist, val[1] - dist), new Point(val[2] - dist, val[3] - dist),
//                    new Scalar(0, 0, 255), 1);
//
//            Core.line(edge, new Point(val[0] + dist, val[1] + dist), new Point(val[2] + dist, val[3] + dist),
//                    new Scalar(255, 0, 0), 1);
//
//            Core.line(edge, new Point(val[0] - dist, val[1] + dist), new Point(val[2] - dist, val[3] + dist),
//                    new Scalar(0, 255, 0), 1);
//        }
//
//        Highgui.imwrite(tempImg, edge);
//        edge = Highgui.imread(tempImg);
//        Core.addWeighted(img, 0.1, edge, 1, 0, img);
//        Highgui.imwrite(tempImg, img);

        // System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        Mat src = Imgcodecs.imread(Config.RESOURCE_DIR + "muuchka_lines.png");
        //Creating an empty matrices to store edges, source, destination
        Mat gray = new Mat(src.rows(), src.cols(), src.type());
//        Mat edges = new Mat(src.rows(), src.cols(), src.type());
        Mat dst = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));


        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.GaussianBlur(gray, edges, new Size(0, 0), 0.85);
//        // Imgproc.blur(gray, edges, new Size(3, 3));
//        Core.addWeighted(gray, 1.5, gray, -0.5, 0, gray);

        int lowThreshold = 28;
        int ratio = 3;
        // Imgproc.Canny(src, src, lowThreshold, lowThreshold*ratio);


        Mat lines = new Mat();
        Imgproc.HoughLinesP(gray, lines, 1, Math.PI / 180, 100, 0, 0);

        for (int i = 0; i < lines.cols(); i++) {
            double[] val = lines.get(0, i);
            Imgproc.line(dst, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(255, 0, 0), 3);
        }

        // Core.addWeighted(img, 0.1, edge, 1, 0, img);

        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".png", dst, mob);
        byte ba[] = mob.toArray();

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));

        ImageIO.write(bi, Config.OUTPUT_IMAGE_FORMAT,
                new File(Config.RESOURCE_DIR + "muchka_edge2." + Config.OUTPUT_IMAGE_FORMAT)
        );

//        Image img = HighGui.toBufferedImage(dst);
//        WritableImage writableImage= SwingFXUtils.toFXImage((BufferedImage) img, null);
//        //Setting the image view
//        ImageView imageView = new ImageView(writableImage);
//        imageView.setX(10);
//        imageView.setY(10);
//        imageView.setFitWidth(575);
//        imageView.setPreserveRatio(true);
//        //Setting the Scene object
//        Group root = new Group(imageView);
//        Scene scene = new Scene(root, 595, 400);
//        stage.setTitle("Gaussian Blur Example");
//        stage.setScene(scene);
//        stage.show();
    }


}
