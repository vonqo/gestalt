package mn.von.gestalt.zenphoton;

import com.google.gson.Gson;
import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.utility.Settings;
import mn.von.gestalt.utility.grimoire.LunarTear;
import mn.von.gestalt.zenphoton.dto.*;

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
    private static float colorPercent = 0.00039f;

    public enum Types {
        TEST1,
        TEST2,
        TEST3
    }


    public static void buildHQZ(Types type, Vector<Color> moodbar, double[][] spectrumData, File output) throws IOException {

        // ================ Scene building - phase ============= //
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
        scene.setRays(5000000);
        scene.setExposure(0.2f);
        scene.setGamma(2.2f);

        List<Light> lightList = new ArrayList<Light>();

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

        // ================ LIGHTS =============== //
        for(int i = 0; i < moodbar.size(); i++) {
            Color clr = moodbar.get(i);
            int x = i*2; int y = i*2;

            Light lightRed = new Light();
            lightRed.setPolarDistance(polarDist);
            lightRed.setCartesianX(x); lightRed.setCartesianY(y);
            lightRed.setLightPower(clr.getRed() * colorPercent);
            lightRed.setWaveLength(absoluteRed);

            Light lightGreen = new Light();
            lightGreen.setPolarDistance(polarDist);
            lightGreen.setCartesianX(x); lightGreen.setCartesianY(y);
            lightGreen.setLightPower(clr.getGreen() * colorPercent);
            lightGreen.setWaveLength(absoluteGreen);

            Light lightBlue = new Light();
            lightBlue.setPolarDistance(polarDist);
            lightBlue.setCartesianX(x); lightBlue.setCartesianY(y);
            lightBlue.setLightPower(clr.getBlue() * colorPercent);
            lightBlue.setWaveLength(absoluteBlue);

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
        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        Material material1 = new Material();

        MaterialProperty property11 = new MaterialProperty();
        property11.setType(MaterialProperty.MaterialPropertyType.Diffuse);
        property11.setWeigth(1.0f);
        property11.wrapToList();

        MaterialProperty property12 = new MaterialProperty();
        property12.setType(MaterialProperty.MaterialPropertyType.Reflective);
        property12.setWeigth(0f);
        property12.wrapToList();

        material1.addMaterialProperty(property11);
        material1.addMaterialProperty(property12);
        materials.add(material1);


        Material material2 = new Material();

        MaterialProperty property21 = new MaterialProperty();
        property21.setType(MaterialProperty.MaterialPropertyType.Diffuse);
        property21.setWeigth(0.9f);
        property21.wrapToList();

        MaterialProperty property22 = new MaterialProperty();
        property22.setType(MaterialProperty.MaterialPropertyType.Transmissive);
        property22.setWeigth(0.1f);
        property22.wrapToList();

        material2.addMaterialProperty(property21);
        material2.addMaterialProperty(property22);
        materials.add(material2);

        Material material3 = new Material();

        MaterialProperty property31 = new MaterialProperty();
        property31.setType(MaterialProperty.MaterialPropertyType.Transmissive);
        property31.setWeigth(0.05f);
        property31.wrapToList();

        MaterialProperty property32 = new MaterialProperty();
        property32.setType(MaterialProperty.MaterialPropertyType.Reflective);
        property32.setWeigth(0.8f);
        property32.wrapToList();

        MaterialProperty property33 = new MaterialProperty();
        property33.setType(MaterialProperty.MaterialPropertyType.Diffuse);
        property33.setWeigth(0.15f);
        property33.wrapToList();

        material3.addMaterialProperty(property31);
        material3.addMaterialProperty(property32);
        material3.addMaterialProperty(property33);
        materials.add(material3);

        Material material4 = new Material();

        MaterialProperty property41 = new MaterialProperty();
        property41.setType(MaterialProperty.MaterialPropertyType.Transmissive);
        property41.setWeigth(0.7f);
        property41.wrapToList();

        MaterialProperty property42 = new MaterialProperty();
        property42.setType(MaterialProperty.MaterialPropertyType.Reflective);
        property42.setWeigth(0);
        property42.wrapToList();

        MaterialProperty property43 = new MaterialProperty();
        property43.setType(MaterialProperty.MaterialPropertyType.Diffuse);
        property43.setWeigth(0.3f);
        property43.wrapToList();

        material4.addMaterialProperty(property41);
        material4.addMaterialProperty(property42);
        material4.addMaterialProperty(property43);
        materials.add(material4);

        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        ZObject obj1 = new ZObject();
        obj1.setMaterialIndex(0);
        obj1.setX0(0); obj1.setY0(0);
        obj1.setDx(2000); obj1.setDy(0);
        obj1.toList(); objects.add(obj1);

        ZObject obj2 = new ZObject();
        obj2.setMaterialIndex(0);
        obj2.setX0(2000); obj1.setY0(0);
        obj2.setDx(2000); obj1.setDy(0);
        obj2.toList(); objects.add(obj2);

        ZObject obj3 = new ZObject();
        obj3.setMaterialIndex(0);
        obj3.setX0(0); obj1.setY0(0);
        obj3.setDx(0); obj1.setDy(2000);
        obj3.toList(); objects.add(obj3);

        ZObject obj4 = new ZObject();
        obj4.setMaterialIndex(0);
        obj4.setX0(2000); obj4.setY0(0);
        obj4.setDx(0); obj4.setDy(2000);
        obj4.toList(); objects.add(obj4);

        ZObject object1 = new ZObject();
        object1.setMaterialIndex(1);
        object1.setX0(1600); object1.setY0(0);
        object1.setDx(150); object1.setDy(730);
        object1.toList(); objects.add(object1);

        ZObject object2 = new ZObject();
        object2.setMaterialIndex(2);
        object2.setX0(1750); object2.setY0(730);
        object2.setDx(-70); object2.setDy(200);
        object2.toList(); objects.add(object2);

        ZObject object3 = new ZObject();
        object3.setMaterialIndex(1);
        object3.setX0(1900); object3.setY0(1000);
        object3.setDx(-250); object3.setDy(180);
        object3.toList(); objects.add(object3);

        ZObject object4 = new ZObject();
        object4.setMaterialIndex(1);
        object4.setX0(100); object4.setY0(1560);
        object4.setDx(630); object4.setDy(-440);
        object4.toList(); objects.add(object4);

        ZObject object5 = new ZObject();
        object5.setMaterialIndex(2);
        object5.setX0(690); object5.setY0(1430);
        object5.setDx(510); object5.setDy(220);
        object5.toList(); objects.add(object5);

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

    private List<Material> buildMaterials() {
        //TODO: materials
        return null;
    }

    private List<ZObject> buildObjects() {
        //TODO: objects
        return  null;
    }

    private List<Light> buildLights() {
        //TODO: lights
        return null;
    }

    private Scene initializeScene() {
        //TODO: scene
        return null;
    }

}
