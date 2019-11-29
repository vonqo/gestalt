package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;

public class MaterialProperty implements Serializable {

    private char type;
    private float weigth;

    public MaterialProperty() {
        super();
    }

    public Material.MaterialPropertyType getType() {
        if(type == 'd') return Material.MaterialPropertyType.Diffuse;
        if(type == 't') return Material.MaterialPropertyType.Transmissive;
        if(type == 'r') return Material.MaterialPropertyType.Reflective;
        return null;
    }

    public void setType(Material.MaterialPropertyType type) {
        if(type == Material.MaterialPropertyType.Diffuse) this.type = 'd';
        else if(type == Material.MaterialPropertyType.Transmissive) this.type = 't';
        else if(type == Material.MaterialPropertyType.Reflective) this.type = 'r';
    }

    public float getWeigth() {
        return this.weigth;
    }

    public void setWeigth(float weigth) {
        this.weigth = weigth;
    }
}
