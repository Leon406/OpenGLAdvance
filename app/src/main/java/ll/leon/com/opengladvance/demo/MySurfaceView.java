package ll.leon.com.opengladvance.demo;

import android.content.Context;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.view.MotionEvent;

import ll.leon.com.opengladvance.R;
import ll.leon.com.opengladvance.utils.BaseGLView;
import ll.leon.com.opengladvance.utils.MatrixHelper;


class MySurfaceView extends BaseGLView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
   // private SceneRenderer mRenderer;//场景渲染器

    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置Y坐标

    int textureIdEarth;//系统分配的地球纹理id
    int textureIdEarthNight;//系统分配的地球夜晚纹理id
    int textureIdCloud, textureIdmoon, textureIdmoonNight;//系统分配的云层纹理id

    float yAngle = 0;//太阳灯光绕y轴旋转的角度
    float xAngle = 0;//摄像机绕X轴旋转的角度

    float eAngle = 0;//地球自转角度
    float cAngle = 0;//天球自转的角度
    float nAngle = 0;

    private float ratio;

    public MySurfaceView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //创建地球对象
        earth = new Earth(this, 2.0f);
        cloud = new Cloud(this, 2.02f);
        moon = new Earth(this, 0.5f);
        //打开深度检测
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //初始化变换矩阵
        MatrixHelper.setInitStack();
    }

    @Override
    protected void onChange(int width, int height) {
        ratio = (float) width / height;
        //调用此方法计算产生透视投影矩阵
        MatrixHelper.setProject(-ratio, ratio, -1, 1, 4f, 100);
        //调用此方法产生摄像机9参数位置矩阵
        MatrixHelper.setCamera(0, 0, 7.2f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //打开背面剪裁
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //初始化纹理
        textureIdEarth = initTexture(R.drawable.earth);
        textureIdEarthNight = initTexture(R.drawable.earthn);
        textureIdCloud = initTexture(R.drawable.cloud);
        textureIdmoon = initTexture(R.drawable.moon);
        textureIdmoonNight = initTexture(R.drawable.moon);
        //设置太阳灯光的初始位置
        MatrixHelper.setLightLocationSun(100, 5, 0);

        //启动一个线程定时旋转地球
        new Thread() {
            public void run() {
                while (MatrixHelper.threadFlag) {
                    //地球自转角度
                    eAngle = (eAngle + 2) % 360;
                    //天气球自转角度
                    cAngle = (cAngle + 0.2f) % 360;
                    nAngle = eAngle - (nAngle + 2) % 360;
                    SystemClock.sleep(100);
                }
            }
        }.start();
    }

    @Override
    protected void onFrameDraw() {
        MatrixHelper.pushMatrix();
        MatrixHelper.rotate(eAngle, 0, 1, 0);
        earth.drawSelf(textureIdEarth, textureIdEarthNight);
        //设置深度缓冲区为只读模式
        GLES20.glDepthMask(false);
        //开启
        GLES20.glEnable(GLES20.GL_BLEND);
        //设置混合因子
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //绘制云层
        cloud.drawSelf(textureIdCloud);
        //关闭
        GLES20.glDisable(GLES20.GL_BLEND);
        MatrixHelper.popMatrix();
        MatrixHelper.pushMatrix();

        MatrixHelper.rotate(cAngle, 0, 1, 0);
        MatrixHelper.popMatrix();
        MatrixHelper.pushMatrix();
        MatrixHelper.rotate(nAngle, 0, 1, 0);
        MatrixHelper.translate(-2, 0, 0);
        moon.drawSelf(textureIdmoon, textureIdmoon);
        MatrixHelper.popMatrix();

        //设置深度缓冲区为读写模式
        GLES20.glDepthMask(true);

    }

    Earth earth, moon;//地球,月球
    Cloud cloud;//云层

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //触控横向位移太阳绕y轴旋转
                float dx = x - mPreviousX;//计算触控笔X位移
                yAngle += dx * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
                float sunx = (float) (Math.cos(Math.toRadians(yAngle)) * 100);
                float sunz = -(float) (Math.sin(Math.toRadians(yAngle)) * 100);
                MatrixHelper.setLightLocationSun(sunx, 5, sunz);

                //触控纵向位移摄像机绕x轴旋转 -90～+90
                float dy = y - mPreviousY;//计算触控笔Y位移
                xAngle += dy * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
                if (xAngle > 90) {
                    xAngle = 90;
                } else if (xAngle < -90) {
                    xAngle = -90;
                }
                float cy = (float) (7.2 * Math.sin(Math.toRadians(xAngle)));
                float cz = (float) (7.2 * Math.cos(Math.toRadians(xAngle)));
                float upy = (float) Math.cos(Math.toRadians(xAngle));
                float upz = -(float) Math.sin(Math.toRadians(xAngle));
                MatrixHelper.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);
        }
        mPreviousX = x;//记录触控笔位置
        mPreviousY = y;
        return true;
    }
}
