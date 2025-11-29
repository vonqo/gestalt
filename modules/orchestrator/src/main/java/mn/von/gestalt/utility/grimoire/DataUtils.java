package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class DataUtils {

    public static ArrayList<Double> spectogramMinMaxToPercent(double[][] spectogramData, int distributionSize) {
        ArrayList<Double> list = new ArrayList<Double>(distributionSize);
        int unitRegion = spectogramData.length / distributionSize;
        double max = 0, min = Double.MAX_VALUE;

        for(int i = 0, g = 0, e = 0; i < distributionSize; i++) {
            double sum = 0.0;
            for(e = 0; e < unitRegion; e++) {
                for(int k = 0; k < spectogramData[g+e].length; k++) {
                    sum += spectogramData[g+e][k];
                }
            }

            g += e;
            if(max < sum) max = sum;
            if(i > 10 && min > sum && sum != 0.0) min = sum;
            list.add(sum);
        }

        double diff = max - min;
        for(int i = 0; i < list.size(); i++) {
            double percent = 0;
            if((list.get(i) - min) > 0) {
                percent = (list.get(i) - min) / diff;
            }
            list.set(i, percent);
        }

        return list;
    }


    // Benchmarking avgt  200   0.956 ± 0.011  ns/op
    // The fastest approach compared against logarithmic and string conversion
    public static int countDigit(int number) {
        if (number < 100000) {
            if (number < 100) {
                if (number < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                if (number < 1000) {
                    return 3;
                } else {
                    if (number < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            if (number < 10000000) {
                if (number < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                if (number < 100000000) {
                    return 8;
                } else {
                    if (number < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
                }
            }
        }
    }

    public static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
    }

    // Resize method using linear interpolation
    public static double[] linearResize(double[] array, int size) {
        double ratio = (double) (array.length - 1) / (size - 1);
        double[] out = new double[size];
        for(int i = 0; i < size; i++) {
            double low = Math.floor(ratio * i);
            double high = Math.ceil(ratio * i);
            double weight = ratio * i - low;

            double a = array[(int) low];
            double b = array[(int) high];

            out[i] = a * (1 - weight) + b * weight;
        }
        return out;
    }

    // Resize method using bicubic interpolation
    public static double[] bicubicResize(double[] array, int newSize) {
        double[] outputArray = new double[newSize];
        double scaleFactor = (double) (outputArray.length - 1) / (newSize - 1);

        for (int i = 0; i < newSize; i++) {
            double newIndex = i * scaleFactor;
            outputArray[i] = interpolate(array, newIndex);
        }

        return outputArray;
    }

    // Resize method using bicubic interpolation
    public static Color[] bicubicResize(Color[] inputColors, int newSize) {
        Color[] outputColors = new Color[newSize];
        double scaleFactor = (double) (inputColors.length - 1) / (newSize - 1);

        for (int i = 0; i < newSize; i++) {
            double newIndex = i * scaleFactor;
            outputColors[i] = interpolate(inputColors, newIndex);
        }

        return outputColors;
    }

    public static double interpolate(double[] data, double x) {
        int x1 = (int) Math.floor(x);
        double t = x - x1;

        // Ensure we have at least 4 points around the target x
        int x0 = Math.max(x1 - 1, 0);
        int x2 = Math.min(x1 + 1, data.length - 1);
        int x3 = Math.min(x1 + 2, data.length - 1);

        return cubicInterpolate(data[x0], data[x1], data[x2], data[x3], t);
    }

    // Interpolation method for Colors array
    public static Color interpolate(Color[] colors, double x) {
        int x1 = (int) Math.floor(x);
        double t = x - x1;

        // Ensuring we have enough points for bicubic interpolation
        int x0 = Math.max(x1 - 1, 0);
        int x2 = Math.min(x1 + 1, colors.length - 1);
        int x3 = Math.min(x1 + 2, colors.length - 1);

        // Extract color channels
        int r0 = colors[x0].getRed(), r1 = colors[x1].getRed(), r2 = colors[x2].getRed(), r3 = colors[x3].getRed();
        int g0 = colors[x0].getGreen(), g1 = colors[x1].getGreen(), g2 = colors[x2].getGreen(), g3 = colors[x3].getGreen();
        int b0 = colors[x0].getBlue(), b1 = colors[x1].getBlue(), b2 = colors[x2].getBlue(), b3 = colors[x3].getBlue();

        // Perform bicubic interpolation for each channel
        int r = (int) Math.round(cubicInterpolate(r0, r1, r2, r3, t));
        int g = (int) Math.round(cubicInterpolate(g0, g1, g2, g3, t));
        int b = (int) Math.round(cubicInterpolate(b0, b1, b2, b3, t));

        // Clamp values between 0-255
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return new Color(r, g, b);
    }

    private static double cubicInterpolate(double p0, double p1, double p2, double p3, double t) {
        double a0 = p3 - p2 - p0 + p1;
        double a1 = p0 - p1 - a0;
        double a2 = p2 - p0;
        double a3 = p1;

        return a0 * (t * t * t) + a1 * (t * t) + a2 * t + a3;
    }

    public static ArrayList<Color> sortColorsByHSV(ArrayList<Color> colors) {
        ArrayList<Color> tmpColors = (ArrayList)colors.clone();

        tmpColors.sort(Comparator.comparing((Color c) -> {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            return hsb[0]; // Sort by Hue
        }).thenComparing(c -> {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            return hsb[1]; // Sort by Saturation
        }).thenComparing(c -> {
            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            return hsb[2]; // Sort by Value (Brightness)
        }));

        return  tmpColors;
    }
}
