package simulation;

import engine.CustomMath;
import engine.ShapesRendererPP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

import static java.lang.Math.PI;

public class PhysicsThreadManager{

    private static boolean done = false;

    private static boolean pause = false;

    private static ArrayList<double[][]> marblePointList = new ArrayList<>();
    protected static void addToMarblePointList(double[][] d){
        marblePointList.add(d);
    }
    protected static void setToMarblePointList(ArrayList<double[][]> d){
        marblePointList = d;
    }
    public static ArrayList<double[][]> getMarblePointList(){
        return marblePointList;
    }
    public static Marble[] getMarbles() {
        return marbles;
    }
    public static Marble[] marbles;
    private static double[][] originalMarblePoints;

    private static double[][] marbleTrackVertices; //speicherung der ganzen Vertices der verschiedenen Blöcke
    private static int[][] marbleTrackIndices;


    public PhysicsThreadManager(double[][] points){
        marblePointList = new ArrayList<>();
        originalMarblePoints = points;
        marbles = new Marble[points.length];
        done = false;
        for(int i = 0; i<points.length; i++){
            marbles[i] = new Marble(points[i]);
        }
    }
    public static void setPause(boolean b){
        pause = b;
    }
    public static boolean isPause(){
        return pause;
    }
    public static boolean isDone(){
        return done;
    }
    public static void setDone(boolean b){
        done = b;
    }

