package mn.von.gestalt;

import mn.von.gestalt.utility.config.Config;
import mn.von.gestalt.utility.grimoire.DataUtils;
import mn.von.gestalt.utility.thread.GestaltThreadPool;
import mn.von.gestalt.zenphoton.HQZAdapter;
import mn.von.gestalt.zenphoton.HQZUtils;
import mn.von.gestalt.zenphoton.dto.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LunarTearHqz {

    private static float colorPower = 0.00045f;

    public enum Types {
        TEST1,
        TORNADO,
        MATRIX,
        PYRAMID,
        TESSERACT,
        GRAPHTREE,
        BUBBLE2,
        BLANK,
    }

    public void build(Types type, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output, double audioDuration) throws IOException {

        int totalFrame = (int)(audioDuration * 24);
        float totalColorFrame = totalFrame / (float)moodbar.size();
        if(type == Types.TORNADO) {
            buildTornado(totalFrame-1,totalColorFrame,moodbar, spectrumData, rays, output);
        } else if(type == Types.BUBBLE2) {
            buildBubble2(totalFrame, totalColorFrame, moodbar, spectrumData, rays, output);
        }
    }

    public void buildFrames(Types type, ArrayList<Color> moodbar, double[][] spectrumData, long rays,  double audioDuration, int fps, String output) {
        System.out.println(audioDuration+"|"+fps);
        int totalFrame = (int)(audioDuration * fps);
        System.out.println("totalFrame: "+totalFrame);
        float totalColorFrame = totalFrame / (float)moodbar.size();
        System.out.println("totalColorFrame: "+totalColorFrame);

        GestaltThreadPool threadPool = new GestaltThreadPool();
        for(int frame = 0; frame < totalFrame; frame++) {
            int atomicFrame = frame;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(type == Types.TORNADO) {
                            StringBuilder name = new StringBuilder(output);
                            name.append("_");
                            for(int i = 0; i < (4-DataUtils.countDigit(atomicFrame)); i++) name.append("0");
                            buildTornado(atomicFrame, totalColorFrame, moodbar, spectrumData, rays, new File(Config.RESOURCE_DIR+"/"+name+atomicFrame+"."+ Config.OUTPUT_IMAGE_FORMAT));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void buildTornado(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
        System.out.print("building frame: "+frameIndex);

        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        // ================ LIGHTS =============== //
        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(1000);
        int radius = 85; int padding = 540-radius;
        double unitSpace = Math.PI * 2 / moodbar.size();
        double theta = Math.PI;

        for(int i = 0; i <= completedColors; i++, theta += unitSpace) {
            Light lightRed = new Light();
            Light lightGreen = new Light();
            Light lightBlue = new Light();
            MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);

            int x = (int)(Math.cos(theta) * radius) + radius + padding;
            int y = (int)(Math.sin(theta) * radius) + radius + padding;

            int degree = (int)Math.toDegrees(theta);
            degree += 0;
            ArrayList<Integer> polarAngle = new ArrayList<Integer>();
            polarAngle.add(degree-2); polarAngle.add(degree+2);

            ArrayList<Integer> rayAngle = new ArrayList<Integer>();
            rayAngle.add(degree-2); rayAngle.add(degree+2);

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

        Scene scene = HQZUtils.initializeScene(rays,1080,1080, 0.2f, 2.2f);
        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.1f,0.0f,0.9f);
        materials.add(material1);
        Material material2 = HQZUtils.buildMaterial(0.05f,0.8f,0.15f);
        materials.add(material2);
        Material material3 = HQZUtils.buildMaterial(0.7f,0,0.3f);
        materials.add(material3);
        Material material4 = HQZUtils.buildMaterial(0.0f,0.3f,.07f);
        materials.add(material4);
        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,1080,0));
        objects.add(HQZUtils.buildObject(0,1080,0,1080,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,1080));
        objects.add(HQZUtils.buildObject(0,1080,0,0,1080));
        int offset = 200;
        int distance = 1080;
        objects.add(HQZUtils.buildObject(4,0,offset,720,distance,0,360));
        objects.add(HQZUtils.buildObject(4,offset,0,180,0,distance,180));
        objects.add(HQZUtils.buildObject(4,1080-offset,0,480,0,distance,270));
        objects.add(HQZUtils.buildObject(4,0,1080-offset,90,distance,0,160));
        scene.setObjects(objects);

        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
    }

    private void buildBubble2(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
        System.out.println("WARNING! frame building is not supported!");
        System.out.print("building frame: "+frameIndex);

        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        Scene scene = HQZUtils.initializeScene(rays,1080,1080, 0.2f, 2.2f);
        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.1f,0.0f,0.9f);
        materials.add(material1);
        scene.setMaterials(materials);

        // ================ OBJECTS =============== // // ================ LIGHTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        List<Light> lightList = new ArrayList<Light>();
        objects.add(HQZUtils.buildObject(0,0,0,1080,0));
        objects.add(HQZUtils.buildObject(0,1080,0,1080,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,1080));
        objects.add(HQZUtils.buildObject(0,1080,0,0,1080));

        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(1000);

        ArrayList<Integer> polarAngle = new ArrayList<Integer>();
        polarAngle.add(0); polarAngle.add(360);

        ArrayList<Integer> rayAngle = new ArrayList<Integer>();
        rayAngle.add(0); rayAngle.add(360);

        for(int y = 0, i = 0 ,c = 0; y < 33; y++) {
            for(int x = 0; x < 30; x++, i++, c++) {

                objects.addAll(HQZUtils.buildCircle(1,x,y,50));

                Light lightRed = new Light();
                Light lightGreen = new Light();
                Light lightBlue = new Light();
                MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);
                HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, x, y);

                lightList.add(lightRed);
                lightList.add(lightGreen);
                lightList.add(lightBlue);
            }
        }

        scene.setObjects(objects);
        scene.setLights(lightList);
        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
    }
}
