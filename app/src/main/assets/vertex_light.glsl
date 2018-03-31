uniform mat4 uMVPMatrix; //�ܱ任����
uniform mat4 uMMatrix; //�任����
uniform vec3 uLightLocation;	//��Դλ��
uniform vec3 uCamera;	//�����λ��
attribute vec3 aPosition;  //����λ��
attribute vec3 aNormal;    //���㷨����
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying float vFogFactor; //������

//��λ����ռ���ķ���
void pointLight(					//��λ����ռ���ķ���
  in vec3 normal,				//������
  inout vec4 ambient,			//����������ǿ��
  inout vec4 diffuse,				//ɢ�������ǿ��
  inout vec4 specular,			//���������ǿ��
  in vec3 lightLocation,			//��Դλ��
  in vec4 lightAmbient,			//������ǿ��
  in vec4 lightDiffuse,			//ɢ���ǿ��
  in vec4 lightSpecular			//�����ǿ��
){
  ambient=lightAmbient;			//ֱ�ӵó������������ǿ��  
  vec3 normalTarget=aPosition+normal;	//����任��ķ�����
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal); 	//�Է��������
  //����ӱ���㵽�����������
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);  
  //����ӱ���㵽��Դλ�õ�����vp
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);  
  vp=normalize(vp);//��ʽ��vp
  vec3 halfVector=normalize(vp+eye);	//����������ߵİ�����    
  float shininess=50.0;				//�ֲڶȣ�ԽСԽ�⻬
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//��������vp�ĵ����0�����ֵ
  diffuse=lightDiffuse*nDotViewPosition;				//����ɢ��������ǿ��
  float nDotViewHalfVector=dot(newNormal,halfVector);	//������������ĵ�� 
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//���淴���ǿ������
  specular=lightSpecular*powerFactor;    			//���㾵��������ǿ��
}
    float  get(){
 //���㶥�㵽������ľ���
    float fogDistance=length( uCamera-(uMMatrix*vec4(aPosition,1.0)).xyz);
    const float end=490.0;//��Ľ���λ��
    const float start=350.0;//��Ŀ�ʼλ��
    //����������
     float tmp=1.0-smoothstep(start,end,fogDistance);
    return tmp;
}
//float  get2(){
// //���㶥�㵽������ľ���
//    float fogDistance=length( uCamera-(uMMatrix*vec4(aPosition,1.0)).xyz);
//    const float end=490.0;//��Ľ���λ��
//    const float start=350.0;//��Ŀ�ʼλ��
//    //����������
//     float tmp=max(min((end-fogDistance)/(end-start),1.0),1.0);
//    return tmp;
//}
void main(){
     //�����ܱ任���� ���㵱ǰ�Ķ���λ��
    gl_Position=uMVPMatrix*vec4(aPosition,1.0);
    vec4 ambientTemp,diffuseTemp,specularTemp;
    pointLight(normalize(aNormal),ambientTemp,diffuseTemp,specularTemp,
    uLightLocation,vec4(0.4,0.4,0.4,1.0),vec4(0.7,0.7,0.7,1.0),vec4(0.3,0.3,0.3,1.0));
    ambient= ambientTemp;
    diffuse=diffuseTemp;
    specular=specularTemp;
    //�������Ч��
   vFogFactor=get();

}