package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;

public class MixedLight implements Serializable {

    private transient Light red;
    private transient Light green;
    private transient Light blue;

    public MixedLight(){}

    public MixedLight(Light red, Light green, Light blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Light getRed() {
        return red;
    }

    public void setRed(Light red) {
        this.red = red;
    }

    public Light getGreen() {
        return green;
    }

    public void setGreen(Light green) {
        this.green = green;
    }

    public Light getBlue() {
        return blue;
    }

    public void setBlue(Light blue) {
        this.blue = blue;
    }
}
