package engine;
/**
 * Copyright 2012-2013 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */


import com.jogamp.opengl.GL2ES2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads a vertex and fragment shader from files and stores the OpenGL-ID
 * for the shader program.
 *
 * Based on a tutorial by Chua Hock-Chuan
 * http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 *
 * and on an example by Xerxes Rånby
 * http://jogamp.org/git/?p=jogl-demos.git;a=blob;f=src/demos/es2/RawGL2ES2demo.java;hb=HEAD
 *
 * @author Karsten Lehn
 * @version 15.9.2015, 10.9.2017
 *
 */

public class ShaderProgram {
    private int shaderProgramID;
    GL2ES2 gl;

    /** Construct shader program object for a defined OpenGL profile
     *
     * @param gl	OpenGL profile
     */
    public ShaderProgram(GL2ES2 gl) {
        this.gl = gl;
    }

    /**
     * Returns the OpenGL-ID of the shader program
     */
    public int getShaderProgramID() {
        return shaderProgramID;
    }

    /**
     * Deletes the shader program associated with this object
     * (and stored in the field shaderProgramID)
     */
    public void deleteShaderProgram() {
        gl.glDeleteProgram(shaderProgramID);
    }

    /**
     * Loads a vertex and a fragment shader and links to a shader program
     * @param path						Directory path where the shaders are located
     * @param vertexShaderFileName		File name of the vertex shader
     * @param fragmentShaderFileName	File name of the fragment shader
     */
    public void loadShaderAndCreateProgram(String path,
                                           String vertexShaderFileName,
                                           String fragmentShaderFileName) {
        // In the core profile each shader program must have a
        // vertex and a fragment shader

        // Load and compile vertex shader
        String vertexShaderString;
        int vertexShader;
        String vertexPathAndFileName = path + vertexShaderFileName;
        System.out.println("Loading vertex shader from file: " + vertexPathAndFileName);
        vertexShaderString = loadFileToString(vertexPathAndFileName);
        vertexShader = compileShader(GL2ES2.GL_VERTEX_SHADER, vertexShaderString);

        // Load and compile fragment shader
        String fragmentShaderString;
        int fragmentShader;
        String fragmentPathAndFileName = path + fragmentShaderFileName;
        System.out.println("Loading fragment shader from file: " + fragmentPathAndFileName);
        fragmentShaderString = loadFileToString(fragmentPathAndFileName);
        fragmentShader = compileShader(GL2ES2.GL_FRAGMENT_SHADER, fragmentShaderString);

        shaderProgramID = gl.glCreateProgram();
        gl.glAttachShader(shaderProgramID, vertexShader);
        gl.glAttachShader(shaderProgramID, fragmentShader);

        gl.glLinkProgram(shaderProgramID);

        // Shader can be deleted because they are linked into the program
        gl.glDetachShader(shaderProgramID, vertexShader);  // not necessarily needed
        gl.glDeleteShader(vertexShader);
        gl.glDetachShader(shaderProgramID, fragmentShader); // not necessarily needed
        gl.glDeleteShader(fragmentShader);
    }

    /**
     * Compiles a shader of a given shader type from a character string array.
     * Checks compile status and outputs the error log.
     *
     * @param shaderType	OpenGL-Shader type (eg. GL3.GL_VERTEX_SHADER)
     * @param shaderString	Character string containing the shader source code
     * @return 				OpenGL shader ID
     */
    private int compileShader(int shaderType, String shaderString) {
        int shader;

        //Compile shader String into a program.
        shader = gl.glCreateShader(shaderType);
        String[] shaderLines = new String[] { shaderString };
        int[] shaderLengths = new int[] { shaderLines[0].length() };
        gl.glShaderSource(shader, shaderLines.length, shaderLines, shaderLengths, 0);
        gl.glCompileShader(shader);

        //Check compile status.
        int[] compiled = new int[1];
        gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, compiled,0);
        if (compiled[0] != 0) {
            System.out.println("Shader compiled sucessfully");}
        else {
            // read and print compiler log to console
            int[] logLength = new int[1];
            gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(shader, logLength[0], (int[])null, 0, log, 0);

            System.err.println("Error compiling shader: " + new String(log));
            System.exit(1);
        }

        return shader;
    }

    /**
     * Loads the contents of a (text) file into a string variable.
     * @param fileName: name of the file including the (relative) path
     * @return contents of the (text) file
     *
     */
    private String loadFileToString(String fileName) {
        String fileContent = "";

        try
        {
            StringBuffer buffer = new StringBuffer();
            FileReader charStream = new FileReader(fileName);

            int bufferItem;
            bufferItem = charStream.read();
            while (bufferItem != -1) {
                buffer.append((char) bufferItem);
                bufferItem = charStream.read();
            }
            charStream.close();
            fileContent = buffer.toString();
        }
        catch(FileNotFoundException e)
        {
            System.err.println("File \"" + fileName + "\" not found!");
            System.exit(1);
        }
        catch(IOException e)
        {
            System.err.println("IO Expection encountered when reading file!");
            System.exit(1);
        }
        return fileContent;
    }
}
