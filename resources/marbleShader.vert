#version 430
//Autor: Bela Korb
//angelehnt an: Visual Computin 1, OpenGl Uebung:CG_P05_P06_OpenGL_180921
layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vNormal;

// Projection and model-view matrix as input uniform variables
layout (location = 0) uniform mat4 pMatrix;
layout (location = 1) uniform mat4 mvMatrix;
layout (location = 14) uniform vec4 lightPosition0;


layout (location = 15) uniform float lightIntensity0;



// Outputs from vertex shader
out VS_OUT
{
    vec3 N;
    vec3 L;
    vec3 V;
    float lightIntensity;
}

vs_out;

vec4 newLightPosition;

void main() {




    //vColor=vec4(vInColor,1.0f);
    vec4 vColor=vec4(1f,1f,1f,1.0f);

    // Calculate view-space coordinate
    vec4 P = mvMatrix * vec4(vPosition, 1.0);
    // Calculate normal in view-space
    vs_out.N = mat3(mvMatrix) * vNormal;
    // Calculate light vector

    newLightPosition = mvMatrix* lightPosition0;



    vs_out.lightIntensity = lightIntensity0;




    vs_out.L = newLightPosition.xyz -P.xyz;

    // Calculate view vector
    vs_out.V = -P.xyz;
    // Calculate the clip-space position of each vertex
    gl_Position = pMatrix * P;

}
