#version 430
//Autor: Bela Korb
//angelehnt an: Visual Computin 1, OpenGl Uebung:CG_P05_P06_OpenGL_180921
// Parameters of light source as uniform variables from application

layout (location = 3) uniform vec4 lightSourceAmbient;
layout (location = 4) uniform vec4 lightSourceDiffuse;
layout (location = 5) uniform vec4 lightSourceSpecular;
// Material parameters as uniform variables
layout (location = 6) uniform vec4 materialAmbient;
layout (location = 7) uniform vec4 materialDiffuse;
layout (location = 8) uniform vec4 materialSpecular;
layout (location = 9) uniform float materialShininess;



layout (location = 24) uniform float lightflickering;
in vec4 vColor;out vec4 FragColor;
uniform sampler2D tex;
in vec2 texcoord;



// Input from vertex shader
in VS_OUT
{
    vec3 N;
    vec3 L[10];
    vec3 V;
    float lightIntensity[10];
} fs_in;

void main() {
        //float intensity =10/(10+0.22*length(fs_in.L)+0.20*pow(length(fs_in.L),2));


        vec3 ambient = vec3(materialAmbient) * vec3(lightSourceAmbient);
        vec3 diffuseAlbedo = vec3(materialDiffuse) * vec3(lightSourceDiffuse);
        vec3 specularAlbedo = vec3(materialSpecular) * vec3(lightSourceSpecular);
        // Normalize the incoming N, L and V vectors
        vec3 N = normalize(fs_in.N);
        //vec3 L = normalize(fs_in.L);
        vec3 V = normalize(fs_in.V);
        //vec3 H = normalize(L + V);
        //Compute the diffuse and specular components for each fragment
        //vec3 diffuse = max(dot(N, L), 0.0) * diffuseAlbedo;
        //vec3 specular = pow(max(dot(N, H), 0.0), materialShininess) * specularAlbedo;
        // Writefinal color to the framebuffer

        float attenuation=0;
        vec3 L;
        vec3 H;
        vec3 diffuse;
        vec3 specular;
         vec3 endDif;
                 vec3 endSpec;
       float flickering;
       float endIntensity;
        for(int i = 0;i<10;i++){
        flickering=  fs_in.lightIntensity[i];//(pow(sin(lightflickering+20*i),2)*0.4f+pow(cos(lightflickering*3+20*i),2)*0.5f+pow(sin(8*lightflickering+20*i),2)*0.3f+pow(cos(5*lightflickering+20*i),2)*pow(sin(lightflickering*0.7+20*i),2)+1);
          attenuation =1/(1+0.35*length(fs_in.L[i])+0.44*pow(1.4*length(fs_in.L[i]),2));
          L = normalize(fs_in.L[i]);
          H = normalize(L + V);
          diffuse = sqrt(max(dot(N, L), 0.0))*diffuseAlbedo;
          specular = pow(max(dot(N, H), 0.0), materialShininess)*specularAlbedo;
          endDif+=diffuse*attenuation*flickering;
          endSpec+=specular*attenuation*flickering;

        }


        FragColor = vec4(ambient +endDif+endSpec , 1.0)*texture(tex,texcoord);

}
