package mn.von.gestalt;

import mn.von.gestalt.qrfractal.QRFractal;
import org.opencv.core.Core;

/**
 This is the place where all magic works

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Orchestrator {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        nu.pattern.OpenCV.loadShared();
    }

    public static void main(String args[]) {
        QRFractal fractal = new QRFractal();
        fractal.run();
    }

}
