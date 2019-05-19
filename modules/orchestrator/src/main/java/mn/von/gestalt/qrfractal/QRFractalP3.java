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

    private Integer WINDOW_SIZE = 450;
    private Queue<QRCode> qrBuffer;
    private Random randomGenerator;
    private ArrayList<String> textBank;

    public void setup() {
        size(WINDOW_SIZE, WINDOW_SIZE);
        initializeTextBankAndImages();
    }

    private void initializeTextBankAndImages() {
        randomGenerator = new Random();
        String[] texts = {
                "1",
        };
        textBank = new ArrayList<String>(Arrays.asList(texts));
        qrBuffer = new LinkedList<QRCode>();
        qrBuffer.add(new QRCode(getRandomString(), WINDOW_SIZE));
    }

    private String getRandomString() {
        return textBank.get(randomGenerator.nextInt(textBank.size()));
    }

    private UnitPoint getRandomPoint() {
        List<Integer> units = qrBuffer.peek().getRANDOM_UNIT_LIST();
        int size = qrBuffer.peek().getSIZE();
        int nominee = units.get(randomGenerator.nextInt(units.size()));
        UnitPoint point = new UnitPoint();
        point.setX(nominee / size);
        point.setY(nominee % size);
        return point;
    }

    public void draw(){
        background(25);
        PImage image = qrBuffer.peek().getAsPImage();
        image(image, 0, 0);
        // System.out.println(frameRate);
    }

}

class UnitPoint {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}