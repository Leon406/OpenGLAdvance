package ll.leon.com.opengladvance.mix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.AttributeSet;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import ll.leon.com.opengladvance.R;
import ll.leon.com.opengladvance.utils.BaseGLView10;

public class MySurfaceView extends BaseGLView10 {


    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurfaceView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(GL10 gl) {
        gl.glDisable(GL10.GL_DITHER);
        //设置hint项目模式为 快速模式
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        //背景色
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        //开启深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);

        //定义像素算法(源混合因子,目标混合因子)
        //1(默认效果)
//            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_DST_ALPHA);
//            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO);
        //2(黑屏)  //开启混合
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_DST_ALPHA);


        //初始化纹理
        baseTextureId = initTexture(gl, R.drawable.base);
        topTextureId = initTexture(gl, R.drawable.top);
        int one = 65535;
        c1 = new ColorRect(one, 0, 0, one);
        c2 = new ColorRect(0, one, 0, one);

        t1 = new TextureRect(baseTextureId);
        t2 = new TextureRect(topTextureId);
    }

    @Override
    protected void onChange(GL10 gl ,int width, int height) {
        gl.glViewport(0, 0, width, height);
        //设置投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        //设置当前矩阵为单位矩阵
        gl.glLoadIdentity();
        //透视投影比例
        float r = (float) width / height;
        gl.glFrustumf(-r, r, -1, 1, 1, 100);
    }

    @Override
    protected void onFrameDraw(GL10 gl) {
        gl.glBlendFunc(src, dst);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //设置为模型矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //设置当前矩阵为单位矩阵
        gl.glLoadIdentity();

//            gl.glPushMatrix();//红色
//            gl.glTranslatef(0, 0, -2.0f);
//            c1.drawSelf(gl);
//            gl.glPopMatrix();
//
//
//            gl.glPushMatrix();//绿色
//            gl.glTranslatef(-0.7f, -0.3f, -1.9f);
//            c2.drawSelf(gl);
//            gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, -2.0f);
        t1.drawSelf(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(-0.3f, -0.3f, -1.9f);
        t2.drawSelf(gl);
        gl.glPopMatrix();
    }

    private int src = GL10.GL_ONE;
    private int dst = GL10.GL_ONE;
    private int baseTextureId;
    private int topTextureId;
    private ColorRect c1;
    private ColorRect c2;
    private TextureRect t1;
    private TextureRect t2;

    public void setSrc(int src) {
        this.src = src;
    }

    public void setDst(int dst) {
        this.dst = dst;
    }
    private int initTexture(GL10 gl, int drawableID) {

        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int textureid = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureid);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        InputStream is = this.getResources().openRawResource(drawableID);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();


        return textureid;
    }
}
