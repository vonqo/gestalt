package mn.von.gestalt.zenphoton;

import com.google.gson.Gson;
import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.utility.Settings;
import mn.von.gestalt.zenphoton.dto.Scene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class hqzAdapter {

    public static String hqzExecutablePath = Settings.GESTALT_PATH+"/modules/zenphoton/hqz/";

    public static void buildHQZ(Scene scene) throws IOException {

        Gson gson = new Gson();
        String jsonInString = gson.toJson(scene);
        System.out.println(jsonInString);

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
