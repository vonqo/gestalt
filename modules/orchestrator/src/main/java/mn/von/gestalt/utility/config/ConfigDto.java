package mn.von.gestalt.utility.config;

import java.io.Serializable;

public class ConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String moodbarExecuteable;
    private String hqzExecutable;
    private String ffmpegExecutable;
    private NeuralStyleConfigDto neuralStyle;
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

    public String getResourceDir() {
        return resourceDir;
    }

    public void setResourceDir(String resourceDir) {
        this.resourceDir = resourceDir;
    }

    public NeuralStyleConfigDto getNeuralStyle() {
        return neuralStyle;
    }

    public void setNeuralStyle(NeuralStyleConfigDto neuralStyle) {
        this.neuralStyle = neuralStyle;
    }


}

