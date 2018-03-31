package ll.leon.com.opengladvance.image;

import android.opengl.GLES20;
import android.view.View;


import java.nio.FloatBuffer;

import ll.leon.com.opengladvance.utils.ShaderUtils;


/**
 * 创建时间：2017/11/11
 *
 * @author Leon
 */

public class Image extends ShaderUtils {
    private FloatBuffer bPos;
    private FloatBuffer bCoord;
    private int mProgram;
    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    public int glHMatrix;
    private int hIsHalf;
    public int glHUxy;
    private int hChangeType;
    private int hChangeColor;

    public float[][] images = new float[][]{
            {0.0f, 0.0f, 0.0f}//原图
            , {0.299f, 0.587f, 0.114f}//灰度
            , {0.1f, 0.1f, 0.0f}//暖色调
            , {0.0f, 0.0f, 0.1f}//冷色调
            , {0.2125f, 0.7154f, 0.0721f}//浮雕
            , {0f, 0f, 0f}//图像对比度增强
            , {0f, 0f, 10f}//放大镜
            , {0f, 0f, 10f}//马赛克
            , {0f, 0f, 0f}//图像扭曲
            , {0f, 0f, 0f}//图像颠倒
            , {0f, 0f, 0f}//膨胀
            , {0f, 0f, 0f}//腐蚀
            , {0f, 0f, 0f}//普通模糊
            , {0f, 0f, 0f}//高斯模糊
    };

    public Image(View mView) {
        initData();
        initShader(mView);
    }


    @Override
    public void initShader(View mView) {
        mProgram = createProgram(mView.getResources(), "filter/vertex.glsl", "filter/fragment.glsl");
        glHPosition = getAttrId(mProgram, "vPosition");
        glHCoordinate = getAttrId(mProgram, "vCoordinate");
        glHTexture = getUniformId(mProgram, "vTexture");
        glHMatrix = getUniformId(mProgram, "vMatrix");
        hIsHalf = getUniformId(mProgram, "vIsHalf");
        glHUxy = getUniformId(mProgram, "uXY");
        hChangeType = getUniformId(mProgram, "vChangeType");
        hChangeColor = getUniformId(mProgram, "vChangeColor");
    }


    @Override
    public void initData() {
        //图片四个角的坐标
        final float[] sPos = {
                -1.0f, 1.0f,
                -1.0f, -1.0f,
                1.0f, 1.0f,
                1.0f, -1.0f

        };
        //图片四个角的纹理坐标
        final float[] sCoord = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
        };
        bPos = getFloatBuffer(sPos);
        bCoord = getFloatBuffer(sCoord);

        setTextureParam();
    }


    public boolean isHalf = false;
    public int one = 0;

    @Override
    public void drawSelf(int textureId) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniform1i(hChangeType, one);
        GLES20.glUniform3fv(hChangeColor, 1, images[one], 0);
        GLES20.glUniform1i(hIsHalf, isHalf ? 1 : 0);
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        GLES20.glUniform1i(glHTexture, 0);
        GLES20.glVertexAttribPointer(glHPosition, 2, GLES20.GL_FLOAT, false, 0, bPos);
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }


}
