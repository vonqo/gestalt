package mn.von.gestalt;

import com.google.gson.Gson;
import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.spectogram.Spectrumizer;
import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;
import mn.von.gestalt.utility.grimoire.AudioUtils;
import mn.von.gestalt.utility.grimoire.ImageTransformer;
import mn.von.gestalt.utility.grimoire.LunarTear;
import mn.von.gestalt.zenphoton.dto.*;
import mn.von.gestalt.zenphoton.hqzAdapter;

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

//        String sogname = "yellow";
//        String displayText = "Coldplay - Yellow";
//        String testPath = "/home/anomaly/Desktop/mood_test/requests/";
//        String pathMp3 = testPath+sogname+".mp3";
//        String pathWav = testPath+sogname+".wav";
//        try {
//            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        try {
//             Vector<Color> moodbar = MoodbarAdapter.buildMoodbar(testPath+sogname+".mp3",testPath+"/bar");
//            Vector<Color> moodbar1 = MoodbarAdapter.buildMoodbar(testPath+"divine.mp3",testPath+"/bar1");
//            Vector<Color> moodbar2 = MoodbarAdapter.buildMoodbar(testPath+"shootingstar.mp3",testPath+"/bar2");
//            Vector<Color> moodbar3 = MoodbarAdapter.buildMoodbar(testPath+"manaach.mp3",testPath+"/bar3");
//            Vector<Color> moodbar0 = MoodbarAdapter.buildMoodbar(testPath+"agaar.mp3",testPath+"/bar4");
//            ArrayList<BufferedImage> moodbarList = new ArrayList<BufferedImage>();
//            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar0, 150));
//            moodbarList.add(MoodbarAdapter.toBufferedImage(moodbar1, 150));
//            moodbarList.add(MoodbarAdapter.toBuf  feredImage(moodbar2, 150));
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



            // pnginitialize spectogram
//            Spectrumizer spectrumizer = new Spectrumizer(pathWav, 4096);
//            spectrumizer.applyMoodbar(moodbar);
//            spectrumizer.build();
//
//            // save with rotation
//            BufferedImage circle = ImageTransformer.rectangularToPolarCoordinate(
//                    spectrumizer.asBufferedImage(),
//                    1000,100
//            );
//
//            BufferedImage circleMood = ImageTransformer.rectangularToPolarCoordinate(
//                    spectrumizer.asBufferedMoodbar(),
//                    1000,100
//            );
//
//            LunarTear.setBackgroundColor(Color.WHITE);
//            LunarTear.setFontColor(Color.BLACK);
//            LunarTear.setFontSize(34);
//            BufferedImage lunarTear = LunarTear.MoodbarAndSpectogramCollection(
//                    spectrumizer.asBufferedImage(),
//                    spectrumizer.asBufferedMoodbar(),
//                    MoodbarAdapter.toBufferedImage(moodbar, 150),
//                    circle, circleMood,
//                    displayText
//            );
//            ImageIO.write(lunarTear, "png", new File(testPath+"/"+sogname+"_collection.png"));
//
//            LunarTear.setBackgroundColor(Color.BLACK);
//            LunarTear.setFontColor(Color.WHITE);
//            BufferedImage bubble = ImageTransformer.bubbleMoodbar(spectrumizer.getDATA(), moodbar, 50);
//            ImageIO.write(
//                    LunarTear.addTitle(bubble, displayText), "png",
//                    new File(testPath+"/"+sogname+"_bubble.png")
//            );

//

            // ==============================================================
            LunarTear.RGB2WV_Generate_LossyExhaustingTable();
//            Scene scene = new Scene();
//            Resolution reso = new Resolution();
//            reso.setHeight(1000);
//            reso.setWidth(1000);
//            reso.toList();
//            scene.setResolution(reso);
//            Viewport viewport = new Viewport();
//            viewport.setHeight(1000);
//            viewport.setWidth(1000);
//            viewport.setLeft(0); viewport.setTop(0);
//            viewport.toList();
//            scene.setViewport(viewport);
//            scene.setRays(1000000);
//            scene.setExposure(0.2f);
//            scene.setGamma(2.2f);
//
//            // ================ MATERIAL =============== //
//            List<Material> materials = new ArrayList<Material>();
//            Material material1 = new Material();
//
//            MaterialProperty property11 = new MaterialProperty();
//            property11.setType(MaterialProperty.MaterialPropertyType.Diffuse);
//            property11.setWeigth(0.5f);
//            property11.wrapToList();
//
//            MaterialProperty property12 = new MaterialProperty();
//            property12.setType(MaterialProperty.MaterialPropertyType.Reflective);
//            property12.setWeigth(0.5f);
//            property12.wrapToList();
//
//            material1.addMaterialProperty(property11);
//            material1.addMaterialProperty(property12);
//            materials.add(material1);
//            scene.setMaterials(materials);
//
//            // ================ OBJECTS =============== //
//            List<ZObject> objects = new ArrayList<ZObject>();
//            ZObject obj1 = new ZObject();
//            obj1.setMaterialIndex(0);
//            obj1.setX0(0); obj1.setY0(0);
//            obj1.setDx(1000); obj1.setDy(0);
//            obj1.toList(); objects.add(obj1);
//
//            ZObject obj2 = new ZObject();
//            obj2.setMaterialIndex(0);
//            obj2.setX0(1000); obj1.setY0(0);
//            obj2.setDx(1000); obj1.setDy(0);
//            obj2.toList(); objects.add(obj2);
//
//            ZObject obj3 = new ZObject();
//            obj3.setMaterialIndex(0);
//            obj3.setX0(0); obj1.setY0(0);
//            obj3.setDx(0); obj1.setDy(1000);
//            obj3.toList(); objects.add(obj3);
//
//            ZObject obj4 = new ZObject();
//            obj4.setMaterialIndex(0);
//            obj4.setX0(1000); obj4.setY0(0);
//            obj4.setDx(0); obj4.setDy(1000);
//            obj4.toList(); objects.add(obj4);
//
//            scene.setObjects(objects);
//
//            // ================ LIGHTS =============== //
//            List<Light> lights = new ArrayList<Light>();
//            Light light1 = new Light();
//            light1.setLightPower(1);
//            light1.setCartesianX(2); light1.setCartesianY(2);
//
//            ArrayList<Integer> polarAngle = new ArrayList<Integer>();
//            polarAngle.add(0); polarAngle.add(90);
//            light1.setPolarAngle(polarAngle);
//
//            ArrayList<Integer> polarDist = new ArrayList<Integer>();
//            polarDist.add(0); polarDist.add(750);
//            light1.setPolarDistance(polarDist);
//
//            ArrayList<Integer> rayAngle = new ArrayList<Integer>();
//            rayAngle.add(0); rayAngle.add(360);
//            light1.setRayAngle(rayAngle);
//
//            light1.setWaveLength(580);
//            light1.toList(); lights.add(light1);
//            scene.setLights(lights);
//
//            hqzAdapter.buildHQZ(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
