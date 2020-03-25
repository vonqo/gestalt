package mn.von.gestalt.utility.grimoire;

import mn.von.gestalt.utility.config.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VideoUtils {

    private int fps;
    private int imageWidth;
    private int imageHeight;
    private String framePrefix;
    private File audio;

    public VideoUtils() {
        this.fps = 25;
        this.imageHeight = 1080;
        this.imageWidth = 1080;
        this.framePrefix = "test_";
    }

    public VideoUtils(int fps, int imageWidth, int imageHeight, String framePrefix, File audio) {
        this.fps = fps;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.framePrefix = framePrefix;
        this.audio = audio;
    }

    public void encodeToVideo(String frameDir, String outputVideo) throws IOException {
        List<String> ffmpegParams = new ArrayList<String>();
        String[] videoParams = {
                Config.FFMEG_EXEC,
                "-r", String.valueOf(this.fps),
                "-f", "image2",
                "-s", imageWidth+"x"+imageHeight,
                "-i", this.framePrefix+"%05d.png",
                "-vcodec", "libx264",
                "-crf", "18",
                "-pix_fmt", "yuv420p",
                outputVideo
        };
        ffmpegParams.addAll(Arrays.asList(videoParams));

        if(audio != null) {
            String[] audioParams = {
                "-i", this.audio.getName(),
                "-acodec", "copy"
            };
            ffmpegParams.addAll(Arrays.asList(audioParams));
        }

        ProcessBuilder processBuilder;
        Process process;
        processBuilder = new ProcessBuilder(ffmpegParams).redirectErrorStream(true);
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
