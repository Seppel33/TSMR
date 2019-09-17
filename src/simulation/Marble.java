package simulation;

public class Marble {
    //mass
    private double radius = 0.14;// cm/5
    private double[] position = new double[3];
    private double[] vector = {0,0,0}; //Geschwindigkeit = Länge des Vektors , Richtung = Normalisierter Vektor
    private double[] rotationVelocity = {0,0,0};

    public Marble(double[] p){
        position[0] = p[0];
        position[1] = p[1];
        position[2] = p[2];
    }
    public double[] getVector() {
        return vector;
    }
    public void setVector(double[] velocity) {
        this.vector = velocity;
    }
    public double getRadius() {
        return radius;
    }
    public double[] getPosition() {
        return position;
    }
    public void setPosition(double[] position) {
        this.position = position;
    }
    public double[] getRotationVelocity() {
        return rotationVelocity;
    }
    public void setRotationVelocity(double[] rotationVelocity) {
        this.rotationVelocity = rotationVelocity;
    }



}
