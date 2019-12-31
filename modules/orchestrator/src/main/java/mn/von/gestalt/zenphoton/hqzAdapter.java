package mn.von.gestalt.zenphoton;

import com.google.gson.Gson;
import mn.von.gestalt.moodbar.MoodbarAdapter;
<<<<<<< HEAD
import mn.von.gestalt.utility.Settings;
=======
import mn.von.gestalt.zenphoton.dto.Light;
import mn.von.gestalt.zenphoton.dto.Resolution;
>>>>>>> 8d14806d6f869a8a47e877aa1f1f2e1600e5dcef
import mn.von.gestalt.zenphoton.dto.Scene;
import mn.von.gestalt.zenphoton.dto.Viewport;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class hqzAdapter {

    public static String hqzExecutablePath = Settings.GESTALT_PATH+"/modules/zenphoton/hqz/";
    private static int absoluteRed = 635;
    private static int absoluteGreen = 520;
    private static int absoluteBlue = 465;
    private static float colorPercent = 0.0039f;


    public static void buildHQZ(Vector<Color> moodbar, double[][] spectrumData) throws IOException {

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
        scene.setRays(1000000);
        scene.setExposure(0.2f);
        scene.setGamma(2.2f);

        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarAngle = new ArrayList<Integer>();
        polarAngle.add(0); polarAngle.add(90);

        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(750);

        ArrayList<Integer> rayAngle = new ArrayList<Integer>();
        rayAngle.add(0); rayAngle.add(360);


        for(int i = 0; i < moodbar.size(); i++) {
            Color clr = moodbar.get(i);
            int x = i; int y = i;

            Light lightRed = new Light();
            lightRed.setRayAngle(rayAngle);
            lightRed.setPolarDistance(polarDist);
            lightRed.setPolarAngle(polarAngle);
            lightRed.setCartesianX(x); lightRed.setCartesianY(y);
            lightRed.setLightPower(clr.getRed() * colorPercent);
            lightRed.setWaveLength(absoluteRed);

            Light lightGreen = new Light();
            lightGreen.setRayAngle(rayAngle);
            lightGreen.setPolarDistance(polarDist);
            lightGreen.setPolarAngle(polarAngle);
            lightGreen.setCartesianX(x); lightGreen.setCartesianY(y);
            lightGreen.setLightPower(clr.getGreen() * colorPercent);
            lightGreen.setWaveLength(absoluteGreen);

            Light lightBlue = new Light();
            lightBlue.setRayAngle(rayAngle);
            lightBlue.setPolarDistance(polarDist);
            lightBlue.setPolarAngle(polarAngle);
            lightBlue.setCartesianX(x); lightBlue.setCartesianY(y);
            lightBlue.setLightPower(clr.getBlue() * colorPercent);
            lightBlue.setWaveLength(absoluteBlue);

            lightList.add(lightRed);
            lightList.add(lightGreen);
            lightList.add(lightBlue);
        }
        scene.setLights(lightList);

        // ================ Post processing - phase ================ //
        Gson gson = new Gson();
        String jsonInString = gson.toJson(scene);
        ProcessBuilder processBuilder;
        Process process;
        String jsonInputName = Settings.GESTALT_PATH+"/modules/zenphoton/hqz/examples/moodphoton.json";
        String outputName = "test_output.png";
        Files.write(Paths.get(jsonInputName), jsonInString.getBytes());

        processBuilder = new ProcessBuilder(hqzExecutablePath+"hqz",jsonInputName,outputName).redirectErrorStream(true);

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
                Logger.getLogger(hqzAdapter.class.getName()).log(Level.INFO, "Zenphoton build finished");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(hqzAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