    public static void prepare(float[][] marbleTrackVerticesI,int[][] marbleTrackIndicesI) {
        double[][] prepAllMTVerts;
        double[][] prepAllNormVectors;
        int numberOfQuadrants;

        numberOfQuadrants=marbleTrackIndicesI.length;

        PhysicsThreadManager.marbleTrackVertices=new double[marbleTrackVerticesI.length][];
        for(int i = 0;i<marbleTrackVerticesI.length;i++){
            PhysicsThreadManager.marbleTrackVertices[i]=new double[marbleTrackVerticesI[i].length];
            for (int j = 0;j<marbleTrackVerticesI[i].length;j++){
                PhysicsThreadManager.marbleTrackVertices[i][j]=(double)marbleTrackVerticesI[i][j];
            }
        }


        PhysicsThreadManager.marbleTrackIndices=marbleTrackIndicesI;
        // float[] positionM = {0,2,0};
        //float[] directionM = {1,-5f,1};
        prepAllNormVectors = new double[numberOfQuadrants][];
        prepAllMTVerts = new double[numberOfQuadrants][];
        for (int n = 0; n < numberOfQuadrants; n++) {
            prepAllNormVectors[n] = new double[marbleTrackIndices[n].length];
            prepAllMTVerts[n] = new double[marbleTrackIndices[n].length * 3];


            for (int j = 0; j < marbleTrackIndices[n].length; j++) {
                for (int k = 0; k < 3; k++) {
                    prepAllMTVerts[n][(j * 3) + k] = marbleTrackVertices[n][(marbleTrackIndices[n][j] * 6) + k];
                }

            }
            for(int i = 0;i<marbleTrackIndices[n].length/3;i++){

                prepAllNormVectors[n][(i*3)+0]=(prepAllMTVerts[n][(i*9)+4]-prepAllMTVerts[n][(i*9)+1])*(prepAllMTVerts[n][(i*9)+8]
                        -prepAllMTVerts[n][(i*9)+2]) - (prepAllMTVerts[n][(i*9)+7]-prepAllMTVerts[n][(i*9)+1])*(prepAllMTVerts[n][(i*9)+5]-prepAllMTVerts[n][(i*9)+2]);
                prepAllNormVectors[n][(i*3)+1]=(prepAllMTVerts[n][(i*9)+5]-prepAllMTVerts[n][(i*9)+2])*(prepAllMTVerts[n][(i*9)+6]
                        -prepAllMTVerts[n][(i*9)+0]) - (prepAllMTVerts[n][(i*9)+8]-prepAllMTVerts[n][(i*9)+2])*(prepAllMTVerts[n][(i*9)+3]-prepAllMTVerts[n][(i*9)+0]);
                prepAllNormVectors[n][(i*3)+2]=(prepAllMTVerts[n][(i*9)+3]-prepAllMTVerts[n][(i*9)+0])*(prepAllMTVerts[n][(i*9)+7]
                        -prepAllMTVerts[n][(i*9)+1]) - (prepAllMTVerts[n][(i*9)+6]-prepAllMTVerts[n][(i*9)+0])*(prepAllMTVerts[n][(i*9)+4]-prepAllMTVerts[n][(i*9)+1]);
                double[] vec3 = new double[3];
                vec3[0]= prepAllNormVectors[n][(i*3)+0];
                vec3[1]=prepAllNormVectors[n][(i*3)+1];
                vec3[2]=prepAllNormVectors[n][(i*3)+2];
                CustomMath.normalizeVector(vec3);
                prepAllNormVectors[n][(i*3)+0]=vec3[0];
                prepAllNormVectors[n][(i*3)+1]=vec3[1];
                prepAllNormVectors[n][(i*3)+2]=vec3[2];

            }
        }

        DataExchange.setNumberOfQuadrants(numberOfQuadrants);
        DataExchange.setPrepAllMTVerts(prepAllMTVerts);
        DataExchange.setPrepAllNormVectors(prepAllNormVectors);

        double[] lengthOfDirection = new double[marbles.length];
        Arrays.fill(lengthOfDirection, 1);
        DataExchange.setLenghtOfDirectionVectors(lengthOfDirection);
        DataExchange dataExchange = new DataExchange(marbles.length);
        //....................................


        DataExchange.override(marbles.length);

        //Barrier welche alle Kollisions-Threads anhält und weiter lässt, wenn alle an der Barrier angekommen sind. Dazwischen wird der Barrier Thread ausgeführt
        CyclicBarrier cyclicBarrier = new CyclicBarrier(marbles.length + 1,
                () -> {

                    //Versuch Murmel-Murmel Kolliosion einzubauen

                    //System.out.println("Barrier Loop");
                    //DataExchange.override(marbles.length);

                    /*
                    boolean[] nCollision = new boolean[marbles.length];
                    for(int i = 0; i<marbles.length; i++) {
                        if (DataExchange.getCollisionReturn()[i][0] < DataExchange.getMarbleReturn()[i][0]) {
                            nCollision[i] = true;
                        } else {
                            nCollision[i] = false;
                        }
                    }
                    boolean[] endResult = new boolean[marbles.length];
                    Arrays.fill(endResult, false);
                    for(int x=0; x<marbles.length; x++){
                        for (int y =0; y<marbles.length; y++){
                            if(x!=y) {
                                if (DataExchange.getPossibleMarbelCollisions()[x][y] && !nCollision[y]) {
                                    endResult[y] = true;
                                }
                            }
                        }
                    }
                    int[] whatToUse = new int[marbles.length];
                    boolean noMatch = false;
                    for(int z =0; z<marbles.length; z++){
                        if(endResult[z]){
                            for(int c = 0; c<marbles.length; c++){
                                if(z!=c && endResult[c]){
                                    if(DataExchange.getCollisionWith()[z] == c && DataExchange.getCollisionWith()[c] == z) {
                                        endResult[c] = false;
                                        endResult[z] = false;
                                        whatToUse[c] = 2;
                                        whatToUse[z] = 2;
                                    }
                                }
                            }
                            if(endResult[z]){
                                whatToUse[z] = 4;
                                noMatch = true;
                            }
                        }else if(nCollision[z]){
                            whatToUse[z] = 1;
                        }
                    }
                    boolean[] threadPause = new boolean[marbles.length];
                    for (int k = 0; k< whatToUse.length; k++) {
                        if(noMatch){
                            if(whatToUse[k] == 1 || whatToUse[k] == 2){
                                threadPause[k] = true;
                            }else{
                                threadPause[k] = false;
                            }
                            DataExchange.setCThreadFree(threadPause);
                        }else{
                            double[] lengthOfDirection = new double[marbles.length];
                            Arrays.fill(lengthOfDirection, 1);
                            DataExchange.setLenghtOfDirectionVectors(lengthOfDirection);
                            if(whatToUse[k] == 2){
                                double[] vector = {DataExchange.getMarbleReturn()[k][4], DataExchange.getMarbleReturn()[k][5],DataExchange.getMarbleReturn()[k][6]};
                                marbles[k].setVector(vector);
                                double[] position ={DataExchange.getMarbleReturn()[k][1], DataExchange.getMarbleReturn()[k][2],DataExchange.getMarbleReturn()[k][3]};
                                marbles[k].setPosition(position);
                            }
                        }
                    }



                    if(PhysicsThreadManager.reset){
                        for (int i = 0; i < marbles.length; i++) {
                            marbles[i] = new Marble(originalMarblePoints[i]);

                        }
                        PhysicsThreadManager.reset = false;
                    }
                    boolean[] pause = new boolean[marbles.length];
                    if(PhysicsThreadManager.pause){
                        for (int k = 0; k< marbles.length; k++) {
                            pause[k] = true;
                        }
                        DataExchange.setCThreadFree(pause);

                     */

                    //done wenn esc gedrückt wurde
                    if(done){
                        Thread.currentThread().interrupt();
                        return;
                    }
                    //verhindert das unendliche vorrechnen der Threads
                    if (PhysicsThreadManager.getMarblePointList().size() > 10800 || PhysicsThreadManager.getMarblePointList().size() - ShapesRendererPP.getFrameIndex() > 999) {
                        PhysicsThreadManager.setPause(true);
                    } else {
                        PhysicsThreadManager.setPause(false);
                    }
                    if (!PhysicsThreadManager.isPause()) {
                        double[][] directionOfM = new double[marbles.length][];
                        DataExchange.override(marbles.length);
                        boolean allDone = true;
                        //Überbrüfung ob alle Murmeln fertig sind, also keine Kollision mehr auftritt
                        for (int i = 0; i < marbles.length; i++) {
                            //System.out.println("SC: " +DataExchange.getCollisionReturn()[i][0] + " SM: " +DataExchange.getMarbleReturn()[i][0]);
                            //if(DataExchange.getCollisionReturn()[i][0]<DataExchange.getMarbleReturn()[i][0]) {
                            if (DataExchange.getCollisionReturn()[i][0] == 2 || DataExchange.getCollisionReturn()[i][0] == -1) {
                                DataExchange.setLenghtOfDirectionVectors(i, 1);
                            } else {
                                DataExchange.setLenghtOfDirectionVectors(i, DataExchange.getLenghtOfDirectionVectors()[i] - DataExchange.getCollisionReturn()[i][0]);
                                allDone = false;
                            }
                            if (!(DataExchange.getCollisionReturn()[i][4] == 0 && DataExchange.getCollisionReturn()[i][5] == 0 & DataExchange.getCollisionReturn()[i][6] == 0)) {
                                double[] position = {DataExchange.getCollisionReturn()[i][4], DataExchange.getCollisionReturn()[i][5], DataExchange.getCollisionReturn()[i][6]};
                                double[] vector = {DataExchange.getCollisionReturn()[i][7], DataExchange.getCollisionReturn()[i][8], DataExchange.getCollisionReturn()[i][9]};
                                directionOfM[i] = marbles[i].getVector();
                                marbles[i].setPosition(position);
                                marbles[i].setVector(vector);
                            }
//                            }else{
//                                double[] position = {DataExchange.getMarbleReturn()[i][4], DataExchange.getMarbleReturn()[i][5], DataExchange.getMarbleReturn()[i][6]};
//                                double[] vector = {DataExchange.getMarbleReturn()[i][7], DataExchange.getMarbleReturn()[i][8], DataExchange.getMarbleReturn()[i][9]};
//                                marbles[i].setPosition(position);
//                                marbles[i].setVector(vector);
//                                allDone = false;
//                            }
                        }
                        double[][] points = new double[marbles.length][6];
                        //setzen der neuen Positionen der Murmeln und welche Threads pausieren müssen
                        for (int x = 0; x < marbles.length; x++) {
                            if (allDone) {
                                points[x][0] = marbles[x].getPosition()[0];
                                points[x][1] = marbles[x].getPosition()[1];
                                points[x][2] = marbles[x].getPosition()[2];
                                points[x][3] = marbles[x].getRotationVelocity()[0];
                                points[x][4] = marbles[x].getRotationVelocity()[1];
                                points[x][5] = marbles[x].getRotationVelocity()[2];
                                if (x == marbles.length - 1) {
                                    PhysicsThreadManager.addToMarblePointList(points);
                                }
                            } else if (DataExchange.getCollisionReturn()[x][0] == 2) {
                                DataExchange.setCThreadFree(true, x);
                            } else {
                                double[] normal = {DataExchange.getCollisionReturn()[x][1], DataExchange.getCollisionReturn()[x][2], DataExchange.getCollisionReturn()[x][3]};
                                double[] reflectionVector = {DataExchange.getCollisionReturn()[x][7], DataExchange.getCollisionReturn()[x][8], DataExchange.getCollisionReturn()[x][9]};
                                marbles[x].setVector(PhysicsThreadManager.afterCollision(reflectionVector, normal, marbles[x], DataExchange.getCollisionReturn()[x][0], directionOfM[x]));
                            }
                        }
                    }
                });

        PhysicsCollisionThread[] subThreads = new PhysicsCollisionThread[marbles.length];
        for(int i=0; i<marbles.length; i++){
            subThreads[i] = new PhysicsCollisionThread(i,cyclicBarrier, marbles[i]);
            subThreads[i].newCalculationData(1);
            subThreads[i].start();
        }

        Thread marblePhysics = new PhysicsMarbleThread(cyclicBarrier);
        marblePhysics.start();

    }

