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
import java.util.List;

/**
 QR Element

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class QRCode implements Serializable {

    private String TEXT;
    private Integer SIZE;
    private BitMatrix BYTE_MATRIX;
    private Integer UNIT_SIZE;
    private Integer BORDER_SPACE;
    private Map<EncodeHintType, Object> HINT_MAP;
    private List<Integer> RANDOM_UNIT_LIST;
    private BufferedImage BIMAGE;
    private static QRCodeWriter QR_WRITER;

    public QRCode(String TEXT, Integer SIZE) {
        super();
        this.TEXT = TEXT;
        this.SIZE= SIZE;
        HINT_MAP = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        HINT_MAP.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        HINT_MAP.put(EncodeHintType.MARGIN, 0);
        QR_WRITER = new QRCodeWriter();
        try {
            this.generateMatrix();
            this.normalizeRandomList();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void generateMatrix() throws WriterException {
        BYTE_MATRIX = QR_WRITER.encode(TEXT, BarcodeFormat.QR_CODE, SIZE, SIZE, HINT_MAP);
        int size = BYTE_MATRIX.getWidth();
        for(int i = 0; i < size; i++) {
            if(BYTE_MATRIX.get(i,i)){
                BORDER_SPACE = i;
                break;
            }
        }
        for(int i = BORDER_SPACE; i < size; i++) {
            if(!BYTE_MATRIX.get(i,i)) {
                UNIT_SIZE = i - BORDER_SPACE;
                break;
            }
        }
        System.out.println("unit size: "+UNIT_SIZE);
    }

    private List<Integer> normalizeRandomList() {
        int size = BYTE_MATRIX.getWidth() - BORDER_SPACE - 1;
        System.out.println("border space: "+BORDER_SPACE);
        System.out.println("actual size: "+BYTE_MATRIX.getWidth());
        System.out.println("size: "+size);
        for(int x = BORDER_SPACE; x < size; x += UNIT_SIZE) {
            for(int y = BORDER_SPACE; y < size; y += UNIT_SIZE) {
                if(BYTE_MATRIX.get(x,y)) {
                    System.out.print("#");
                } else {
                    System.out.print("_");
                }
            }
            System.out.println();
        }
        return null;
    }

    public BufferedImage getAsImage() {
        int size = BYTE_MATRIX.getWidth() - (BORDER_SPACE * 2) - 1;
        BIMAGE = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        BIMAGE.createGraphics();
        Graphics2D graphics = (Graphics2D) BIMAGE.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size, size);

        size += BORDER_SPACE;
        graphics.setColor(Color.BLACK);
        for (int x = BORDER_SPACE, x2 = 0; x < size; x++, x2++) {
            for (int y = BORDER_SPACE, y2 = 0; y < size; y++, y2++) {
                if (BYTE_MATRIX.get(x, y)) {
                    graphics.fillRect(x2, y2, 1, 1);
                }
            }
        }
        return BIMAGE;
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
        return BYTE_MATRIX;
    }

    public void setByteMatrix(BitMatrix byteMatrix) {
        this.BYTE_MATRIX = byteMatrix;
    }

    public BufferedImage getImage() {
        return BIMAGE;
    }

    public void setImage(BufferedImage image) {
        this.BIMAGE = image;
    }
}
