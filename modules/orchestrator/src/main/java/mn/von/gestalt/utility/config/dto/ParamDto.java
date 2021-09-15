package mn.von.gestalt.utility.config.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class ParamDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String exportType;
    private String fontName;
    private ArrayList<String> audioFile;
    private ArrayList<String> displayText;
    private boolean hasBanner;
    private int ray;
    private String extraDataFile;
    private VideoExportDto videoExportDto;

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public ArrayList<String> getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(ArrayList<String> audioFile) {
        this.audioFile = audioFile;
    }

    public ArrayList<String> getDisplayText() {
        return displayText;
    }

    public void setDisplayText(ArrayList<String> displayText) {
        this.displayText = displayText;
    }

    public boolean isHasBanner() {
        return hasBanner;
    }

    public void setHasBanner(boolean hasBanner) {
        this.hasBanner = hasBanner;
    }

    public int getRay() {
        return ray;
    }

    public void setRay(int ray) {
        this.ray = ray;
    }

    public VideoExportDto getVideoExportDto() {
        return videoExportDto;
    }

    public void setVideoExportDto(VideoExportDto videoExportDto) {
        this.videoExportDto = videoExportDto;
    }

    public String getExtraDataFile() {
        return extraDataFile;
    }

    public void setExtraDataFile(String extraDataFile) {
        this.extraDataFile = extraDataFile;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
