package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class Resolution extends ArrayList implements Serializable {

    private transient int width;
    private transient int height;

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

    public void toList() {
        this.clear();
        this.add(width);
        this.add(height);
    }
}
