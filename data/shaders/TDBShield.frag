#version 110

uniform sampler2D shieldTex;
uniform sampler2D fxTex;
uniform float type;
uniform vec4 state;
uniform float leve;

varying vec2 fragUV;

vec2 recoverShieldUV(float size,vec2 uv,vec2 rel){
    return ((rel*256.0+(uv-0.5)*2.0*size)/256.0);
}

void main() {
    vec4 col = texture2D(shieldTex,fragUV);
    float alpha = 0.0;
    //type =>mode: 0 for normal band spread | 1 for hit | 2 for nothing-> stencil func
    if(type<=0.5){
        col.w = 0.5;
        float life = state.y;
        float dist = length((fragUV - 0.5) * 2.0)*256.0;
        float bandSize = state.z/6.0*((life<0.1)?life*10.0:1.0);
        float combinedBaseAlpha = state.w;
        float startDist = life * state.z * 2.0;  // 增加50%速度
        alpha = sin(3.1415926*(dist-startDist)/bandSize);
        if(dist<startDist||dist>startDist+bandSize){
            alpha = 0.0;
        }
        alpha = alpha * combinedBaseAlpha * leve;
        if(alpha>1.0) alpha = 1.0;
    }else {
        alpha = 1.0;
    }
    //vec4 alphaVec = (1.0,1.0,1.0,alpha);
    gl_FragColor = vec4(col.xyz,col.w*alpha);
}