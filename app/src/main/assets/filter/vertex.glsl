attribute vec4 vPosition;
attribute vec2 vCoordinate;//纹理数据
uniform mat4 vMatrix;
//给片元传递数据（纹理，变换前的顶点，变换后的顶点）
varying vec2 aCoordinate;
varying vec4 aPos;
varying vec4 gPosition;

void main(){
    gl_Position=vMatrix*vPosition;
    aPos=vPosition;
    aCoordinate=vCoordinate;
    gPosition=vMatrix*vPosition;
}