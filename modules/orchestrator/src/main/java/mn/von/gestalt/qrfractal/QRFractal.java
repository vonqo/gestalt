package mn.von.gestalt.qrfractal;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.time.LocalDateTime;
import java.util.*;
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
    private Queue<QRCode> qrBuffer;
    private BitMatrix matrixBuffer;
    private Random randomGenerator;
    private ArrayList<String> textBank;
    private Thread thread;
    private AtomicBoolean RENDERING = new AtomicBoolean(true);

    public QRFractal() {
        randomGenerator = new Random();
        qrBuffer = new LinkedList<QRCode>();
        matrixBuffer = new BitMatrix(WINDOW_SIZE);

        this.initializeTextBank();


        qrBuffer.add(new QRCode(this.getRandomString(), WINDOW_SIZE));
        qrBuffer.add(new QRCode(this.getRandomString(), WINDOW_SIZE));
    }

    private void initializeTextBank() {
        String[] texts = {
                "Then you can't directly instantiate the interface Queue<E>. But, you still can refer to an object that implements the Queue interface by the type of the interface, like:",
        };
        textBank = new ArrayList<String>(Arrays.asList(texts));
    }

    private String getRandomString() {
        return textBank.get(randomGenerator.nextInt(textBank.size()));
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
                        do {
                            System.out.println("draw");
                            Graphics graphics = bs.getDrawGraphics();
                            graphics.setColor(Color.WHITE);
                            graphics.fillRect(0, 0, WINDOW_SIZE, WINDOW_SIZE);
                            QRCode qrCode = qrBuffer.peek();
                            try {
                                qrCode.resizeMatrix(800);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                            graphics.setColor(Color.BLACK);
                            for (int i = 0; i < WINDOW_SIZE; i++) {
                                for (int j = 0; j < WINDOW_SIZE; j++) {
                                    if (qrCode.getByteMatrix().get(i, j)) {
                                        graphics.fillRect(i, j, 1, 1);
                                    }
                                }
                            }
                            graphics.dispose();

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
