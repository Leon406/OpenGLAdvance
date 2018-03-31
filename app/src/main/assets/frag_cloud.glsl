//云层着色器
precision mediump float;
varying vec2 vTextureCoord;//接收从顶点着色器过来的参数
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
uniform sampler2D sTexture;//纹理内容数据
void main(){
    //采样(RGBA)
    vec4 finalcolor=texture2D(sTexture,vTextureCoord);
    //透明度计算
    //从纹理图中采样出 颜色值（RGB）并求平均
    //再将平均值作为透明度
    finalcolor.a=(finalcolor.r+finalcolor.g+finalcolor.b)/3.0;
    finalcolor=finalcolor*(ambient+specular+diffuse);
    gl_FragColor= finalcolor;
}              