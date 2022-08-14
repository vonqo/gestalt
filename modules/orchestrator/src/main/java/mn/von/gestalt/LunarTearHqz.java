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
import java.util.Random;

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
        MUCHKA_BDAY_PRINTABLE,
        PHOTON_CUBE,
        PULSE,
        CARDIAC,
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
        } else if(type == Types.MUCHKA_BDAY_PRINTABLE) {
            buildMuchkaBdayPrintable(totalFrame-1,totalColorFrame, moodbar, spectrumData, rays, output);
        } else if(type == Types.PULSE) {
            System.out.println("building pulse");
            buildPulse(totalFrame-1, totalColorFrame, moodbar, spectrumData, rays, output);
        } else if(type == Types.CARDIAC) {
            System.out.println("building cardiac");
            buildCardiac(totalFrame-1, totalColorFrame, moodbar, spectrumData, rays, output);
        } else if(type == Types.PHOTON_CUBE) {
            System.out.println("photon_cube");
            buildPhotonCube(totalFrame-1,totalColorFrame, moodbar, spectrumData, rays, output);
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
                        } else if(type == Types.MUCHKA_BDAY_PRINTABLE) {
                            buildMuchkaBdayPrintable(atomicFrame, totalColorFrame, moodbar, spectrumData, rays, new File(Config.RESOURCE_DIR+"/"+name+atomicFrame+"."+Config.OUTPUT_IMAGE_FORMAT));
                        } else {
                            System.out.println("UNSUPPORTED TYPE");
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
        int radius = 45; int padding = 540-radius;
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
        Material material4 = HQZUtils.buildMaterial(0,0.3f,.07f);
        materials.add(material4);
        Material material5 = HQZUtils.buildMaterial(0.2f,0.3f,.05f);
        materials.add(material5);
        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,1080,0));
        objects.add(HQZUtils.buildObject(0,1080,0,1080,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,1080));
        objects.add(HQZUtils.buildObject(0,1080,0,0,1080));
        int offset = 200;
        int distance = 1080;

//        Random random = new Random();
//
//        List<MaterialExtension> ext = new ArrayList<>();
//        ext.add(new MaterialExtension(random.nextInt(360)-90, random.nextInt(360)-90));
//        ext.add(new MaterialExtension(random.nextInt(360)-90, random.nextInt(360)-90));
//        ext.add(new MaterialExtension(random.nextInt(360)-90, random.nextInt(360)-90));
//        ext.add(new MaterialExtension(random.nextInt(360)-90, random.nextInt(360)-90));
//        ext.add(new MaterialExtension(random.nextInt(360)-90, random.nextInt(360)-90));
//        ext.add(new MaterialExtension(random.nextInt(360)-90, random.nextInt(360)-90));
//        ext.add(new MaterialExtension(random.nextInt(360)-90, random.nextInt(360)-90));

        // objects.addAll(HQZUtils.buildCircle(5, 1080 / 2, 1080 / 2, 160));
        objects.addAll(HQZUtils.buildRegularHexagon(5, 1080 / 2, 1080 / 2, 130));
        objects.addAll(HQZUtils.buildRegularPentagon(5, 1080 / 2, 1080 / 2, 250));
        objects.addAll(HQZUtils.buildRegularSquare(5, 1080 / 2, 1080 / 2, 400));
        objects.addAll(HQZUtils.buildRegularTriangle(5, 1080 / 2, 1080 / 2, 500));
