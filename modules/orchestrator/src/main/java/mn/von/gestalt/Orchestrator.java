package mn.von.gestalt;

import mn.von.gestalt.spectogram.dl4jDataVec.Spectrogram;
import mn.von.gestalt.spectogram.dl4jDataVec.Wave;

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


        String pathMp3 = "/Users/eirenevon/Desktop/test.mp3";
        String pathWav = "/Users/eirenevon/Desktop/test.wav";
//        try {
//            AudioUtils.mp3ToWav(new File(pathMp3), pathWav);
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Wave wav = new Wave(pathWav);
        Spectrogram spectrogram = new Spectrogram(wav, 2048, 0);
        double[][] data = spectrogram.getNormalizedSpectrogramData();

        System.out.println("=======================");
        System.out.println(wav.length());
        System.out.println(wav.size());
        System.out.println("=======================");
        System.out.println("data.len: "+data.length);
        System.out.println(data[0].length);
        System.out.println(data[1].length);
        System.out.println(data[2].length);
        System.out.println(data[3].length); //772 813


//        try {
//            // J.Chuluun - Uran Khas
//            MoodbarAdapter.moodToImage(
//                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/mood_test/urankhas.mp3",
//                            "/home/enkh-amar/Desktop/mood_test/urankhas"),
//                            150,
//                            new File("/home/enkh-amar/Desktop/mood_test/urankhas.png")
//            );
//
//            // Aphex Twin - Xtal
//            MoodbarAdapter.moodToImage(
//                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/mood_test/xtal.mp3",
//                            "/home/enkh-amar/Desktop/mood_test/xtal"),
//                            150,
//                            new File("/home/enkh-amar/Desktop/mood_test/xtal.png")
//            );
//
//            // Crystal Castle - Kept
//            MoodbarAdapter.moodToImage(
//                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/mood_test/kept.mp3",
//                            "/home/enkh-amar/Desktop/mood_test/kept"),
//                            150,
//                            new File("/home/enkh-amar/Desktop/mood_test/kept.png")
//            );
//
//            // Magnolian - Uvuljuu
//            MoodbarAdapter.moodToImage(
//                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/mood_test/magnolian.mp3",
//                            "/home/enkh-amar/Desktop/mood_test/magnolian"),
//                    150,
//                    new File("/home/enkh-amar/Desktop/mood_test/magnolian.png")
//            );
//
//            // Daft Punk - Harder, Better, Faster, Stronger
//            MoodbarAdapter.moodToImage(
//                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/mood_test/daftpunk.mp3",
//                            "/home/enkh-amar/Desktop/mood_test/daftpunk"),
//                    150,
//                    new File("/home/enkh-amar/Desktop/mood_test/daftpunk.png")
//            );
//
//            // Mohanik - Zuulun Misheel
//            MoodbarAdapter.moodToImage(
//                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/mood_test/mohanik.mp3",
//                            "/home/enkh-amar/Desktop/mood_test/mohanik"),
//                    150,
//                    new File("/home/enkh-amar/Desktop/mood_test/mohanik.png")
//            );
//
//
//            // The Hu - Yuve Yuve
//            MoodbarAdapter.moodToImage(
//                    MoodbarAdapter.buildMoodbar("/home/enkh-amar/Desktop/mood_test/yuve.mp3",
//                            "/home/enkh-amar/Desktop/mood_test/yuve"),
//                    150,
//                    new File("/home/enkh-amar/Desktop/mood_test/yuve.png")
//            );
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

}
