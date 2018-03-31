package ll.leon.com.opengladvance.wave;

import android.opengl.GLES20;
import android.view.View;

import java.nio.FloatBuffer;

import ll.leon.com.opengladvance.utils.MatrixHelper;
import ll.leon.com.opengladvance.utils.ShaderUtils;

//有波浪效果的纹理矩形
public class TextureRect extends ShaderUtils {
    int[] mPrograms = new int[3];//自定义渲染管线着色器程序id
    int[] muMVPMatrixHandle = new int[3];//总变换矩阵引用
    int[] maPositionHandle = new int[3]; //顶点位置属性引用
    int[] maTexCoorHandle = new int[3]; //顶点纹理坐标属性引用
    int[] maStartAngleHandle = new int[3]; //本帧起始角度属性引用
    int[] muWidthSpanHandle = new int[3];//横向长度总跨度引用
    float WIDTH_SPAN = 3.3F;
    private FloatBuffer verBuffer, texBuffer;
    public int currIndex =2;
    private float corrStartAngle = 0;
    public int vCount;

    public TextureRect(View mv) {
        //初始化顶点和纹理坐标 数据
        initData();
        //初始化着色器
        initShader(mv, 0, "vertex_tex_x.glsl");
        initShader(mv, 1, "vertex_tex_xie.glsl");
        initShader(mv, 2, "vertex_tex_xy.glsl");
    }

    @Override
    public void initData() {
        //列数和行数
        int cols = 24;
        int rows = cols / 3 * 4;
        //每格的单位长度
        float SIZE = WIDTH_SPAN / cols;
        //初始化顶点==================================================
        //行数*列数=矩形的个数
        //矩形的个数*2*3=顶点个数
        vCount = cols * rows * 2 * 3;
        //顶点容器              [一个顶点有 xyz ]
        float[] ver = new float[vCount * 3];
        //计数器
        int count = 0;
        //计算顶点
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                //行数*列数=矩形的个数
                //求顶点！！
                //左上角
                float x = -SIZE * cols / 2 + i * SIZE;
                float y = SIZE * rows / 2 - j * SIZE;
                float z = 0;

                ver[count++] = x;
                ver[count++] = y;
                ver[count++] = z;

                ver[count++] = x;
                ver[count++] = y - SIZE;
                ver[count++] = z;

                ver[count++] = x + SIZE;
                ver[count++] = y;
                ver[count++] = z;
                //===================
                ver[count++] = x + SIZE;
                ver[count++] = y;
                ver[count++] = z;

                ver[count++] = x;
                ver[count++] = y - SIZE;
                ver[count++] = z;

                ver[count++] = x + SIZE;
                ver[count++] = y - SIZE;
                ver[count++] = z;
            }
        }

        verBuffer = getFloatBuffer(ver);
        //得到纹理坐标数据
        float[] texcoor = getTexCoor(cols, rows);
        texBuffer = getFloatBuffer(texcoor);
    }

    @Override
    public void initShader(View v) {

    }

    public float[] getTexCoor(int bw, int bh) {
        //计算纹理坐标个数ST
        //纹理坐标分量总个数
        int texindx = bw * bh * 2 * 3 * 2;
        float[] tex = new float[texindx];
        //纹理坐标的最大值是1
        //纹理的 列数和行数
        float sizew = 1.0f / bw;
        float sizeh = 0.75f / bh;
        //计数器
        int c = 0;
        //计算纹理个数
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                //左上角
                float s = j * sizew;
                float t = i * sizeh;
                tex[c++] = s;
                tex[c++] = t;

                tex[c++] = s;
                tex[c++] = t + sizeh;

                tex[c++] = s + sizew;
                tex[c++] = t;
                //=================
                tex[c++] = s + sizew;
                tex[c++] = t;

                tex[c++] = s;
                tex[c++] = t + sizeh;


                tex[c++] = s + sizew;
                tex[c++] = t + sizeh;
            }
        }

        return tex;
    }

    //初始化shader
    public void initShader(View mv, int index, String vertexName) {

        mPrograms[index] = createProgram(mv.getResources(),vertexName,"frag_tex.glsl");
        //获取程序中顶点位置属性引用
        maPositionHandle[index] = GLES20.glGetAttribLocation(mPrograms[index], "aPosition");
        //获取程序中顶点纹理坐标属性引用
        maTexCoorHandle[index] = GLES20.glGetAttribLocation(mPrograms[index], "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle[index] = GLES20.glGetUniformLocation(mPrograms[index], "uMVPMatrix");
        //获取本帧起始角度属性引用
        maStartAngleHandle[index] = GLES20.glGetUniformLocation(mPrograms[index], "uStartAngle");
        //获取横向长度总跨度引用
        muWidthSpanHandle[index] = GLES20.glGetUniformLocation(mPrograms[index], "uWidthSpan");
    }


    public void drawSelf(int texId) {

        GLES20.glUseProgram(mPrograms[currIndex]);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle[currIndex], 1, false
                , MatrixHelper.getFinalMatrix(), 0);
        GLES20.glUniform1f(maStartAngleHandle[currIndex], corrStartAngle+=.1);
        GLES20.glUniform1f(muWidthSpanHandle[currIndex], WIDTH_SPAN);
        //传入顶点和纹理的数据
        GLES20.glVertexAttribPointer(maPositionHandle[currIndex], 3,
                GLES20.GL_FLOAT, false, 0, verBuffer);
        GLES20.glVertexAttribPointer(maTexCoorHandle[currIndex], 2,
                GLES20.GL_FLOAT, false, 0, texBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle[currIndex]);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle[currIndex]);
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }

}
