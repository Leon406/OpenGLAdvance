//预览相机的着色器，顶点着色器不变，需要修改片元着色器，
//不再用sampler2D采样，需要使用samplerExternalOES 纹理采样器，
///并且要在头部增加使用扩展纹理的声明#extension GL_OES_EGL_image_external : require。
#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES vTexture;
void main() {
    gl_FragColor = texture2D( vTexture, textureCoordinate );
}