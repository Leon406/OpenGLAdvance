package ll.leon.com.opengladvance.mix3d;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import ll.leon.com.opengladvance.R;
//import ll.leon.com.opengladvance.mix3d.utils.MatrixState;
import ll.leon.com.opengladvance.utils.MatrixHelper;
import ll.leon.com.opengladvance.utils.ShaderUtils;


//纹理三角形
public class Earth extends ShaderUtils {
    private final int textureIdEarthNight;
    public int mProgram//自定义渲染管线程序id
            , muMVPMatrixHandle//总变换矩阵引用id
            , maPositionHandle //顶点位置属性引用id
            , maTexCoorHandle;//顶点纹理坐标属性引用id
    //顶点坐标数据缓冲,顶点纹理坐标数据缓冲
    private int maNormalHandle//顶点法向量属性引用id
            ;//顶点纹理坐标属性引用id


    public FloatBuffer mVertexBuffer, mTexCoorBuffer;
    private int vCount;
    private ShortBuffer indicesBuffer;
    public float angle;
    private int aColorHandle;
    private FloatBuffer colorBuffer;
    private int texuresID;
    private double r = 2;
    private int maCameraHandle;
    private int maSunLightLocationHandle;
    private int uDayTexHandle;
    private int uNightTexHandle;
    private int muMMatrixHandle;

    public Earth(View mv, double r) {
        this.r = r;
        //初始化着色器
        initData();
        initShader(mv);
        texuresID = initTexture(R.raw.earth, mv);
        textureIdEarthNight = initTexture(R.raw.earthn, mv);
    }
    @Override
    public void initData() {
        //初始化顶点坐标与着色数据
        initVertexData();
    }
    public int initTexture(int drawableId, View mv) {
        //2.04生成纹理ID
        int[] textures = new int[1];
        //2.05
        GLES20.glGenTextures(1,textures, 0);
        //2.06
        int textureId = textures[0];
        //2.07
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //2.08
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //2.09
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //2.10
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //2.11
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        //2.12通过输入流加载图片===============begin===================
        InputStream is = mv.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================

        //2.13实际加载纹理
        GLUtils.texImage2D
                ( GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmapTmp, 0);
        bitmapTmp.recycle();          //纹理加载成功后释放图片

        return textureId;
    }


