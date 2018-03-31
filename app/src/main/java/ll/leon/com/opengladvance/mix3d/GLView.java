package ll.leon.com.opengladvance.mix3d;

import android.content.Context;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

//import ll.leon.com.opengladvance.mix3d.utils.MatrixState;
import ll.leon.com.opengladvance.BuildConfig;
import ll.leon.com.opengladvance.utils.BaseGLView;
import ll.leon.com.opengladvance.utils.MatrixHelper;

/**
 * Created by Leon on 2017/12/10 0010.
 */

public class GLView extends BaseGLView {


    public boolean flag = true;
    public int angle = 0;

    public Earth earth;

    public GLView(Context context) {
        this(context, null);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onCreate() {
        GLES20.glClearColor(.5f, .5f, .5f, 1);
        earth = new Earth(this, 0.5);
        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        MatrixHelper.setLightLocationSun(100, 5, 0);

        new Thread() {
            @Override
            public void run() {
                while (flag) {
                    SystemClock.sleep(40);
                    earth.angle =(earth.angle+1)%360;
                }
            }
        }.start();


    }

    @Override
    protected void onChange(int width, int height) {
        float ratio = ((float) width) / height;
        MatrixHelper.setProject(-ratio, ratio, -1, 1, 1, 10);
        MatrixHelper.setCamera(0, 0, 3,
                0, 0, 0,
                1, -1, 0);
    }

    @Override
    protected void onFrameDraw() {
        earth.drawSelf();
    }

    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置Y坐标

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                earth.angle = (earth.angle + 2) % 360;
        }
        mPreviousX = x;//记录触控笔位置
        mPreviousY = y;
        return true;
    }
}
