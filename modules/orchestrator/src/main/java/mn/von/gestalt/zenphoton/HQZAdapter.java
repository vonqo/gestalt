package mn.von.gestalt.zenphoton;

import com.google.gson.Gson;
import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.zenphoton.dto.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HQZAdapter {

    public void buildPhoton(Scene scene, File output) throws IOException {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(scene);
        ProcessBuilder processBuilder;
        Process process;
        String jsonInputName = Config.RESOURCE_DIR+"/moodphoton.json";
        Files.write(Paths.get(jsonInputName), jsonInString.getBytes());

        processBuilder = new ProcessBuilder(Config.HQZ_EXEC,jsonInputName,output.getName()).redirectErrorStream(true);
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

}
