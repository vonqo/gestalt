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

    public enum Types {
        TEST1,
        TORNADO,
        TORNADO_WIDE,
        MATRIX,
        PYRAMID,
        TESSERACT,
        GRAPHTREE,
        BUBBLE2,
        BUBBLE2_PRINTABLE,
        BLANK,
    }

    public void build(Types type, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output, double audioDuration) throws IOException {

        int totalFrame = (int)(audioDuration * 24);
        float totalColorFrame = totalFrame / (float)moodbar.size();
        if(type == Types.TORNADO) {
            buildTornado(totalFrame-1,totalColorFrame,moodbar, spectrumData, rays, output);
        } else if(type == Types.BUBBLE2) {
            // buildBubble2Widescreen(totalFrame-1, totalColorFrame, moodbar, spectrumData, rays, output);
            buildBubble2(totalFrame-1, totalColorFrame, moodbar, spectrumData, rays, output);
        } else if(type == Types.BUBBLE2_PRINTABLE) {
            buildBubble2Printable(totalFrame-1, totalColorFrame, moodbar, spectrumData, rays, output);
        } else if(type == Types.TORNADO_WIDE) {
            buildTornadoWide(totalFrame-1, totalColorFrame, moodbar, rays, output);
        }
    }

    public void buildFrames(Types type, ArrayList<Color> moodbar, double[][] spectrumData, long rays,  double audioDuration, int fps, String output) {
        System.out.println(audioDuration+"|"+fps);
        int totalFrame = (int)(audioDuration * fps);
        System.out.println("totalFrame: "+totalFrame);
        float totalColorFrame = totalFrame / (float)moodbar.size();
        System.out.println("totalColorFrame: "+totalColorFrame);

//        try {
//            buildBubble2Widescreen(9000, totalColorFrame, moodbar, spectrumData, rays, new File(Config.RESOURCE_DIR+"/test2"+ Config.OUTPUT_IMAGE_FORMAT));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        GestaltThreadPool threadPool = new GestaltThreadPool();
        for(int frame = 0; frame < totalFrame; frame++) {
            int atomicFrame = frame;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        StringBuilder name = new StringBuilder(output);
                        name.append("_");
                        for(int i = 0; i < (5-DataUtils.countDigit(atomicFrame)); i++) name.append("0");
                        if(type == Types.TORNADO) {
                            buildTornado(atomicFrame, totalColorFrame, moodbar, spectrumData, rays, new File(Config.RESOURCE_DIR+"/"+name+atomicFrame+"."+ Config.OUTPUT_IMAGE_FORMAT));
                        } else if(type == Types.BUBBLE2) {
                            buildBubble2Widescreen(atomicFrame, totalColorFrame, moodbar, spectrumData, rays, new File(Config.RESOURCE_DIR+"/"+name+atomicFrame+"."+ Config.OUTPUT_IMAGE_FORMAT));
                        } else if(type == Types.TORNADO_WIDE) {
                            buildTornadoWide(atomicFrame, totalColorFrame, moodbar, rays, new File(Config.RESOURCE_DIR+"/"+name+atomicFrame+"."+Config.OUTPUT_IMAGE_FORMAT));
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
        float colorPower = 0.00045f;

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

    private void buildTornadoWide(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, long rays, File output) throws IOException {
        System.out.print("building frame: "+frameIndex);

        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        int screenWidth = 1920;
        int screenHeight = 1080;

        // ================ LIGHTS =============== //
        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(1000);
        int radius = 25; int padding = (screenHeight/2)-radius;
        int xPaddingAddition = 150;
        double unitSpace = Math.PI * 2 / moodbar.size();
        double theta = Math.PI;
        float colorPower = 0.00045f;

        for(int i = 0; i <= completedColors; i++, theta += unitSpace) {
            Light lightRed = new Light();
            Light lightGreen = new Light();
            Light lightBlue = new Light();
            MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);

            int x = (int)(Math.cos(theta) * radius) + radius + padding + xPaddingAddition;
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

        Scene scene = HQZUtils.initializeScene(rays, screenWidth, screenHeight, 0.2f, 2.2f);
        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.1f,0.0f,0.9f);
        materials.add(material1);
        Material material2 = HQZUtils.buildMaterial(0.3f,0.4f,0.2f);
        materials.add(material2);
        Material material3 = HQZUtils.buildMaterial(0.7f,0.0f,0.3f);
        materials.add(material3);
        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,screenHeight));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,0,screenHeight));

        // ============ MATERIAL EXTENSION ============= //
        List<MaterialExtension> hexExt = new ArrayList<>();
        hexExt.add(new MaterialExtension(32,180 ));
        hexExt.add(new MaterialExtension(0,0 ));
        hexExt.add(new MaterialExtension(0,0 ));
        hexExt.add(new MaterialExtension(0,0 ));
        hexExt.add(new MaterialExtension(0,0 ));
        hexExt.add(new MaterialExtension(0,0 ));
        hexExt.add(new MaterialExtension(0,0 ));

        List<MaterialExtension> squareExt = new ArrayList<>();
        squareExt.add(new MaterialExtension(0,0));
        squareExt.add(new MaterialExtension(0,0));
        squareExt.add(new MaterialExtension(0,0));
        squareExt.add(new MaterialExtension(0,0));
        squareExt.add(new MaterialExtension(0,0));

        List<ZObject> hexagon = HQZUtils.buildRegularHexagon(2, padding + xPaddingAddition + radius,
                padding + radius, 270, hexExt);
        List<ZObject> square = HQZUtils.buildRegularSquare(3, padding + xPaddingAddition + radius,
                padding + radius, 500, squareExt);

        objects.addAll(hexagon);
        objects.addAll(square);

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

        int screenWidth = 2650;
        int screenHeight = 2850;
