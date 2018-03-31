uniform mat4 uMVPMatrix; //�ܱ任����
uniform float uStartAngle;//��֡��ʼ�Ƕ�
uniform float uWidthSpan;//���򳤶��ܿ��
attribute vec3 aPosition;  //����λ��
attribute vec2 aTexCoor;    //������������
varying vec2 vTextureCoord;  //���ڴ��ݸ�ƬԪ��ɫ���ı���

void main(){
    //���˴�С
    float angleSpanH=4.0*3.14159265;
    //��ʼ��Xλ��
    float startX=-uWidthSpan/2.0;
    //����Ƕ�
    float currAngle=uStartAngle+((aPosition.x-startX)/uWidthSpan)*angleSpanH;
    float tz=sin(currAngle)*0.1;
    gl_Position=uMVPMatrix*vec4(aPosition.x,aPosition.y,tz,1.0);
    vTextureCoord=aTexCoor;
}                      