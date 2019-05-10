package mn.von.gestalt.qrfractal;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import processing.core.PConstants;
import processing.core.PImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 QR Element

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class QRCode implements Serializable {

    private String TEXT;
    private Integer SIZE;
    private BitMatrix byteMatrix;
    private Integer UNIT_SIZE;
    private Integer BORDER_SPACE;
    private Map<EncodeHintType, Object> hintMap;
    private BufferedImage image;
    private static QRCodeWriter qrCodeWriter;

    public QRCode(String TEXT, Integer SIZE) {
        super();
        this.TEXT = TEXT;
        this.SIZE= SIZE;
        hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 0);
        qrCodeWriter = new QRCodeWriter();
        try {
            this.generateMatrix();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void generateMatrix() throws WriterException {
        byteMatrix = qrCodeWriter.encode(TEXT, BarcodeFormat.QR_CODE, SIZE, SIZE, hintMap);
        int size = byteMatrix.getWidth();
        for(int i = 0; i < size; i++) {
            if(byteMatrix.get(i,i)){
                BORDER_SPACE = i;
                break;
            }
        }
        for(int i = BORDER_SPACE; i < size; i++) {
            if(!byteMatrix.get(i,i)) {
                UNIT_SIZE = i;
                break;
            }
        }
    }

    public BufferedImage getAsImage() {
        int size = byteMatrix.getWidth() - (BORDER_SPACE * 2) - 1;
        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size, size);

        size += BORDER_SPACE;
        graphics.setColor(Color.BLACK);
        for (int x = BORDER_SPACE, x2 = 0; x < size; x++, x2++) {
            for (int y = BORDER_SPACE, y2 = 0; y < size; y++, y2++) {
                if (byteMatrix.get(x, y)) {
                    graphics.fillRect(x2, y2, 1, 1);
                }
            }
        }
        return image;
    }

    public PImage getAsPImage() {
        BufferedImage image = getAsImage();
        PImage img=new PImage(image.getWidth(),image.getHeight(), PConstants.ARGB);
        image.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
        img.updatePixels();
        return img;
    }

    public Integer getUNIT_SIZE() {
        return this.UNIT_SIZE;
    }

    public String getTEXT() {
        return TEXT;
    }

    public void setTEXT(String TEXT) {
        this.TEXT = TEXT;
    }

    public Integer getSIZE() {
        return SIZE;
    }

    public void setSIZE(Integer SIZE) {
        this.SIZE = SIZE;
    }

    public BitMatrix getByteMatrix() {
        return byteMatrix;
    }

    public void setByteMatrix(BitMatrix byteMatrix) {
        this.byteMatrix = byteMatrix;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
