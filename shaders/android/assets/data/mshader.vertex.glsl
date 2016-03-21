attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec3 a_light;
attribute vec3 a_lightColor;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoord0;
varying vec3 v_normal;
varying vec3 v_light;
varying vec3 v_eye;

void main(){
    v_texCoord0 = a_texCoord0;
    v_normal = normalize(a_normal);
    v_light = normalize(vec3(5.0, 0.0, 0.0) - a_position);
    vec4 v = u_projViewTrans * vec4(a_position,1.0);
    v_eye = normalize(vec3(v[0], v[1], v[2]));

    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1);
}