package mn.von.gestalt.nstyle;

import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.zenphoton.HQZAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeuralStyleAdapter {

    public void buildNeuralStyle(File content, File style, File output) throws IOException {

        ProcessBuilder processBuilder;
        Process process;

        // Configred for
        // CUDA 10.2 + cuDNN 7 + RTX 2070 8GB
        processBuilder = new ProcessBuilder(Config.NEURALSTYLE_EXEC,
                "-style_image", Config.NEURALSTYLE_STYLE_DIR+style.getName(),
                "-content_image", Config.NEURALSTYLE_CONTENT_DIR+content.getName(),
                "-backend", "cudnn",
                "-cudnn_autotune",
                "-image_size", "1000",
                "-style_weight", "3e2",
                "-pooling", "avg",
                "-optimizer", "lbfgs",
                "-save_iter", "0",
                "-num_iterations", "800",
                "-output_image", Config.RESOURCE_DIR+output.getName()
        ).redirectErrorStream(true);

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
                Logger.getLogger(NeuralStyleAdapter.class.getName()).log(Level.INFO, "NeuralStyle build finished");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(NeuralStyleAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
