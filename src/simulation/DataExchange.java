package simulation;

public class DataExchange {
    //cThreadFree => true wenn ein Thread auf einen anderen warten muss
    private static boolean[] cThreadFree;
    //marbleReturn => Rückgrabewerte des Murmel-Murmel-Kollision Threads
    private static double[][] marbleReturn;
    //collisionReturn => Rückgabewerte der Murmel-Block-Kollision Threads
    private static double[][] collisionReturn;

    private static double[] lenghtOfDirectionVectors;
    private static double gravity = -0.006;

    //Werte zum Vergleich von Murmel-Murmel- und Murmel-Block-Kollision
    private static boolean[][] possibleMarbelCollisions;
    private static int[] collisionWith;

    //Konstante Werte der Bahn-Daten
    private static double[][] prepAllMTVerts;
    private static double[][] prepAllNormVectors;
    private static int numberOfQuadrants;



    public DataExchange(int numberOfMarbles){
        marbleReturn = new double[numberOfMarbles][10];
        for (int i = 0; i<numberOfMarbles; i++){
            marbleReturn[i][0] =3;
        }
        collisionReturn = new double[numberOfMarbles][10];
        for (int i = 0; i<numberOfMarbles; i++){
            collisionReturn[i][0] = -1;
        }
        possibleMarbelCollisions = new boolean[numberOfMarbles][numberOfMarbles];
    }

    public static void override(int numberOfMarbles){
        cThreadFree = new boolean[numberOfMarbles];
        for(int i =0;i<numberOfMarbles; i++){
            cThreadFree[i] = false;
        }
    }

    public static double getGravity(){
        return gravity;
    }
    public static void setPossibleMarbelCollisions(boolean[][] x){
        possibleMarbelCollisions = x;
    }
    public static boolean[][] getPossibleMarbelCollisions(){
        return possibleMarbelCollisions;
    }
    public static synchronized void setCollisionReturn(int index, double[] returnData){
        collisionReturn[index] = returnData;
    }
    public static double[][] getCollisionReturn(){
        return collisionReturn;
    }
    public static synchronized void setMarbleReturn(int index, double[] returnData){
        marbleReturn[index] = returnData;
    }
    public static double[][] getMarbleReturn(){
        return marbleReturn;
    }
    public static boolean[] getCThreadFree(){
        return cThreadFree;
    }
    public static void setCThreadFree(boolean[] b){
        cThreadFree = b;
    }
    public static void setCThreadFree(boolean b, int index){
        cThreadFree[index] = b;
    }
    public static synchronized double[] getLenghtOfDirectionVectors() {
        return lenghtOfDirectionVectors;
    }

    public static synchronized void setLenghtOfDirectionVectors(int index, double lenghtOfDirectionVectors) {
        DataExchange.lenghtOfDirectionVectors[index] = lenghtOfDirectionVectors;
    }
    public static synchronized void setLenghtOfDirectionVectors(double[] lenghtOfDirectionVectors) {
        DataExchange.lenghtOfDirectionVectors = lenghtOfDirectionVectors;
    }
    public static synchronized int[] getCollisionWith() {
        return collisionWith;
    }

    public static synchronized void setCollisionWith(int[] collisionWith) {
        DataExchange.collisionWith = collisionWith;
    }

    public static double[][] getPrepAllMTVerts() {
        return prepAllMTVerts;
    }

    public static void setPrepAllMTVerts(double[][] prepAllMTVerts) {
        DataExchange.prepAllMTVerts = prepAllMTVerts;
    }

    public static double[][] getPrepAllNormVectors() {
        return prepAllNormVectors;
    }

    public static void setPrepAllNormVectors(double[][] prepAllNormVectors) {
        DataExchange.prepAllNormVectors = prepAllNormVectors;
    }

    public static int getNumberOfQuadrants() {
        return numberOfQuadrants;
    }

    public static void setNumberOfQuadrants(int numberOfQuadrants) {
        DataExchange.numberOfQuadrants = numberOfQuadrants;
    }

}
