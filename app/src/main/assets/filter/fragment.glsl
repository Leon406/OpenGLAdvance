precision mediump float;

uniform sampler2D vTexture;//采样器（这个值会从java里面传过来）
uniform int vChangeType;
uniform vec3 vChangeColor;
uniform int vIsHalf;
uniform float uXY;//屏幕宽高比

//给片元传递数据（纹理数据，变换前的顶点，变换后的顶点）
varying vec2 aCoordinate;
varying vec4 aPos;
varying vec4 gPosition;

//修改颜色（防止颜色值超过一）
void modifyColor(vec4 color){
    color.r=max(min(color.r,1.0),0.0);
    color.g=max(min(color.g,1.0),0.0);
    color.b=max(min(color.b,1.0),0.0);
    color.a=max(min(color.a,1.0),0.0);
}

void main(){
    //取得对应纹理坐标的纹理值
    vec4 nColor=texture2D(vTexture,aCoordinate);
    if(aPos.x>0.0||vIsHalf==0){
        if(vChangeType==1){ //grat图片
            float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;
            gl_FragColor=vec4(c,c,c,nColor.a);
        }else if(vChangeType==2||vChangeType==3){//简单色彩处理，冷暖色调、增加亮度、降低亮度等
            vec4 mcolor=nColor+vec4(vChangeColor,0.0);
            modifyColor(mcolor);
            gl_FragColor=mcolor;
        }else if(vChangeType==4){
        //纹理
        vec2 tex=aCoordinate;
        //纹理尺寸
        vec2 TexSize=vec2(100.0,100.0);
        //下一个纹理坐标
        vec2 uplftUV=vec2(  tex.x-1.0/TexSize.x,tex.y-1.0/TexSize.y );

         vec4 upColor=texture2D(vTexture,uplftUV);
         //求差运算
         vec4 delColor=nColor-upColor;
         //灰度
          vec4 bkColor=vec4(0.5,0.5,0.5,1.0);
          gl_FragColor=bkColor+delColor;
//           //计算
//           float lun=dot(delColor.rgb,vChangeColor);
//         gl_FragColor=vec4(vec3(lun),0.0)+bkColor;
        }else if(vChangeType==5){  //对比度增强
            float k = 0.6;
            vec3 iRGB = nColor.rgb;
            vec3 tRGB = vec3(0.0,0.0,0.0);

            gl_FragColor = vec4(mix(iRGB,tRGB,k),1.0);
        }else if(vChangeType==6){
        float dis=distance(vec2(gPosition.x,gPosition.x/uXY),vec2(vChangeColor.r,vChangeColor.g));
            if(dis < vChangeColor.z) {
                    nColor = texture2D(vTexture,vec2(aCoordinate.x/2.0,aCoordinate.y/2.0));
            }
                gl_FragColor =nColor;
        } else if(vChangeType==7){

                //混合纹理
                vec2  texSize=vec2(400.0,400.0);
               //马赛克模版
                vec2  mosaicSize=vec2(4.0,4.0);
                vec2 intXY =vec2(aCoordinate.x*texSize.x,aCoordinate.y*texSize.y);
                vec2 xyMosaic = vec2(floor(intXY.x/mosaicSize.x)*mosaicSize.x,floor(intXY.y/mosaicSize.y)*mosaicSize.y);

                vec2 uvMosaic =vec2(xyMosaic.x/texSize.x,xyMosaic.y/texSize.y);
                gl_FragColor = texture2D(vTexture,uvMosaic);
        } else if(vChangeType==8){
            float uD=80.0;//角度
            float uR=0.5;//旋转半径
            //传过来的纹理坐标
             vec2 st=aCoordinate;
             //模型
            ivec2 ires=ivec2(512,512);
            //向量的S坐标
            float Res=float(ires.s);
           //计算半径
            float Radius=Res*uR;
             //变换纹理
            vec2 xy=Res*st;
            vec2 dxy=xy-vec2(Res/2.0,Res/2.0);
            float r =length(dxy);
            //计算抛物线递减因子得到角度
            float beta= atan(dxy.y,dxy.x)+radians(uD)*2.0*(-(r/Radius)*(r/Radius)+1.0);
            vec2 xy1=xy;
            //范围内有效果
            if(r<=Radius){
                xy1=Res/2.0+r*vec2(cos(beta),sin(beta));
            }
            st=xy1/Res;
            gl_FragColor=vec4(texture2D(vTexture,st).rgb,1.0);
        }else if(vChangeType==9){   //颠倒
            gl_FragColor=texture2D(vTexture,vec2(1.0-aCoordinate.x,1.0-aCoordinate.y));
        }else if(vChangeType==10){    //膨胀
                                float block = 500.0;
                                float data = 1.0/block;
                                vec4 maxColor = vec4(-1.0);
                                int i;
                                int j;
                                for(i= -1; i<2;i++) {

                                     for(j = -1;j<2;j++){
                                            maxColor = max(texture2D(vTexture,vec2(
                                            aCoordinate.x+float(i)*data,aCoordinate.y+float(i)*data)),maxColor);
                                     }
                                }

                        gl_FragColor=maxColor;
            }else if(vChangeType==11){    //腐蚀
                                  float block = 500.0;
                                  float data = 1.0/block;
                                  vec4 maxColor = vec4(1.0);
                                  int i;
                                  int j;
                                  for(i= -1; i<2;i++) {

                                       for(j = -1;j<2;j++){
                                              maxColor = min(texture2D(vTexture,vec2(
                                              aCoordinate.x+float(i)*data,aCoordinate.y+float(i)*data)),maxColor);
                                       }
                                  }

                          gl_FragColor=maxColor;
              }else if(vChangeType==12) {  //普通模糊  平均

                    //     中间点 取 周围点的平均值！
                         vec2 modle= vec2(0.005,0.005);
                         //左上
                          nColor+=texture2D(vTexture,vec2(aCoordinate-modle ));
                         //左中
                         nColor+=texture2D(vTexture,vec2(aCoordinate.x-modle.x,aCoordinate.y ));
                         //左下
                         nColor+=texture2D(vTexture,vec2(aCoordinate.x-modle.x,aCoordinate.y+modle.y ));
                         //中上
                         nColor+=texture2D(vTexture,vec2(aCoordinate.x,aCoordinate.y-modle.y ));
                         //中间
                    //        nColor+=texture2D(vTexture,aCoordinate);
                         //中下
                         nColor+=texture2D(vTexture,vec2(aCoordinate.x,aCoordinate.y+modle.y ));
                         //右上
                         nColor+=texture2D(vTexture,vec2(aCoordinate.x+modle.x,aCoordinate.y-modle.y ));
                         //右中
                         nColor+=texture2D(vTexture,vec2(aCoordinate.x+modle.x,aCoordinate.y));
                         //右下
                         nColor+=texture2D(vTexture,vec2(aCoordinate.x+modle.x,aCoordinate.y+modle.y ));
                         //平均
                         nColor/=9.0;
                         gl_FragColor=nColor;

               }else if(vChangeType==13) {  //高斯模糊  权重
                     float block=150.0;
                           float data=1.0/block;
                           vec4 color=vec4(0.0);
                         //权重
                         float  factor[9];
                         factor[0]=1.0;
                         factor[1]=1.0;
                         factor[2]=1.0;
                         factor[3]=1.0;
                         factor[4]=1.0;
                         factor[5]=1.0;
                         factor[6]=1.0;
                         factor[7]=1.0;
                         factor[8]=1.0;
                         //处理周围的像素点
                         for(int i =-1;i<=1;i++){
                             for(int j =-1;j<=1;j++){
                                float  x=max(0.0,aCoordinate.x+float(i)*data);
                                float  y=max(0.0,aCoordinate.y+float(i)*data);
                                         color+=texture2D(vTexture,vec2(x,y)*factor[(i+1)*3+(j+1)]);
                             }
                         }
                         color/=9.0;
                         gl_FragColor=color;


               }else{
             gl_FragColor=nColor;
          }
    }else{
        gl_FragColor=nColor;
    }
}


