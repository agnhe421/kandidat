#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec2 v_texCoord0;
varying vec3 v_normal;
varying vec4 v_light;

void main(){
    vec4 color = texture2D(u_texture, v_texCoord0.st);
    vec4 nl = v_light*vec4(v_normal,1.0);
    float light = nl[0] + nl[1] + nl[2];
    gl_FragColor = light*color + vec4(0.4,0.4,0.4,1.0);
    //gl_FragColor = vec4(v_texCoord0, 0.0, 1.0);
}