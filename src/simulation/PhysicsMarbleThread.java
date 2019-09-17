package simulation;

import engine.CustomMath;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PhysicsMarbleThread extends Thread{
    private CyclicBarrier cyclicBarrier;
    private Marble[] marbles;
    private double[][] returnData;
    public PhysicsMarbleThread(CyclicBarrier cyclicBarrier){
        this.cyclicBarrier = cyclicBarrier;
        marbles= PhysicsThreadManager.marbles;
        returnData = new double[marbles.length][7];
    }
    public void run(){

        System.out.println("Marble Physics Thread Starting");
        //do as long as program running
        while (!PhysicsThreadManager.isDone()){
            if (!PhysicsThreadManager.isPause() && marbles.length>1){
                //System.out.println("Marble Thread Calculating");


                double lengthOfDirectionVector;
                if (DataExchange.getLenghtOfDirectionVectors()[0] < DataExchange.getLenghtOfDirectionVectors()[1]) {
                    lengthOfDirectionVector = DataExchange.getLenghtOfDirectionVectors()[0];
                } else {
                    lengthOfDirectionVector = DataExchange.getLenghtOfDirectionVectors()[1];
                }


                //Längeneinheit pro Frame
                double marbleOneLength = Math.sqrt(marbles[0].getVector()[0] * marbles[0].getVector()[0] + marbles[0].getVector()[1] * marbles[0].getVector()[1] + marbles[0].getVector()[2] * marbles[0].getVector()[2]);
                double marbleTwoLength = Math.sqrt(marbles[1].getVector()[0] * marbles[1].getVector()[0] + marbles[1].getVector()[1] * marbles[1].getVector()[1] + marbles[1].getVector()[2] * marbles[1].getVector()[2]);
                double sumMarbleLengths = marbleOneLength + marbleTwoLength;
                double sumRadiusSpheres = marbles[0].getRadius() + marbles[1].getRadius();
                double dist = Math.sqrt((marbles[0].getPosition()[0] - marbles[1].getPosition()[0]) * (marbles[0].getPosition()[0] - marbles[1].getPosition()[0])
                        + (marbles[0].getPosition()[1] - marbles[1].getPosition()[1]) * (marbles[0].getPosition()[1] - marbles[1].getPosition()[1])
                        + (marbles[0].getPosition()[2] - marbles[1].getPosition()[2]) * (marbles[0].getPosition()[2] - marbles[1].getPosition()[2]));

                //System.out.println("Dist: " + dist + " SML: " + sumMarbleLengths);
                if (sumMarbleLengths >= dist - sumRadiusSpheres) {

                    System.out.println("Möglich Collision");

                    //Vektor für Geradengleichung

                    double[] vec = {marbles[0].getVector()[0] - marbles[1].getVector()[0],
                            marbles[0].getVector()[1] - marbles[1].getVector()[1],
                            marbles[0].getVector()[2] - marbles[1].getVector()[2]};

//                    // Ausmultiplizieren (Skalarprodukt)
//                    double scalar = vec[0] * -marbles[1].getPosition()[0]
//                            + vec[1] * -marbles[1].getPosition()[1]
//                            + vec[2] * -marbles[1].getPosition()[2];
//
//                    //t berechnen
//                    double t = (scalar - vec[0] * marbles[0].getPosition()[0] - (vec[1] * marbles[0].getPosition()[1]) - vec[2] * marbles[0].getPosition()[2]) /
//                            (marbles[0].getVector()[0] * marbles[0].getVector()[0] + marbles[0].getVector()[1] * marbles[0].getVector()[1] + marbles[0].getVector()[2] * marbles[0].getVector()[2]);
//
//                    //Schnittpunkt berechnen
//                    double[] sPoint = {marbles[0].getPosition()[0] + (t * vec[0]),
//                            marbles[0].getPosition()[1] + (t * vec[1]),
//                            marbles[0].getPosition()[2] + (t * vec[2])};
//
//                    //Verbindungsvektor berechnen
//                    double[] cVec = {marbles[1].getVector()[0] - sPoint[0],
//                            marbles[1].getVector()[1] - sPoint[1],
//                            marbles[1].getVector()[2] - sPoint[2]};
//
//
//
//                    //Länge des Verbindungsvektor
//                    double cVecLength = Math.sqrt(cVec[0] * cVec[0] + cVec[1] * cVec[1] + cVec[2] * cVec[2]);


                    double divide = (vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
                    double p = (2 * vec[0] * (marbles[0].getPosition()[0] - marbles[1].getPosition()[0]) + 2 * vec[1] * (marbles[0].getPosition()[1] - marbles[1].getPosition()[1])
                            + 2 * vec[2] * (marbles[0].getPosition()[2] - marbles[1].getPosition()[2]))
                            / divide;
                    double q = (marbles[1].getPosition()[0] * marbles[1].getPosition()[0] + marbles[0].getPosition()[0] * marbles[0].getPosition()[0] - 2 * marbles[1].getPosition()[0] * marbles[0].getPosition()[0]
                            + marbles[1].getPosition()[1] * marbles[1].getPosition()[1] + marbles[0].getPosition()[1] * marbles[0].getPosition()[1] - 2 * marbles[1].getPosition()[1] * marbles[0].getPosition()[1]
                            + marbles[1].getPosition()[2] * marbles[1].getPosition()[2] + marbles[0].getPosition()[2] * marbles[0].getPosition()[2] - 2 * marbles[1].getPosition()[2] * marbles[0].getPosition()[2] - sumRadiusSpheres * sumRadiusSpheres) / (divide);


                    double r1 = -(p / 2) + Math.sqrt((p / 2) * (p / 2) - q);
                    double r2 = -(p / 2) - Math.sqrt((p / 2) * (p / 2) - q);
                    boolean r1InRange = (r1 <= lengthOfDirectionVector && r1 >= 0);
                    boolean r2InRange = (r2 <= lengthOfDirectionVector && r2 >= 0);

                    double r = 3;
                    if (r1InRange && r2InRange) {
                        if (r1 < r2) {
                            r = r1;
                        } else {
                            r = r2;
                        }
                    } else if (r1InRange) {
                        r = r1;
                    } else if (r2InRange) {
                        r = r2;
                    }
                    //if(r!=3){
                    if (false) {

                        System.out.println("Collision");

                        //Kollision ausgerechnet werden

//                        double vecLength = Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]) /
//                                Math.sqrt((2 * sumRadiusSpheres) * (2 * sumRadiusSpheres) - (cVecLength * cVecLength));
//
//                        double t2 = t - vecLength;

                        //Vektor zwischen Kugel und Kollisionspunkt
                        double[] p1 = {marbles[0].getVector()[0] * r + marbles[0].getPosition()[0],
                                marbles[0].getVector()[1] * r + marbles[0].getPosition()[1],
                                marbles[0].getVector()[2] * r + marbles[0].getPosition()[2]};

                        double[] p2 = {marbles[1].getVector()[0] * r + marbles[1].getPosition()[0],
                                marbles[1].getVector()[1] * r + marbles[1].getPosition()[1],
                                marbles[1].getVector()[2] * r + marbles[1].getPosition()[2]};


                        double d = Math.sqrt((p2[0] - p1[0]) * (p2[0] - p1[0]) + (p2[1] - p1[1]) * (p2[1] - p1[1]) + (p2[2] - p1[2]) * (p2[2] - p1[2]));
                        //normalized vector von Mittelpunkt der beiden Kugeln
                        double[] n = {p2[0] - p1[0] / d,
                                p2[1] - p1[1] / d,
                                p2[2] - p1[2] / d};


                        //Finale Geschwindigkeit der beiden Kugeln
                        double k = (marbleOneLength * n[0] + marbleOneLength * n[1] + marbleOneLength * n[2] - marbleTwoLength * n[0] + marbleTwoLength * n[1] + marbleTwoLength * n[2]);


                        //Neue Längeneinheit pro Frame
                        double[] marbleOneLength1 = {marbles[0].getVector()[0] - k * n[0],
                                marbles[0].getVector()[1] - k * n[1],
                                marbles[0].getVector()[2] - k * n[2]};


                        double[] marbleOneLength2 = {marbles[1].getVector()[0] - k * n[0],
                                marbles[1].getVector()[1] - k * n[1],
                                marbles[1].getVector()[2] - k * n[2]};

                        /* v-2*s*n=r */
                        double[] v1 = CustomMath.normalizeVector(marbles[0].getVector());
                        double[] v2 = CustomMath.normalizeVector(marbles[1].getVector());
                        double s = v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
                        double[] refVector1 = {(v1[0] - 2 * s * n[0]) * marbleOneLength1[0], (v1[1] - 2 * s * n[1]) * marbleOneLength1[1], (v1[2] - 2 * s * n[2]) * marbleOneLength1[2]};
                        double[] refVector2 = {(v2[0] - 2 * s * n[0]) * marbleOneLength2[0], (v2[1] - 2 * s * n[1]) * marbleOneLength2[1], (v2[2] - 2 * s * n[2]) * marbleOneLength2[2]};

                        double[] returnD1 = {3, -n[0], -n[1], -n[2], p1[0], p1[1], p1[2], refVector1[0], refVector1[1], refVector1[2]};
                        returnData[0] = returnD1;
                        double[] returnD2 = {3, n[0], n[1], n[2], p2[0], p2[1], p2[2], refVector2[0], refVector2[1], refVector2[2]};
                        returnData[1] = returnD2;

                    } else {
                        for (int i = 0; i < marbles.length; i++) {
                            returnData[i][0] = 3;
                        }
                    }

                } else {

                    for (int i = 0; i < marbles.length; i++) {
                        returnData[i][0] = 3;
                    }
                }
                boolean[][] b = {{false, false}, {false, false}};
                int[] collisionWith = {-1, -1};

                DataExchange.setMarbleReturn(0, returnData[0]);
                DataExchange.setMarbleReturn(1, returnData[1]);
                DataExchange.setPossibleMarbelCollisions(b);
                DataExchange.setCollisionWith(collisionWith);
            }
            //End Block
            try {
                cyclicBarrier.await();
            } catch (InterruptedException ex) {
                System.out.println("Loop Interrupted2");
                return;
            } catch (BrokenBarrierException ex) {
                System.out.println("Loop Broken");
                return;
            }
        }

        System.out.println("Marble Physics Thread terminated");

    }
}
