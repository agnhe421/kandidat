attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec3 a_light;
attribute vec3 a_lightColor;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoord0;
varying vec3 v_normal;
varying vec4 v_light;

void main(){
    v_texCoord0 = a_texCoord0;
    v_normal = a_normal;
    v_light = u_projViewTrans * u_worldTrans * vec4(1.0, 0.0, 0.0, 1.0);

    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1);
}