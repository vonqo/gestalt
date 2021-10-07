package mn.von.gestalt.utility.config.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class ParamDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String fontName;
    private VideoExportDto videoExport;
    private ArrayList<AudioDto> audio;

    public ParamDto() { }

    public VideoExportDto getVideoExportDto() {
        return videoExport;
    }

    public void setVideoExportDto(VideoExportDto videoExportDto) {
        this.videoExport = videoExportDto;
    }

    public ArrayList<AudioDto> getAudioDtos() {
        return audio;
    }

    public void setAudioDtos(ArrayList<AudioDto> audio) {
        this.audio = audio;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
