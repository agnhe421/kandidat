#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec2 v_texCoord0;
varying vec3 v_normal;
varying vec3 v_light;
varying vec3 v_eye;

void main(){
    vec4 color = texture2D(u_texture, v_texCoord0.st);
    float nl = 0.5*max(0.0, dot(v_light,v_normal));
    vec3 R = 2.0*dot(v_light,v_normal) * v_normal - v_light;
    gl_FragColor = vec4(dot(R, v_eye)*vec3(0.5,0.5,0.5), 1.0); //* color + 0.2*color;
    gl_FragColor = color;
}