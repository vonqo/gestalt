package mn.von.gestalt.zenphoton;

import com.google.gson.Gson;
import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.utility.grimoire.ImageSupporter;
import mn.von.gestalt.zenphoton.dto.*;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
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
        String sceneDataFile = FilenameUtils.removeExtension(output.getName())+".json";
        String jsonInputName = Config.RESOURCE_DIR+"/"+sceneDataFile;
        Files.write(Paths.get(jsonInputName), jsonInString.getBytes());

        processBuilder = new ProcessBuilder(Config.HQZ_EXEC,jsonInputName,output.getAbsolutePath()).redirectErrorStream(true);
        process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println("\n\t"+output.getName()+"::"+line);
            ImageIO.write(
                ImageSupporter.fillBlack(scene.getResolution().getWidth(),scene.getResolution().getHeight()), Config.OUTPUT_IMAGE_FORMAT, output
            );
        }

        try{
            int wait = process.waitFor();
            System.out.println(wait);
            if (wait == 0) {
                System.out.println("frame finished: "+output.getName());
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(HQZAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
