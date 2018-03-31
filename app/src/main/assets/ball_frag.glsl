precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D sTextureDay;
uniform sampler2D sTextureNight;
varying vec4 vDiffuse;
varying vec4 vColor;//接收 从顶点着色器传过来的颜色值
varying vec4 vAmbient;//环境光强度(最终)
varying vec4 vSpecular;//镜面光强度(最终)
void main(){

      vec4 finalColorDay;
      vec4 finalColorNight;

      finalColorDay= texture2D(sTextureDay, vTextureCoord);
      finalColorDay = finalColorDay* (vDiffuse);
      finalColorNight = texture2D(sTextureNight, vTextureCoord);
      finalColorNight = finalColorNight*vec4(0.5,0.5,0.5,1.0);

      if(vDiffuse.x>0.21){
        gl_FragColor=finalColorDay;
      } else if(vDiffuse.x<0.05){
         gl_FragColor=finalColorNight;
      }else{
         float t=(vDiffuse.x-0.05)/0.16;
         gl_FragColor=t*finalColorDay+(1.0-t)*finalColorNight;
      }                       ;
//
//  gl_FragColor =
//            texture2D(sTextureDay,vTextureCoord) +  vDiffuse;
}