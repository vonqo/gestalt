package mn.von.gestalt;

import org.opencv.core.Core;

public class Orchestrator {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        nu.pattern.OpenCV.loadShared();
    }

    public static void main(String args[]) {

    }

}
