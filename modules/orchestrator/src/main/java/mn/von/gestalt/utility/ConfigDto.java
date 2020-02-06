package mn.von.gestalt.utility;

import java.io.Serializable;

public class ConfigDto implements Serializable {

    private String moodbarExecuteable;
    private String hqzExecutable;
    private String ffmpegExecutable;
    private String neuralstyleExecutable;
    private String resourceDir;

    public ConfigDto() {
        super();
    }

    public String getMoodbarExecuteable() {
        return moodbarExecuteable;
    }

    public void setMoodbarExecuteable(String moodbarExecuteable) {
        this.moodbarExecuteable = moodbarExecuteable;
    }

    public String getHqzExecutable() {
        return hqzExecutable;
    }

    public void setHqzExecutable(String hqzExecutable) {
        this.hqzExecutable = hqzExecutable;
    }

    public String getFfmpegExecutable() {
        return ffmpegExecutable;
    }

    public void setFfmpegExecutable(String ffmpegExecutable) {
        this.ffmpegExecutable = ffmpegExecutable;
    }

    public String getNeuralstyleExecutable() {
        return neuralstyleExecutable;
    }

    public void setNeuralstyleExecutable(String neuralstyleExecutable) {
        this.neuralstyleExecutable = neuralstyleExecutable;
    }

    public String getResourceDir() {
        return resourceDir;
    }

    public void setResourceDir(String resourceDir) {
        this.resourceDir = resourceDir;
    }
}
