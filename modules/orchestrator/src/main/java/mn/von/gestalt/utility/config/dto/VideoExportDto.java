package mn.von.gestalt.utility.config.dto;

import java.io.Serializable;

public class VideoExportDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isVideoExport;
    private int usableCore;
    private int startFrame;
    private int endFrame;

    public boolean isVideoExport() {
        return isVideoExport;
    }

    public void setVideoExport(boolean videoExport) {
        isVideoExport = videoExport;
    }

    public int getUsableCore() {
        return usableCore;
    }

    public void setUsableCore(int usableCore) {
        this.usableCore = usableCore;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }
}
