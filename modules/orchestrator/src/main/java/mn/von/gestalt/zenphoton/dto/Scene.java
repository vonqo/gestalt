package mn.von.gestalt.zenphoton.dto;

import java.io.Serializable;
import java.util.List;

public class Scene implements Serializable {

    private float exposure;
    private float gamma;
    private long rays;
    private Resolution resolution;
    private Viewport viewport;
    private List<Light> lights;
    private List<Material> materials;
    private List<ZObject> objects;

    public Scene() {
        super();
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public long getRays() {
        return rays;
    }

    public void setRays(long rays) {
        this.rays = rays;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<ZObject> getObjects() {
        return objects;
    }

    public void setObjects(List<ZObject> objects) {
        this.objects = objects;
    }
}