    //Wird ausgeführt, wenn die weitergegebene Murmel eine Kollision in dem Abschitt hatte
    protected static double[] afterCollision(double[] reflectionVector, double[] normal, Marble marble, double smallestR, double[] directionVectorOfMarble){
        double speedOfMarble=Math.sqrt(directionVectorOfMarble[0]*directionVectorOfMarble[0]+directionVectorOfMarble[1]*directionVectorOfMarble[1]+directionVectorOfMarble[2]*directionVectorOfMarble[2]);
        if(speedOfMarble>0.00000008) {
            double[] rotVec = calculateMarbleRotation(normal, marble, reflectionVector);
            reflectionVector[0] *= 0.99 - normal[0] * 0.1;
            reflectionVector[1] *= 0.62 - normal[1] * 0.6;
            reflectionVector[2] *= 0.99 - normal[2] * 0.1;


//            CustomMath.normalizeVector(directionVectorOfMarble);
//            double s = normal[0]*directionVectorOfMarble[0]+normal[1]*directionVectorOfMarble[1] +normal[2]*directionVectorOfMarble[2];
//            reflectionVector[0] *= (1-(0.98-(1+s)*0.88));
//            reflectionVector[1] *= (1-(0.98-(1+s)*0.88));
//            reflectionVector[2] *= (1-(0.98-(1+s)*0.88));

            //reflectionVector[0] += rotVec[0] * 0.9;
            //reflectionVector[1] += rotVec[1] * 0.9;
            //reflectionVector[2] += rotVec[2] * 0.9;

            reflectionVector[1] += DataExchange.getGravity() * smallestR;

            double scalar=0;
            for(int i = 0;i<3;i++)scalar+=reflectionVector[i]*normal[i];

            if(scalar<0){
                //kreuzprodukt normale x refvektor
                double[] crossVec=new double[3];
                for(int i = 0;i<3;i++)crossVec[i]=reflectionVector[(1+i)%3]*normal[(2+i)%3]-reflectionVector[(2+i)%3]*normal[(1+i)%3];
                double[] newVec = new double[3];
                for(int i = 0;i<3;i++)newVec[i]=-(crossVec[(1+i)%3]*normal[(2+i)%3]-crossVec[(2+i)%3]*normal[(1+i)%3]);
                reflectionVector=newVec;
            }


        }else{
            for(int i = 0;i<3;i++) reflectionVector[i]=0;
        }

        return reflectionVector;
    }

