package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.List;

public class Material implements Serializable {

    public enum MaterialPropertyType {
        Diffuse,
        Reflective,
        Transmissive
    }

    private List<MaterialProperty> materialProperty;

    public Material() {
        super();
    }

    public List<MaterialProperty> getMaterialProperty() {
        return materialProperty;
    }

    public void setMaterialProperty(List<MaterialProperty> materialProperty) {
        this.materialProperty = materialProperty;
    }
}


