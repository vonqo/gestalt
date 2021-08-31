package mn.von.gestalt.utility.config.dto;

import java.io.Serializable;

public class ConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private ParamDto paramDto;
    private SystemDto systemDto;

    public ConfigDto() {
        super();
    }

    public ParamDto getParamDto() {
        return paramDto;
    }

    public void setParamDto(ParamDto paramDto) {
        this.paramDto = paramDto;
    }

    public SystemDto getSystemDto() {
        return systemDto;
    }

    public void setSystemDto(SystemDto systemDto) {
        this.systemDto = systemDto;
    }
}

