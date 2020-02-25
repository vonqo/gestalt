package mn.von.gestalt.utility.config;

import java.io.Serializable;

public class NeuralStyleConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String executable;
    private String styleDir;
    private String contentDir;

    public NeuralStyleConfigDto() {
        super();
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public String getStyleDir() {
        return styleDir;
    }

    public void setStyleDir(String styleDir) {
        this.styleDir = styleDir;
    }

    public String getContentDir() {
        return contentDir;
    }

    public void setContentDir(String contentDir) {
        this.contentDir = contentDir;
    }
}