//        int screenWidth = 2250;
//        int screenHeight = 2460;

        Scene scene = HQZUtils.initializeScene(rays, screenWidth, screenHeight, 0.2f, 1.0f);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
//        Material material1 = HQZUtils.buildMaterial(0.0f,0.1f,0.9f);
        Material material1 = HQZUtils.buildMaterial(0,0.999f,0.001f);
        materials.add(material1);

        // ================ OBJECTS =============== // // ================ LIGHTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,screenHeight));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,0,screenHeight));

        int padding = 3;
        int baseRadius = 10;
        int dynamicRadius = 30;
        int radius = baseRadius + dynamicRadius;
        ArrayList<Double> bubbleSizeList = DataUtils.spectogramMinMaxToPercent(spectrumData, moodbar.size());

        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(3);

        ArrayList<Integer> polarAngle = new ArrayList<Integer>();
        polarAngle.add(0); polarAngle.add(90);

        ArrayList<Integer> rayAngle = new ArrayList<Integer>();
        rayAngle.add(90); rayAngle.add(180);

//        for(int i = 0; i < bubbleSizeList.size(); i++) {
//            System.out.println(bubbleSizeList.get(i));
//        }

        for(int y = 1, i = 0; y <= 33; y++) {
            for(int x = 1; x <= 30; x++, i++) {
                int pointY = y * ((radius+padding) * 2) - radius;
                int pointX = x * ((radius+padding) * 2) - radius;

                objects.addAll(HQZUtils.buildCircle(1,pointX,pointY,baseRadius+(int)(dynamicRadius * bubbleSizeList.get(i))));

                Light lightRed = new Light();
                Light lightGreen = new Light();
                Light lightBlue = new Light();
                MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);
                HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, pointX, pointY, 0.00005f);

                lightList.add(lightRed);
                lightList.add(lightGreen);
                lightList.add(lightBlue);
            }
        }
        scene.setLights(lightList);
        scene.setMaterials(materials);
        scene.setObjects(objects);
        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
    }

    private void buildBubble2Widescreen(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {

        long startTime = System.currentTimeMillis();
        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        int screenWidth = 1920;
        int screenHeight = 1080;
//        int screenWidth = 2250;
//        int screenHeight = 2460;

        Scene scene = HQZUtils.initializeScene(rays, screenWidth, screenHeight, 0.2f, 1.0f);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
//        Material material1 = HQZUtils.buildMaterial(0.0f,0.1f,0.9f);
        Material material1 = HQZUtils.buildMaterial(0,0.9f,0.001f);
        materials.add(material1);

        // ================ OBJECTS =============== // // ================ LIGHTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,screenHeight));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,0,screenHeight));

        int padding = 1;
        int baseRadius = 5;
        int dynamicRadius = 15;
        int radius = baseRadius + dynamicRadius;
        ArrayList<Double> bubbleSizeList = DataUtils.spectogramMinMaxToPercent(spectrumData, moodbar.size());

        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(2); polarDist.add(35);

        ArrayList<Integer> polarAngle = new ArrayList<Integer>();
        polarAngle.add(0); polarAngle.add(360);

        ArrayList<Integer> rayAngle = new ArrayList<Integer>();
        rayAngle.add(0); rayAngle.add(360);

        int marginX = 50;
        int marginY = 80;

        float colorPower = 0.00125f;

        for(int y = 1, i = 0; y <= 23 && i <= completedColors; y++) {
            for(int x = 1; x <= 43 && i <= completedColors; x++, i++) {

                int pointY = y * ((radius+padding) * 2) - radius;
                int pointX = x * ((radius+padding) * 2) - radius;

                pointY += marginY;
                pointX += marginX;

                objects.addAll(HQZUtils.buildCircle(1,pointX,pointY,baseRadius+(int)(dynamicRadius * bubbleSizeList.get(i))));

                Light lightRed = new Light();
                Light lightGreen = new Light();
                Light lightBlue = new Light();
                MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);

                if(i == completedColors && inProgressColor != 0) {
                    float power =  colorPower * inProgressColor;
                    HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, pointX, pointY, power);
                } else {
                    HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, pointX, pointY, colorPower);
                }

                lightList.add(lightRed);
                lightList.add(lightGreen);
                lightList.add(lightBlue);


            }
        }
        scene.setLights(lightList);
        scene.setMaterials(materials);
        scene.setObjects(objects);
        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("frame "+frameIndex+" in milliseconds: " + timeElapsed);
    }

    /**
     * Printable size:B2 quality:300dpi
     * @param frameIndex
     * @param totalColorFrame
     * @param moodbar
     * @param spectrumData
     * @param rays
     * @param output
     * @throws IOException
     */
    private void buildBubble2Printable(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
        long startTime = System.currentTimeMillis();
        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        int screenWidth = 5906;
        int screenHeight = 8350;

        Scene scene = HQZUtils.initializeScene(rays, screenWidth, screenHeight, 0.3f, 1.0f);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
//        Material material1 = HQZUtils.buildMaterial(0.0f,0.1f,0.9f);
        Material material1 = HQZUtils.buildMaterial(0.1f,0.8f,0.002f);
        materials.add(material1);

        // ================ OBJECTS =============== // // ================ LIGHTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,screenHeight));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,0,screenHeight));

        int padding = 14;
        int baseRadius = 25;
        int dynamicRadius = 65;
        int radius = baseRadius + dynamicRadius;
        ArrayList<Double> bubbleSizeList = DataUtils.spectogramMinMaxToPercent(spectrumData, moodbar.size());

        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(2); polarDist.add(35);

        ArrayList<Integer> polarAngle = new ArrayList<Integer>();
        polarAngle.add(0); polarAngle.add(360);

        ArrayList<Integer> rayAngle = new ArrayList<Integer>();
        rayAngle.add(0); rayAngle.add(360);

        int marginY = 350;
        int marginX = 150;


        float colorPower = 0.00050f;

        for(int y = 1, i = 0; y <= 37 && i <= completedColors; y++) {
            for(int x = 1; x <= 27 && i <= completedColors; x++, i++) {

                int pointY = y * ((radius+padding) * 2) - radius;
                int pointX = x * ((radius+padding) * 2) - radius;

                pointY += marginY;
                pointX += marginX;

                objects.addAll(HQZUtils.buildCircle(1,pointX,pointY,baseRadius+(int)(dynamicRadius * bubbleSizeList.get(i))));

                Light lightRed = new Light();
                Light lightGreen = new Light();
                Light lightBlue = new Light();
                MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);

                if(i == completedColors && inProgressColor != 0) {
                    float power =  colorPower * inProgressColor;
                    HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, pointX, pointY, power);
                } else {
                    HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, pointX, pointY, colorPower);
                }

                lightList.add(lightRed);
                lightList.add(lightGreen);
                lightList.add(lightBlue);


            }
        }
        scene.setLights(lightList);
        scene.setMaterials(materials);
        scene.setObjects(objects);
        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("frame "+frameIndex+" in milliseconds: " + timeElapsed);
    }
}
