package ll.leon.com.opengladvance.fog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.view.View;

import ll.leon.com.opengladvance.utils.MatrixHelper;
import ll.leon.com.opengladvance.utils.SimpleShaderUtils;

public class TextureRect extends SimpleShaderUtils {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用
    int maPositionHandle; //顶点位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用
    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    int vCount;//顶点数量

    float width;
    float height;

    public TextureRect(View mv, float width, float height    ) {

        this.width = width;
        this.height = height;

        initVertexData();
        initShader(mv);

    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData() {
        //顶点坐标数据的初始化================begin============================
        vCount = 6;//每个格子两个三角形，每个三角形3个顶点
        float vertices[] =
                {
                        -width / 2, 0, -height / 2,
                        -width / 2, 0, height / 2,
                        width / 2, 0, height / 2,

                        -width / 2, 0, -height / 2,
                        width / 2, 0, height / 2,
                        width / 2, 0, -height / 2,
                };
        //创建顶点坐标数据缓冲
        mVertexBuffer =getFloatBuffer(vertices);

        float[] normals = {
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
        };
        //顶点法向量数据的初始化================begin============================
        mNormalBuffer =getFloatBuffer(normals);
        //顶点着色数据的初始化================end============================
    }

    public void initShader(View mv) {

        mProgram = createProgram(mv.getResources(),"vertex_light.glsl","frag_light.glsl");
        //获取程序中顶点位置属性引用
        maPositionHandle = getAttrId(mProgram, "aPosition");
        //获取程序中顶点法向量属性引用
        maNormalHandle = getAttrId(mProgram, "aNormal");
        //获取程序中总变换矩阵id
        muMVPMatrixHandle = getUniformId(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用
        maCameraHandle = getUniformId(mProgram, "uCamera");
        //获取程序中光源位置引用
        maLightLocationHandle = getUniformId(mProgram, "uLightLocation");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = getUniformId(mProgram, "uMMatrix");
    }

    public void drawSelf() {
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        uniformMatrix4fv(muMVPMatrixHandle,MatrixHelper.getFinalMatrix());
        //将位置、旋转变换矩阵传入着色器程序
        uniformMatrix4fv(muMMatrixHandle,MatrixHelper.getMMatrix());

        //将摄像机位置传入着色器程序
        uniform3fv(maCameraHandle,MatrixHelper.cameraFB);
//        GLES20.glUniform3fv(maCameraHandle, 1, MatrixHelper.cameraFB);
        //将光源位置传入着色器程序
        uniform3fv(maLightLocationHandle,MatrixHelper.lightPositionFB);
      //  GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixHelper.lightPositionFB);
        //将顶点法向量数据传入渲染管线
        vertexAttribPointer(maPositionHandle,mVertexBuffer);
        vertexAttribPointer(maNormalHandle,mNormalBuffer);
//        GLES20.glVertexAttribPointer
//                (
//                        maPositionHandle,
//                        3,
//                        GLES20.GL_FLOAT,
//                        false,
//                        3 * 4,
//                        mVertexBuffer
//                );
//        //将顶点法向量数据传入渲染管线
//        GLES20.glVertexAttribPointer
//                (
//                        maNormalHandle,
//                        3,
//                        GLES20.GL_FLOAT,
//                        false,
//                        3 * 4,
//                        mNormalBuffer
//                );
        //允许顶点位置数据数组
        enableVertexAttribArray(maPositionHandle,maNormalHandle);

        //绘制矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        disableVertexAttribArray(maPositionHandle,maNormalHandle);
    }

}
