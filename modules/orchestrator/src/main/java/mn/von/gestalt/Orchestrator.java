package mn.von.gestalt;

import mn.von.gestalt.moodbar.MoodbarAdapter;
import mn.von.gestalt.qrfractal.QRFractal;
import mn.von.gestalt.qrfractal.QRFractalP3;
import mn.von.gestalt.spectogram.Spectrogram;
import mn.von.gestalt.spectogram.Wave;
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


//        Spectrogram spectrogram = new Spectrogram(new Wave("/home/enkh-amar/Desktop/mood_test/wavtest.wav"));
//        double[][] data = spectrogram.getNormalizedSpectrogramData();
//        System.out.println(data.length);
//        System.out.println(data[0].length);
//        System.out.println(data[1].length);
//        System.out.println(data[2].length);
        String path = "/home/enkh-amar/Desktop/mood_test/yuno/";

        try {
            // antidote
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar(path+"antidote.mp3",
                            path+"antidote"),
                            150,
                            new File(path+"antidote.png")
            );

            // er_ni_iim_bn
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar(path+"er_ni_iim_bn.mp3",
                            path+"er_ni_iim_bn"),
                    150,
                    new File(path+"er_ni_iim_bn.png")
            );

            // frequency
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar(path+"frequency.mp3",
                            path+"frequency"),
                    150,
                    new File(path+"frequency.png")
            );

            // haaya
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar(path+"haaya.mp3",
                            path+"haaya"),
                    150,
                    new File(path+"haaya.png")
            );

            // pointless
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar(path+"pointless.mp3",
                            path+"pointless"),
                    150,
                    new File(path+"pointless.png")
            );

            // toosontsor
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar(path+"toosontsor.mp3",
                            path+"toosontsor"),
                    150,
                    new File(path+"toosontsor.png")
            );

            // zuudnii_uneg
            MoodbarAdapter.moodToImage(
                    MoodbarAdapter.buildMoodbar(path+"zuudnii_uneg.mp3",
                            path+"zuudnii_uneg"),
                    150,
                    new File(path+"zuudnii_uneg.png")
            );

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
