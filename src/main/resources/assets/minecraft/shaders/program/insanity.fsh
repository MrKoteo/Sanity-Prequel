#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec3 ConvergeX = vec3(-4.0,  0.0, 2.0);
uniform vec3 ConvergeY = vec3( 0.0, -4.0, 2.0);
uniform vec3 RadialConvergeX = vec3(1.0, 1.0, 1.0);
uniform vec3 RadialConvergeY = vec3(1.0, 1.0, 1.0);
uniform float Strength = 0.0;

void main() {
    vec3 CoordX = texCoord.x * RadialConvergeX;
    vec3 CoordY = texCoord.y * RadialConvergeY;

    CoordX += ConvergeX * oneTexel.x * Strength - (RadialConvergeX - 1.0) * 0.5;
    CoordY += ConvergeY * oneTexel.y * Strength - (RadialConvergeY - 1.0) * 0.5;

    float RedValue   = texture2D(DiffuseSampler, vec2(CoordX.x, CoordY.x)).r;
    float GreenValue = texture2D(DiffuseSampler, vec2(CoordX.y, CoordY.y)).g;
    float BlueValue  = texture2D(DiffuseSampler, vec2(CoordX.z, CoordY.z)).b;

    vec3 color = vec3(RedValue, GreenValue, BlueValue);

    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, 1 - Strength);

    gl_FragColor = vec4(color, 1.0);
}