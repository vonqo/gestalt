package mn.von.gestalt.utility.grimoire;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FileUtils {

    public static void moodbarToFile(ArrayList<Color> moodbar, String output) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(output, "UTF-8");
        writer.println("Moodbar color codes | Color Note Code (PunkOwl)");
        writer.println("============================");
        for(Color color : moodbar) {
            writer.println("rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")");
        }

        writer.close();
    }

}
