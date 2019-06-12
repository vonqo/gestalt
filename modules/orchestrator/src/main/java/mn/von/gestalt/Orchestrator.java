package mn.von.gestalt;

import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.qrfractal.QRFractal;
import mn.von.gestalt.qrfractal.QRFractalP3;
import org.opencv.core.Core;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 This is the place where all magic works

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class Orchestrator {

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        nu.pattern.OpenCV.loadShared();
//    }

    public static void main(String args[]) {

        // Example of running QRFractal (DEPRECIATED)
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                QRFractal canvas = new QRFractal();
//
//                JFrame frame = new JFrame();
//                frame.add(canvas);
//                frame.setTitle("Test");
//                frame.pack();
//                frame.setLocationRelativeTo(null);
//                frame.setVisible(true);
//
//                canvas.start();
//            }
//        });

        // Example of running QRFractalP3
//        String[] appletArgs = new String[] {"mn.von.gestalt.qrfractal.QRFractalP3"};
//        QRFractalP3.main(appletArgs);

        try {
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/moodbar/bar.mp3", "tmp1"),
                    150,
                    new File("/home/enkh-amar/Desktop/moodbar/bar.png")
            );

            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/moodbar/bar.mp3", "tmp2"),
                    150,
                    new File("/home/enkh-amar/Desktop/moodbar/bar.png")
            );

            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/moodbar/bar.mp3", "tmp3"),
                    150,
                    new File("/home/enkh-amar/Desktop/moodbar/bar.png")
            );

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
