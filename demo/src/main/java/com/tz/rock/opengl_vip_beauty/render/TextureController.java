package com.tz.rock.opengl_vip_beauty.render;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.tz.rock.opengl_vip_beauty.interfaces.FrameCallback;
import com.tz.rock.opengl_vip_beauty.interfaces.Renderer;
import com.tz.rock.opengl_vip_beauty.utils.AFilter;
import com.tz.rock.opengl_vip_beauty.utils.EasyGlUtils;
import com.tz.rock.opengl_vip_beauty.utils.GroupFilter;
import com.tz.rock.opengl_vip_beauty.utils.MatrixUtils;
import com.tz.rock.opengl_vip_beauty.utils.NoFilter;
import com.tz.rock.opengl_vip_beauty.utils.TextureFilter;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Description: 借助GLSurfaceView创建的GL环境，做渲染工作。不将内容渲染到GLSurfaceView
 * 的Surface上，而是将内容绘制到外部提供的Surface、SurfaceHolder或者SurfaceTexture上。
 */
public class TextureController implements GLSurfaceView.Renderer {

    private Object surface;

    private GLView mGLView;
    private Context mContext;

    private Renderer mRenderer;                                 //用户附加的Renderer或用来监听Renderer
    private TextureFilter mEffectFilter;                        //特效处理的Filter
    private GroupFilter mGroupFilter;                           //中间特效
    private AFilter mShowFilter;                                //用来渲染输出的Filter
    private Point mDataSize;                                    //数据的大小
    private Point mWindowSize;                                  //输出视图的大小
    private AtomicBoolean isParamSet = new AtomicBoolean(false);
    private float[] SM = new float[16];                           //用于绘制到屏幕上的变换矩阵
    private int mShowType = MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式
    private int mDirectionFlag = -1;                               //AiyaFilter方向flag

    private float[] callbackOM = new float[16];                   //用于绘制回调缩放的矩阵

    //创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];

    private boolean isRecord = false;                             //录像flag
    private boolean isShoot = false;                              //一次拍摄flag
    private ByteBuffer[] outPutBuffer = new ByteBuffer[3];      //用于存储回调数据的buffer
    private FrameCallback mFrameCallback;                       //回调
    private int frameCallbackWidth, frameCallbackHeight;        //回调数据的宽高
    private int indexOutput = 0;                                  //回调数据使用的buffer索引

    public TextureController(Context context) {
        this.mContext = context;
        //2.01初始化
        init();
    }

    private void init() {
        //2.02创建GLSurFaceView
        mGLView = new GLView(mContext);

        //避免GLView的attachToWindow和detachFromWindow崩溃
        ViewGroup v = new ViewGroup(mContext) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        v.addView(mGLView);
        v.setVisibility(View.GONE);
        /*
        * 到这，我们就可以愉快的使用GLSurfaceView来提供GL环境，
        * 给指定的Surface或者SurfaceTexture渲染图像了。
        * */
        //==========================================================

        //特效处理的Filter
        mEffectFilter = new TextureFilter(mContext.getResources());
        //用来渲染输出的Filter
        mShowFilter = new NoFilter(mContext.getResources());
        //中间特效（增加特效）
        mGroupFilter = new GroupFilter(mContext.getResources());
        //图像大小
        //设置默认的DateSize，DataSize由AiyaProvider根据数据源的图像宽高进行设置
        mDataSize = new Point(720, 1280);
        mWindowSize = new Point(720, 1280);
    }
//===============================================================================
//===============================================================================
//===============================================================================

    /**
     * 自定义GLSurfaceView，暴露出onAttachedToWindow
     * 方法及onDetachedFromWindow方法，取消holder的默认监听
     * onAttachedToWindow及onDetachedFromWindow必须保证view
     * 存在Parent
     */
    private class GLView extends GLSurfaceView {

        public GLView(Context context) {
            super(context);
            //2.02.01初始化GLSurfaceView
            init();
        }

