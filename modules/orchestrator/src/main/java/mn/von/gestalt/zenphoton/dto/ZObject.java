package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class ZObject extends ArrayList implements Serializable {

    private int materialIndex;
    private int x0;
    private int y0;
    private int a0;
    private int dx;
    private int dy;
    private int da;

    public ZObject() {
        super();
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public int getX0() {
        return x0;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getY0() {
        return y0;
    }

    public void setY0(int y0) {
        this.y0 = y0;
    }

    public int getA0() {
        return a0;
    }

    public void setA0(int a0) {
        this.a0 = a0;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public int getDa() {
        return da;
    }

    public void setDa(int da) {
        this.da = da;
    }

    public void toList() {
        this.clear();
        //TODO implement
    }
}
