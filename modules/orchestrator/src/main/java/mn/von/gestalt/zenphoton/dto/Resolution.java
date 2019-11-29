package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;

public class Resolution implements Serializable {

    private int width;
    private int height;

    public Resolution() {
        super();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
