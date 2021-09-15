package mn.von.gestalt.utility.config.dto;

import java.io.Serializable;

public class ConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private ParamDto param;
    private SystemDto system;

    public ConfigDto() {
        super();
    }

    public ParamDto getParamDto() {
        return param;
    }

    public void setParamDto(ParamDto paramDto) {
        this.param = paramDto;
    }

    public SystemDto getSystemDto() {
        return system;
    }

    public void setSystemDto(SystemDto systemDto) {
        this.system = systemDto;
    }
}