//        objects.add(HQZUtils.buildObject(4,0,offset,720,distance,0,360));
//        objects.add(HQZUtils.buildObject(4,offset,0,180,0,distance,180));
//        objects.add(HQZUtils.buildObject(4,1080-offset,0,480,0,distance,270));
//        objects.add(HQZUtils.buildObject(4,0,1080-offset,90,distance,0,160));
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
        int radius = 40; int padding = (screenHeight/2)-radius;
        int xPaddingAddition = 150;
        double unitSpace = Math.PI * 2 / moodbar.size();
        double theta = Math.PI;
        float colorPower = 0.00215f;

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
                HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, x, y, colorPower);
            }

            lightList.add(lightRed);
            lightList.add(lightGreen);
            lightList.add(lightBlue);
        }

        Scene scene = HQZUtils.initializeScene(rays,screenWidth,screenHeight, 0.2f, 2.2f);
        // Scene scene = HQZUtils.initializeScene(rays, screenWidth, screenHeight, 0.237f, 2.5f);
        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.1f,0.0f,0.9f);
        materials.add(material1);
        Material material2 = HQZUtils.buildMaterial(0.75f,0.25f,0.0f);
        materials.add(material2);
        Material material3 = HQZUtils.buildMaterial(0.55f,0.45f,0.0f);
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
        hexExt.add(new MaterialExtension(0,0 ));
        hexExt.add(new MaterialExtension(-23,17 ));
        hexExt.add(new MaterialExtension(42,61 ));
        hexExt.add(new MaterialExtension(36,41 ));
        hexExt.add(new MaterialExtension(41,56 ));
        hexExt.add(new MaterialExtension(21,72 ));
        hexExt.add(new MaterialExtension(12,67 ));

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

    private void buildMuchkaBdayPrintable(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
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

                int edge = DataUtils.getRandomNumberInRange(2,6);
                List<ZObject> objs = null;
                int r2 = baseRadius+(int)(dynamicRadius * bubbleSizeList.get(i));

                switch (edge) {
                    case 2:
                        objs = HQZUtils.buildCircle(1,pointX,pointY,r2);
                        break;
                    case 3:
                        objs = HQZUtils.buildRegularTriangle(1,pointX,pointY,r2);
                        break;
                    case 4:
                        objs = HQZUtils.buildRegularSquare(1,pointX,pointY,r2);
                        break;
                    case 5:
                        objs = HQZUtils.buildRegularPentagon(1,pointX,pointY,r2);
                        break;
                    case 6:
                        objs = HQZUtils.buildRegularHexagon(1,pointX,pointY,r2);
                        break;
                }

                objects.addAll(objs);



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

    private void buildPhotonCube(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
        long startTime = System.currentTimeMillis();
        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        int screenWidth = 5906;
        int screenHeight = 8350;

        Scene scene = HQZUtils.initializeScene(rays, screenWidth, screenHeight, 0.4f, 2.0f);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
//        Material material1 = HQZUtils.buildMaterial(0.0f,0.1f,0.9f);
        Material material1 = HQZUtils.buildMaterial(0.0f,0.8f,0.2f);
        materials.add(material1);

        // ================ OBJECTS =============== // // ================ LIGHTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,screenHeight));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,0,screenHeight));

        int paddingX = 210;
        int paddingY = 220;
        ArrayList<Double> bubbleSizeList = DataUtils.spectogramMinMaxToPercent(spectrumData, moodbar.size());

        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(2); polarDist.add(35);

        ArrayList<Integer> polarAngle = new ArrayList<Integer>();
        polarAngle.add(0); polarAngle.add(360);

        ArrayList<Integer> rayAngle = new ArrayList<Integer>();
        rayAngle.add(0); rayAngle.add(360);

        int marginY = 0;
        int marginX = 0;

        float colorPower = 0.00050f;

        for(int y = 1, i = 0; y <= 37 && i <= completedColors; y++) {
            for(int x = 1; x <= 27 && i <= completedColors; x++, i++) {

                int pointY = y * paddingY;
                int pointX = x * paddingX;

                pointY += marginY;
                pointX += marginX;


                List<MaterialExtension> ext = new ArrayList<>();

                int l = (int) (bubbleSizeList.get(i) * 180);
                int r = Math.max(l - 25, 0);

                ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(r, l),DataUtils.getRandomNumberInRange(r, l)));
                ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(r, l),DataUtils.getRandomNumberInRange(r, l)));
                ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(r, l),DataUtils.getRandomNumberInRange(r, l)));
                ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(r, l),DataUtils.getRandomNumberInRange(r, l)));
                List<ZObject> objs = HQZUtils.buildWalledCube(1, pointX, pointY, 100, 230, ext);
                // List<ZObject> objs = HQZUtils.buildRegularSquare(1, pointX, pointY, 40, ext);
                objects.addAll(objs);

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

    private void buildPulse(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
        long startTime = System.currentTimeMillis();
        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        System.out.print("building frame: "+frameIndex);

        int screenSize = 1080;
        Scene scene = HQZUtils.initializeScene(rays, screenSize, screenSize, 0.3f, 1.1f);

        // ================ LIGHTS =============== //
        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(1000);
        int radius = 24;
        int padding = (screenSize/2)-radius;
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
        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.0f,0.7f,0.3f);
        materials.add(material1);

        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,1080,0));
        objects.add(HQZUtils.buildObject(0,1080,0,1080,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,1080));
        objects.add(HQZUtils.buildObject(0,1080,0,0,1080));

        int pulseRadius = 450;
        int extensionSize = HQZUtils.findCircleOptimalLineCount(pulseRadius);
        List<MaterialExtension> ext = new ArrayList<>();
        for(int i = 0; i < extensionSize+1; i++) {
            ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(-90, 90), DataUtils.getRandomNumberInRange(-90, 90)));
        }
        objects.addAll(HQZUtils.buildCircle(1,(screenSize/2),(screenSize/2),pulseRadius, ext));

        pulseRadius = 280;
        extensionSize = HQZUtils.findCircleOptimalLineCount(pulseRadius);
        ext = new ArrayList<>();
        for(int i = 0; i < extensionSize+1; i++) {
            ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(-90, 90), DataUtils.getRandomNumberInRange(-90, 90)));
        }
        objects.addAll(HQZUtils.buildCircle(1,(screenSize/2),(screenSize/2),pulseRadius, ext));

        scene.setObjects(objects);

        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
    }

    private void buildCardiac(int frameIndex, float totalColorFrame, ArrayList<Color> moodbar, double[][] spectrumData, long rays, File output) throws IOException {
        long startTime = System.currentTimeMillis();
        float frame = frameIndex / totalColorFrame;
        int completedColors = (int)frame;
        float inProgressColor = frame - completedColors;

        System.out.print("building frame: "+frameIndex);

        int screenSize = 1080;
        Scene scene = HQZUtils.initializeScene(rays, screenSize, screenSize, 0.19f, 0.9f);

        // ================ LIGHTS =============== //
        List<Light> lightList = new ArrayList<Light>();
        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(0); polarDist.add(1000);
        int radius = 24;
        int padding = (screenSize/2)-radius;
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
        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.0f,0.7f,0.3f);
        materials.add(material1);
        Material material2 = HQZUtils.buildMaterial(0.0f,0.7f,0.3f);
        materials.add(material2);

        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,1080,0));
        objects.add(HQZUtils.buildObject(0,1080,0,1080,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,1080));
        objects.add(HQZUtils.buildObject(0,1080,0,0,1080));

        // =========================================================================== //
        int cardiacSize = 15;
        int extensionSize = 80;
        List<MaterialExtension> ext = new ArrayList<>();
        for(int i = 0; i < extensionSize+1; i++) {
            ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(-90, 90), DataUtils.getRandomNumberInRange(-90, 90)));
        }
        objects.addAll(HQZUtils.buildCardiac(2,extensionSize,(screenSize/2),(screenSize/2),cardiacSize, ext));
        // =========================================================================== //

        // =========================================================================== //
        cardiacSize = 32;
        extensionSize = 95;
        ext = new ArrayList<>();
        for(int i = 0; i < extensionSize+1; i++) {
            ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(-90, 90), DataUtils.getRandomNumberInRange(-90, 90)));
        }
        objects.addAll(HQZUtils.buildCardiac(2,extensionSize,(screenSize/2),(screenSize/2),cardiacSize, ext));
        // =========================================================================== //

