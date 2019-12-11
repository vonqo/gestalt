package mn.von.gestalt.zenphoton.dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;

public class MaterialProperty extends ArrayList implements Serializable {

    public enum MaterialPropertyType {
        Diffuse,
        Reflective,
        Transmissive
    }
    private transient char type;
    private transient float weigth;

    public MaterialProperty() {
        super();
    }

    public MaterialPropertyType getType() {
        if(type == 'd') return MaterialPropertyType.Diffuse;
        if(type == 't') return MaterialPropertyType.Transmissive;
        if(type == 'r') return MaterialPropertyType.Reflective;
        return null;
    }

    public void setType(MaterialPropertyType type) {
        if(type == MaterialPropertyType.Diffuse) this.type = 'd';
        else if(type == MaterialPropertyType.Transmissive) this.type = 't';
        else if(type == MaterialPropertyType.Reflective) this.type = 'r';

    }

    public float getWeigth() {
        return this.weigth;
    }

    public void setWeigth(float weigth) {
        this.weigth = weigth;
    }

    public void wrapToList() {
        this.clear();
        this.add(this.weigth);
        this.add(this.type);
    }
}
