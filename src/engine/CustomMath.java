package engine;
public class CustomMath {

    /**
     * Autor: Bela Korb
     *
     * Methode zum erstellen einer Translationsmatrix
     *
     * @param x: Translation entlang der x-Achse
     * @param z: Translation entlang der z-Achse
     * @return Wiedergabe der Translationsmatrix
     */
    public static float[] transmat(float x, float z){
        float[] transmat = {1,0,0,x,0,1,0,0,0,0,1,z,0,0,0,1};
        return transmat;
    }

    /**
     * Autor: Bela Korb
     *
     * Methode zum erstellen einer Translationsmatrix
     *
     * @param x Translation entlang der x-Achse
     * @param y Translation entlang der y-Achse
     * @param z Translation entlang der z-Achse
     * @return Wiedergabe der Translationsmatrix
     */
    public static float[] transmat(float x,float y,float z){
        float[] transmat = {1,0,0,x,0,1,0,y,0,0,1,z,0,0,0,1};
        return transmat;
    }

    /**
     * Autor: Bela Korb
     *
     * Methode zum erstellen einer Rotationsmatrix
     *
     * @param rotation Rotation um die y-Achse
     *
     * @return Wiedergabe der Rotationsmatrix
     */
    public static float[] rotmatY(float rotation){
        rotation=(float)Math.toRadians(rotation);
        float[] rotmat = {(float)Math.cos(rotation),0,(float)Math.sin(rotation),0,  0,1,0,0,    (float)-Math.sin(rotation),0,(float)Math.cos(rotation),0,   0,0,0,1};
        return rotmat;
    }
    public static float[] rotmatX(float rotation){
        rotation=(float)Math.toRadians(rotation);
        float[] rotmat = {1,0,0,0,  0,(float)Math.cos(rotation),(float)-Math.sin(rotation),0,    0,(float)Math.sin(rotation),(float)Math.cos(rotation),0,  0,0,0,1};
        return rotmat;
    }
    public static float[] rotmatZ(float rotation){
        rotation=(float)Math.toRadians(rotation);
        float[] rotmat = {(float)Math.cos(rotation),(float)-Math.sin(rotation),0,0,     (float)Math.sin(rotation),(float)Math.cos(rotation),0,0,    0,0,1,0,     0,0,0,1};
        return rotmat;
    }

    /**
     * Autor: Bela Korb
     *
     * Methode zum Multiplizieren zweier Matrizen
     *
     * @param a matrix eins
     * @param b matrix zwei
     * @return Rückgabe der berechneten Matrix
     */
    public static float[] matrixmult(float[] a,float[] b){
        float[] c = new float[16];
        float[] atemp= new float[4];
        float[] btemp= new float[4];
        for(int i = 0; i<16;i++){
            for(int j=0;j<4;j++){
                atemp[j]=a[(i/4)*4+j];
                btemp[j]=b[i%4+j*4];
            }
            c[i]=atemp[0]*btemp[0]+atemp[1]*btemp[1]+atemp[2]*btemp[2]+atemp[3]*btemp[3];

        }
        return c;
    }

    /**
     * Autor: Sebastian Schmidt
     *
     * Addiert zwei Matrizen zusammen
     * @param mat1 Matrix 1
     * @param mat2 Matrix 2
     * @return addierte Matrix
     */
    public static float[] matrixadd(float[] mat1, float[] mat2){
        float[] newMat = new float[3];
        newMat[0]= mat1[0]+mat2[0];
        newMat[1]= mat1[1]+mat2[1];
        newMat[2]= mat1[2]+mat2[2];
        return newMat;
    }

    /**
     * Autor: Bela Korb
     *
     * Methode zum multiplizieren einer Matrix mit Vertices und Normalen.
     * Der mitgegebene float[] vek besteht abwechseln aus drei Vertice Koordinaten und drei Normalen Koordinaten.
     *
     * @param matMove Bewegungsmatrix (Kombiniert aus Translations- und Rotationsmatrix)
     * @param matRot Rotationsmatrix zum rotieren der Normalen
     * @param vek Vertice/Nomalen Array, welches neu berechnet werden soll
     * @return Rückgabe des berechneten Vertice/Normalen Arrays
     */
    public static float[] matMultVek(float[] matMove,float[] matRot,float[] vek){

        float[] vertices = new float[4];
        float[] verticesTemp = new float[4];
        float[] endVek = new float[vek.length];

        for (int i = 0; i < vek.length/6; i++) {

            for (int k = 0; k < 3; k++) {
                vertices[k] = vek[i * 6 + k];
            }
            vertices[3] = 1f;
            for (int l = 0; l < 4; l++) {

                verticesTemp[l] = matMove[l * 4] * vertices[0] + matMove[l * 4 + 1] * vertices[1] + matMove[l * 4 + 2] * vertices[2] + matMove[l * 4 + 3] * vertices[3];

            }
            for (int k = 0; k < 3; k++) {
                endVek[i * 6 + k] = verticesTemp[k];
            }


            for (int k = 0; k < 3; k++) {
                vertices[k] = vek[i * 6 + k + 3];
            }
            vertices[3] = 1f;

            for (int l = 0; l < 4; l++) {

                verticesTemp[l] = matRot[l * 4] * vertices[0] + matRot[l * 4 + 1] * vertices[1] + matRot[l * 4 + 2] * vertices[2] + matRot[l * 4 + 3] * vertices[3];

            }
            for (int k = 0; k < 3; k++) {
                endVek[i * 6 + k + 3] = verticesTemp[k];
            }

        }
        return endVek;
    }

    public static double[] normalizeVector(double[] vec){
        double lenghtOfVec = Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
        for (int i = 0;i<3;i++) vec[i]=vec[i]/lenghtOfVec;
        return vec;
    }

}
