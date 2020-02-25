package mn.von.gestalt.utility.grimoire;

import mn.von.gestalt.utility.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoUtils {

    public static void encodeToVideo(String frameDir, String outputVideo) throws IOException {

        ProcessBuilder processBuilder;
        Process process;
        processBuilder = new ProcessBuilder(
                Config.FFMEG_EXEC,
                "-r", "1/5",
                "-i", "img%03d.png",
                "-c:v", "libx264",
                "-vf", "fps=25",
                "-pix_fmt", "yuv420p",
                "out.mp4"
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
                Logger.getLogger(VideoUtils.class.getName()).log(Level.INFO, "Video build finished");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(VideoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
