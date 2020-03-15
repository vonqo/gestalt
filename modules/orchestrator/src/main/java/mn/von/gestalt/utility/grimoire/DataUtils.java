package mn.von.gestalt.utility.grimoire;

import java.util.ArrayList;

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
}
