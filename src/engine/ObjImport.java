package engine;
import de.hshl.obj.objects.TriangleObject;
import de.hshl.obj.wavefront.Wavefront;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ObjImport {
    private int[] marbleInd;
    private float[]marbleVert;
    private int[] id0Ind;
    private float[] id0Vert;
    private int[] id1Ind;
    private float[] id1Vert;
    private int[] id2Ind;
    private float[] id2Vert;
    private int[] id3Ind;
    private float[] id3Vert;
    private int[] id4Ind;
    private float[] id4Vert;
    private int[] id5Ind;
    private float[] id5Vert;
    private int[] id6Ind;
    private float[] id6Vert;
    private int[] id7Ind;
    private float[] id7Vert;
    private int[] id8Ind;
    private float[] id8Vert;
    private int[] id9Ind;
    private float[] id9Vert;
    private int[] floorInd;
    private float[] floorVert;
    private int [][] bigBallsInd=new int[10][];
    private float [][] bigBallsVert=new float[10][];



    public ObjImport() {
        try {
            Wavefront.ObjectLoader loader = Wavefront.objects();


            List<TriangleObject> marble = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\marble.obj"));
            List<TriangleObject> floor = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\boden.obj"));

            List<TriangleObject> id0 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id0.obj"));
            List<TriangleObject> id1 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id1.obj"));
            List<TriangleObject> id2 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id2.obj"));
            List<TriangleObject> id3 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id3.obj"));
            List<TriangleObject> id4 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id4.obj"));
            List<TriangleObject> id5 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id5.obj"));
            List<TriangleObject> id6 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id6.obj"));
            List<TriangleObject> id7 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id7.obj"));
            List<TriangleObject> id8 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id8.obj"));
            List<TriangleObject> id9 = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\id9.obj"));
            List<TriangleObject> bigBalls = loader.ignoreMaterials().ignoreStructures().ignoreTextureCoordinates().loadFromFile(Paths.get("resources\\OBJs\\roundMarble.obj"));



            marbleVert = marble.get(0).surfaces.get(0).shape.vertices;
            marbleInd = marble.get(0).surfaces.get(0).shape.indices;
            for(int i = 0 ;i<marbleVert.length;i++){
                marbleVert[i]=marbleVert[i]*1.4f/5/2;
            }

            floorVert = floor.get(0).surfaces.get(0).shape.vertices;
            floorInd = floor.get(0).surfaces.get(0).shape.indices;
            for(int i = 0;i<floorVert.length;i++){
                floorVert[i] =floorVert[i]*10f;
            }

            id0Vert = id0.get(0).surfaces.get(0).shape.vertices;
            id0Ind = id0.get(0).surfaces.get(0).shape.indices;

            id1Vert = id1.get(0).surfaces.get(0).shape.vertices;
            id1Ind = id1.get(0).surfaces.get(0).shape.indices;

            id2Vert = id2.get(0).surfaces.get(0).shape.vertices;
            id2Ind = id2.get(0).surfaces.get(0).shape.indices;

            id3Vert = id3.get(0).surfaces.get(0).shape.vertices;
            id3Ind = id3.get(0).surfaces.get(0).shape.indices;

            id4Vert = id4.get(0).surfaces.get(0).shape.vertices;
            id4Ind = id4.get(0).surfaces.get(0).shape.indices;

            id5Vert = id5.get(0).surfaces.get(0).shape.vertices;
            id5Ind = id5.get(0).surfaces.get(0).shape.indices;

            id6Vert = id6.get(0).surfaces.get(0).shape.vertices;
            id6Ind = id6.get(0).surfaces.get(0).shape.indices;

            id7Vert = id7.get(0).surfaces.get(0).shape.vertices;
            id7Ind = id7.get(0).surfaces.get(0).shape.indices;

            id8Vert = id8.get(0).surfaces.get(0).shape.vertices;
            id8Ind = id8.get(0).surfaces.get(0).shape.indices;

            id9Vert = id9.get(0).surfaces.get(0).shape.vertices;
            id9Ind = id9.get(0).surfaces.get(0).shape.indices;

            for(int i = 0;i<10;i++) {
                bigBallsVert[i] = bigBalls.get(0).surfaces.get(0).shape.vertices.clone();

                bigBallsInd[i] = bigBalls.get(0).surfaces.get(0).shape.indices;
                float randomVal1=(float)Math.random();
                float randomVal2=(float)Math.random();
                float randomVal3=(float)Math.random();
                float randomVal4=(float)Math.random();
                float randomVal5=(float)Math.random();
                for(int j=0;j<bigBallsVert[i].length;j++){
                    bigBallsVert[i][j]=bigBallsVert[i][j]*(randomVal3*5+3f);
                }
                if(randomVal4<0.5f){
                    randomVal4=-1;
                }else{
                    randomVal4=1;
                }

                if(randomVal5<0.5f){
                    randomVal5=-1;
                }else{
                    randomVal5=1;
                }

                float[] pos = {(float)Math.random()*100-50,2.5f+(float)Math.random()*4,(float)-Math.random()*50};
                double d = Math.sqrt((pos[0]-2.5)*(pos[0]-2.5)+(pos[2]-2.5)*(pos[2]-2.5));
                while(d<12&&d>16){


                    pos[0] = (float)Math.random()*100-50;

                    pos[2] =(float)Math.random()*100-50;
                    d = Math.sqrt((pos[0]-2.5)*(pos[0]-2.5)+(pos[2]-2.5)*(pos[2]-2.5));
                }
                float[] transMat = CustomMath.transmat(pos[0],pos[1],pos[2]);
                float[] matRot =CustomMath.rotmatY(0);

                    bigBallsVert[i]=CustomMath.matMultVek(transMat,matRot,bigBallsVert[i]);

            }


        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    public float[] getFloorVertices() {
        return floorVert;
    }
    public int[] getFloorIndices() {
        return floorInd;
    }
    public float[] getMarbleVertices() {
        return marbleVert;
    }
    public int[] getMarbleIndices() {
        return marbleInd;
    }
    public float[][] getBigBallVertices() {
        return bigBallsVert;
    }
    public int[][] getBigBallsIndices() {
        return bigBallsInd;
    }


    public float[] getIdXVerticies(int id){
        float[] vert = {0};
        switch (id){
            case 0:
                vert = id0Vert;
                break;
            case 1:
                vert = id1Vert;
                break;
            case 2:
                vert = id2Vert;
                break;
            case 3:
                vert = id3Vert;
                break;
            case 4:
                vert = id4Vert;
                break;
            case 5:
                vert = id5Vert;
                break;
            case 6:
                vert = id6Vert;
                break;
            case 7:
                vert = id7Vert;
                break;
            case 8:
                vert = id8Vert;
                break;
            case 9:
                vert = id9Vert;
                break;
        }
        return vert;
    }
    public int[] getIdXIndices(int id){
        int[] vert = {0};
        switch (id){
            case 0:
                vert = id0Ind;
                break;
            case 1:
                vert = id1Ind;
                break;
            case 2:
                vert = id2Ind;
                break;
            case 3:
                vert = id3Ind;
                break;
            case 4:
                vert = id4Ind;
                break;
            case 5:
                vert = id5Ind;
                break;
            case 6:
                vert = id6Ind;
                break;
            case 7:
                vert = id7Ind;
                break;
            case 8:
                vert = id8Ind;
                break;
            case 9:
                vert = id9Ind;
                break;
        }
        return vert;
    }



}
