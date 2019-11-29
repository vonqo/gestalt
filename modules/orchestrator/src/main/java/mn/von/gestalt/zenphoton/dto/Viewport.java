package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;

public class Viewport implements Serializable {

    private int left;
    private int top;
    private int width;
    private int height;

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
}
