package mn.von.gestalt.utility.grimoire;

import java.awt.*;

public class PointData {
    double x;
    double y;
    double z;
    // Color is stored as 3 bytes (0-255) for RGB
    int red;
    int green;
    int blue;
    int alpha;

    /**
     * Constructor for a point with coordinate and hexadecimal color.
     * * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @param color The color.
     */
    public PointData(double x, double y, double z, Color color) {
        this.x = x;
        this.y = y;
        this.z = z;

        // Parse the hexadecimal color into separate R, G, B components
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.alpha = color.getAlpha();
    }
}
