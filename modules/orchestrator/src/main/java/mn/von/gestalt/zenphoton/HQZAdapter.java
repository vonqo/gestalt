package mn.von.gestalt.zenphoton;

import com.google.gson.Gson;
import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.utility.Settings;
import mn.von.gestalt.utility.grimoire.LunarTear;
import mn.von.gestalt.zenphoton.dto.*;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HQZAdapter {

    public static String hqzExecutablePath = Settings.GESTALT_PATH+"/modules/zenphoton/hqz/";
    private static int absoluteRed = 635;
    private static int absoluteGreen = 520;
    private static int absoluteBlue = 465;
    private static float colorPercent = 0.0004f;

    public enum Types {
        TEST1,
        TORNADO,
        MATRIX,
        PYRAMID,
        TESSERACT,
        GRAPHTREE,
        BUBBLE2
    }

    public void buildHQZ(Types type, Vector<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
        // ================ Scene building - phase ============= //
        Scene scene = initializeScene(rays);

        // ================ LIGHTS =============== //
        List<Light> lightList = buildLights(moodbar, type);
        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = buildMaterials(type);
        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = buildObjects(type);
        scene.setObjects(objects);

        // ================ Post processing - phase ================ //
        Gson gson = new Gson();
        String jsonInString = gson.toJson(scene);
        ProcessBuilder processBuilder;
        Process process;
        String jsonInputName = Settings.GESTALT_PATH+"/modules/zenphoton/hqz/examples/moodphoton.json";
        Files.write(Paths.get(jsonInputName), jsonInString.getBytes());

        processBuilder = new ProcessBuilder(hqzExecutablePath+"hqz",jsonInputName,output.getName()).redirectErrorStream(true);
        process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        try{
            int wait = process.waitFor();
            System.out.println(wait);
            if (wait == 0) {
                Logger.getLogger(HQZAdapter.class.getName()).log(Level.INFO, "Zenphoton build finished");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(HQZAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ============================================================= //
    // ========================= SCENE ============================= //
    // ============================================================= //
    private Scene initializeScene(long rays) {
        Scene scene = new Scene();
        Resolution reso = new Resolution();
        reso.setHeight(2000);
        reso.setWidth(2000);
        reso.toList();
        scene.setResolution(reso);
        Viewport viewport = new Viewport();
        viewport.setHeight(2000);
        viewport.setWidth(2000);
        viewport.setLeft(0); viewport.setTop(0);
        viewport.toList();
        scene.setViewport(viewport);
        scene.setRays(rays);
        scene.setExposure(0.2f);
        scene.setGamma(2.2f);
        return scene;
    }

    // ============================================================= //
    // ========================= MATERIALS ========================= //
    // ============================================================= //
    private List<Material> buildMaterials(Types types) {
        List<Material> materials = new ArrayList<Material>();
        materials.add(buildMaterial(0.0f,0.0f,1.0f));

        if(types == Types.TEST1 || types == Types.TORNADO) {
            Material material2 = buildMaterial(0.1f,0.0f,0.9f);
            materials.add(material2);

            Material material3 = buildMaterial(0.05f,0.8f,0.15f);
            materials.add(material3);

            Material material4 = buildMaterial(0.7f,0,0.3f);
            materials.add(material4);

        }

        return materials;
    }

    private Material buildMaterial(float transmissive, float reflective, float diffuse) {
        Material mater = new Material();

        MaterialProperty transmissiveProperty = new MaterialProperty();
        transmissiveProperty.setType(MaterialProperty.MaterialPropertyType.Transmissive);
        transmissiveProperty.setWeigth(transmissive);
        transmissiveProperty.wrapToList();
        mater.addMaterialProperty(transmissiveProperty);

        MaterialProperty reflectiveProperty = new MaterialProperty();
        reflectiveProperty.setType(MaterialProperty.MaterialPropertyType.Reflective);
        reflectiveProperty.setWeigth(reflective);
        reflectiveProperty.wrapToList();
        mater.addMaterialProperty(reflectiveProperty);

        MaterialProperty diffuseProperty = new MaterialProperty();
        diffuseProperty.setType(MaterialProperty.MaterialPropertyType.Diffuse);
        diffuseProperty.setWeigth(diffuse);
        diffuseProperty.wrapToList();
        mater.addMaterialProperty(diffuseProperty);

        return mater;
    }

    // ============================================================= //
    // ========================= OBJECTS =========================== //
    // ============================================================= //
    private List<ZObject> buildObjects(Types types) {
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(buildObject(0,0,0,2000,0));
        objects.add(buildObject(0,2000,0,2000,0));
        objects.add(buildObject(0,0,0,0,2000));
        objects.add(buildObject(0,2000,0,0,2000));

        // ============================================================= //
        if(types == Types.TEST1) {
            ZObject object1 = buildObject(1,1600,0,150,730);
            objects.add(object1);

            ZObject object2 = buildObject(1,1750,730,-60,450);
            objects.add(object2);

            ZObject object3 = buildObject(1,100,100,100,100);
            objects.add(object3);

            ZObject object4 = buildObject(1,100,1560,830,-380);
            objects.add(object4);

            ZObject object5 = buildObject(2,690,1430,510,220);
            objects.add(object5);

            ZObject object6 = buildObject(2,25,650,510,220);
            objects.add(object6);

            ZObject object7 = buildObject(1,1500,1750,510,100);
            objects.add(object7);

        } else if(types == Types.TORNADO) {
            ZObject object1 = buildObject(1,1400,0,350,600);
            objects.add(object1);

            ZObject object2 = buildObject(1,1750,730,-60,450);
            objects.add(object2);

            ZObject object3 = buildObject(1,290,1430,510,220);
            objects.add(object3);

            ZObject object4 = buildObject(1,800,1750,510,100);
            objects.add(object4);
        } else if(types == Types.GRAPHTREE) {

        }
        return objects;
    }

    private ZObject buildObject(int materialIndex, int x0, int y0, int dx, int dy) {
        ZObject obj = new ZObject();
        obj.setMaterialIndex(materialIndex);
        obj.setX0(x0); obj.setY0(y0);
        obj.setDx(dx); obj.setDy(dy);
        obj.toList();
        return obj;
    }

    // ============================================================= //
    // ========================= LIGHTS ============================ //
    // ============================================================= //
    private List<Light> buildLights(Vector<Color> moodbar, Types types) {
        List<Light> lightList = new ArrayList<Light>();

        if(types == Types.TEST1) {
            ArrayList<Integer> polarAngle = new ArrayList<Integer>();
            polarAngle.add(-3); polarAngle.add(3);

            ArrayList<Integer> polarAngle2 = new ArrayList<Integer>();
            polarAngle2.add(87); polarAngle2.add(93);

            ArrayList<Integer> polarDist = new ArrayList<Integer>();
            polarDist.add(0); polarDist.add(1500);

            ArrayList<Integer> rayAngle = new ArrayList<Integer>();
            rayAngle.add(-3); rayAngle.add(3);

            ArrayList<Integer> rayAngle2 = new ArrayList<Integer>();
            rayAngle2.add(87); rayAngle2.add(93);

            for(int i = 0; i < moodbar.size(); i++) {
                Color clr = moodbar.get(i);
                int x = i*2; int y = i*2;

                Light lightRed = new Light();
                Light lightGreen = new Light();
                Light lightBlue = new Light();
                MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);

                buildRGBLight(mixedLight, clr, polarDist, x, y);

                if(i % 2 == 0) {
                    lightGreen.setPolarAngle(polarAngle);
                    lightRed.setPolarAngle(polarAngle);
                    lightBlue.setPolarAngle(polarAngle);
                    lightRed.setRayAngle(rayAngle);
                    lightBlue.setRayAngle(rayAngle);
                    lightGreen.setRayAngle(rayAngle);
                } else {
                    lightGreen.setPolarAngle(polarAngle2);
                    lightRed.setPolarAngle(polarAngle2);
                    lightBlue.setPolarAngle(polarAngle2);
                    lightRed.setRayAngle(rayAngle2);
                    lightBlue.setRayAngle(rayAngle2);
                    lightGreen.setRayAngle(rayAngle2);
                }

                lightRed.toList(); lightBlue.toList(); lightGreen.toList();
                lightList.add(lightRed);
                lightList.add(lightGreen);
                lightList.add(lightBlue);
            }
        } else if(types == Types.TORNADO) {

            ArrayList<Integer> polarDist = new ArrayList<Integer>();
            polarDist.add(0); polarDist.add(1500);

            int radius = 250; int padding = 1000-radius;
            double unitSpace = Math.PI * 2 / moodbar.size();
            double theta = Math.PI;

            for(int i = 0; i < moodbar.size(); i++, theta += unitSpace) {
                Color clr = moodbar.get(i);

                Light lightRed = new Light();
                Light lightGreen = new Light();
                Light lightBlue = new Light();
                MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);

                int x = (int)(Math.cos(theta) * radius) + radius + padding;
                int y = (int)(Math.sin(theta) * radius) + radius + padding;
                buildRGBLight(mixedLight, clr, polarDist, x, y);

                int degree = (int)Math.toDegrees(theta);
                degree += 45;
                ArrayList<Integer> polarAngle = new ArrayList<Integer>();
                polarAngle.add(degree-2); polarAngle.add(degree+2);

                ArrayList<Integer> rayAngle = new ArrayList<Integer>();
                rayAngle.add(degree-2); rayAngle.add(degree+2);

                lightGreen.setPolarAngle(polarAngle);
                lightRed.setPolarAngle(polarAngle);
                lightBlue.setPolarAngle(polarAngle);
                lightRed.setRayAngle(rayAngle);
                lightBlue.setRayAngle(rayAngle);
                lightGreen.setRayAngle(rayAngle);

                lightRed.toList(); lightBlue.toList(); lightGreen.toList();
                lightList.add(lightRed);
                lightList.add(lightGreen);
                lightList.add(lightBlue);
            }
        }

        return lightList;
    }

    private void buildRGBLight(MixedLight mixedLight, Color color, List<Integer> polarDist, int x, int y) {
        Light red = mixedLight.getRed();
        red.setPolarDistance(polarDist);
        red.setCartesianX(x); red.setCartesianY(y);
        red.setLightPower(color.getRed() * colorPercent);
        red.setWaveLength(absoluteRed);

        Light green = mixedLight.getGreen();
        green.setPolarDistance(polarDist);
        green.setCartesianX(x); green.setCartesianY(y);
        green.setLightPower(color.getGreen() * colorPercent);
        green.setWaveLength(absoluteGreen);

        Light blue = mixedLight.getBlue();
        blue.setPolarDistance(polarDist);
        blue.setCartesianX(x); blue.setCartesianY(y);
        blue.setLightPower(color.getBlue() * colorPercent);
        blue.setWaveLength(absoluteBlue);
    }

}
