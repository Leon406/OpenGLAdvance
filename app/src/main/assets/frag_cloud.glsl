//�Ʋ���ɫ��
precision mediump float;
varying vec2 vTextureCoord;//���մӶ�����ɫ�������Ĳ���
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
uniform sampler2D sTexture;//������������
void main(){
    //����(RGBA)
    vec4 finalcolor=texture2D(sTexture,vTextureCoord);
    //͸���ȼ���
    //������ͼ�в����� ��ɫֵ��RGB������ƽ��
    //�ٽ�ƽ��ֵ��Ϊ͸����
    finalcolor.a=(finalcolor.r+finalcolor.g+finalcolor.b)/3.0;
    finalcolor=finalcolor*(ambient+specular+diffuse);
    gl_FragColor= finalcolor;
}              