    //初始化顶点坐标与着色数据的方法
    public void initVertexData() {
        //顶点坐标数据的初始化================begin============================


        final float UNIT_SIZE = 0.5f;
        ArrayList<Float> alVertix = new ArrayList<Float>();//存放顶点坐标的ArrayList
        final float angleSpan = 10f;//将球进行单位切分的角度
        //垂直方向angleSpan度一份
        for (float vAngle = 90; vAngle > -90; vAngle = vAngle - angleSpan) {
            //水平方向angleSpan度一份
            for (float hAngle = 360; hAngle > 0; hAngle = hAngle - angleSpan) {

                //纵向横向各到一个角度后计算对应的此点在球面上的坐标
                double xozLength = r * UNIT_SIZE * cos(vAngle);
                float x1 = (float) (xozLength * cos(hAngle));
                float z1 = (float) (xozLength * sin(hAngle));
                float y1 = (float) (r * UNIT_SIZE * sin(vAngle));
                xozLength = r * UNIT_SIZE * cos(vAngle - angleSpan);
                float x2 = (float) (xozLength * cos(hAngle));
                float z2 = (float) (xozLength * sin(hAngle));
                float y2 = (float) (r * UNIT_SIZE * sin(vAngle - angleSpan));
                xozLength = r * UNIT_SIZE * cos(vAngle - angleSpan);
                float x3 = (float) (xozLength * cos(hAngle - angleSpan));
                float z3 = (float) (xozLength * sin(hAngle - angleSpan));
                float y3 = (float) (r * UNIT_SIZE * sin(vAngle - angleSpan));
                xozLength = r * UNIT_SIZE * cos(vAngle);
                float x4 = (float) (xozLength * cos(hAngle - angleSpan));
                float z4 = (float) (xozLength * sin(hAngle - angleSpan));
                float y4 = (float) (r * UNIT_SIZE * sin(vAngle));
                //构建第一三角形
                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);

                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);

                alVertix.add(x4);
                alVertix.add(y4);
                alVertix.add(z4);
                //构建第二三角形
                alVertix.add(x4);
                alVertix.add(y4);
                alVertix.add(z4);

                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);

                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
            }
        }

        vCount = alVertix.size() / 3;

        float vertices[] = new float[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }

        //创建顶点坐标数据缓冲
        mVertexBuffer = getFloatBuffer(vertices);
        //顶点坐标数据的初始化================end============================

        //顶点纹理坐标数据的初始化================begin============================

        float colors[] = new float[]{

                1, 0, 1, 1,
                0, 0f, 1f, 1f,
                0, 1f, 1f, 1f,

                1, 0f, 1f, 1f,
                1, 0, 1, 1,
                0, 0f, 1f, 1f,

                0, 1f, 1f, 1f,
                1, 0f, 1f, 1f,
                1, 0, 1, 1,

                0, 0f, 1f, 1f,
                0, 1f, 1f, 1f,
                1, 0f, 1f, 1f,

                0, 0f, 1f, 1f,
                0, 1f, 1f, 1f,
                1, 0f, 1f, 1f,

                0, 0f, 1f, 1f,
                0, 1f, 1f, 1f,
                1, 0f, 1f, 1f,
        };

        colorBuffer = getFloatBuffer(colors);
        float[] texCoor = generateTexCoor(//获取切分整图的纹理数组
                (int) (360 / angleSpan), //纹理图切分的列数
                (int) (180 / angleSpan)  //纹理图切分的行数
        );
        //创建顶点纹理坐标数据缓冲
        mTexCoorBuffer = getFloatBuffer(texCoor);
        //顶点纹理坐标数据的初始化================end============================

    }

    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw, int bh) {
        float[] result = new float[bw * bh * 6 * 2];
        float sizew = 1.0f / bw;//列数
        float sizeh = 1.0f / bh;//行数
        int c = 0;
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s = j * sizew;
                float t = i * sizeh;
                result[c++] = s;
                result[c++] = t;
                result[c++] = s;
                result[c++] = t + sizeh;
                result[c++] = s + sizew;
                result[c++] = t;
                result[c++] = s + sizew;
                result[c++] = t;
                result[c++] = s;
                result[c++] = t + sizeh;
                result[c++] = s + sizew;
                result[c++] = t + sizeh;
            }
        }
        return result;
    }



    //初始化着色器
    public void initShader(View mv) {
        //加载顶点着色器的脚本内容
        String mVertexShader = loadFromAssetsFile("ball_vert.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader = loadFromAssetsFile("ball_frag.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点法向量属性引用id
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        aColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用id
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中光源位置引用id
        maSunLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "ulightLocation");
        //获取白天、黑夜两个纹理引用
        uDayTexHandle = GLES20.glGetUniformLocation(mProgram, "sTextureDay");
        uNightTexHandle = GLES20.glGetUniformLocation(mProgram, "sTextureNight");
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");

    }

    @Override
    public void drawSelf(int textureId) {
        drawSelf();
    }


    public void drawSelf() {
        //保护现场
        MatrixHelper.pushMatrix();
        //制定使用某套share程序
        GLES20.glUseProgram(mProgram);
        MatrixHelper.setInitStack();

        MatrixHelper.rotate(angle, 0, 0, 1);
        //给着色器赋值
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1,
                false, MatrixHelper.getFinalMatrix(), 0);

        //将摄像机位置传入着色器程序
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixHelper.cameraFB);
        //将光源位置传入着色器程序
        GLES20.glUniform3fv(maSunLightLocationHandle, 1, MatrixHelper.lightPositionFB);


        //画笔设置顶点数据
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(aColorHandle, 4, GLES20.GL_FLOAT,
                false, 4 * 2, colorBuffer);
        //画笔设置纹理数据
        GLES20.glVertexAttribPointer(maTexCoorHandle, 2, GLES20.GL_FLOAT,
                false, 2 * 4, mTexCoorBuffer);

        GLES20.glVertexAttribPointer    //为画笔指定顶点法向量数据
                (
                        maNormalHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //开启顶点和纹理绘制
        //画笔设置顶点数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        GLES20.glEnableVertexAttribArray(aColorHandle);
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texuresID);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIdEarthNight);
        GLES20.glUniform1i(uDayTexHandle, 0);
        GLES20.glUniform1i(uNightTexHandle, 1);
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
//        GLES20.glDrawElements(GLES20.GL_POINTS, 4*2, GLES20.GL_UNSIGNED_BYTE,indicesBuffer);
//恢复现场
        MatrixHelper.popMatrix();
    }
}
