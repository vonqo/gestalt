package mn.von.gestalt.utility.grimoire;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 Audio utility functions

 @author <A HREF="mailto:[enkh-amar.g@must.edu.mn]">[Enkh-Amar.G]</A>
 @version $Revision: 1.0
 @see [https://github.com/lupino22/gestalt]
 **/
public class AudioUtils {

    public static byte [] getAudioDataBytes(byte [] sourceBytes, AudioFormat audioFormat) throws UnsupportedAudioFileException, IllegalArgumentException, Exception {
        if(sourceBytes == null || sourceBytes.length == 0 || audioFormat == null){
            throw new IllegalArgumentException("Illegal Argument passed to this method");
        }

        try (final ByteArrayInputStream bais = new ByteArrayInputStream(sourceBytes);
             final AudioInputStream sourceAIS = AudioSystem.getAudioInputStream(bais)) {
            AudioFormat sourceFormat = sourceAIS.getFormat();
            AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(), sourceFormat.getChannels()*2, sourceFormat.getSampleRate(), false);
            try (final AudioInputStream convert1AIS = AudioSystem.getAudioInputStream(convertFormat, sourceAIS);
                 final AudioInputStream convert2AIS = AudioSystem.getAudioInputStream(audioFormat, convert1AIS);
                 final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte [] buffer = new byte[8192];
                while(true){
                    int readCount = convert2AIS.read(buffer, 0, buffer.length);
                    if(readCount == -1){
                        break;
                    }
                    baos.write(buffer, 0, readCount);
                }
                return baos.toByteArray();
            }
        }
    }

    public static void mp3ToWav(File mp3Data, String filePath) throws UnsupportedAudioFileException, IOException {
        // open stream
        AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(mp3Data);
        AudioFormat sourceFormat = mp3Stream.getFormat();
        // create audio format object for the desired stream/audio format
        // this is *not* the same as the file format (wav)
        System.out.println("sample rate: "+sourceFormat.getSampleRate());
        AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(), 16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);
        // create stream that delivers the desired format
        AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);
        // write stream into a file with file format wav
        AudioSystem.write(converted, AudioFileFormat.Type.WAVE, new File(filePath));
    }

    public static double getDuration(String filePath) throws IOException, UnsupportedAudioFileException {
        File file = new File(filePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        return (frames+0.0) / format.getFrameRate();
    }

}
