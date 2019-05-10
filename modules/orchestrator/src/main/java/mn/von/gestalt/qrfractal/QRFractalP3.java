package mn.von.gestalt.qrfractal;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

/**
 QR Fractal zooming with Processing 3

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class QRFractalP3 extends PApplet {

    private Integer WINDOW_SIZE = 800;
    private Queue<PImage> qrBuffer;
    private Random randomGenerator;
    private ArrayList<String> textBank;

    public void setup() {
        size(WINDOW_SIZE, WINDOW_SIZE);
        initializeTextBankAndImages();
    }

    private void initializeTextBankAndImages() {
        randomGenerator = new Random();
        String[] texts = {
                "1 1 1 1 1 1 1 1 dsa sadasK LJFS FJASKLF JSLDK;F SADJFLKSDFKJ LSDJ FSFKLJDSLK;FJLS;KDJ FLK;JS FSDFSAD",
        };
        textBank = new ArrayList<String>(Arrays.asList(texts));
        qrBuffer = new LinkedList<PImage>();
        qrBuffer.add(new QRCode(getRandomString(), WINDOW_SIZE).getAsPImage());
        qrBuffer.add(new QRCode(getRandomString(), WINDOW_SIZE).getAsPImage());
    }

    private String getRandomString() {
        return textBank.get(randomGenerator.nextInt(textBank.size()));
    }

    public void draw(){
        background(255);
        ellipse(mouseX, mouseY, 20, 20);
        image(qrBuffer.peek(), 0, 0);
    }

}
