package mn.von.gestalt.qrfractal;

import java.util.ArrayList;
import java.util.Arrays;

/**
 QR Fractal zooming

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class QRFractal implements Runnable{

    private ArrayList<QRCode> qrs;
    private ArrayList<String> textBank;

    public QRFractal() {
        this.initializeTextBank();
    }

    private void initializeTextBank() {
        String[] texts = {
                "dsdadasda",
                "test-me",
                "test-me-again",
        };
        textBank = new ArrayList<String>(Arrays.asList(texts));
    }

    @Override
    public void run() {

    }
}
