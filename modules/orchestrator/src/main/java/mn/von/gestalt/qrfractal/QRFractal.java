package mn.von.gestalt.qrfractal;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 QR Fractal zooming

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class QRFractal extends Canvas {

    int i = 0;
    private Integer WINDOW_SIZE = 800;
    private ArrayList<QRCode> qrs;
    private ArrayList<String> textBank;
    private Thread thread;
    private AtomicBoolean RENDERING = new AtomicBoolean(true);

    public QRFractal() {

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
    public Dimension getPreferredSize() {
        return new Dimension(WINDOW_SIZE, WINDOW_SIZE);
    }

    public void stop() {
        if (thread != null) {
            RENDERING.set(false);
            try {
                thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void start() {

        this.initializeTextBank();
        RENDERING.set(true);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                createBufferStrategy(3);
                do {
                    BufferStrategy bs = getBufferStrategy();
                    while (bs == null) {
                        bs = getBufferStrategy();
                    }
                    do {
                        // The following loop ensures that the contents of the drawing buffer
                        // are consistent in case the underlying surface was recreated
                        do {
                            // Get a new graphics context every time through the loop
                            // to make sure the strategy is validated
                            System.out.println("draw");
                            Graphics graphics = bs.getDrawGraphics();

                            // Render to graphics
                            // ...
                            graphics.setColor(Color.RED);
                            graphics.fillRect(0, 0, 100, 100);
                            // Dispose the graphics
                            graphics.dispose();

                            // Repeat the rendering if the drawing buffer contents
                            // were restored
                        } while (bs.contentsRestored());

                        System.out.println(LocalDateTime.now().getSecond() + " show " + (i++));
                        // Display the buffer
                        bs.show();
                    } while (bs.contentsLost());
                    System.out.println("done");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } while (RENDERING.get());
            }
        });
        thread.start();
    }
}
