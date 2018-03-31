precision mediump float;
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying float vFogFactor; //������
void main(){
    //������ɫ
    vec4 objectColor=vec4(0.95,0.95,0.95,1.0);
    //����ɫ
    vec4 fogColor=vec4(0.95,0.95,0.3,1.0);
    //�ж�������
    if(vFogFactor!=0.0){

      objectColor=objectColor*ambient+objectColor*diffuse+specular*objectColor;

        gl_FragColor=objectColor*vFogFactor+fogColor*(1.0-vFogFactor);
    }else{
    gl_FragColor=objectColor;
    }
}