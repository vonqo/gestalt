package mn.von.gestalt.utility.config;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Config {

    public static String MOODBAR_EXEC = "";
    public static String HQZ_EXEC = "";
    public static String FFMEG_EXEC = "";
    public static String NEURALSTYLE_EXEC = "";
    public static String NEURALSTYLE_STYLE_DIR = "";
    public static String NEURALSTYLE_CONTENT_DIR = "";

    public static String RESOURCE_DIR = "";
    public static String OUTPUT_IMAGE_FORMAT = "png";

    public static void loadConfig() {
        StringBuilder configBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get("config.json"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> configBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gsonParser = new Gson();
        ConfigDto configData = gsonParser.fromJson(configBuilder.toString(), ConfigDto.class);
        Config.MOODBAR_EXEC = configData.getMoodbarExecuteable();
        Config.HQZ_EXEC = configData.getHqzExecutable();
        Config.FFMEG_EXEC = configData.getFfmpegExecutable();
        Config.RESOURCE_DIR = configData.getResourceDir();

        NeuralStyleConfigDto nConfig = configData.getNeuralStyle();
        Config.NEURALSTYLE_EXEC = nConfig.getExecutable();
        Config.NEURALSTYLE_STYLE_DIR = nConfig.getStyleDir();
        Config.NEURALSTYLE_CONTENT_DIR = nConfig.getContentDir();
    }

}
