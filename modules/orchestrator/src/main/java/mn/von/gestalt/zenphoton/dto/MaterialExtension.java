package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;

public class MaterialExtension implements Serializable {

    private int a0;
    private int da;

    public MaterialExtension(int a0, int da) {
        this.a0 = a0;
        this.da = da;
    }

    public int getA0() {
        return a0;
    }

    public void setA0(int a0) {
        this.a0 = a0;
    }

    public int getDa() {
        return da;
    }

    public void setDa(int da) {
        this.da = da;
    }
}
