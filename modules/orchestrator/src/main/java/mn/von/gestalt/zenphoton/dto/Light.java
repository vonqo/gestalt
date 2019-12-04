package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Light extends ArrayList implements Serializable {

    private transient int lightPower;
    private transient int cartesianX;
    private transient int cartesianY;
    private transient List<Integer> polarAngle;
    private transient List<Integer> polarDistance;
    private transient List<Integer> rayAngle;
    private transient int waveLength;

    public Light() {
        super();
    }

    public int getLightPower() {
        return lightPower;
    }

    public void setLightPower(int lightPower) {
        this.lightPower = lightPower;
    }

    public int getCartesianX() {
        return cartesianX;
    }

    public void setCartesianX(int cartesianX) {
        this.cartesianX = cartesianX;
    }

    public int getCartesianY() {
        return cartesianY;
    }

    public void setCartesianY(int cartesianY) {
        this.cartesianY = cartesianY;
    }

    public List<Integer> getPolarAngle() {
        return polarAngle;
    }

    public void setPolarAngle(List<Integer> polarAngle) {
        this.polarAngle = polarAngle;
    }

    public List<Integer> getPolarDistance() {
        return polarDistance;
    }

    public void setPolarDistance(List<Integer> polarDistance) {
        this.polarDistance = polarDistance;
    }

    public List<Integer> getRayAngle() {
        return rayAngle;
    }

    public void setRayAngle(List<Integer> rayAngle) {
        this.rayAngle = rayAngle;
    }

    public int getWaveLength() {
        return waveLength;
    }

    public void setWaveLength(int waveLength) {
        this.waveLength = waveLength;
    }

    public void toList() {
        this.clear();
        this.add(lightPower);
        //TODO implement
    }
}
