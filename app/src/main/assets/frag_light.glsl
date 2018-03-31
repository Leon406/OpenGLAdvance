precision mediump float;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying float vFogFactor; //雾因子
void main(){
    //物体上色
    vec4 objectColor=vec4(0.95,0.95,0.95,1.0);
    //雾颜色
    vec4 fogColor=vec4(0.95,0.95,0.3,1.0);
    //判断雾因子
    if(vFogFactor!=0.0){

      objectColor=objectColor*ambient+objectColor*diffuse+specular*objectColor;

        gl_FragColor=objectColor*vFogFactor+fogColor*(1.0-vFogFactor);
    }else{
    gl_FragColor=objectColor;
    }
}