package mn.von.gestalt.zenphoton;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mn.von.gestalt.utility.grimoire.DataUtils;
import mn.von.gestalt.zenphoton.dto.*;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class HQZUtils {

    private static int absoluteRed = 635;
    private static int absoluteGreen = 520;
    private static int absoluteBlue = 465;
    private static float colorPower = 0.00045f;

    public static Scene initializeScene(long rays, int width, int height, float exposure, float gamma) {
        Scene scene = new Scene();
        Resolution reso = new Resolution();
        reso.setHeight(height);
        reso.setWidth(width);
        reso.toList();
        scene.setResolution(reso);
        Viewport viewport = new Viewport();
        viewport.setHeight(height);
        viewport.setWidth(width);
        viewport.setLeft(0); viewport.setTop(0);
        viewport.toList();
        scene.setViewport(viewport);
        scene.setRays(rays);
        scene.setExposure(exposure);
        scene.setGamma(gamma);
        return scene;
    }

    public static List<ZObject> getObjectsFromFile(String file) {
        StringBuilder fileBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(file), StandardCharsets.UTF_8)) {
            stream.forEach(s -> fileBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ZObject> objects = new ArrayList<>();
        JsonArray array = JsonParser.parseString(fileBuilder.toString()).getAsJsonArray();
        for(int i = 0; i < array.size(); i++) {
            JsonArray values = array.get(i).getAsJsonArray();
            ZObject obj;
            if(values.size() == 5) {
                obj = buildObject(
                        values.get(0).getAsInt(),
                        values.get(1).getAsInt(),
                        values.get(2).getAsInt(),
                        values.get(3).getAsInt(),
                        values.get(4).getAsInt()
                );
            } else {
                obj = buildObject(
                        values.get(0).getAsInt(),
                        values.get(1).getAsInt(),
                        values.get(2).getAsInt(),
                        values.get(3).getAsInt(),
                        values.get(4).getAsInt(),
                        values.get(5).getAsInt(),
                        values.get(6).getAsInt()
                );
            }

            objects.add(obj);
        }

        return objects;
    }

    public static Material buildMaterial(float transmissive, float reflective, float diffuse) {
        Material mater = new Material();

        MaterialProperty transmissiveProperty = new MaterialProperty();
        transmissiveProperty.setType(MaterialProperty.MaterialPropertyType.Transmissive);
        transmissiveProperty.setWeigth(transmissive);
        transmissiveProperty.wrapToList();
        mater.addMaterialProperty(transmissiveProperty);

        MaterialProperty reflectiveProperty = new MaterialProperty();
        reflectiveProperty.setType(MaterialProperty.MaterialPropertyType.Reflective);
        reflectiveProperty.setWeigth(reflective);
        reflectiveProperty.wrapToList();
        mater.addMaterialProperty(reflectiveProperty);

        MaterialProperty diffuseProperty = new MaterialProperty();
        diffuseProperty.setType(MaterialProperty.MaterialPropertyType.Diffuse);
        diffuseProperty.setWeigth(diffuse);
        diffuseProperty.wrapToList();
        mater.addMaterialProperty(diffuseProperty);

        return mater;
    }

    public static ZObject buildObject(int materialIndex, int x0, int y0, int dx, int dy) {
        ZObject obj = new ZObject();
        obj.setMaterialIndex(materialIndex);
        obj.setX0(x0); obj.setY0(y0);
        obj.setDx(dx); obj.setDy(dy);
        obj.toList();
        return obj;
    }

    public static ZObject buildObject(int materialIndex, int x0, int y0, int dx, int dy, int a0, int da) {
        ZObject obj = new ZObject();
        obj.setMaterialIndex(materialIndex);
        obj.setX0(x0); obj.setY0(y0); obj.setA0(a0);
        obj.setDx(dx); obj.setDy(dy); obj.setDa(da);
        obj.toListExtended();
        return obj;
    }

    public static void buildRGBLight(MixedLight mixedLight, Color color, List<Integer> polarDist, ArrayList<Integer> polarAngle, ArrayList<Integer> rayAngle, int x, int y) {
        Light red = mixedLight.getRed();
        red.setPolarDistance(polarDist);
        red.setCartesianX(x); red.setCartesianY(y);
        red.setLightPower(color.getRed() * colorPower);
        red.setWaveLength(absoluteRed);
        red.setPolarAngle(polarAngle);
        red.setRayAngle(rayAngle);

        Light green = mixedLight.getGreen();
        green.setPolarDistance(polarDist);
        green.setCartesianX(x); green.setCartesianY(y);
        green.setLightPower(color.getGreen() * colorPower);
        green.setWaveLength(absoluteGreen);
        green.setPolarAngle(polarAngle);
        green.setRayAngle(rayAngle);

        Light blue = mixedLight.getBlue();
        blue.setPolarDistance(polarDist);
        blue.setCartesianX(x); blue.setCartesianY(y);
        blue.setLightPower(color.getBlue() * colorPower);
        blue.setWaveLength(absoluteBlue);
        blue.setPolarAngle(polarAngle);
        blue.setRayAngle(rayAngle);

        red.toList(); blue.toList(); green.toList();
    }

    public static void buildRGBLight(MixedLight mixedLight, Color color, List<Integer> polarDist, ArrayList<Integer> polarAngle, ArrayList<Integer> rayAngle, int x, int y, float power) {
        Light red = mixedLight.getRed();
        red.setPolarDistance(polarDist);
        red.setCartesianX(x); red.setCartesianY(y);
        red.setLightPower(color.getRed() * power);
        red.setWaveLength(absoluteRed);
        red.setPolarAngle(polarAngle);
        red.setRayAngle(rayAngle);

        Light green = mixedLight.getGreen();
        green.setPolarDistance(polarDist);
        green.setCartesianX(x); green.setCartesianY(y);
        green.setLightPower(color.getGreen() * power);
        green.setWaveLength(absoluteGreen);
        green.setPolarAngle(polarAngle);
        green.setRayAngle(rayAngle);

        Light blue = mixedLight.getBlue();
        blue.setPolarDistance(polarDist);
        blue.setCartesianX(x); blue.setCartesianY(y);
        blue.setLightPower(color.getBlue() * power);
        blue.setWaveLength(absoluteBlue);
        blue.setPolarAngle(polarAngle);
        blue.setRayAngle(rayAngle);

        red.toList(); blue.toList(); green.toList();
    }

    public static List<ZObject> reposition(List<ZObject> objects, int offsetX, int offsetY) {
        List<ZObject> newObjects = new ArrayList<>();

        for (ZObject obj : objects) {
            ZObject newObject = buildObject(
                    obj.getMaterialIndex(),
                    obj.getX0() + offsetX,
                    obj.getY0() + offsetY,
                    obj.getDx(),
                    obj.getDy(),
                    obj.getA0(),
                    obj.getDa()
            );
            newObjects.add(newObject);
        }
        return newObjects;
    }

    public static List<ZObject> rotateObjectWithAnchor(List<ZObject> objects, double angle, int x, int y) {
        return rotateObject(reposition(objects, -x, -y), angle);
    }

    public static List<ZObject> rotateObject(List<ZObject> objects, double angle) {
        List<ZObject> newObjects = new ArrayList<>();
        double rad = angle * Math.PI/180;

        for (ZObject obj : objects) {
            int x1 = obj.getX0();
            int y1 = obj.getY0();

            int x2 = obj.getDx() + x1;
            int y2 = obj.getDy() + y1;

            double x1New = x1 * Math.cos(rad) - y1 * Math.sin(rad);
            double y1New = y1 * Math.cos(rad) + x1 * Math.sin(rad);

            double x2New = (x2 * Math.cos(rad) - y2 * Math.sin(rad)) - x1New;
            double y2New = (y2 * Math.cos(rad) + x2 * Math.sin(rad)) - y1New;


            ZObject newObject = buildObject(
                    obj.getMaterialIndex(),
                    (int) Math.round(x1New),
                    (int) Math.round(y1New),
                    (int) Math.round(x2New),
                    (int) Math.round(y2New),
                    obj.getA0(),
                    obj.getDa()
            );
            newObjects.add(newObject);
        }
        return newObjects;
    }

    public static List<ZObject> scaleObject(List<ZObject> objects, double scale) {
        List<ZObject> newObjects = new ArrayList<>();

        for (ZObject obj : objects) {
            ZObject newObject = buildObject(
                    obj.getMaterialIndex(),
                    (int) Math.round(obj.getX0() * scale),
                    (int) Math.round(obj.getY0() * scale),
                    (int) Math.round(obj.getDx() * scale),
                    (int) Math.round(obj.getDy() * scale),
                    obj.getA0(),
                    (int) Math.round(obj.getDa() * scale)
            );
            newObjects.add(newObject);
        }

        return  newObjects;
    }

    public static List<ZObject> buildCircle(int materialIndex, int x, int y, int radius) {
        int lineCount = findCircleOptimalLineCount(radius);
        return buildRegularPolygons(materialIndex, lineCount, x, y, radius,null);
    }

    public static List<ZObject> buildCircle(int materialIndex, int x, int y, int radius, List<MaterialExtension> ext) {
        int lineCount = findCircleOptimalLineCount(radius);
        return buildRegularPolygons(materialIndex, lineCount, x, y, radius, ext);
    }

    public static List<ZObject> buildRegularTriangle(int materialIndex, int x, int y, int radius) {
        return buildRegularPolygons(materialIndex, 3, x, y, radius,null);
    }

    public static List<ZObject> buildRegularSquare(int materialIndex, int x, int y, int radius) {
        return buildRegularPolygons(materialIndex, 4, x, y, radius,null);
    }

    public static List<ZObject> buildRegularSquare(int materialIndex, int x, int y, int radius, List<MaterialExtension> ext) {
        return buildRegularPolygons(materialIndex, 4, x, y, radius, ext);
    }

    public static List<ZObject> buildRegularPentagon(int materialIndex, int x, int y, int radius) {
        return buildRegularPolygons(materialIndex, 5, x, y, radius,null);
    }

    public static List<ZObject> buildRegularPentagon(int materialIndex, int x, int y, int radius, List<MaterialExtension> ext) {
        return buildRegularPolygons(materialIndex, 5, x, y, radius, ext);
    }

    public static List<ZObject> buildRegularHexagon(int materialIndex, int x, int y, int radius) {
        return buildRegularPolygons(materialIndex, 6, x, y, radius, null);
    }

    public static List<ZObject> buildRegularHexagon(int materialIndex, int x, int y, int radius, List<MaterialExtension> ext) {
        return buildRegularPolygons(materialIndex, 6, x, y, radius, ext);
    }

    private static List<ZObject> buildRegularPolygons(
            int materialIndex,
            int lineCount,
            int x, int y, int radius,
            List<MaterialExtension> ext
    ) {
        boolean isUsingExt = (ext != null && ext.size() == lineCount+1);

        List<ZObject> objects = new ArrayList<ZObject>();
        double theta = Math.PI;

        // CHANGE LATER !!!
//        int degree = DataUtils.getRandomNumberInRange(0, 180);
//        double rotation = Math.toRadians(degree);
//        theta += rotation;
        // CHANGE LATER !!! - END

        double unitSpace = 2 * Math.PI / lineCount;

        int preX = -1;
        int preY = -1;

        for(int i = 0; i <= lineCount; i++, theta += unitSpace) {
            int postX = (int)(Math.cos(theta) * radius) + x;
            int postY = (int)(Math.sin(theta) * radius) + y;

            if(preX > 0 && preY > 0) {
                if(isUsingExt) {
                    objects.add(buildObject(materialIndex, preX, preY, postX-preX, postY-preY,
                            ext.get(i).getA0(), ext.get(i).getDa()));
                } else {
                    objects.add(buildObject(materialIndex, preX, preY, postX-preX, postY-preY));
                }
            }

            preX = postX;
            preY = postY;
        }
        return objects;
    }

    public static int findCircleOptimalLineCount(int radius) {
        double p = 2 * Math.PI * radius;
        return (int)(p/Math.log(radius) * 1.5);
    }

    public static List<ZObject> buildCardiac(int materialIndex, int lineCount, int x, int y, int size, List<MaterialExtension> ext) {
        boolean isUsingExt = (ext != null && ext.size() == lineCount+1);

        List<ZObject> objects = new ArrayList<ZObject>();
        double unitSpace = 2 * Math.PI / lineCount;

        double theta = Math.PI;
        int preX = -1;
        int preY = -1;

        // CHANGE LATER !!!
//        int degree = DataUtils.getRandomNumberInRange(0, 180);
//        double rotation = Math.toRadians(degree);
//        theta += rotation;
        // CHANGE LATER !!! - END

        for(int i = 0; i <= lineCount; i++, theta += unitSpace) {

            int postX = (int)(-16*size*Math.pow(Math.sin(theta), 3)) + x;
            int postY = (int)(-(13*size*Math.cos(theta)-5*size*Math.cos(2*theta)-2*size*Math.cos(3*theta)-Math.cos(4*theta))) + y;

            if(preX > 0 && preY > 0) {
                if(isUsingExt) {
                    objects.add(buildObject(materialIndex, preX, preY, postX-preX, postY-preY, ext.get(i).getA0(), ext.get(i).getDa()));
                } else {
                    objects.add(buildObject(materialIndex, preX, preY, postX-preX, postY-preY));
                }
            }
            preX = postX;
            preY = postY;
        }

        return objects;
    }

    public static List<ZObject> buildWalledCube(int materialIndex, int x, int y, int size, int wallSize, List<MaterialExtension> ext) {
        boolean isUsingExt = (ext != null);

        List<ZObject> objects = new ArrayList<ZObject>();
        int pad = size / 2;

        int startX = x - pad;
        int startY = y - pad;

        int diff = (wallSize - size) / 2;

        objects.add(buildObject(materialIndex, startX, startY, wallSize, 0, ext.get(0).getA0(), ext.get(0).getDa()));
        objects.add(buildObject(materialIndex, startX + size, startY, 0, startY, ext.get(1).getA0(), ext.get(1).getDa()));
        objects.add(buildObject(materialIndex, startX, startY + size, size, 0, ext.get(2).getA0(), ext.get(2).getDa()));
        objects.add(buildObject(materialIndex, startX, startY, 0, wallSize, ext.get(3).getA0(), ext.get(3).getDa()));

        return  objects;
    }
}
