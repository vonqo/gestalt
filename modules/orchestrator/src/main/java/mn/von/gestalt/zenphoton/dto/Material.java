package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Material extends ArrayList implements Serializable {

    public Material() {
        super();
    }

    public void addMaterialProperty(MaterialProperty materialProperty) {
        this.add(materialProperty);
    }
}