//        // =========================================================================== //
//        int pulseRadius = 425;
//        extensionSize = HQZUtils.findCircleOptimalLineCount(pulseRadius);
//        ext = new ArrayList<>();
//        for(int i = 0; i < extensionSize+1; i++) {
//            ext.add(new MaterialExtension(DataUtils.getRandomNumberInRange(-90, 90), DataUtils.getRandomNumberInRange(-90, 90)));
//        }
//        objects.addAll(HQZUtils.buildCircle(1,(screenSize/2),(screenSize/2),pulseRadius, ext));
//        // =========================================================================== //

        scene.setObjects(objects);

        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("frame "+frameIndex+" in milliseconds: " + timeElapsed);
    }

    public void buildDrawing(int screenWidth, int screenHeight, List<ZObject> drawObjects, ArrayList<Color> moodbar, long rays, File output) throws IOException {
//        int screenWidth = 1080;
//        int screenHeight = 1920;
        Scene scene = HQZUtils.initializeScene(rays, screenWidth, screenHeight, 0.15f, 0.8f);

        // ================ LIGHTS =============== //
        List<Light> lightList = new ArrayList<Light>();

        ArrayList<Integer> polarDist = new ArrayList<Integer>();
        polarDist.add(2); polarDist.add(360);

        ArrayList<Integer> polarAngle = new ArrayList<Integer>();
        polarAngle.add(0); polarAngle.add(360);

        ArrayList<Integer> rayAngle = new ArrayList<Integer>();
        rayAngle.add(0); rayAngle.add(360);

        for(int y = 1, i = 0; y <= 25; y++) {
            for(int x = 1; x <= 40; x++, i++) {
                int pointY = y * 43;
                int pointX = x * 48;

                Light lightRed = new Light();
                Light lightGreen = new Light();
                Light lightBlue = new Light();
                MixedLight mixedLight = new MixedLight(lightRed,lightGreen,lightBlue);
                HQZUtils.buildRGBLight(mixedLight, moodbar.get(i), polarDist, polarAngle, rayAngle, pointX, pointY, 0.00055f);

                lightList.add(lightRed);
                lightList.add(lightGreen);
                lightList.add(lightBlue);
            }
        }

        scene.setLights(lightList);

        // ================ MATERIALS =============== //
        List<Material> materials = new ArrayList<Material>();
        materials.add(HQZUtils.buildMaterial(0.0f,0.0f,1.0f));
        Material material1 = HQZUtils.buildMaterial(0.0f,0.0f,1.0f);
        materials.add(material1);
        scene.setMaterials(materials);

        // ================ OBJECTS =============== //
        List<ZObject> objects = new ArrayList<ZObject>();
        objects.add(HQZUtils.buildObject(0,0,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,screenWidth,0));
        objects.add(HQZUtils.buildObject(0,0,0,0,screenHeight));
        objects.add(HQZUtils.buildObject(0,screenWidth,0,0,screenHeight));
        objects.addAll(drawObjects);
        scene.setObjects(objects);

        HQZAdapter adapter = new HQZAdapter();
        adapter.buildPhoton(scene, output);
    }
}
