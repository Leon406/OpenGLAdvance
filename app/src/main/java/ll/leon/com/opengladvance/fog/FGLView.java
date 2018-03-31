package ll.leon.com.opengladvance.fog;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import ll.leon.com.opengladvance.utils.BaseGLView;
import ll.leon.com.opengladvance.utils.MatrixHelper;

class FGLView extends BaseGLView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 200;//角度缩放比例
    private float mPreviousX;//上次的触控位置X坐标
    //关于摄像机的变量
    float cx = 0;//摄像机x位置
    float cy = 150;//摄像机y位置
    float cz = 400;//摄像机z位置
    private TextureRect rect;

    public FGLView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        //设置屏幕背景色RGBA
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //打开背面剪裁
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //初始化变换矩阵
        MatrixHelper.setInitStack();
        //加载要绘制的物体
        cft = LoadUtil.loadFromFileVertexOnlyFace("obj/cft.obj", FGLView.this.getResources(), FGLView.this);
        qt = LoadUtil.loadFromFileVertexOnlyAverage("obj/qt.obj", FGLView.this.getResources(), FGLView.this);
        rect = new TextureRect(FGLView.this, 200, 200);
    }

    @Override
    protected void onChange(int width, int height) {
        float ratio = (float) width / height;
        //调用此方法计算产生透视投影矩阵
        float a = 0.5f;
        MatrixHelper.setProject(-ratio * a, ratio * a, -1 * a, 1 * a, 2, 1000);
        //初始化光源位置
        MatrixHelper.setLightLocationSun(100, 100, 100);
    }

    @Override
    protected void onFrameDraw() {
        MatrixHelper.setCamera
                (
                        cx,    //人眼位置的X
                        cy, //人眼位置的Y
                        cz, //人眼位置的Z
                        0,    //人眼球看的点X
                        0,  //人眼球看的点Y
                        0,  //人眼球看的点Z
                        0,    //up向量
                        1,
                        0
                );
        //缩放物体
        MatrixHelper.pushMatrix();

        MatrixHelper.pushMatrix();
        //绘制物体
        rect.drawSelf();
        //绘制长方体
        MatrixHelper.pushMatrix();

        MatrixHelper.pushMatrix();
        MatrixHelper.scale(5.0f, 5.0f, 5.0f);
        //绘制物体
        //绘制长方体
        MatrixHelper.pushMatrix();
        MatrixHelper.translate(-disWithCenter, 0f, 0);
        cft.drawSelf();
        MatrixHelper.popMatrix();
        //绘制球体
        MatrixHelper.pushMatrix();
        MatrixHelper.translate(disWithCenter, 0f, 0);
        qt.drawSelf();
        MatrixHelper.popMatrix();
        MatrixHelper.popMatrix();
    }
    LoadedObjectVertexNormalFace cft;
    LoadedObjectVertexNormalAverage qt;
    final float disWithCenter = 12.0f;//物体离中心点的距离
    private class FGLRenderer implements Renderer {
        //从指定的obj文件中加载对象


        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //设置camera位置

        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            float a = 0.5f;
            MatrixHelper.setProject(-ratio * a, ratio * a, -1 * a, 1 * a, 2, 1000);
            //初始化光源位置
            MatrixHelper.setLightLocationSun(100, 100, 100);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixHelper.setInitStack();
            //加载要绘制的物体
            cft = LoadUtil.loadFromFileVertexOnlyFace("obj/cft.obj", FGLView.this.getResources(), FGLView.this);
            qt = LoadUtil.loadFromFileVertexOnlyAverage("obj/qt.obj", FGLView.this.getResources(), FGLView.this);
            rect = new TextureRect(FGLView.this, 200, 200);
        }
    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;//计算触控笔X位移
                cx += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
                //将cx限制在一定范围内
                cx = Math.max(cx, -200);
                cx = Math.min(cx, 200);
                break;
        }
        mPreviousX = x;//记录触控笔位置
        return true;
    }
}
