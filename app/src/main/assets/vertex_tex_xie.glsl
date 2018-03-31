uniform mat4 uMVPMatrix; //�ܱ任����
uniform float uStartAngle;//��֡��ʼ�Ƕ�
uniform float uWidthSpan;//���򳤶��ܿ��
attribute vec3 aPosition;  //����λ��
attribute vec2 aTexCoor;    //������������
varying vec2 vTextureCoord;  //���ڴ��ݸ�ƬԪ��ɫ���ı���
void main(){
            //���㲨�˴�С
        float angleSpanH=4.0*3.14159265;
        //��ʼ��Xλ��
        float startX=-uWidthSpan/2.0;
        //����Ƕ�
        float currAngleH=uStartAngle+((aPosition.x-startX)/uWidthSpan)*angleSpanH;

        //���㲨�˴�С
        float angleSpanZ=4.0*3.14159265;
        float uHeightSpan=0.75*uWidthSpan;
        //��ʼ��Yλ��
        float startY=-uHeightSpan/2.0;
        float currAngleZ=((aPosition.y-startY)/uHeightSpan)*angleSpanZ;
        float tzh=sin(currAngleH-currAngleZ)*0.1;

        gl_Position=uMVPMatrix*vec4(aPosition.x,aPosition.y,tzh,1.0);
        vTextureCoord=aTexCoor;


}                      