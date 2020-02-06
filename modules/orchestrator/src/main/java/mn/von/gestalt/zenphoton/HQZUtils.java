package mn.von.gestalt.zenphoton;

import mn.von.gestalt.zenphoton.dto.Material;
import mn.von.gestalt.zenphoton.dto.MaterialProperty;
import mn.von.gestalt.zenphoton.dto.ZObject;

public class HQZUtils {

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

    public static ZObject buildObject(int materialIndex, int x0, int y0, int a0, int dx, int dy, int da) {
        ZObject obj = new ZObject();
        obj.setMaterialIndex(materialIndex);
        obj.setX0(x0); obj.setY0(y0); obj.setA0(a0);
        obj.setDx(dx); obj.setDy(dy); obj.setDa(da);
        obj.toListExtended();
        return obj;
    }

}
