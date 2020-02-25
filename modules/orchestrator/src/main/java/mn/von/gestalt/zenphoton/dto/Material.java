package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Material extends ArrayList implements Serializable {

    private static final long serialVersionUID = 1L;
    public Material() {
        super();
    }

    public void addMaterialProperty(MaterialProperty materialProperty) {
        this.add(materialProperty);
    }
}


