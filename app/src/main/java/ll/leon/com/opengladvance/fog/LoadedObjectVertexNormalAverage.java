package ll.leon.com.opengladvance.fog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.view.View;

import ll.leon.com.opengladvance.utils.MatrixHelper;
import ll.leon.com.opengladvance.utils.SimpleShaderUtils;

//加载后的物体——携带顶点信息，自动计算面平均法向量
public class LoadedObjectVertexNormalAverage extends SimpleShaderUtils {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用
    int maCameraHandle; //摄像机位置属性引用

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    int vCount = 0;

    public LoadedObjectVertexNormalAverage(View mv, float[] vertices, float[] normals) {
        //初始化顶点坐标与着色数据
        initVertexData(vertices, normals);
        //初始化shader
        initShader(mv);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices, float[] normals) {
        vCount = vertices.length / 3;

        mVertexBuffer =getFloatBuffer(vertices);
        mNormalBuffer =getFloatBuffer(normals);

    }

    //初始化shader
    public void initShader(View mv) {

        mProgram = createProgram(mv.getResources(), "vertex_light.glsl","frag_light.glsl");
        //获取程序中顶点位置属性引用
        maPositionHandle = getAttrId(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用
        maNormalHandle = getAttrId(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle =getUniformId(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = getUniformId(mProgram, "uMMatrix");
        //获取程序中光源位置引用
        maLightLocationHandle = getUniformId(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle = getUniformId(mProgram, "uCamera");
    }

    public void drawSelf() {
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixHelper.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixHelper.getMMatrix(), 0);
        //将光源位置传入着色器程序
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixHelper.lightPositionFB);
        //将摄像机位置传入着色器程序
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixHelper.cameraFB);
        // 将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //将顶点法向量数据传入渲染管线
        GLES20.glVertexAttribPointer
                (
                        maNormalHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );
        //启用顶点位置、法向量数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        //绘制加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }
}
