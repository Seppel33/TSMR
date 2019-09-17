package simulation;

import engine.CustomMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.lang.Math.PI;

public class PhysicsCollisionThread extends Thread{
    private int threadNumber;
    private CyclicBarrier cyclicBarrier;
    private double[] positionOfMarble;
    private double[] directionVectorOfMarble;
    private double lengthOfDirectionVector;
    private Marble marble;

    private static double[][] prepAllMTVerts;
    private static double[][] prepAllNormVectors;
    private static int numberOfQuadrants;


    public PhysicsCollisionThread(int threadNumber, CyclicBarrier cyclicBarrier, Marble marble){
        this.threadNumber = threadNumber;
        this.cyclicBarrier = cyclicBarrier;
        numberOfQuadrants = DataExchange.getNumberOfQuadrants();
        prepAllMTVerts = DataExchange.getPrepAllMTVerts();
        prepAllNormVectors = DataExchange.getPrepAllNormVectors();
        this.marble = marble;
        lengthOfDirectionVector = 1;
    }

    public void run(){

        System.out.println("Collision Physics Thread " + threadNumber + " starting");

        //do as long as program running
        while (!PhysicsThreadManager.isDone()) {
            //if needed , do further calculations
            if (!PhysicsThreadManager.isPause()) {
                if (!DataExchange.getCThreadFree()[threadNumber]) {
                    //  System.out.println("Collision Physics Thread " + threadNumber + " calculating");
                    boolean collision = false;

                    lengthOfDirectionVector = DataExchange.getLenghtOfDirectionVectors()[threadNumber];
                    positionOfMarble = marble.getPosition();
                    directionVectorOfMarble = marble.getVector();



                    double[][] rList = new double[prepAllMTVerts.length][];//fläche
                    ArrayList<Double> smallestRSurfaceList = new ArrayList<>();
                    ArrayList<Integer> smallestRSurfaceIndexList1 = new ArrayList<>();
                    ArrayList<Integer> smallestRSurfaceIndexList2 = new ArrayList<>();

                    ArrayList<Double> smallestSEdgeList = new ArrayList<>();        //kante
                    ArrayList<Double> smallestREdgeList = new ArrayList<>();
                    ArrayList<Integer> smallestSEdgeIndexList1 = new ArrayList<>();
                    ArrayList<Integer> smallestSEdgeIndexList2 = new ArrayList<>();
                    ArrayList<Integer> smallestSEdgeIndexList3 = new ArrayList<>();

                    ArrayList<Double> smallestRPointList = new ArrayList<>();//punkt
                    ArrayList<Integer> smallestRPointIndexList1 = new ArrayList<>();
                    ArrayList<Integer> smallestRPointIndexList2 = new ArrayList<>();
                    ArrayList<Integer> smallestRPointIndexList3 = new ArrayList<>();



                    boolean[] marbleNearBlock = new boolean[numberOfQuadrants];
                    double lengthOfMarbleDirectionVector = Math.sqrt(directionVectorOfMarble[0] * directionVectorOfMarble[0] + directionVectorOfMarble[1] * directionVectorOfMarble[1] + directionVectorOfMarble[2] * directionVectorOfMarble[2]);

                    for (int i = 0; i < numberOfQuadrants; i++) {


                        if ((Math.sqrt((prepAllMTVerts[i][0] - positionOfMarble[0]) * (prepAllMTVerts[i][0] - positionOfMarble[0])
                                + (prepAllMTVerts[i][1] - positionOfMarble[1]) * (prepAllMTVerts[i][1] - positionOfMarble[1])
                                + (prepAllMTVerts[i][2] - positionOfMarble[2]) * (prepAllMTVerts[i][2] - positionOfMarble[2]))) < Math.sqrt(3) + lengthOfMarbleDirectionVector) {
                            marbleNearBlock[i] = true;
                        } else {
                            marbleNearBlock[i] = false;
                        }


                    }
                    double[][] sameVectorScalar = new double[numberOfQuadrants][];
                    for (int i = 0; i < numberOfQuadrants; i++) {
                        sameVectorScalar[i] = new double[prepAllMTVerts[i].length / 9];
                        for (int j = 0; j < prepAllMTVerts[i].length / 9; j++) {

                            for (int k = 0; k < 3; k++) {
                                sameVectorScalar[i][j] += directionVectorOfMarble[k] * prepAllNormVectors[i][j * 3 + k];
                            }
                        }
                    }


                    //fläche
                    for (int counter = 0; counter < numberOfQuadrants; counter++) {
                        rList[counter] = new double[prepAllNormVectors[counter].length / 3];
                        if (marbleNearBlock[counter]) {
                            //initalisieren der Variablen
                            double[] prepNormVectors = prepAllNormVectors[counter];
                            double[] prepMTVerts = prepAllMTVerts[counter];
                            // float[][] rList= new float[marbleTrackVertices.length][];


                            //erstellen einer ebenengleichung und errechnnen der Strecke, die die Kugel zurücklegen muss um eine Kollision zu erzeugen->r
                            for (int i = 0; i < prepNormVectors.length / 3; i++) {
                                if (sameVectorScalar[counter][i] < 0) {
                                    double d = prepNormVectors[(i * 3)] * prepMTVerts[i * 9] + prepNormVectors[(i * 3) + 1] * prepMTVerts[(i * 9) + 1] + prepNormVectors[(i * 3) + 2] * prepMTVerts[(i * 9) + 2];
                                    double r = (d + marble.getRadius() - (prepNormVectors[(i * 3)] * positionOfMarble[0]) - (prepNormVectors[(i * 3) + 1] * positionOfMarble[1]) - (prepNormVectors[(i * 3) + 2] * positionOfMarble[2]))
                                            / (prepNormVectors[(i * 3)] * directionVectorOfMarble[0] + prepNormVectors[(i * 3) + 1]
                                            * directionVectorOfMarble[1] + prepNormVectors[(i * 3) + 2] * directionVectorOfMarble[2]);

                                    rList[counter][i] = r;
                                } else {
                                    rList[counter][i] = -1;
                                }

                            }
                        }
                    }
                    //kleinstes r zwischen 0 und 1 errechnen
                    for (int i = 0; i < rList.length; i++) {
                        for (int j = 0; j < rList[i].length; j++) {
                            // if((i!=index1&&j!=index2)){
                            if (rList[i][j] > 0 && rList[i][j] <= lengthOfDirectionVector) {
                                smallestRSurfaceList.add(rList[i][j]);
                                smallestRSurfaceIndexList1.add(i);
                                smallestRSurfaceIndexList2.add(j);
                            }
                            //}
                        }
                    }


                    //all
                    double[] newMarblePoint = new double[3];
                    double[] newMarbleVector = new double[3];
                    double[] returnArray = new double[6];


                    //kante


                    //kleinstes r zwischen 0 und 1 wird gespeichert
                    //damit wird die Kollision überprüft
                    //das r wird aus der Liste genommen
                    //wenn keine Kollision stattfindet wird alles nochmal durchgegangen ohne das letzte r

                    for (int counter = 0; counter < numberOfQuadrants; counter++) {

                        //initalisieren der Variablen
                        if (marbleNearBlock[counter]) {
                            double[] prepMTVerts = prepAllMTVerts[counter];
                            // double[][] rList= new double[marbleTrackVertices.length][];
                            double[] surfacePoint = new double[9];
                            double[] surfacePoint2 = new double[9];
                            //sur
                            double[] spVector = new double[9];
                            for (int i = 0; i < prepMTVerts.length / 9; i++) {
                                //if (sameVectorScalar[counter][i] < 0) {
                                for (int j = 0; j < 9; j++) {
                                    surfacePoint[j] = prepMTVerts[(i * 9) + j];
                                }
                                for (int j = 0; j < 9; j++) {
                                    spVector[j] = surfacePoint[(j + 3) % 9] - surfacePoint[j];
                                }

                                for (int j = 0; j < 9; j++) {
                                    surfacePoint[j] = prepMTVerts[(i * 9) + j];
                                }
                                for (int j = 0; j < 9; j++) {
                                    spVector[j] = surfacePoint[(j + 3) % 9] - surfacePoint[j];
                                }

                                for (int j = 0; j < 3; j++) {
                            /*
                            boolean sameLastPoints;
                            try {
                                sameLastPoints = (((surfacePoint[0] == point1[0]) && (surfacePoint[1] == point1[1]) && (surfacePoint[2] == point1[2]))
                                        && ((surfacePoint[0] + spVector[0] == point2[0]) && (surfacePoint[1] + spVector[1] == point2[1]) && (surfacePoint[2] + spVector[2] == point2[2])))
                                        || (((surfacePoint[0] == point2[0]) && (surfacePoint[1] == point2[1]) && (surfacePoint[2] == point2[2]))
                                        && ((surfacePoint[0] + spVector[0] == point1[0]) && (surfacePoint[1] + spVector[1] == point1[1]) && (surfacePoint[2] + spVector[2] == point1[2])));
                            } catch (NullPointerException e) {
                                sameLastPoints = false;
                            }

                             */

                                    // if (!sameLastPoints) {

                                    double h = (spVector[(j * 3)] * spVector[(j * 3)] + spVector[(j * 3) + 1] * spVector[(j * 3) + 1] + spVector[(j * 3) + 2] * spVector[(j * 3) + 2]);
                                    double m = (spVector[j * 3] * directionVectorOfMarble[0] + spVector[(j * 3) + 1] * directionVectorOfMarble[1] + spVector[(j * 3) + 2] * directionVectorOfMarble[2])
                                            / h;
                                    double n = (spVector[(j * 3)] * (positionOfMarble[0] - surfacePoint[(j * 3)]) + spVector[(j * 3) + 1] * (positionOfMarble[1] - surfacePoint[(j * 3) + 1]) + spVector[(j * 3) + 2] * (positionOfMarble[2] - surfacePoint[(j * 3) + 2]))
                                            / h;

                                    double e = directionVectorOfMarble[0] * directionVectorOfMarble[0] + directionVectorOfMarble[1] * directionVectorOfMarble[1] + directionVectorOfMarble[2] * directionVectorOfMarble[2]
                                            + m * m * (h)
                                            - m * 2 * (spVector[(j * 3)] * directionVectorOfMarble[0] + spVector[(j * 3) + 1] * directionVectorOfMarble[1] + spVector[(j * 3) + 2] * directionVectorOfMarble[2]);
                                    double f = 2 * m * n * (h) + 2 * (positionOfMarble[0] * directionVectorOfMarble[0] + positionOfMarble[1] * directionVectorOfMarble[1] + positionOfMarble[2] * directionVectorOfMarble[2])
                                            - 2 * (surfacePoint[(j * 3)] * directionVectorOfMarble[0] + surfacePoint[(j * 3) + 1] * directionVectorOfMarble[1] + surfacePoint[(j * 3) + 2] * directionVectorOfMarble[2])
                                            - 2 * m * (positionOfMarble[0] * spVector[(j * 3)] + positionOfMarble[1] * spVector[(j * 3) + 1] + positionOfMarble[2] * spVector[(j * 3) + 2])
                                            + 2 * m * (surfacePoint[(j * 3)] * spVector[(j * 3)] + surfacePoint[(j * 3) + 1] * spVector[(j * 3) + 1] + surfacePoint[(j * 3) + 2] * spVector[(j * 3) + 2])
                                            - 2 * n * (spVector[(j * 3)] * directionVectorOfMarble[0] + spVector[(j * 3) + 1] * directionVectorOfMarble[1] + spVector[(j * 3) + 2] * directionVectorOfMarble[2]);
                                    double g = n * n * h - 2 * n * (positionOfMarble[0] * spVector[(j * 3)] + positionOfMarble[1] * spVector[(j * 3) + 1] + positionOfMarble[2] * spVector[(j * 3) + 2])
                                            + 2 * n * (surfacePoint[(j * 3)] * spVector[(j * 3)] + surfacePoint[(j * 3) + 1] * spVector[(j * 3) + 1] + surfacePoint[(j * 3) + 2] * spVector[(j * 3) + 2])
                                            -marble.getRadius() * marble.getRadius() + positionOfMarble[0] * positionOfMarble[0] + positionOfMarble[1] * positionOfMarble[1] + positionOfMarble[2] * positionOfMarble[2]
                                            - 2 * (positionOfMarble[0] * surfacePoint[(j * 3)] + positionOfMarble[1] * surfacePoint[(j * 3) + 1] + positionOfMarble[2] * surfacePoint[(j * 3) + 2])
                                            + surfacePoint[(j * 3)] * surfacePoint[(j * 3)] + surfacePoint[(j * 3) + 1] * surfacePoint[(j * 3) + 1] + surfacePoint[(j * 3) + 2] * surfacePoint[(j * 3) + 2];

                                    double s1 = (-f / (2 * e)) + Math.sqrt((f / (2 * e)) * (f / (2 * e)) - (g / e));
                                    double s2 = (-f / (2 * e)) - Math.sqrt((f / (2 * e)) * (f / (2 * e)) - (g / e));
                                    double r1 = s1 * m + n;
                                    double r2 = s2 * m + n;
                                    boolean s1andR1InRange = ((s1 <= lengthOfDirectionVector && s1 > 0) && (r1 <= 1 && r1 >= 0));
                                    boolean s2andR2InRange = ((s2 <= lengthOfDirectionVector && s2 > 0) && (r2 <= 1 && r2 >= 0));

                                    if ((s2andR2InRange && s1andR1InRange && s2 < s1) ||
                                            (s2andR2InRange && !s1andR1InRange)) {


                                        smallestSEdgeList.add(s2);
                                        smallestSEdgeIndexList1.add(counter);
                                        smallestSEdgeIndexList2.add(i);
                                        smallestSEdgeIndexList3.add(j);
                                        smallestREdgeList.add(r2);


                                    } else if (s1andR1InRange) {

                                        smallestSEdgeList.add(s1);
                                        smallestSEdgeIndexList1.add(counter);
                                        smallestSEdgeIndexList2.add(i);
                                        smallestSEdgeIndexList3.add(j);
                                        smallestREdgeList.add(r1);

                                        //}
                                    }
                                }

                                // sList[counter][i]=0;
                                // }
                            }
                        }
                    }
        /*

        Punktkollision

         */
                    //Punkt
                    for (int counter = 0; counter < numberOfQuadrants; counter++) {
                        if (marbleNearBlock[counter]) {
                            //initalisieren der Variablen

                            double[] prepMTVerts = prepAllMTVerts[counter];
                            // float[][] rList= new float[marbleTrackVertices.length][];
                            double[] surfacePoint = new double[9];
                            //sur

                            for (int i = 0; i < prepMTVerts.length / 9; i++) {
                                if (sameVectorScalar[counter][i] < 0) {
                                    for (int j = 0; j < 9; j++) {
                                        surfacePoint[j] = prepMTVerts[(i * 9) + j];
                                    }
                                    // if (surfacePoint != indexPoint) {
                                    for (int j = 0; j < 3; j++) {


                                        double divide = (directionVectorOfMarble[0] * directionVectorOfMarble[0] + directionVectorOfMarble[1] * directionVectorOfMarble[1] + directionVectorOfMarble[2] * directionVectorOfMarble[2]);
                                        double p = (2 * directionVectorOfMarble[0] * (positionOfMarble[0] - surfacePoint[(j * 3)]) + 2 * directionVectorOfMarble[1] * (positionOfMarble[1] - surfacePoint[(j * 3) + 1])
                                                + 2 * directionVectorOfMarble[2] * (positionOfMarble[2] - surfacePoint[(j * 3) + 2]))
                                                / divide;
                                        double q = (surfacePoint[(j * 3)] * surfacePoint[(j * 3)] + positionOfMarble[0] * positionOfMarble[0] - 2 * surfacePoint[(j * 3)] * positionOfMarble[0]
                                                + surfacePoint[(j * 3) + 1] * surfacePoint[(j * 3) + 1] + positionOfMarble[1] * positionOfMarble[1] - 2 * surfacePoint[(j * 3) + 1] * positionOfMarble[1]
                                                + surfacePoint[(j * 3) + 2] * surfacePoint[(j * 3) + 2] + positionOfMarble[2] * positionOfMarble[2] - 2 * surfacePoint[(j * 3) + 2] * positionOfMarble[2] - marble.getRadius() * marble.getRadius()) / (divide);


                                        double r1 = -(p / 2) + Math.sqrt((p / 2) * (p / 2) - q);
                                        double r2 = -(p / 2) - Math.sqrt((p / 2) * (p / 2) - q);
                                        boolean r1InRange = (r1 <= lengthOfDirectionVector && r1 >= 0);
                                        boolean r2InRange = (r2 <= lengthOfDirectionVector && r2 >= 0);

                                        if ((r2InRange && r1InRange && r2 < r1) ||
                                                (r2InRange && !r1InRange)) {


                                            smallestRPointList.add(r2);
                                            smallestRPointIndexList1.add(counter);
                                            smallestRPointIndexList2.add(i);
                                            smallestRPointIndexList3.add(j);


                                        } else if (r1InRange) {

                                            smallestRPointList.add(r1);
                                            smallestRPointIndexList1.add(counter);
                                            smallestRPointIndexList2.add(i);
                                            smallestRPointIndexList3.add(j);

                                        }
                                        // sList[counter][i]=0;
                                    }
                                    // }
                                }
                            }
                        }
                    }


                    //all
                    double rPointSmaller = 2;
                    if (smallestRPointList.size() > 0) rPointSmaller = smallestRPointList.get(0);

                    for (int k = 1; k < smallestRPointList.size(); k++) {
                        if (smallestRPointList.get(k) < rPointSmaller) {
                            rPointSmaller = smallestRPointList.get(k);


                        }
                    }


                    double sEdgeSmaller = 2;
                    if (smallestSEdgeList.size() > 0) sEdgeSmaller = smallestSEdgeList.get(0);

                    for (int k = 1; k < smallestSEdgeList.size(); k++) {
                        if (smallestSEdgeList.get(k) < sEdgeSmaller) {
                            sEdgeSmaller = smallestSEdgeList.get(k);


                        }
                    }
                    int forCounter = 0;
                    if (smallestRSurfaceList.size() == 0) forCounter = -1;

                    for (int k = forCounter; k < smallestRSurfaceList.size() + 1; k++) {

                        double rSurfaceSmaller = 2;
                        if (smallestRSurfaceList.size() > 0) rSurfaceSmaller = smallestRSurfaceList.get(0);

                        for (int i = 1; i < smallestRSurfaceList.size(); i++) {
                            if (smallestRSurfaceList.get(i) < rSurfaceSmaller) {
                                rSurfaceSmaller = smallestRSurfaceList.get(i);
                            }
                        }


                        if (((rSurfaceSmaller < sEdgeSmaller) && (rSurfaceSmaller < rPointSmaller)) && (rSurfaceSmaller <= 1)) {


                            int indexOfRRemove = 0;
                            if (smallestRSurfaceList.size() > 0) {
                                double smallestR = smallestRSurfaceList.get(0);
                                int smallestRIndex1 = 0;
                                int smallestRIndex2 = 0;
                                if (smallestRSurfaceList.size() == 1) {
                                    smallestR = smallestRSurfaceList.get(0);
                                    smallestRIndex1 = smallestRSurfaceIndexList1.get(0);
                                    smallestRIndex2 = smallestRSurfaceIndexList2.get(0);
                                } else {

                                    for (int i = 1; i < smallestRSurfaceList.size(); i++) {
                                        if (smallestRSurfaceList.get(i) < smallestR) {
                                            smallestR = smallestRSurfaceList.get(i);
                                            smallestRIndex1 = smallestRSurfaceIndexList1.get(i);
                                            smallestRIndex2 = smallestRSurfaceIndexList2.get(i);
                                            indexOfRRemove = i;
                                        }
                                    }
                                }


                                smallestRSurfaceList.set(indexOfRRemove, 2.0);


                                double[] prepNormVectors = prepAllNormVectors[smallestRIndex1];
                                double[] prepMTVerts = prepAllMTVerts[smallestRIndex1];


                                double[] collisionPoint = new double[3];

                                collisionPoint[0] = (positionOfMarble[0] + smallestR * directionVectorOfMarble[0]) -marble.getRadius() * prepNormVectors[smallestRIndex2 * 3];
                                collisionPoint[1] = (positionOfMarble[1] + smallestR * directionVectorOfMarble[1]) - marble.getRadius() * prepNormVectors[(smallestRIndex2 * 3) + 1];
                                collisionPoint[2] = (positionOfMarble[2] + smallestR * directionVectorOfMarble[2]) - marble.getRadius()* prepNormVectors[(smallestRIndex2 * 3) + 2];
                                // collisionPoint[2] = (positionOfMarble[2] + smallestR * directionVectorOfMarble[2] * lengthOfDirectionVector) - 0.17f * prepNormVectors[(smallestRIndex2 * 3) + 2];

                                double alpha = (Math.acos(((prepMTVerts[(smallestRIndex2 * 9) + 0] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 3] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 1] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 4] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 2] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 5] - collisionPoint[2]))
                                        / (Math.sqrt((prepMTVerts[(smallestRIndex2 * 9) + 0] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 0] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 1] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 1] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 2] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 2] - collisionPoint[2]))
                                        * Math.sqrt((prepMTVerts[(smallestRIndex2 * 9) + 3] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 3] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 4] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 4] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 5] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 5] - collisionPoint[2])))));

                                double beta = (Math.acos(((prepMTVerts[(smallestRIndex2 * 9) + 0] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 6] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 1] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 7] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 2] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 8] - collisionPoint[2]))
                                        / (Math.sqrt((prepMTVerts[(smallestRIndex2 * 9) + 0] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 0] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 1] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 1] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 2] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 2] - collisionPoint[2]))
                                        * Math.sqrt((prepMTVerts[(smallestRIndex2 * 9) + 6] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 6] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 7] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 7] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 8] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 8] - collisionPoint[2])))));

                                double gamma = (Math.acos(((prepMTVerts[(smallestRIndex2 * 9) + 6] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 3] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 7] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 4] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 8] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 5] - collisionPoint[2]))
                                        / (Math.sqrt((prepMTVerts[(smallestRIndex2 * 9) + 6] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 6] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 7] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 7] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 8] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 8] - collisionPoint[2]))
                                        * Math.sqrt((prepMTVerts[(smallestRIndex2 * 9) + 3] - collisionPoint[0]) * (prepMTVerts[(smallestRIndex2 * 9) + 3] - collisionPoint[0])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 4] - collisionPoint[1]) * (prepMTVerts[(smallestRIndex2 * 9) + 4] - collisionPoint[1])
                                        + (prepMTVerts[(smallestRIndex2 * 9) + 5] - collisionPoint[2]) * (prepMTVerts[(smallestRIndex2 * 9) + 5] - collisionPoint[2])))));


                                if (alpha + beta + gamma < 2 * PI + 0.001 && alpha + beta + gamma > 2 * PI - 0.001) {
                                    //neuer Punkt + neuer Richtungsvektor


                                    //S calar
                                    //v-2*s*n=r
                                    double[] testMarblePoint = new double[3];

                                    double s = directionVectorOfMarble[0] * prepNormVectors[(smallestRIndex2 * 3)]
                                            + directionVectorOfMarble[1] * prepNormVectors[(smallestRIndex2 * 3) + 1] + directionVectorOfMarble[2] * prepNormVectors[(smallestRIndex2 * 3) + 2];
                                    for (int i = 0; i < 3; i++) {
                                        newMarbleVector[i] = (directionVectorOfMarble[i] - 2 * s * prepNormVectors[(smallestRIndex2 * 3) + i]);
                                    }
                                    for (int i = 0; i < 3; i++) {
                                        testMarblePoint[i] = collisionPoint[i] +marble.getRadius() * prepNormVectors[smallestRIndex2 * 3 + i] * 1.012;
                                        //  testMarblePoint[i] = positionOfMarble[i] + (smallestR * directionVectorOfMarble[i]);
                                    }

                                    //System.out.println("Face:   " + smallestR);

                                    double[] normal = new double[3];
                                    normal[0] = prepAllNormVectors[smallestRIndex1][smallestRIndex2 * 3];
                                    normal[1] = prepAllNormVectors[smallestRIndex1][smallestRIndex2 * 3 + 1];
                                    normal[2] = prepAllNormVectors[smallestRIndex1][smallestRIndex2 * 3 + 2];

                                    double[] returnData = {smallestR, normal[0], normal[1], normal[2], testMarblePoint[0],testMarblePoint[1],testMarblePoint[2],newMarbleVector[0],newMarbleVector[1],newMarbleVector[2]};
                                    DataExchange.setCollisionReturn(threadNumber, returnData);
                                    collision = true;
                                    //kollision muss nochmal für den neuen vektor überprüft werden
                                    break;
                                }


                            }


                        } else if ((sEdgeSmaller < rPointSmaller) && (sEdgeSmaller <= 1)) {

                            if (smallestSEdgeList.size() > 0) {
                                double smallestS = smallestSEdgeList.get(0);
                                double andR = smallestREdgeList.get(0);
                                int smallestSIndex1 = smallestSEdgeIndexList1.get(0);
                                int smallestSIndex2 = smallestSEdgeIndexList2.get(0);
                                int smallestSIndex3 = smallestSEdgeIndexList3.get(0);
                                if (smallestSEdgeList.size() == 1) {
                                    smallestS = smallestSEdgeList.get(0);
                                    andR = smallestREdgeList.get(0);
                                    smallestSIndex1 = smallestSEdgeIndexList1.get(0);
                                    smallestSIndex2 = smallestSEdgeIndexList2.get(0);
                                    smallestSIndex3 = smallestSEdgeIndexList3.get(0);
                                } else {

                                    for (int sIndex = 1; sIndex < smallestSEdgeList.size(); sIndex++) {
                                        if (smallestSEdgeList.get(sIndex) < smallestS) {
                                            smallestS = smallestSEdgeList.get(sIndex);
                                            andR = smallestREdgeList.get(sIndex);
                                            smallestSIndex1 = smallestSEdgeIndexList1.get(sIndex);
                                            smallestSIndex2 = smallestSEdgeIndexList2.get(sIndex);
                                            smallestSIndex3 = smallestSEdgeIndexList3.get(sIndex);

                                        }
                                    }
                                }

                                double[] sP = new double[3];
                                double[] spV = new double[3];
                                double[] sP2 = new double[3];
                                for (int j = 0; j < 3; j++) {
                                    sP[j] = prepAllMTVerts[smallestSIndex1][(smallestSIndex2 * 9) + (smallestSIndex3 * 3) + j];
                                    spV[j] = prepAllMTVerts[smallestSIndex1][(smallestSIndex2 * 9) + ((smallestSIndex3 * 3 + 3) % 9) + j] - sP[j];
                                    sP2[j] = prepAllMTVerts[smallestSIndex1][(smallestSIndex2 * 9) + (smallestSIndex3 * 3 + 3) % 9 + j];

                                }


                                double[] marblePoint = new double[3];
                                double[] linePoint = new double[3];
                                double[] normalVector = new double[3];
                                double[] reflectionVector = new double[3];

                                for (int i = 0; i < 3; i++) {
                                    marblePoint[i] = positionOfMarble[i] + smallestS * directionVectorOfMarble[i];
                                    linePoint[i] = sP[i] + andR * spV[i];
                                    normalVector[i] = marblePoint[i] - linePoint[i];
                                }

                                CustomMath.normalizeVector(normalVector);

                                //v-2*s*n=r
                                double scalar = directionVectorOfMarble[0] * normalVector[0] + directionVectorOfMarble[1] * normalVector[1] + directionVectorOfMarble[2] * normalVector[2];
                                for (int i = 0; i < 3; i++) {
                                    reflectionVector[i] = directionVectorOfMarble[i] - 2 * scalar * normalVector[i];
                                }

                                double[] newTestMP = new double[3];
                                for (int i = 0; i < 3; i++)
                                    newTestMP[i] = linePoint[i] + normalVector[i] * 1.012 *marble.getRadius();

                                returnArray[0] = newTestMP[0];
                                returnArray[1] = newTestMP[1];
                                returnArray[2] = newTestMP[2];
                                returnArray[3] = reflectionVector[0];
                                returnArray[4] = reflectionVector[1];
                                returnArray[5] = reflectionVector[2];

                                double[] returnData = {smallestS, normalVector[0], normalVector[1], normalVector[2], returnArray[0],returnArray[1],returnArray[2],returnArray[3],returnArray[4],returnArray[5]};
                                DataExchange.setCollisionReturn(threadNumber, returnData);
                                collision = true;
                                //System.out.println("Line:  " + smallestS + "     ref" + reflectionVector[1]);
                                break;
                            }


                        } else if (rPointSmaller <= 1) {


                            if (smallestRPointList.size() > 0) {
                                double smallestR = smallestRPointList.get(0);

                                int smallestRIndex1 = smallestRPointIndexList1.get(0);
                                int smallestRIndex2 = smallestRPointIndexList2.get(0);
                                int smallestRIndex3 = smallestRPointIndexList3.get(0);
                                if (smallestRPointList.size() == 1) {
                                    smallestR = smallestRPointList.get(0);

                                    smallestRIndex1 = smallestRPointIndexList1.get(0);
                                    smallestRIndex2 = smallestRPointIndexList2.get(0);
                                    smallestRIndex3 = smallestRPointIndexList3.get(0);
                                } else {

                                    for (int rIndex = 1; rIndex < smallestRPointList.size(); rIndex++) {
                                        if (smallestRPointList.get(rIndex) < smallestR) {
                                            smallestR = smallestRPointList.get(rIndex);

                                            smallestRIndex1 = smallestRPointIndexList1.get(rIndex);
                                            smallestRIndex2 = smallestRPointIndexList2.get(rIndex);
                                            smallestRIndex3 = smallestRPointIndexList3.get(rIndex);

                                        }
                                    }
                                }

                                double[] trianglePoint = new double[3];

                                for (int j = 0; j < 3; j++) {
                                    trianglePoint[j] = prepAllMTVerts[smallestRIndex1][(smallestRIndex2 * 9) + (smallestRIndex3 * 3) + j];

                                }


                                double[] marblePoint = new double[3];
                                // float[] linePoint = new float[3];
                                double[] normalVector = new double[3];
                                double[] reflectionVector = new double[3];
                                double[] newMarblePoint2 = new double[3];
                                for (int i = 0; i < 3; i++) {
                                    marblePoint[i] = positionOfMarble[i] + smallestR * directionVectorOfMarble[i];

                                    normalVector[i] = marblePoint[i] - trianglePoint[i];

                                }


                                CustomMath.normalizeVector(normalVector);

                                //v-2*s*n=r
                                double scalar = directionVectorOfMarble[0] * normalVector[0] + directionVectorOfMarble[1] * normalVector[1] + directionVectorOfMarble[2] * normalVector[2];
                                for (int i = 0; i < 3; i++) {
                                    reflectionVector[i] = directionVectorOfMarble[i] - 2 * scalar * normalVector[i];
                                }


                                for (int i = 0; i < 3; i++) {
                                    // newMarblePoint[i] = positionOfMarble[i] + (s * directionVectorOfMarble[i]*lengthOfDirectionVector) + (newMarbleVector[i] * (1 - smallestR));
                                    newMarblePoint2[i] = trianglePoint[i] + normalVector[i] *marble.getRadius() * 1.012;
                                }

                                double[] returnData = {smallestR, normalVector[0], normalVector[1], normalVector[2],newMarblePoint2[0],newMarblePoint2[1],newMarblePoint2[2],reflectionVector[0],reflectionVector[1],reflectionVector[2]};
                                DataExchange.setCollisionReturn(threadNumber, returnData);
                                collision = true;
                                //System.out.println("Point:   " + Arrays.toString(newMarblePoint2));
                                break;
                            }


                        }

                        //erstellen einer ebenengleichung und errechnnen der Strecke, die die Kugel zurücklegen muss um eine Kollision zu erzeugen->r


                    }

                    if(!collision) {
                        for (int i = 0; i < 3; i++) {
                            newMarblePoint[i] = positionOfMarble[i] + directionVectorOfMarble[i];
                        }
                        returnArray[0] = newMarblePoint[0];
                        returnArray[1] = newMarblePoint[1];
                        returnArray[2] = newMarblePoint[2];
                        returnArray[3] = directionVectorOfMarble[0];
                        returnArray[4] = directionVectorOfMarble[1] + DataExchange.getGravity() * lengthOfDirectionVector;
                        returnArray[5] = directionVectorOfMarble[2];
                        //begrenzung der maximalen Geschwindigkeit
                        double speedOfMarble = Math.sqrt(returnArray[3] * returnArray[3] + returnArray[4] * returnArray[4] + returnArray[5] * returnArray[5]);
                        if (speedOfMarble > 0.1) {
                            for (int i = 3; i < 6; i++) {
                                returnArray[i] = (returnArray[i] / speedOfMarble) * 0.1;
                            }
                        }

                        if (returnArray[1] <marble.getRadius()) {
                            returnArray[1] =marble.getRadius();
                            returnArray[4] = 0;
                            returnArray[3] *= 0.9;
                            returnArray[5] *= 0.9;
                            double[] rotationVelocity = marble.getRotationVelocity();
                            for (int i = 0; i < 3; i++) {
                                rotationVelocity[i] *= 0.9;
                            }
                            marble.setRotationVelocity(rotationVelocity);
                        }
                        double[] returnData = {2,0,0,0,returnArray[0],returnArray[1],returnArray[2],returnArray[3],returnArray[4],returnArray[5]};
                        DataExchange.setCollisionReturn(threadNumber, returnData);
                    }


                } else {
                    // System.out.println("Collision Physics Thread " + threadNumber + " paused");
                }
            }

            //End Block  Warte auf alle anderen Threads an der Barrier
            try {
                cyclicBarrier.await();
            } catch (InterruptedException ex) {
                System.out.println("Loop Interrupted");
                return;
            } catch (BrokenBarrierException ex) {
                System.out.println("Loop Broken");
                return;
            }

        }
        System.out.println("Collision Physics Thread " + threadNumber + " terminated");

    }

    public void newCalculationData(double lengthOfDirectionVector){
        this.lengthOfDirectionVector = lengthOfDirectionVector;
        this.positionOfMarble = this.marble.getPosition();
        this.directionVectorOfMarble = this.marble.getVector();
    }

}
