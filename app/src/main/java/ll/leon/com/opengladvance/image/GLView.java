package ll.leon.com.opengladvance.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.AttributeSet;


import ll.leon.com.opengladvance.utils.BaseGLView;
import ll.leon.com.opengladvance.utils.MatrixHelper;


/**
 * Description:
 */

public class GLView extends BaseGLView {


    private int resId;

    public GLView(Context context) {
        super(context);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Image image;
    private int textureId;
    private float uXY;
    public GLView(Context context,  int id) {
        super(context);

        resId =id;
    }

    @Override
    protected void onCreate() {
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        image = new Image(this);
        textureId = initTexture(resId);
    }

    @Override
    protected void onChange(int width, int height) {
        int w = textureWidth;
        int h = textureHeight;
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        uXY = sWidthHeight;

        if (sWH > sWidthHeight) {
            MatrixHelper.orthoM( -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
        } else {
            MatrixHelper.orthoM( -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
        }
        //设置相机位置
        MatrixHelper.setCamera(0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        MatrixHelper.multiplyMM();
    }

    @Override
    protected void onFrameDraw() {
        GLES20.glUniform1f(image.glHUxy, uXY);
        GLES20.glUniformMatrix4fv(image.glHMatrix, 1, false, MatrixHelper.mMVPMatrix, 0);
        image.drawSelf(textureId);
    }
}
