package mn.von.gestalt.utility.grimoire;

public class VideoUtils {

    public static void encodeToVideo(String frameDir, String outputVideo) {


        //TODO: count frame
        //TODO: encode
        String cmd = "ffmpeg -r 1/5 -i img%03d.png -c:v libx264 -vf fps=25 -pix_fmt yuv420p out.mp4";


    }

}
