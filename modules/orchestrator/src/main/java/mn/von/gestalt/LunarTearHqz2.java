package mn.von.gestalt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.utility.config.dto.AudioDto;
import mn.von.gestalt.utility.config.dto.VideoExportDto;
import mn.von.gestalt.utility.grimoire.DataUtils;
import mn.von.gestalt.utility.thread.GestaltThreadPool;
import mn.von.gestalt.zenphoton.HQZAdapter;
import mn.von.gestalt.zenphoton.HQZUtils;
import mn.von.gestalt.zenphoton.dto.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LunarTearHqz2 {

    public enum Types {
        DRAWING,
    }

    public void buildFrames(Types type, ArrayList<Color> moodbar, double[][] spectrumData, double audioDuration, AudioDto audioDto, VideoExportDto videoExportDto) {

        System.out.println(audioDuration+"|"+videoExportDto.getFps());

        int totalFrame = (int)(audioDuration * videoExportDto.getFps());
        System.out.println("totalFrame: "+totalFrame);
        float totalColorFrame = totalFrame / (float)moodbar.size();
        System.out.println("totalColorFrame: "+totalColorFrame);

        String output = audioDto.getAudioFile().get(0);

        GestaltThreadPool threadPool = new GestaltThreadPool(videoExportDto.getUsableCore());

        for(int frame = videoExportDto.getStartFrame(); frame < totalFrame; frame++) {
            int atomicFrame = frame;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    StringBuilder name = new StringBuilder(output);
                    name.append("_");
                    for(int i = 0; i < (5- DataUtils.countDigit(atomicFrame)); i++) name.append("0");

                    if(type == Types.DRAWING) {
                        try {
                            buildDrawing(
                                atomicFrame,
                                totalColorFrame,
                                moodbar,
                                spectrumData,
                                audioDto,
                                videoExportDto,
                                new File(Config.RESOURCE_DIR+"/"+name+atomicFrame+"."+Config.OUTPUT_IMAGE_FORMAT)
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void buildDrawing(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, AudioDto audioDto, VideoExportDto videoExportDto, File output) throws IOException {
        System.out.println("building frame: "+frameIndex);

        long startTime = System.currentTimeMillis();
        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        int screenWidth = 1920;
        int screenHeight = 1080;

        Scene scene = HQZUtils.initializeScene(audioDto.getRay(), screenWidth, screenHeight, 0.4f, 2.0f);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.0f,0.1f,0.9f);
        materials.add(material1);
        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        final String objectFile = Config.RESOURCE_DIR + audioDto.getExtraDataFile();
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(objectFile));
        ArrayList<ArrayList<Integer>> extraObjects = gson.fromJson(br, new TypeToken<ArrayList<ArrayList<Integer>>>(){}.getType());
        ArrayList<ZObject> zObjects = new ArrayList<ZObject>();

        for(int i = 0; i < extraObjects.size(); i++) {
            zObjects.add(HQZUtils.buildObject(
                extraObjects.get(i).get(0),
                extraObjects.get(i).get(1),
                extraObjects.get(i).get(2),
                extraObjects.get(i).get(3),
                extraObjects.get(i).get(4)
            ));
        }

        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,screenHeight));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,0,screenHeight));
        objects.addAll(zObjects);
        scene.setObjects(objects);

        // ================ LIGHTS =============== //
        float colorPower = 0.00045f;
        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(1000);
        int degree = 0;
        double theta = 0;
        double unitSpace = 1.92;

        for(int i = 0; i <= completedColors; i++, theta += unitSpace) {
            Light lightRed = new Light();
            Light lightGreen = new Light();
            Light lightBlue = new Light();
            MixedLight mixedLight = new MixedLight(lightRed, lightGreen, lightBlue);

            int x = (screenHeight - 225);
            int y = (int)theta;

            ArrayList<Integer> polarAngle = new ArrayList<Integer>();
            polarAngle.add(degree); polarAngle.add(degree);

            ArrayList<Integer> rayAngle = new ArrayList<Integer>();
            rayAngle.add(degree); rayAngle.add(degree);

            if(i == completedColors && inProgressColor != 0) {
                float power = colorPower * inProgressColor;
                HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, x, y, power);
            } else {
                HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, x, y);
            }

            lightList.add(lightRed);
            lightList.add(lightGreen);
            lightList.add(lightBlue);
        }
        scene.setLights(lightList);

        // ================ FINALIZE =============== //
        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("frame "+frameIndex+" in milliseconds: " + timeElapsed);
    }

}
