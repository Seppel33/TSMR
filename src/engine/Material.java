package engine;
/**
 *  Java class holding material parameters for surfaces.
 *  @author Karsten Lehn
 *  @version 23.10.2017
 */
public class Material {
    private float[] emission;
    private float[] ambient;
    private float[] diffuse;
    private float[] specular;
    private float shininess;

    public Material() {
    }

    public Material(float[] emission, float[] ambient, float[] diffuse, float[] specular, float shininess) {
        this.emission = emission;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    public float[] getEmission() {
        return emission;
    }

    public void setEmission(float[] emission) {
        this.emission = emission;
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

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
}
