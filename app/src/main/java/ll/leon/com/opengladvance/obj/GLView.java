package ll.leon.com.opengladvance.obj;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import ll.leon.com.opengladvance.R;
import ll.leon.com.opengladvance.mtl.LoadUtil;
import ll.leon.com.opengladvance.utils.BaseGLView;
import ll.leon.com.opengladvance.utils.MatrixHelper;


/**
 * @author Leon
 * @Desc: GLView
 */

public class GLView extends BaseGLView {

    float yAngle;//绕Y轴旋转的角度
    float xAngle; //绕x轴旋转的角度
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private int textureId;
    ObjTexture objTexture;

    public GLView(Context context) {
        super(context);
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onCreate() {
        MatrixHelper.setInitStack();
        //初始化光源位置
        MatrixHelper.setLightLocationSun(40, 10, 20);
        objTexture = LoadUtil.loadFromFile("obj/ch_t.obj", getResources(), this);
//        objTexture = LoadUtil.loadFromFile("test.obj", getResources(), this);
        textureId = initTexture(R.drawable.ghxp);
    }


    @Override
    protected void onChange(int width, int height) {
        float ratio = (float) width / height;
        //调用此方法计算产生透视投影矩阵
        MatrixHelper.setProject(-ratio, ratio, -1, 1, 2, 100);
        //调用此方法产生摄像机9参数位置矩阵
        MatrixHelper.setCamera(0, 0, 0, 0f, 0f, -1f, 0f, 1.0f, 0.0f);
    }

    @Override
    protected void onFrameDraw() {

        MatrixHelper.pushMatrix();
        MatrixHelper.translate(0, -2f, -25f);
        //绕Y轴、Z轴旋转
        MatrixHelper.rotate(yAngle, 0, 1, 0);
        MatrixHelper.rotate(xAngle, 1, 0, 0);

        //若加载的物体部位空则绘制物体
        if (objTexture != null) {
            objTexture.drawSelf(textureId);
        }
        MatrixHelper.popMatrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//计算触控笔Y位移
                float dx = x - mPreviousX;//计算触控笔X位移
                yAngle += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
                xAngle += dy * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
                requestRender();//重绘画面
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
}
