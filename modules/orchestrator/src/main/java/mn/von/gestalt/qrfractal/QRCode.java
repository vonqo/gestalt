package mn.von.gestalt.qrfractal;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Hashtable;

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
    private Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap;
    private BufferedImage image;
    private static QRCodeWriter qrCodeWriter;

    public QRCode(String TEXT, Integer SIZE) {
        super();
        this.TEXT = TEXT;
        this.SIZE= SIZE;
        hintMap = new Hashtable<>();
        qrCodeWriter = new QRCodeWriter();
    }

    public void generateMatrix() throws WriterException {
        byteMatrix = qrCodeWriter.encode(TEXT, BarcodeFormat.QR_CODE, SIZE, SIZE, hintMap);
    }

    public BufferedImage getAsImage() {
        int size = byteMatrix.getWidth();
        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size, size);

        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return image;
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
