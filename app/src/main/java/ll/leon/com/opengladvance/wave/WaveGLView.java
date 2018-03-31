package ll.leon.com.opengladvance.wave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import ll.leon.com.opengladvance.R;
import ll.leon.com.opengladvance.utils.BaseGLView;
import ll.leon.com.opengladvance.utils.MatrixHelper;

/**
 * Created by Leon on 2017/12/31 0031.
 */

public class WaveGLView extends BaseGLView {
    public WaveGLView(Context context) {
        super(context);
    }

    public WaveGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public   TextureRect texRect;
    @Override
    protected void onCreate() {
        textureFlagId = initTexture(R.drawable.android_flag);
        texRect = new TextureRect(this);
        //关闭背面剪裁
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        //初始化变换矩阵
        MatrixHelper.setInitStack();
    }

    @Override
    protected void onChange(int width, int height) {
//计算GLSurfaceView的宽高比
        float ratio = (float) width / height;
        //调用此方法计算产生透视投影矩阵
        MatrixHelper.setProject(-ratio, ratio, -1, 1, 4, 100);
        //调用此方法产生摄像机9参数位置矩阵
        MatrixHelper.setCamera(0, 0, 10, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    @Override
    protected void onFrameDraw() {
        MatrixHelper.pushMatrix();
        MatrixHelper.translate(0, 0, -1);
        MatrixHelper.rotate(yAngle, 0, 1, 0);
        MatrixHelper.rotate(xAngle, 1, 0, 0);
        //绘制纹理矩形
        texRect.drawSelf(textureFlagId);
        MatrixHelper.popMatrix();
    }

    public int initTexture(int drawableId) {//textureId
        //生成纹理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1,    //纹理个数
                textures,  //生成纹理ID
                0   //偏移量
        );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        Bitmap bitmapTmp = BitmapFactory.decodeResource(getResources(), drawableId);

        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D, //纹理类型
                        0,
                        GLUtils.getInternalFormat(bitmapTmp),
                        bitmapTmp, //纹理图像
                        GLUtils.getType(bitmapTmp),
                        0 //纹理边框尺寸
                );

        bitmapTmp.recycle();

        return textureId;
    }
    int textureFlagId;//国旗纹理id
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    float xAngle;//整体场景绕X轴旋转的角度
    float yAngle;//整体场景绕Y轴旋转的角度
    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置X坐标
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;//计算触控笔X位移
                float dy = y - mPreviousY;//计算触控笔Y位移
                yAngle += dx * TOUCH_SCALE_FACTOR;//设置整体场景绕Y轴旋转角度
                xAngle += dy * TOUCH_SCALE_FACTOR;//设置绕整体场景X轴旋转角度
                requestRender();//重绘画面
        }
        mPreviousX = x;//记录触控笔位置
        mPreviousY = y;//记录触控笔位置
        return true;
    }
}
