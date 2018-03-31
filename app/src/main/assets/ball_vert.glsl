uniform mat4 uMVPMatrix;
attribute vec3 aPosition;
attribute vec4 aColor;//顶点颜色数据
attribute vec2 aTexCoor;
attribute vec3 aNormal;
varying vec2 vTextureCoord;
varying vec4 vColor;//接收 从顶点着色器传过来的颜色值
varying vec4 vDiffuse;
//

uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uCamera; //相机位置
uniform vec3 ulightLocation;//光位置

varying vec4 vAmbient;//环境光强度(最终)
varying vec4 vSpecular;//镜面光强度(最终)
vec4 pointLight(vec3 normal, vec3 lightSrc,vec4 lightDiffuse) {

        vec3 vp = normalize(lightSrc -(uMVPMatrix *vec4(aPosition,1)).xyz);
        vec3 new = normalize( (uMVPMatrix *vec4(normal+aPosition,1)).xyz
                    -  (uMVPMatrix *vec4(aPosition,1)).xyz);
        return lightDiffuse *max(0.0,dot(vp,new));
}

//定位光
//void pointLight2(
//in vec3 normal,//法向量
//
//inout vec4 ambient,//环境光强度(最终)
//inout vec4 diffuse,//散射光强度(最终)
//inout vec4 specular,//镜面光强度(最终)
//
//in vec3 lightLocation,//光源的位置
//
//in vec4 lightAmbient,//环境光强度
//in vec4 lightDiffuse,//散射光强度
//in vec4 lightSpecular//镜面光强度
//){
//
//    ambient=lightAmbient;//环境光
//////散射光=材质反射系数*散射光强度*max（cos(入射角)，0）
//    //计算变换后的法向量
//    vec3 normalTarget=normal+aPosition;
//    vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz
//    -(uMMatrix*vec4(aPosition,1)).xyz;
//    //标准化法向量
//   newNormal= normalize(newNormal);
////   //计算从表面点到光源的位置=（光源位置-变换后的坐标位置）
//   vec3 vp=normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);//法向量
////   //（求法向量与 vp点积）与0的最大值
////   //散射光
////    diffuse=lightDiffuse * max(0.0f,dot(newNormal,vp));
//
////镜面光=材质反射系数*镜面光强度*max（0,（cos(入射角)）[粗糙度(次方)]）
//
//    vec3 eye=normalize( uCamera-(uMMatrix*vec4(aPosition,1)).xyz);//计算表面点到摄像机的向量
//
//    vec3 halfVector=normalize(vp+eye);//求视线与光线的半向量
//    float ess=50.0f;  //粗糙度
////    dot(newNormal,halfVector);  //法向量和半向量的点积
//    //镜面光
//    specular=lightSpecular*max(0,pow(dot(newNormal,halfVector),ess));
//}
void main()     
{                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1);

    vec4 ambientT= vec4(0,0,0,0);
    vec4 diffuseT= vec4(0,0,0,0);
    vec4 specularT=vec4(0,0,0,0);

//    pointLight2(normalize(aNormal),
//                ambientT,
//                diffuseT,
//                specularT,
//                ulightLocation,
//                  vec4(0.05f,0.05f,0.05f,1),
//                  vec4(1,1,1,1),
//                  vec4(0.3f,0.3f,0.3f,1)
//                  );
//
//    vAmbient= ambientT;
//    vDiffuse= diffuseT;
//    vSpecular= specularT;
    vTextureCoord = aTexCoor;
   	vColor = aColor;
   	vDiffuse = pointLight(normalize(aPosition),vec3(-10.0,-10.0,-20.0),vec4(1.0,1.0,1.0,1.0));
}                      