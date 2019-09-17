package engine;
import com.jogamp.opengl.math.VectorUtil;
/**
 * Java class for creating vertex and buffer data for drawing a box
 * using Jogl/OpenGL.
 * Intended to be used for an OpenGL scene renderer.
 * All methods are static.
 *
 * @author Karsten Lehn
 * @version 21.10.2017, 27.10.2017
 */
public class Box {
    /**
     * Creates eight vertices for a cuboid (box) with one single color.
     * To be used together with makeFastBoxIndicesForTriangleStrip()
     * For using a fast drawing algorithm.
     * One color vector per vertex is placed.
     * No normal vectors are placed.
     * The center of gravity of this shape in the origin.
     * @param width width of the box (x direction)
     * @param height height of the box (y direction)
     * @param depth depth of the box (z direction)
     * @param color three dimensional color vector for each vertex
     * @return list of vertices
     */

    private static float textureScaling = 0.5f;
    /**
     * Veraendert durch: Bela Korb, Sebastian Schmidt
     *
     * Creates 24 vertices for a cuboid (box) with one single color
     * and correctly placed normal vectors.
     * To be used together with makeBoxIndicesForTriangleStrip()
     * @param width with of box (x direction)
     * @param height height of box (y direction)
     * @param depth depth of box (z direction)
     * @param color three dimensional color vector for each vertex
     * @return list of vertices
     */
    public static float[] makeBoxVertices(float width, float height, float depth, float[] color) {

        float halfOfWidth = width / 2;
        float halfOfDepth = depth / 2;
        float point1X = halfOfWidth;
        float point1Z = -halfOfDepth;
        float point2X = halfOfWidth;
        float point2Z = halfOfDepth;


        /*
        Bela Korb

        Die richtigen Normalen der Box werden berechnet
         */
        float[] frontVec1 ={point2X-point1X,0,point2Z-point1Z};
        float[] frontVec2 = {0,2.8f,0};
        float[] nb = new float[3];
        VectorUtil.crossVec3(nb,frontVec1,frontVec2);
        nb = VectorUtil.normalizeVec3(nb);
        float[] nf = {-nb[0],-nb[1],-nb[2]};


        float[] rightVec1 ={nf[0],0,nf[2]};
        float[] rightVec2 = {0,2.8f,0};
        float[] nl = new float[3];
        VectorUtil.crossVec3(nl,rightVec1,rightVec2);
        nl = VectorUtil.normalizeVec3(nl);
        float[] nr = {-nl[0],-nl[1],-nl[2]};
        // Definition of positions of vertices for a cuboid


        float[] p0 = {halfOfWidth, height, halfOfDepth}; // 0 front
        float[] p1 = {halfOfWidth, height, -halfOfDepth}; // 1 front //1 526 wand
        float[] p2 = {halfOfWidth, 0f, -halfOfDepth}; // 2 front   //3074
        float[] p3 = {halfOfWidth, 0f, halfOfDepth}; // 3 front
        float[] p4 = {-halfOfWidth, height, halfOfDepth}; // 4 back
        float[] p5 = {-halfOfWidth, height, -halfOfDepth}; // 5 back
        float[] p6 = {-halfOfWidth, 0f, -halfOfDepth}; // 6 back
        float[] p7 = {-halfOfWidth, 0f, halfOfDepth}; // 7 back


        // color vector
        float[] c = color;

        // Definition of normal vectors for cuboid surfaces

        float[] nu = { 0,  1,  0}; // 0 up (top)
        float[] nd = { 0, -1,  0}; // 0 down (bottom)

        // Cuboid vertices to be drawn as triangle stripes
        // Interlaces with color information and normal vectors
        float[] verticies = {
                // front surface
                // index: 0
                //x     y       z
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],    // color
                nf[0], nf[1], nf[2],   // normal
                0f,0f,
                // index: 1
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                0f,1f,
                // index: 2
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                1f,0f,
                // index: 3
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                1f,1f,

                // back surface
                // index: 4
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],    // color
                nb[0], nb[1], nb[2],   // normal
                0f,0f,
                // index: 5
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                0f,1f,
                // index: 6
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                1f,0f,
                // index: 7
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                1f,1f,
                // left surface
                // index: 8
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],    // color
                nl[0], nl[1], nl[2],   // normal
                0f,0f,
                // index: 9
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                0f,1f,
                // index: 10
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                1f,0f,
                // index: 11
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                1f,1f,

                // right surface
                // index: 12
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],    // color
                nr[0], nr[1], nr[2],   // normal
                0f,0f,
                // index: 13
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                0f,1f,
                // index: 14
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                1f,0f,
                // index: 15
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                1f,1f,

                // top surface
                // index: 16
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],    // color
                nu[0], nu[1], nu[2],   // normal
                0f,0f,
                // index: 17
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                0f,2f,
                // index: 18
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                2f,0f,
                // index: 19
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                2f,2f,

                // bottom surface
                // index: 20
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                0f,0f,
                // index: 21
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                0f,1f,
                // index: 22
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                1f,0f,
                // index: 23
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                1f,1f,
        };
        return verticies;
    }

    /**
     * Creates 28 indices for drawing a cuboid (box).
     * To be used together with makeBoxVertices()
     * To be used with "glDrawElements" and "GL_TRIANGLE_STRIP".
     * @return indices into the vertex array of the cube (box)
     */
    public static int[] makeBoxIndicesForTriangleStrip() {
        // Indices to reference the number of the box vertices
        // defined in makeBoxVertices()
        int[] indices = {
                // Note: back faces are drawn,
                // but drawing is faster than using "GL_TRIANGLES"
                21, 23, 20, 22,         // down (bottom)
                1, 3, 0, 2, 2, 3,       // front
                12, 13, 14, 15,         // right
                4, 5, 6, 7,             // back
                8, 9, 10, 11, 10, 10,   // left
                16, 17, 18, 19          // up (top)
        };
        return indices;
    }

    /**
     * Returns the number of indices of a cuboid (box) for the draw call.
     * To be used together with makeBoxIndicesForTriangleStrip
     * @return number of indices
     */
    public static int noOfIndicesForBox() {
        return 28;
    }


    public static float lengthCalc(float[] point1,float[] point2){
        float length= (float)Math.sqrt((Math.pow(point1[0]-point2[0],2)+ Math.pow(point1[2]-point2[2],2)));
        return length;
    }

    public static float[] makeOuterWall(float point1X, float point1Z, float point2X, float point2Z, float[] color, float y){
        float[] frontVec1 ={point2X-point1X,0,point2Z-point1Z};
        float[] frontVec2 = {0,2.8f,0};
        float[] nf = new float[3];
        VectorUtil.crossVec3(nf,frontVec1,frontVec2);
        nf = VectorUtil.normalizeVec3(nf);
        float[] nb = {nf[0],nf[1],nf[2]};


        float[] rightVec1 ={nf[0],0,nf[2]};
        float[] rightVec2 = {0,2.8f,0};
        float[] nl = new float[3];
        VectorUtil.crossVec3(nl,rightVec1,rightVec2);
        nl = VectorUtil.normalizeVec3(nl);
        float[] nr = {-nl[0],-nl[1],-nl[2]};
        nl[0]= -nl[0];
        nl[1]= -nl[1];
        nl[2]= -nl[2];
        // Definition of positions of vertices for a cuboid
        float[] p0 = {point1X+nf[0], y, point1Z+nf[2]}; // 0 front
        float[] p1 = {point2X+nf[0], y, point2Z+nf[2]}; // 1 front //1 526 wand
        float[] p2 = {point2X+nf[0], 0f, point2Z+nf[2]}; // 2 front   //3074
        float[] p3 = {point1X+nf[0], 0f, point1Z+nf[2]}; // 3 front
        float[] p4 = {point1X-nf[0], y, point1Z-nf[2]}; // 4 back
        float[] p5 = {point2X-nf[0], y, point2Z-nf[2]}; // 5 back
        float[] p6 = {point2X-nf[0], 0f, point2Z-nf[2]}; // 6 back
        float[] p7 = {point1X-nf[0], 0f, point1Z-nf[2]}; // 7 back


        // color vector
        float[] c = color;


        float[] nu = { 0,  -1,  0}; // 0 up (top)
        float[] nd = { 0, 1,  0}; // 0 down (bottom)

        // Cuboid vertices to be drawn as triangle stripes
        // Interlaces with color information and normal vectors
        // Die richtigen Texturkoordinaten werden mit weitergegeben
        float[] verticies = {
                // front surface
                // index: 0
                //x     y       z
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],    // color
                nf[0], nf[1], nf[2],   // normal
                0f,0f,
                // index: 1
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                0f,2.8f*textureScaling,
                // index: 2
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                lengthCalc(p0,p1)*textureScaling,0f,
                // index: 3
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nf[0], nf[1], nf[2],   // normal
                lengthCalc(p0,p1)*textureScaling,2.8f*textureScaling,

                // back surface
                // index: 4
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],    // color
                nb[0], nb[1], nb[2],   // normal
                0f,0f,
                // index: 5
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                0f,2.8f*textureScaling,
                // index: 6
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                lengthCalc(p4,p5)*textureScaling,0f,
                // index: 7
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nb[0], nb[1], nb[2],   // normal
                lengthCalc(p4,p5)*textureScaling,2.8f*textureScaling,

                // left surface
                // index: 8
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],    // color
                nl[0], nl[1], nl[2],   // normal
                0f,0f,
                // index: 9
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                0f,2.8f*textureScaling,
                // index: 10
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                lengthCalc(p3,p7)*textureScaling,0f,
                // index: 11
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],   // color
                nl[0], nl[1], nl[2],   // normal
                lengthCalc(p3,p7)*textureScaling,2.8f*textureScaling,

                // right surface
                // index: 12
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],    // color
                nr[0], nr[1], nr[2],   // normal
                0f,0f,
                // index: 13
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                0f,2.8f*textureScaling,
                // index: 14
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                lengthCalc(p2,p6)*textureScaling,0f,
                // index: 15
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nr[0], nr[1], nr[2],   // normal
                lengthCalc(p2,p6)*textureScaling,2.8f*textureScaling,

                // top surface
                // index: 16
                p4[0], p4[1], p4[2],   // position
                c[0],  c[1],  c[2],    // color
                nu[0], nu[1], nu[2],   // normal
                0f,0f,
                // index: 17
                p0[0], p0[1], p0[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                0f,1f,
                // index: 18
                p5[0], p5[1], p5[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                1f,0f,
                // index: 19
                p1[0], p1[1], p1[2],   // position
                c[0],  c[1],  c[2],   // color
                nu[0], nu[1], nu[2],   // normal
                1f,1f,

                // bottom surface
                // index: 20
                p3[0], p3[1], p3[2],   // position
                c[0],  c[1],  c[2],    // color
                nd[0], nd[1], nd[2],   // normal
                0f,0f,
                // index: 21
                p7[0], p7[1], p7[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                0f,1f,
                // index: 22
                p2[0], p2[1], p2[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                1f,0f,
                // index: 23
                p6[0], p6[1], p6[2],   // position
                c[0],  c[1],  c[2],   // color
                nd[0], nd[1], nd[2],   // normal
                1f,1f,
        };
        return verticies;
    }

    public static float[] makeFloor(float point1X, float point1Z, float point2X, float point2Z, float[] color){

        // Definition of positions of vertices for a cuboid
        float[] p0 = {point1X, 0f, point1Z}; // 0 front
        float[] p1 = {point2X, 0f, point1Z}; // 1 front
        float[] p2 = {point2X, 0f, point1Z}; // 2 front
        float[] p3 = {point1X, 0f, point1Z}; // 3 front
        float[] p4 = {point1X, 0f, point2Z}; // 4 back
        float[] p5 = {point2X, 0f, point2Z}; // 5 back
        float[] p6 = {point2X, 0f, point2Z}; // 6 back
        float[] p7 = {point1X, 0f, point2Z}; // 7 back

        // color vector
        float[] c = color;

        // Definition of normal vectors for cuboid surfaces
        float[] nf = { 0,  1,  0}; // 0 front
        float[] nb = { 0,  1,  0}; // 0 back
        float[] nl ={ 0,  1,  0}; // 0 left
        float[] nr = { 0,  1,  0}; // 0 right
        float[] nu = { 0,  1,  0}; // 0 up (top)
        float[] nd = { 0, 1,  0}; // 0 down (bottom)

        // Cuboid vertices to be drawn as triangle stripes
        // Interlaces with color information and normal vectors
        //Berechnung der Texturkoordinaten
        float[] verticies = {
                // front surface
                // index: 0
                //x     y       z
                p0[0], p0[1], p0[2],   // position
                nf[0], nf[1], nf[2],   // normal

                // index: 1
                p3[0], p3[1], p3[2],   // position
                nf[0], nf[1], nf[2],   // normal

                // index: 2
                p1[0], p1[1], p1[2],   // position
                nf[0], nf[1], nf[2],   // normal
                // index: 3
                p2[0], p2[1], p2[2],   // position
                nf[0], nf[1], nf[2],   // normal

                // back surface
                // index: 4
                p5[0], p5[1], p5[2],   // position
                nb[0], nb[1], nb[2],   // normal
                // index: 5
                p6[0], p6[1], p6[2],   // position
                nb[0], nb[1], nb[2],   // normal
                // index: 6
                p4[0], p4[1], p4[2],   // position
                nb[0], nb[1], nb[2],   // normal
                // index: 7
                p7[0], p7[1], p7[2],   // position
                nb[0], nb[1], nb[2],   // normal


                // left surface
                // index: 8
                p4[0], p4[1], p4[2],   // position
                nl[0], nl[1], nl[2],   // normal

                // index: 9
                p7[0], p7[1], p7[2],   // position
                nl[0], nl[1], nl[2],   // normal
                // index: 10
                p0[0], p0[1], p0[2],   // position
                nl[0], nl[1], nl[2],   // normal
                // index: 11
                p3[0], p3[1], p3[2],   // position
                nl[0], nl[1], nl[2],   // normal

                // right surface
                // index: 12
                p1[0], p1[1], p1[2],   // position
                nr[0], nr[1], nr[2],   // normal
                // index: 13
                p2[0], p2[1], p2[2],   // position
                nr[0], nr[1], nr[2],   // normal
                // index: 14
                p5[0], p5[1], p5[2],   // position
                nr[0], nr[1], nr[2],   // normal
                // index: 15
                p6[0], p6[1], p6[2],   // position
                nr[0], nr[1], nr[2],   // normal

                // top surface
                // index: 16
                p4[0], p4[1], p4[2],   // position
                nu[0], nu[1], nu[2],   // normal
                // index: 17
                p0[0], p0[1], p0[2],   // position
                nu[0], nu[1], nu[2],   // normal
                // index: 18
                p5[0], p5[1], p5[2],   // position
                nu[0], nu[1], nu[2],   // normal
                // index: 19
                p1[0], p1[1], p1[2],   // position
                nu[0], nu[1], nu[2],   // normal

                // bottom surface
                // index: 20
                p3[0], p3[1], p3[2],   // position
                nd[0], nd[1], nd[2],   // normal
                // index: 21
                p7[0], p7[1], p7[2],   // position
                nd[0], nd[1], nd[2],   // normal
                // index: 22
                p2[0], p2[1], p2[2],   // position
                nd[0], nd[1], nd[2],   // normal
                // index: 23
                p6[0], p6[1], p6[2],   // position
                nd[0], nd[1], nd[2],   // normal
        };
        return verticies;
    }

}
