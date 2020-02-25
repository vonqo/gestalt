package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class Viewport extends ArrayList implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient int left;
    private transient int top;
    private transient int width;
    private transient int height;

    public Viewport() {
        super();
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
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
        this.add(this.left); this.add(this.top);
        this.add(this.width); this.add(this.height);
    }
}
