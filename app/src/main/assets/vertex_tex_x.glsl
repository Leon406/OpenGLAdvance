uniform mat4 uMVPMatrix; //总变换矩阵
uniform float uStartAngle;//本帧起始角度
uniform float uWidthSpan;//横向长度总跨度
attribute vec3 aPosition;  //顶点位置
attribute vec2 aTexCoor;    //顶点纹理坐标
varying vec2 vTextureCoord;  //用于传递给片元着色器的变量

void main(){
    //波浪大小
    float angleSpanH=4.0*3.14159265;
    //起始的X位置
    float startX=-uWidthSpan/2.0;
    //计算角度
    float currAngle=uStartAngle+((aPosition.x-startX)/uWidthSpan)*angleSpanH;
    float tz=sin(currAngle)*0.1;
    gl_Position=uMVPMatrix*vec4(aPosition.x,aPosition.y,tz,1.0);
    vTextureCoord=aTexCoor;
}                      