        private void init() {
            //避免GLSurfaceView自带的Surface影响渲染
            getHolder().addCallback(null);

            //GLSurfaceView有setEGLWindowSurfaceFactory借助此方法，
            // 我们可以将图像渲染到其他的地方，
            // 比如我们创建一个如下的自定义GLSurfaceView，
            // 就可以将图像渲染到外部指定surface上
            setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory() {
                //创建窗口表面
                @Override
                public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig
                        config, Object window) {

                    //EGL创建窗口表面
                    /*
                    * 在Android上，EGL完善了OpenGL ES。
                    * 利用类似eglCreateWindowSurface的EGL函数可以创建surface 用来render ，
                    * 有了这个surface你就能往这个surface中利用OpenGL ES函数去画图了。
                    * */
                    //这里的surface由外部传入，可以为Surface、SurfaceTexture或者SurfaceHolder
                    return egl.eglCreateWindowSurface(display
                            , config
                            , surface//窗口
                            , null);
                }

                //EGL窗口表面被销毁
                @Override
                public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                    egl.eglDestroySurface(display, surface);
                }
            });
            //设置版本号
            setEGLContextClientVersion(2);
            //设置渲染器
            setRenderer(TextureController.this);
            //设置渲染模式
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            //设置暂停的时候是否保持
            setPreserveEGLContextOnPause(true);
            /*
            * 另外，GLSurfaceView的GL环境是受View的状态影响的，
            * 比如View的可见与否，创建和销毁，等等。
            * 我们需要尽可能的让GL环境变得可控。
            * 接下来我们就需要重写几个方法
            * 来保证我们的相机工作效率，同步生命周期，实现GL环境可控制
            * 因此,GLSurfaceView有两个方法一定要暴露出来
            * attachedToWindow()和detachedFromWindow()
            * 但是 GLSurfaceView的onAttachedToWindow和onDetachedFromWindow是需要保证它有parent的。
            * 所以，在这里必须给GLSurfaceView一个父布局。
            * 这个父布局我就就写在 渲染器init（）里面
            * 来避免GLView的attachToWindow和detachFromWindow崩溃
            *
            * */
        }

        //从窗口分离
        public void detachedFromWindow() {
            super.onDetachedFromWindow();
        }

        //释放
        public void clear() {
            try {
                //最终确定
                finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    //===============================================================================
//===============================================================================
//===============================================================================
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mEffectFilter.create();
        mGroupFilter.create();
        mShowFilter.create();
        if (!isParamSet.get()) {
            if (mRenderer != null) {
                mRenderer.onSurfaceCreated(gl, config);
            }
            sdkParamSet();
        }
        calculateCallbackOM();
        mEffectFilter.setFlag(mDirectionFlag);

        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1, mExportFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, mExportTexture, 0, GLES20.GL_RGBA, mDataSize.x,
                mDataSize.y);
    }


    public void surfaceCreated(Object nativeWindow) {
        this.surface = nativeWindow;
        mGLView.surfaceCreated(null);
    }

    public void surfaceChanged(int width, int height) {
        this.mWindowSize.x = width;
        this.mWindowSize.y = height;
        mGLView.surfaceChanged(null, 0, width, height);
    }

    public void surfaceDestroyed() {
        mGLView.surfaceDestroyed(null);
    }


    //在Surface创建前，应该被调用
    public void setDataSize(int width, int height) {
        mDataSize.x = width;
        mDataSize.y = height;
    }

    public SurfaceTexture getTexture() {
        return mEffectFilter.getTexture();
    }


    public void setRenderer(Renderer renderer) {
        mRenderer = renderer;
    }


    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        MatrixUtils.getMatrix(SM, mShowType,
                mDataSize.x, mDataSize.y, width, height);
        mShowFilter.setSize(width, height);
        mShowFilter.setMatrix(SM);
        mGroupFilter.setSize(mDataSize.x, mDataSize.y);
        mEffectFilter.setSize(mDataSize.x, mDataSize.y);
        mShowFilter.setSize(mDataSize.x, mDataSize.y);
        if (mRenderer != null) {
            mRenderer.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        /*
        * 将我们这个FilterGroup加入到已有的流程中，
        * 只需要将保持的相机数据的Texture作为FilterGroup的输入，
        * 然后将FilterGroup的输出作为渲染到指定窗口的Filter的输入即可
        * */
        if (isParamSet.get()) {
            mEffectFilter.draw();
            mGroupFilter.setTextureId(mEffectFilter.getOutputTexture());
            mGroupFilter.draw();

            //显示传入的texture上，一般是显示在屏幕上
            GLES20.glViewport(0, 0, mWindowSize.x, mWindowSize.y);
            mShowFilter.setMatrix(SM);
            mShowFilter.setTextureId(mGroupFilter.getOutputTexture());
            mShowFilter.draw();
            if (mRenderer != null) {
                mRenderer.onDrawFrame(gl);
            }
            callbackIfNeeded();
        }
    }

    /**
     * 增加滤镜
     *
     * @param filter 滤镜
     */
    public void addFilter(AFilter filter) {
        mGroupFilter.addFilter(filter);
    }


    public void takePhoto() {
        isShoot = true;
    }

    public void setFrameCallback(int width, int height, FrameCallback frameCallback) {
        this.frameCallbackWidth = width;
        this.frameCallbackHeight = height;
        if (frameCallbackWidth > 0 && frameCallbackHeight > 0) {
            if (outPutBuffer != null) {
                outPutBuffer = new ByteBuffer[3];
            }
            calculateCallbackOM();
            this.mFrameCallback = frameCallback;
        } else {
            this.mFrameCallback = null;
        }
    }

    private void calculateCallbackOM() {
        if (frameCallbackHeight > 0 && frameCallbackWidth > 0 && mDataSize.x > 0 && mDataSize.y > 0) {
            //计算输出的变换矩阵
            MatrixUtils.getMatrix(callbackOM, MatrixUtils.TYPE_CENTERCROP, mDataSize.x, mDataSize.y,
                    frameCallbackWidth,
                    frameCallbackHeight);
            MatrixUtils.flip(callbackOM, false, true);
        }
    }


    private void sdkParamSet() {
        if (!isParamSet.get() && mDataSize.x > 0 && mDataSize.y > 0) {
            isParamSet.set(true);
        }
    }

    //需要回调，则缩放图片到指定大小，读取数据并回调
    private void callbackIfNeeded() {
        if (mFrameCallback != null && (isRecord || isShoot)) {
            indexOutput = indexOutput++ >= 2 ? 0 : indexOutput;
            if (outPutBuffer[indexOutput] == null) {
                outPutBuffer[indexOutput] = ByteBuffer.allocate(frameCallbackWidth *
                        frameCallbackHeight * 4);
            }
            GLES20.glViewport(0, 0, frameCallbackWidth, frameCallbackHeight);
            EasyGlUtils.bindFrameTexture(mExportFrame[0], mExportTexture[0]);
            mShowFilter.setMatrix(callbackOM);
            mShowFilter.draw();
            frameCallback();
            isShoot = false;
            EasyGlUtils.unBindFrameBuffer();
            mShowFilter.setMatrix(SM);
        }
    }

    //读取数据并回调
    private void frameCallback() {
        GLES20.glReadPixels(0, 0, frameCallbackWidth, frameCallbackHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, outPutBuffer[indexOutput]);
        mFrameCallback.onFrame(outPutBuffer[indexOutput].array(), 0);
    }


    public void destroy() {
        if (mRenderer != null) {
            mRenderer.onDestroy();
        }
        mGLView.surfaceDestroyed(null);
        mGLView.detachedFromWindow();
        mGLView.clear();
    }

    public void requestRender() {
        mGLView.requestRender();
    }

    public void onPause() {
        mGLView.onPause();
    }

    public void onResume() {
        mGLView.onResume();
    }


}