    //Berechnung der neuen Murmel Rotation und des Vektors, der daraus resultiert
    private static double[] calculateMarbleRotation(double[] normal, Marble marble, double[] reflectionVector){
        //radius in m umrechnen
        double radius = marble.getRadius()*5/100;
        //z-Rotation
        double zVecX = ((marble.getRotationVelocity()[2]* PI *radius)*100/5) * Math.abs(normal[1]);
        double zVecY = ((marble.getRotationVelocity()[2]* PI *radius)*100/5) * Math.abs(normal[0]);

        //y-Rotation
        double yVecX = ((marble.getRotationVelocity()[1]* PI *radius)*100/5) * Math.abs(normal[2]);
        double yVecZ = ((marble.getRotationVelocity()[1]* PI *radius)*100/5) * Math.abs(normal[0]);

        //x-Rotation
        double xVecZ = ((marble.getRotationVelocity()[0]* PI *radius)*100/5) * Math.abs(normal[1]);
        double xVecY = ((marble.getRotationVelocity()[0]* PI *radius)*100/5) * Math.abs(normal[2]);

        double[] newVector = {(Math.sqrt(zVecX*zVecX+yVecX*yVecX)), (Math.sqrt(zVecY*zVecY+xVecY*xVecY)), (Math.sqrt(yVecZ*yVecZ+xVecZ*xVecZ))};

        //z-Rotation
        double zRotX = (reflectionVector[0] /(PI)/radius/100*5) * Math.abs(normal[1]);
        double zRotY = (reflectionVector[1] /(PI)/radius/100*5) * Math.abs(normal[0]);

        //y-Rotation
        double yRotX = (reflectionVector[0] /(PI)/radius/100*5) * Math.abs(normal[2]);
        double yRotZ = (reflectionVector[2] /(PI)/radius/100*5) * Math.abs(normal[0]);

        //x-Rotation
        double xRotZ = (reflectionVector[2] /(PI)/radius/100*5) * Math.abs(normal[1]);
        double xRotY = (reflectionVector[1] /(PI)/radius/100*5) * Math.abs(normal[2]);

        double[] rotation = {(xRotY+xRotZ),-(yRotX+yRotZ),-(zRotX+zRotY)};
        marble.setRotationVelocity(rotation);

        return newVector;
    }
}
