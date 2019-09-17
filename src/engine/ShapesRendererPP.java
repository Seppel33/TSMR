package engine;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.PMVMatrix;
import gui.Main;
import gui.TwoDUI;
import save.Levels;
import simulation.PhysicsThreadManager;

import java.awt.*;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class ShapesRendererPP extends GLJPanel implements GLEventListener {



    private boolean onEsc=false;

    private static final long serialVersionUID = 1L;
    private static float[][] lightPosition;

    // taking shader source code files from relative path
    private final String shaderPath = ".\\resources\\";
    // Shader for objecttype 1


    /*
    Die verschiedenen Fragment- und Vertexshader
    Shader beinhalten berechnungen für Diffuse, Specular, Ambient, Textur, Licht und Lichtabfall
    */
    private final String vertexShader1FileName = "marbleTrack.vert";
    private final String fragmentShader1FileName = "marbleTrack.frag";
    private final String vertexShader2FileName = "texturShader.vert";
    private final String fragmentShader2FileName = "texturShader.frag";
    private final String vertexShader3FileName = "marbleShader.vert";
    private final String fragmentShader3FileName = "marbleShader.frag";


    private LightSource light0;
    private Material materialForFloor;
    private Material material1;
    private Material materialForMarbleTrack;
    private Material materialForMarble;
    private Material materialForMarkedMarble;
    private Material materialForBigMarble;
    private GL3 gl;


    private ShaderProgram shaderProgram1;
    private ShaderProgram shaderProgram2;
    private ShaderProgram shaderProgram3;

    private float timeLine=0;
    private float[] randomVal1=new float[10];
    private float[] randomVal2=new float[10];

    // Pointers (names) for data transfer and handling on GPU
    private int[] vaoName;  // Names of vertex array objects
    private int[] vboName;    // Names of vertex buffer objects
    private int[] iboName;    // Names of index buffer objects

    //Textur Buffer
    private int[] texName;
    private int[] textureIndex;

    // Create objects for the scene
    // The box and roof do not need objects because all methods of these classes are static
    //private Sphere sphere0;
    //private Cone cone0;



    // Object for handling keyboard and mouse interaction
    private InteractionHandler interactionHandler;
    // Projection model view matrix tool
    private PMVMatrix pmvMatrix;
    private int numberOfObjects = 0;

    private float[] cameraPos = {0, 0, 1f};
    private float[] cameraFront = {0, 0, -1f};
    private float[] cameraFront2 = {0, 0, -1f};
    private float cameraSpeed = 0.1f;

    private boolean firstFrame = true;
    private int[] blockIndices;
    private ObjImport objImport;
    private int widthRes;
    private int heightRes;

    public static int[][] getLevelData() {
        return levelData;
    }

    private static int[][] levelData;
    private int selectedLevel;


    public static int[][] getMarbleSectorPosition() {
        return marbleSectorPosition;
    }
    private static int[][] marbleSectorPosition;
    private int[] marbleSectorPositionLast=new int[3];
    private int[] dimensions = new int[3]; //[x,y,z]
    private int numberOfBlocks=0;
    private int numbersOfMarble=0;

    private PhysicsThreadManager physicsThreadManager;

    private float[][] marbleVertices;

    private int[] idToObjectNumber;

    private static float[][] marbleTrackVert;
    private static int[][] marbleTrackInd;

    public void settDUI(TwoDUI tDUI) {
        this.tDUI = tDUI;
    }

    private TwoDUI tDUI;

    public void settDUI2(TwoDUI tDUI2) {
        this.tDUI2 = tDUI2;
    }

    private TwoDUI tDUI2;

    private double[][] marblePoints;
    private static double frameIndex;

    public static void setSpeedSliderValue(double speedSliderValue) { ShapesRendererPP.speedSliderValue = speedSliderValue; }

    private static double speedSliderValue;

    private static boolean pause;
    private static boolean reset;

    private float interpolationVal;

    public ShapesRendererPP(GLCapabilities capabilities,int widthRes,int heightRes) {
        // Create the canvas with the requested OpenGL capabilities
        super(capabilities);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        this.widthRes =    widthRes;//gd.getDisplayMode().getWidth();
        this.heightRes = heightRes;//gd.getDisplayMode().getHeight();
        frameIndex=1;
        speedSliderValue=1;
        reset = false;
        pause = true;

        selectedLevel = Main.selectedLevel;
        Method levelMethod;
        try{
            levelMethod = Levels.class.getDeclaredMethod("getLevel" + selectedLevel);
            levelData = (int[][]) levelMethod.invoke(null);
            dimensions[0] = levelData[0][0];
            dimensions[1] = levelData[0][1];
            dimensions[2] = levelData[0][2];
            levelMethod = Levels.class.getDeclaredMethod("getLevelMarbles" + selectedLevel);
            float[][] marblePoints = (float[][]) levelMethod.invoke(null);
            double[][] dMarblePoints;
            dMarblePoints= new double[marblePoints.length][];
            for(int i = 0;i<marblePoints.length;i++){
                dMarblePoints[i]=new double[marblePoints[i].length];
                for(int j = 0; j<marblePoints[i].length;j++){
                    dMarblePoints[i][j]=(double)marblePoints[i][j];
                }
            }
            physicsThreadManager = new PhysicsThreadManager(dMarblePoints);
            numbersOfMarble=marblePoints.length;
        }catch (Exception e){
            e.printStackTrace();
        }

        marbleSectorPosition= new int[numbersOfMarble][3];
        marblePoints = new double[numbersOfMarble][6];
        objImport = new ObjImport();
        objectManager();
        // Add this object as an OpenGL event listener
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();




    }

    private void objectManager() {
        //Calculate full Number of Objects

        for(int i=1; i<levelData.length; i++){
            if(levelData[i][0]!=-1){
                numberOfBlocks++;
            }
        }
        marbleTrackVert = new float[numberOfBlocks][];
        marbleTrackInd = new int[numberOfBlocks][];
        idToObjectNumber = new int[numberOfBlocks];

        numberOfObjects += 1;
        numberOfObjects += 4;
        numberOfObjects += numberOfBlocks;
        numberOfObjects += numbersOfMarble;
        numberOfObjects +=10;
    }

    private void createAndRegisterInteractionHandler() {
        // The constructor call of the interaction handler generates meaningful default values
        // Nevertheless the start parameters can be set via setters
        // (see class definition of the interaction handler)
        interactionHandler = new InteractionHandler();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL3();


        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        // Verify if VBO-Support is available
        if (!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
            System.out.println("Error: VBO support is missing");
        else
            System.out.println("VBO support is available");



        for(int i =0;i<10;i++) {
            randomVal1[i] = (float) Math.random();
            randomVal2[i] = (float) Math.random();
        }


        //Erstellen von Licht und Material Eigenschaften



        float[] lightAmbientColor = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightDiffuseColor = {(float) 230 / 255, (float) 230 / 255, (float) 230 / 255, 1.0f};
        float[] lightSpecularColor = {162/255f, 39/255f, 44/255f, 1.0f};
        float lightIntesity = 1;
        light0 = new LightSource(lightPosition, lightAmbientColor, lightDiffuseColor, lightSpecularColor, lightIntesity);

        float[] matEmission0 = {0.0f, 0.0f, 0.0f, 1.0f};
        // float[] matAmbient0 = {162/255f, 39/255f, 44/255f, 1.0f};
        float[] matAmbient0 = {125/255/7f+162/255f/7, 100/255/7f+39/255f/7, 0.6f/7f+44/255f/7, 1.0f};
        float[] matDiffuse0 = {125/255f, 100/255f, 0.6f, 1.0f};

        float[] matSpecular0 = {0.6f, 0.6f, 0.6f, 1.0f};
        float matShininess0 = 20.0f;
        materialForFloor = new Material(matEmission0, matAmbient0, matDiffuse0, matSpecular0, matShininess0);

        float[] matEmission1 = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient1 = {(float) 40 / 255, (float) 12 / 255, 0f, 1.0f};
        float[] matDiffuse1 = {(float) 170 / 255, (float) 85 / 255, (float) 31 / 255, 1.0f};

        float[] matSpecular1 = {0.1f, 0.1f, 0.1f, 1.0f};
        float matShininess1 = 40.0f;
        material1 = new Material(matEmission1, matAmbient1, matDiffuse1, matSpecular1, matShininess1);

        float[] matEmission2 = {(float)20/255,(float)4/255,(float)10/255, 1.0f};
        float[] matAmbient2 = {(float) 14 / 255, (float) 3 / 255, (float) 7 / 255, 1.0f};
        float[] matDiffuse2 = {(float) 143 / 255, (float) 30 / 255, (float)73/ 255, 1.0f};

        float[] matSpecular2 = {(float)1, 1, 1, 1.0f};
        //float[] matSpecular2 = {(float)250/70, 60/70, 130/70, 1.0f};
        float matShininess2 = 30.0f;
        materialForMarbleTrack = new Material(matEmission2, matAmbient2, matDiffuse2, matSpecular2, matShininess2);



        float[] matEmission3 = {1f, 1f, 1f, 1.0f};
        float[] matAmbient3 = {(float) 8 / 255, (float) 0 / 255, (float) 9 / 255, 1.0f};
        //  float[] matDiffuse3 = {(float) 83 / 255, (float) 0 / 255, (float) 98 / 255, 1.0f};

        float[] matDiffuse3 = {0.85f+162/255f/4, 0.85f+39/255f/4, 0.85f+44/255f/4, 1.0f};
        float[] matSpecular3 = {1,1,1,1};
        float matShininess3 = 30.0f;
        materialForMarble = new Material(matEmission3, matAmbient3, matDiffuse3, matSpecular3, matShininess3);

        float[] matEmission4 = {1f, 1f, 1f, 1.0f};
        float[] matAmbient4 = {(float) 24 / 255, (float) 2 / 255, (float) 11/ 255, 1.0f};
        float[] matDiffuse4 = {(float) 236 / 255, (float) 19 / 255, (float) 106 / 255, 1.0f};

        float[] matSpecular4 = {1f, 1f, 1f, 1.0f};
        float matShininess4 = 700.0f;
        materialForMarkedMarble = new Material(matEmission4, matAmbient4, matDiffuse4, matSpecular4, matShininess4);
        float[] matEmission5 = {(float)20/255,(float)4/255,(float)10/255, 1.0f};
        float[] matAmbient5 = {(float) 14*3 / 255, (float) 3*3 / 255, (float) 7*3 / 255, 1.0f};
        float[] matDiffuse5 = {(float) 143*1.5f / 255, (float) 30*1.5f / 255, (float)73*1.5f/ 255, 1.0f};

        float[] matSpecular5 = {(float)1, 1, 1, 1.0f};
        //float[] matSpecular2 = {(float)250/70, 60/70, 130/70, 1.0f};
        float matShininess5 = 30.0f;
        materialForMarkedMarble = new Material(matEmission5, matAmbient5, matDiffuse5, matSpecular5, matShininess5);

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        // create vertex array objects for noOfObjects objects (VAO)
        vaoName = new int[numberOfObjects];
        gl.glGenVertexArrays(numberOfObjects, vaoName, 0);
        if (vaoName[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboName = new int[numberOfObjects];
        gl.glGenBuffers(numberOfObjects, vboName, 0);
        if (vboName[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboName = new int[numberOfObjects];
        gl.glGenBuffers(numberOfObjects, iboName, 0);
        if (iboName[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object
        //
        texName = new int[3];
        gl.glGenTextures(3, texName, 0);

        textureIndex = new int[numberOfObjects];


        shaderProgram1 = new ShaderProgram(gl);
        shaderProgram1.loadShaderAndCreateProgram(shaderPath,
                vertexShader1FileName, fragmentShader1FileName);

        shaderProgram2 = new ShaderProgram(gl);
        shaderProgram2.loadShaderAndCreateProgram(shaderPath,
                vertexShader2FileName, fragmentShader2FileName);

        shaderProgram3 = new ShaderProgram(gl);
        shaderProgram3.loadShaderAndCreateProgram(shaderPath,
                vertexShader3FileName, fragmentShader3FileName);




        //speicherung der ganzen Vertices der verschiedenen Blöcke
        marbleVertices=new float[physicsThreadManager.getMarbles().length][];
        for(int i = 0;i<physicsThreadManager.getMarbles().length;i++){
            marbleVertices[i]= objImport.getMarbleVertices().clone();
        }

        //Laden der Texturen in Buffer

        // Initialize objects to be drawn (see respective sub-methods)


        initFloorObj(gl, 0);
        for(int i=1; i<5; i++) {
            // initGuiWall(gl, i);
        }
        int levelPosition = 0;
        for(int i=5;i<(numberOfBlocks+5);i++){
            while (levelData[levelPosition+1][0]==-1){
                levelPosition++;
            }
            initObjectBlock(gl, i, levelPosition);
            levelPosition++;
        }
        for(int i=(numberOfBlocks+5);i<(numberOfBlocks+5+numbersOfMarble);i++){
            initObjectMarble(gl,i);
        }
        for(int i=(numberOfBlocks+5+numbersOfMarble);i<(numberOfBlocks+5+numbersOfMarble)+10;i++){
            initObjectBigMarble(gl,i);
        }



        PhysicsThreadManager.prepare(marbleTrackVert, marbleTrackInd);
        // END: Preparing scene
        // Initialize objects to be drawn (see respective sub-methods)


        // Switch on back face culling
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
//        gl.glCullFace(GL.GL_FRONT);
        // Switch on depth test
        gl.glEnable(GL.GL_DEPTH_TEST);

        // defining polygon drawing mode
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_FILL);
        //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_LINE);

        // Create projection-model-view matrix
        pmvMatrix = new PMVMatrix();
        //pmvMatrix.glTranslatef(0, 2f, 0);


        // Start parameter settings for the interaction handler might be called here
        // END: Preparing scene
    }

    private void initGuiFloor(GL3 gl, int objectNumber) {
        // BEGIN: Prepare cube for drawing (object 1)


        gl.glBindVertexArray(vaoName[objectNumber]);


        double[] coordinates = {-1000,-1000, 1000,1000};
        float[] cubeVertices;
        // textureIndex[objectNumber] = 1;
        float[] color2 = {0.2f, 0.4f, 0.5f};
        cubeVertices = Box.makeFloor(((float) coordinates[0]), ((float) coordinates[1]), ((float) coordinates[2]), ((float) coordinates[3]), color2);

        //float[] cubeVertices = Box.makeBoxVertices(0.8f, 0.5f, 0.4f, color1);
        int[] cubeIndices = Box.makeBoxIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[objectNumber]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[objectNumber]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4,
                IntBuffer.wrap(cubeIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * 4, 0);

        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * 4, 3 * 4);


    }

    private void initObjectMarble(GL3 gl, int objectNumber) {
        // BEGIN: Prepare cube for drawing (object 1)


        gl.glBindVertexArray(vaoName[objectNumber]);


        float[] marbleVertices;
        int[] floorIndices;
        //speichert Vertices
        marbleVertices = objImport.getMarbleVertices().clone();

        float angle;
        float angleTemp;

        //erstellt Rotationsmatrix mit dem bestimmten Winkel
        float[] r = CustomMath.rotmatY(0);



        //erstellt Translationsmatrix, fuer Translation an die richtige Position
        float x = 0f;
        float y =0f;
        float z = 0f;
        float[] t = CustomMath.transmat(x,y,z);
        //multipliziert Rotations- und Translationsmatrix fuer
        float[] j = CustomMath.matrixmult(t, r);


        marbleVertices = CustomMath.matMultVek(j, r, marbleVertices);

        //float[] marbleVertices = Box.makeBoxVertices(0.8f, 0.5f, 0.4f, color1);
        floorIndices = objImport.getMarbleIndices();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[objectNumber]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, marbleVertices.length * 4,
                FloatBuffer.wrap(marbleVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[objectNumber]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, floorIndices.length * 4,
                IntBuffer.wrap(floorIndices), GL.GL_STATIC_DRAW);


        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * 4, 0);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * 4, 3 * 4);


        // END: Prepare cube for drawing


    }

    private void initObjectBigMarble(GL3 gl, int objectNumber) {
        // BEGIN: Prepare cube for drawing (object 1)


        gl.glBindVertexArray(vaoName[objectNumber]);


        float[] marbleVertices;
        int[] bigBallIndices;
        //speichert Vertices
        marbleVertices = objImport.getBigBallVertices()[objectNumber-(numberOfBlocks+5+numbersOfMarble)].clone();

        float angle;
        float angleTemp;

        //erstellt Rotationsmatrix mit dem bestimmten Winkel
        float[] r = CustomMath.rotmatY(0);



        //erstellt Translationsmatrix, fuer Translation an die richtige Position
        float x = 0f;
        float y =0f;
        float z = 0f;
        float[] t = CustomMath.transmat(x,y,z);
        //multipliziert Rotations- und Translationsmatrix fuer
        float[] j = CustomMath.matrixmult(t, r);


        marbleVertices = CustomMath.matMultVek(j, r, marbleVertices);

        //float[] marbleVertices = Box.makeBoxVertices(0.8f, 0.5f, 0.4f, color1);
        bigBallIndices = objImport.getBigBallsIndices()[objectNumber-(numberOfBlocks+5+numbersOfMarble)];

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[objectNumber]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, marbleVertices.length * 4,
                FloatBuffer.wrap(marbleVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[objectNumber]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, bigBallIndices.length * 4,
                IntBuffer.wrap(bigBallIndices), GL.GL_STATIC_DRAW);


        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * 4, 0);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * 4, 3 * 4);


        // END: Prepare cube for drawing


    }

    private void initFloorObj(GL3 gl, int objectNumber) {
        // BEGIN: Prepare cube for drawing (object 1)


        gl.glBindVertexArray(vaoName[objectNumber]);


        float[] floorVertices;
        int[] floorIndices;
        //speichert Vertices
        floorVertices = objImport.getFloorVertices().clone();

        float angle;
        float angleTemp;

        //erstellt Rotationsmatrix mit dem bestimmten Winkel
        float[] r = CustomMath.rotmatY(0);



        //erstellt Translationsmatrix, fuer Translation an die richtige Position
        float x = dimensions[0]/2;
        float y = 0f;
        float z = dimensions[2]/2;
        float[] t = CustomMath.transmat(x,y,z);
        //multipliziert Rotations- und Translationsmatrix fuer
        float[] j = CustomMath.matrixmult(t, r);


        floorVertices = CustomMath.matMultVek(j, r, floorVertices);

        //float[] floorVertices = Box.makeBoxVertices(0.8f, 0.5f, 0.4f, color1);
        floorIndices = objImport.getFloorIndices();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[objectNumber]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, floorVertices.length * 4,
                FloatBuffer.wrap(floorVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[objectNumber]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, floorIndices.length * 4,
                IntBuffer.wrap(floorIndices), GL.GL_STATIC_DRAW);


        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * 4, 0);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * 4, 3 * 4);


        // END: Prepare cube for drawing


    }
    private void initObjectBlock(GL3 gl, int objectNumber, int levelPosition) {
        // BEGIN
        gl.glBindVertexArray(vaoName[objectNumber]);
        idToObjectNumber[objectNumber-5]=levelData[levelPosition+1][0];
        float[] cubeVertices;
        int[] cubeIndices;
        //speichert Vertices
        cubeVertices = objImport.getIdXVerticies(levelData[levelPosition+1][0]).clone();

        //erstellt Rotationsmatrix mit dem bestimmten Winkel
        int rotY=0;
        int rotX=0;
        int rotZ=0;
        switch (levelData[levelPosition+1][1]){
            case 1:
                rotY = 270;
                break;
            case 2:
                rotY = 180;
                break;
            case 3:
                rotY = 90;
                break;

            case 4:
                rotX= 270;
                break;
            case 5:
                rotX= 270;
                rotY = 270;
                break;
            case 6:
                rotX= 270;
                rotY = 180;
                break;
            case 7:
                rotX= 270;
                rotY = 90;
                break;

            case 8:
                rotX=180;
                break;
            case 9:
                rotX= 180;
                rotY = 270;
                break;
            case 10:
                rotX=180;
                rotY = 180;
                break;
            case 11:
                rotX = 180;
                rotY =90;
                break;

            case 12:
                rotX = 90;
                break;
            case 13:
                rotX = 90;
                rotY = 270;
                break;
            case 14:
                rotX = 90;
                rotY = 180;
                break;
            case 15:
                rotX = 90;
                rotY = 90;
                break;

            case 16:
                rotZ = 90;
                break;
            case 17:
                rotZ = 90;
                rotY = 270;
                break;
            case 18:
                rotZ = 90;
                rotY = 180;
                break;
            case 19:
                rotZ = 90;
                rotY = 90;
                break;

            case 20:
                rotZ = 270;
                break;
            case 21:
                rotZ = 270;
                rotY = 270;
                break;
            case 22:
                rotZ = 270;
                rotY = 180;
                break;
            case 23:
                rotZ = 270;
                rotY = 90;
                break;
        }
        float[] rY = CustomMath.rotmatY(rotY);
        float[] rX=CustomMath.rotmatX(rotX);
        float[] rZ=CustomMath.rotmatZ(rotZ);

        float[] r=CustomMath.matrixmult(rX,rY);
        r=CustomMath.matrixmult(rZ,r);
        //erstellt Translationsmatrix, fuer Translation an die richtige Position
        float x = 0.5f;
        float y = 0.5f;
        float z = 0.5f;
        x += (levelPosition) % levelData[0][0];
        y += (int)((levelPosition) / (levelData[0][0] * levelData[0][2]));
        z += (int)(((levelPosition) % (levelData[0][0] * levelData[0][2]) )/ levelData[0][0]);
        float[] t = CustomMath.transmat(x, y, z);
        //multipliziert Rotations- und Translationsmatrix fuer
        float[] j = CustomMath.matrixmult(t, r);


        cubeVertices = CustomMath.matMultVek(j, r, cubeVertices);


        //float[] cubeVertices = Box.makeBoxVertices(0.8f, 0.5f, 0.4f, color1);
        cubeIndices = objImport.getIdXIndices(levelData[levelPosition+1][0]);


        marbleTrackVert[objectNumber - 5] = cubeVertices;
        marbleTrackInd[objectNumber - 5] = cubeIndices;


        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[objectNumber]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[objectNumber]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4,
                IntBuffer.wrap(cubeIndices), GL.GL_STATIC_DRAW);


        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * 4, 0);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * 4, 3 * 4);


        // END: Prepare cube for drawing

    }

    int testverlangsamung=0;

    @Override
    public void display(GLAutoDrawable drawable) {

        if(PhysicsThreadManager.getMarblePointList().size() == 0){
            for(int i = 0; i<numbersOfMarble; i++){
                marblePoints[0][0] = PhysicsThreadManager.getMarbles()[i].getPosition()[0];
                marblePoints[0][1] = PhysicsThreadManager.getMarbles()[i].getPosition()[1];
                marblePoints[0][2] = PhysicsThreadManager.getMarbles()[i].getPosition()[2];
                marblePoints[0][3] = PhysicsThreadManager.getMarbles()[i].getRotationVelocity()[0];
                marblePoints[0][4] = PhysicsThreadManager.getMarbles()[i].getRotationVelocity()[1];
                marblePoints[0][5] = PhysicsThreadManager.getMarbles()[i].getRotationVelocity()[2];
            }
        }else{
            if(PhysicsThreadManager.getMarblePointList().size() <= frameIndex) {
                frameIndex = PhysicsThreadManager.getMarblePointList().size()-1;
            }
            marblePoints = PhysicsThreadManager.getMarblePointList().get((int)frameIndex);
            if(reset){
                frameIndex = 0;
                pause = true;
                reset = false;
            }else if(!pause){
                frameIndex+=speedSliderValue;
                if(frameIndex<0)frameIndex=0;
            }
        }

        timeLine+=0.002;


        tDUI.changeMarblePosition(marblePoints);
        //position segment angeben und nicht position + in TWodui noch ändern
        for(int marbleCounter = 0; marbleCounter<numbersOfMarble;marbleCounter++) {
            marbleSectorPosition[marbleCounter][0] = (int) (marblePoints[marbleCounter][0]);

            if (marbleSectorPosition[marbleCounter][0] > dimensions[0] - 1) marbleSectorPosition[marbleCounter][0] = dimensions[0] - 1;
            if (marbleSectorPosition[marbleCounter][0] < 0) marbleSectorPosition[marbleCounter][0] = 0;

            marbleSectorPosition[marbleCounter][1] = (int) (marblePoints[marbleCounter][1]-0.17f);

            if (marbleSectorPosition[marbleCounter][1] > dimensions[1] - 1) marbleSectorPosition[marbleCounter][1] = dimensions[1] - 1;
            if (marbleSectorPosition[marbleCounter][1] < 0) marbleSectorPosition[marbleCounter][1] = 0;

            marbleSectorPosition[marbleCounter][2] = (int) (marblePoints[marbleCounter][2]);

            if (marbleSectorPosition[marbleCounter][2] > dimensions[2] - 1) marbleSectorPosition[marbleCounter][2] = dimensions[2] - 1;
            if (marbleSectorPosition[marbleCounter][2] < 0) marbleSectorPosition[marbleCounter][2] = 0;


            if (!(marbleSectorPosition[tDUI.getWhichMarble()][0] == marbleSectorPositionLast[0] && marbleSectorPosition[tDUI.getWhichMarble()][1] == marbleSectorPositionLast[1] && marbleSectorPosition[tDUI.getWhichMarble()][2] == marbleSectorPositionLast[2])) {
                tDUI.changeLayer(marbleSectorPosition[tDUI.getWhichMarble()], false);
                //tDUI2.changeLayer(marbleSectorPosition,false);
                for (int i = 0; i < 3; i++) {
                    marbleSectorPositionLast[i] = marbleSectorPosition[tDUI.getWhichMarble()][i];
                }

            }
        }


        testverlangsamung++;
        if(testverlangsamung>0) testverlangsamung=0;
        GL3 gl = drawable.getGL().getGL3();

        gl.glViewport(0,0,widthRes,heightRes);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // Background color of the canvas
        //gl.glClearColor(162/255f, 39/255f, 44/255f, 1.0f);
        gl.glClearColor(125/255/7f+162/255f/7, 100/255/7f+39/255f/7, 0.6f/7f+44/255f/7, 1.0f);

        // Using the PMV-Tool for geometric transforms
        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
        pmvMatrix.glLoadIdentity();
        // Setting the camera position, based on user input



        //aenderung der Mausbewegung fuehrt zu Rotation
        float[] front = {0, 0, 0};
        float[] front2 = {0, 0, 0};
        front[0] = (float) Math.cos(Math.toRadians(interactionHandler.getAngleYaxis()));
        front[2] = (float) Math.sin(Math.toRadians(interactionHandler.getAngleYaxis()));
        front2[0] = (float) Math.cos(Math.PI / 2 + Math.toRadians(interactionHandler.getAngleYaxis()));
        front2[2] = (float) Math.sin(Math.PI / 2 + Math.toRadians(interactionHandler.getAngleYaxis()));
        cameraFront = VectorUtil.normalizeVec3(front);
        cameraFront2 = VectorUtil.normalizeVec3(front2);

        //Kamerabewegung in die entprechende Richtung
        if (!interactionHandler.isEscKeyPressed()) {
            if (interactionHandler.iswKeyPressed() && interactionHandler.isaKeyPressed()) {
                cameraPos[0] += cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(front, front2))[0];
                cameraPos[2] += cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(front, front2))[2];
            } else if (interactionHandler.iswKeyPressed() && interactionHandler.isdKeyPressed()) {
                float[] t = new float[3];
                t[0] = -front[0];
                t[1] = -front[1];
                t[2] = -front[2];
                cameraPos[0] += cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(t, front2))[0];
                cameraPos[2] += cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(t, front2))[2];
            } else if (interactionHandler.issKeyPressed() && interactionHandler.isaKeyPressed()) {
                float[] t = new float[3];
                t[0] = -front2[0];
                t[1] = -front2[1];
                t[2] = -front2[2];
                cameraPos[0] += cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(front, t))[0];
                cameraPos[2] += cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(front, t))[2];
            } else if (interactionHandler.issKeyPressed() && interactionHandler.isdKeyPressed()) {
                cameraPos[0] -= cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(front, front2))[0];
                cameraPos[2] -= cameraSpeed * VectorUtil.normalizeVec3(CustomMath.matrixadd(front, front2))[2];
            } else if ((interactionHandler.issKeyPressed() && interactionHandler.iswKeyPressed()) || (interactionHandler.isaKeyPressed() && interactionHandler.isdKeyPressed())) {
                //do nothing
            } else if (interactionHandler.iswKeyPressed()) {
                cameraPos[0] += cameraSpeed * cameraFront2[0];
                cameraPos[2] += cameraSpeed * cameraFront2[2];
            } else if (interactionHandler.issKeyPressed()) {
                cameraPos[0] -= cameraSpeed * cameraFront2[0];
                cameraPos[2] -= cameraSpeed * cameraFront2[2];
            } else if (interactionHandler.isaKeyPressed()) {
                cameraPos[0] += cameraSpeed * cameraFront[0];
                cameraPos[2] += cameraSpeed * cameraFront[2];
            } else if (interactionHandler.isdKeyPressed()) {
                cameraPos[0] -= cameraSpeed * cameraFront[0];
                cameraPos[2] -= cameraSpeed * cameraFront[2];
            }
        }
        if (firstFrame) {
            this.requestFocus();
            firstFrame = false;
            // interactionHandler.setAngleYaxis(45);
        }

        pmvMatrix.gluLookAt(1.8f, 2.5f, 10f,
                1.8f, 0f, 0f,
                0, 1, 0);
        pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);
        pmvMatrix.glTranslatef(-(levelData[0][0]/2f), -(levelData[0][1]/2f), -(levelData[0][2]/2f));
        // Transform for the complete scene


        //anzeigen der Objekte
        pmvMatrix.glPushMatrix();
        for(int i = 0; i<1; i++) displayFloor(gl, i);
        for(int i = 5;i<numberOfBlocks+5;i++) displayBlock(gl,i);
        for(int i = (numberOfBlocks+5);i<numberOfBlocks+5+numbersOfMarble;i++) displayMarble(gl,i,marblePoints[i-(numberOfBlocks+5)]);
        for(int i = numberOfBlocks+5+numbersOfMarble;i<numberOfBlocks+5+numbersOfMarble+10;i++)displayBigMarble(gl,i);
        pmvMatrix.glPopMatrix();

    }


    private void displayObject(GL3 gl, int i) {

        gl.glUseProgram(shaderProgram1.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        // gl.glUniform1f(2, light0.getIntesity());
        float[] lightPoints = {0, 1, 0, 0};

        gl.glUniform4fv(14, 1, lightPoints, 0);
        gl.glUniform1f(15, 1);

        gl.glUniform4fv(10, 1, materialForFloor.getAmbient(), 0);
        gl.glUniform4fv(11, 1, materialForFloor.getDiffuse(), 0);
        gl.glUniform4fv(12, 1, materialForFloor.getSpecular(), 0);
        gl.glUniform1f(13, materialForFloor.getShininess());


        gl.glBindVertexArray(vaoName[i]);
        // gl.glBindTexture(GL.GL_TEXTURE_2D, textureIndex[i]);

        //gl.glUniform1i(12, 0);

        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, Box.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);

    }

    /**
     * bearbeitet von Bela Korb
     * <br>
     * Zum Anzeigen der Falle
     * @param gl
     * @param i Objektindex
     */
    private void displayBlock(GL3 gl, int i) {
        gl.glUseProgram(shaderProgram1.getShaderProgramID());


        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        // transfer parameters of light source
        //Weitergabe von Licht, Materialeigenschaften
        float[] lightPoints = {0, 1, 0, 0};
        gl.glUniform4fv(14, 1, lightPoints, 0);
        gl.glUniform1f(15, 1);


        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        // transfer material parameters
        gl.glUniform1f(16, 1);


        gl.glUniform4fv(10, 1, materialForMarbleTrack.getAmbient(), 0);
        gl.glUniform4fv(11, 1, materialForMarbleTrack.getDiffuse(), 0);
        gl.glUniform4fv(12, 1, materialForMarbleTrack.getSpecular(), 0);
        gl.glUniform1f(13, materialForMarbleTrack.getShininess());


        gl.glBindVertexArray(vaoName[i]);


        // Draws the elements in the order defined by the index buffer object (IBO)

        gl.glDrawElements(GL.GL_TRIANGLES, objImport.getIdXIndices(idToObjectNumber[i-5]).length, GL.GL_UNSIGNED_INT, 0);

    }
    private void displayFloor(GL3 gl, int i) {

        //gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glUseProgram(shaderProgram3.getShaderProgramID());


        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        // transfer parameters of light source
        //Weitergabe von Licht, Materialeigenschaften
        float[] lightPoints = {0, 1, 0, 0};
        gl.glUniform4fv(14, 1, lightPoints, 0);
        gl.glUniform1f(15, 1);


        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        // transfer material parameters
        gl.glUniform1f(16, 1);



        gl.glUniform4fv(10, 1, materialForFloor.getAmbient(), 0);
        gl.glUniform4fv(11, 1, materialForFloor.getDiffuse(), 0);
        gl.glUniform4fv(12, 1, materialForFloor.getSpecular(), 0);
        gl.glUniform1f(13, materialForFloor.getShininess());


        gl.glBindVertexArray(vaoName[i]);

        // Draws the elements in the order defined by the index buffer object (IBO)

        gl.glDrawElements(GL.GL_TRIANGLES, objImport.getFloorIndices().length, GL.GL_UNSIGNED_INT, 0);

    }

    private void displayMarble(GL3 gl, int i, double[] position) {

        //gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glUseProgram(shaderProgram3.getShaderProgramID());


        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        // transfer parameters of light source
        //Weitergabe von Licht, Materialeigenschaften
        float[] lightPoints = {0, 1, 0, 0};
        gl.glUniform4fv(14, 1, lightPoints, 0);
        gl.glUniform1f(15, 1);


        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        // transfer material parameters
        gl.glUniform1f(16, 1);

        if(i-(numberOfBlocks+5)==tDUI.getWhichMarble()&&tDUI.isOnMarble()){
            gl.glDisable(GL.GL_DEPTH_TEST);
            gl.glUniform4fv(10, 1, materialForMarkedMarble.getAmbient(), 0);
            gl.glUniform4fv(11, 1, materialForMarkedMarble.getDiffuse(), 0);
            gl.glUniform4fv(12, 1, materialForMarkedMarble.getSpecular(), 0);
            gl.glUniform1f(13, materialForMarkedMarble.getShininess());
        }else{
            gl.glUniform4fv(10, 1, materialForMarble.getAmbient(), 0);
            gl.glUniform4fv(11, 1, materialForMarble.getDiffuse(), 0);
            gl.glUniform4fv(12, 1, materialForMarble.getSpecular(), 0);
            gl.glUniform1f(13, materialForMarble.getShininess());
        }



        float[] transformation = CustomMath.transmat((float)position[0], (float)position[1], (float)position[2]);
        //Weitergabe der Punkte an die Grafikkarte
        float[] marbleRotX = CustomMath.rotmatX((float)(position[3]*360*speedSliderValue));
        float[] marbleRotY = CustomMath.rotmatY((float)(position[4]*360*speedSliderValue));
        float[] marbleRotZ =CustomMath.rotmatZ((float)(position[5]*360*speedSliderValue));

        float[] marbleRot = CustomMath.matrixmult(marbleRotX,marbleRotY);
        marbleRot=CustomMath.matrixmult(marbleRotZ,marbleRot);
        if(isPause()){
            marbleRot=CustomMath.rotmatX(0);
        }

        transformation=CustomMath.matrixmult(transformation,marbleRot);
        float[] marbleVert = marbleVertices[i-(numberOfBlocks+5)].clone();
        marbleVertices[i-(numberOfBlocks+5)]=CustomMath.matMultVek(marbleRot,marbleRot,marbleVertices[i-(numberOfBlocks+5)]);
        marbleVert = CustomMath.matMultVek(transformation, marbleRot, marbleVert);

        gl.glBindVertexArray(vaoName[i]);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[i]);

        int[] cubeIndices = objImport.getMarbleIndices();

        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4,
                IntBuffer.wrap(cubeIndices), GL.GL_DYNAMIC_DRAW);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, marbleVert.length * 4,
                FloatBuffer.wrap(marbleVert), GL.GL_DYNAMIC_DRAW);

        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * 4, 0);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * 4, 3 * 4);


        // Draws the elements in the order defined by the index buffer object (IBO)

        gl.glDrawElements(GL.GL_TRIANGLES, objImport.getMarbleIndices().length, GL.GL_UNSIGNED_INT, 0);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }


    private void displayBigMarble(GL3 gl, int i) {

        //gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glUseProgram(shaderProgram3.getShaderProgramID());


        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        // transfer parameters of light source
        //Weitergabe von Licht, Materialeigenschaften
        float[] lightPoints = {0, 1, 0, 0};
        gl.glUniform4fv(14, 1, lightPoints, 0);
        gl.glUniform1f(15, 1);


        gl.glUniform4fv(3, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(4, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(5, 1, light0.getSpecular(), 0);

        // transfer material parameters
        gl.glUniform1f(16, 1);


            gl.glUniform4fv(10, 1, materialForMarkedMarble.getAmbient(), 0);
            gl.glUniform4fv(11, 1, materialForMarkedMarble.getDiffuse(), 0);
            gl.glUniform4fv(12, 1, materialForMarkedMarble.getSpecular(), 0);
            gl.glUniform1f(13, materialForMarkedMarble.getShininess());




        float[] transformation = CustomMath.transmat(0,(float)(Math.sin(timeLine*randomVal1[i-(numberOfBlocks+5+numbersOfMarble)]*4)*(randomVal2[i-(numberOfBlocks+5+numbersOfMarble)]+0.3)*5+(randomVal2[i-(numberOfBlocks+5+numbersOfMarble)]+0.3)*5) , 0);
        //Weitergabe der Punkte an die Grafikkarte
        float[] marbleRot = CustomMath.rotmatX(0);



        transformation=CustomMath.matrixmult(transformation,marbleRot);
         float[] bigMarbleVert = objImport.getBigBallVertices()[i-(numberOfBlocks+5+numbersOfMarble)].clone();
         bigMarbleVert = CustomMath.matMultVek(transformation, marbleRot, bigMarbleVert);

        gl.glBindVertexArray(vaoName[i]);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[i]);

        int[] cubeIndices = objImport.getBigBallsIndices()[i-(numberOfBlocks+5+numbersOfMarble)];

        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4,
                IntBuffer.wrap(cubeIndices), GL.GL_DYNAMIC_DRAW);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, bigMarbleVert.length * 4,
                FloatBuffer.wrap(bigMarbleVert), GL.GL_DYNAMIC_DRAW);

        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * 4, 0);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * 4, 3 * 4);


        // Draws the elements in the order defined by the index buffer object (IBO)

        gl.glDrawElements(GL.GL_TRIANGLES, objImport.getMarbleIndices().length, GL.GL_UNSIGNED_INT, 0);

    }



    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when the OpenGL window is resized.
     * @param drawable The OpenGL drawable
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //GL3 gl = drawable.getGL().getGL3();

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width / (float) height, 0.1f, 1000f);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when OpenGL canvas ist destroyed.
     * @param drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Deleting allocated objects, incl. shader program.");
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram1.deleteShaderProgram();
        shaderProgram2.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);

        if(!onEsc){
            System.exit(0);
        }

    }



    public GL3 getGll() {
        return gl;
    }

    public static float[][] getMarbleTrackVert() {
        return marbleTrackVert;
    }

    public static int[][] getMarbleTrackInd() {
        return marbleTrackInd;
    }

    public void setGll(GL3 gll) {
        this.gl = gll;
    }

    public InteractionHandler getInteractionHandler() {
        return interactionHandler;
    }
    public void setOnEsc(boolean onEsc) {
        this.onEsc = onEsc;
        PhysicsThreadManager.setDone(true);
    }
    public PhysicsThreadManager getPhysics(){
        return physicsThreadManager;
    }
    public static double getFrameIndex(){
        return frameIndex;
    }
    public static void setFrameIndex(int i){
        frameIndex = i;
    }
    public static boolean isPause() {
        return pause;
    }

    public static void setPause(boolean pause) {
        ShapesRendererPP.pause = pause;
    }

    public static boolean isReset() {
        return reset;
    }

    public static void setReset(boolean reset) {
        ShapesRendererPP.reset = reset;
    }
}
