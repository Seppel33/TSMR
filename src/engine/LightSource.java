package engine;
/**
 *  Java class holding parameters for a light source.
 *  @author Karsten Lehn
 *  @version 23.10.2017, 25.10.2017
 */
public class LightSource {
    private float[][] position;
    private float[] ambient;
    private float[] diffuse;
    private float[] specular;
    private float intesity;

    public LightSource() {
    }

    public LightSource(float[][] position, float[] ambient, float[] diffuse, float[] specular, float intensity) {
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.intesity = intensity;
    }

    public float[][] getPosition() {
        return position;
    }

    public void setPosition(float[][] position) {
        this.position = position;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public void setAmbient(float[] ambient) {
        this.ambient = ambient;
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float[] diffuse) {
        this.diffuse = diffuse;
    }

    public float[] getSpecular() {
        return specular;
    }

    public void setSpecular(float[] specular) {
        this.specular = specular;
    }
    public float getIntesity() {
        return intesity;
    }

    public void setIntesity(float intesity) {
        this.intesity = intesity;
    }


}
