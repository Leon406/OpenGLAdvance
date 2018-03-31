attribute vec3 vPosition;
attribute vec2 vCoord;
uniform mat4 vMatrix;
uniform vec3 vKa;
uniform vec3 vKd;
uniform vec3 vKs;

varying vec2 textureCoordinate;

attribute vec3 vNormal;         //法向量
varying vec4 vDiffuse;          //用于传递给片元着色器的散射光最终强度
varying vec4 vAmbient;          //用于传递给片元着色器的环境光最终强度
varying vec4 vSpecular;          //用于传递给片元着色器的镜面光最终强度

//定位光光照计算的方法
void calculateLight(					//定位光光照计算的方法
  in vec3 normal,				//法向量
  inout vec4 ambient,			//环境光最终强度
  inout vec4 diffuse,				//散射光最终强度
  inout vec4 specular,			//镜面光最终强度
  in vec3 uCamera,
  in vec3 lightLocation,			//光源位置
  in vec4 lightAmbient,			//环境光强度
  in vec4 lightDiffuse,			//散射光强度
  in vec4 lightSpecular			//镜面光强度
){
  ambient=lightAmbient;			//直接得出环境光的最终强度

  vec3 normalTarget=vPosition+normal;	//计算变换后的法向量
  vec3 newNormal=(vMatrix*vec4(normalTarget,1)).xyz-(vMatrix*vec4(vPosition,1)).xyz;
//  vec3 newNormal=(uMMatrix*vec4(normal,1)).xyz;
  newNormal=normalize(newNormal); 	//对法向量规格化
  //计算从表面点到摄像机的向量
  vec3 eye= normalize(uCamera-(vMatrix*vec4(vPosition,1)).xyz);
  //计算从表面点到光源位置的向量vp
  vec3 vp= normalize(lightLocation-(vMatrix*vec4(vPosition,1)).xyz);
  vp=normalize(vp);//格式化vp
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;				//计算散射光的最终强度

  vec3 halfVector=normalize(vp+eye);	//求视线与光线的半向量
  float shininess=50.0;				//粗糙度，越小越光滑
  float nDotViewHalfVector=dot(newNormal,halfVector);	//法线与半向量的点积
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//镜面反射光强度因子
  specular=lightSpecular*powerFactor;    			//计算镜面光的最终强度
}

void main(){
    gl_Position = vMatrix*vec4(vPosition,1);
    textureCoordinate = vCoord;

    vec3 lightLocation=vec3(0.0,-200.0,-500.0);      //光照位置
    vec3 camera=vec3(0,200.0,0);
    float shininess=10.0;             //粗糙度，越小越光滑

    vec4 a,d,s;
    calculateLight(normalize(vNormal),a,d,s,camera,lightLocation,vec4(vKa,1.0),vec4(vKd,1.0),vec4(vKs,1.0));
    vAmbient=a;
    vDiffuse=d;
    vSpecular=s;

}