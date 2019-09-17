#version 430
//Autor: Bela Korb
//angelehnt an: Visual Computin 1, OpenGl Uebung:CG_P05_P06_OpenGL_180921
layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vInColor;
layout (location = 2) in vec3 vNormal;
layout (location = 3) in vec2 Texcoord;
// Projection and model-view matrix as input uniform variables
layout (location = 0) uniform mat4 pMatrix;
layout (location = 1) uniform mat4 mvMatrix;
layout (location = 14) uniform vec4 lightPosition0;
layout (location = 15) uniform vec4 lightPosition1;
layout (location = 16) uniform vec4 lightPosition2;
layout (location = 17) uniform vec4 lightPosition3;
layout (location = 18) uniform vec4 lightPosition4;
layout (location = 19) uniform vec4 lightPosition5;
layout (location = 20) uniform vec4 lightPosition6;
layout (location = 21) uniform vec4 lightPosition7;
layout (location = 22) uniform vec4 lightPosition8;
layout (location = 23) uniform vec4 lightPosition9;

layout (location = 25) uniform float lightIntensity0;
layout (location = 26) uniform float lightIntensity1;
layout (location = 27) uniform float lightIntensity2;
layout (location = 28) uniform float lightIntensity3;
layout (location = 29) uniform float lightIntensity4;
layout (location = 30) uniform float lightIntensity5;
layout (location = 31) uniform float lightIntensity6;
layout (location = 32) uniform float lightIntensity7;
layout (location = 33) uniform float lightIntensity8;
layout (location = 34) uniform float lightIntensity9;
// Outputs from vertex shader
out VS_OUT
{
vec3 N;
vec3 L[10];
vec3 V;
float lightIntensity[10];
}

vs_out;
vec4 newLightPosition[10];
out vec4 vColor;
out vec2 texcoord;

void main() {

texcoord=Texcoord;


//vColor=vec4(vInColor,1.0f);
vColor=vec4(1f,1f,1f,1.0f);

// Calculate view-space coordinate
vec4 P = mvMatrix * vec4(vPosition, 1.0);
// Calculate normal in view-space
vs_out.N = mat3(mvMatrix) * vNormal;
// Calculate light vector
newLightPosition[0] = mvMatrix* lightPosition0;
newLightPosition[1] = mvMatrix* lightPosition1;
newLightPosition[2] = mvMatrix* lightPosition2;
newLightPosition[3] = mvMatrix* lightPosition3;
newLightPosition[4] = mvMatrix* lightPosition4;
newLightPosition[5] = mvMatrix* lightPosition5;
newLightPosition[6] = mvMatrix* lightPosition6;
newLightPosition[7] = mvMatrix* lightPosition7;
newLightPosition[8] = mvMatrix* lightPosition8;
newLightPosition[9] = mvMatrix* lightPosition9;

vs_out.lightIntensity[0] = lightIntensity0;
vs_out.lightIntensity[1] = lightIntensity1;
vs_out.lightIntensity[2] = lightIntensity2;
vs_out.lightIntensity[3] = lightIntensity3;
vs_out.lightIntensity[4] = lightIntensity4;
vs_out.lightIntensity[5] = lightIntensity5;
vs_out.lightIntensity[6] = lightIntensity6;
vs_out.lightIntensity[7] = lightIntensity7;
vs_out.lightIntensity[8] = lightIntensity8;
vs_out.lightIntensity[9] = lightIntensity9;

for(int i = 0;i<10;i++){
vs_out.L[i] = newLightPosition[i].xyz -P.xyz;
}
// Calculate view vector
vs_out.V = -P.xyz;
// Calculate the clip-space position of each vertex
gl_Position = pMatrix * P;

}
