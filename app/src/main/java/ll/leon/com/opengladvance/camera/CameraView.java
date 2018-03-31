package ll.leon.com.opengladvance.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ll.leon.com.opengladvance.BuildConfig;

/**
 * Description:
 * 第二步：继承GLSurfaceView实现 Renderer, QCamera 接口，并初始化GLSurfaceView
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer, QCamera {
    private int cameraId = 0;//镜头id（0后置，1前置）

    // 2.1初始化GLSurfaceView
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 2.1初始化GLSurfaceView
        init();
    }

    private Config mConfig;

    private void init() {
        //设置版本号
        setEGLContextClientVersion(2);
        //设置渲染器
        setRenderer(this);
        //设置渲染模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        //创建预览尺寸对象，并设置预览分辨率和宽高比
        this.mConfig = new Config();
        mConfig.minPreviewWidth = 720;
        mConfig.minPictureWidth = 720;
        mConfig.rate = 1.778f;

    }


    private SurfaceTexture surfaceTexture;
    private Runnable mRunnable;

    //第三步：设置初始化方法
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //3.1初始化工具类（顶点数据，顶点数据处理，着色器加载，着色器程序加载）
        Utils.create(getContext().getResources());
        //3.2创建纹理
        Utils.textureId = Utils.createTextureID();
        //3.3建立一个新的纹理图像流(参数：纹理对象的名字)
        surfaceTexture = new SurfaceTexture(Utils.textureId);

        //3.4切换摄像头
        if (mRunnable != null) {
            //开启切换摄像头的线程
            mRunnable.run();
            mRunnable = null;
        }
        //第四步 ：开启摄像头的方法
        open(cameraId);

        //设置预览纹理
        setPreviewTexture(surfaceTexture);
        //设置视频帧监听器
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                //这样SurfaceTexture在拿到新的『流』数据时会通知 GLSurfaceView 刷新
                requestRender();
            }
        });
        //开始预览
        preview();
    }

    //切换摄像头
    public void switchCamera() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                close();//停止预览,释放Camera
                //三元运算符(条件表达式？表达式1：表达式2。为true时调用表达式1，为false时调用表达式2)
                cameraId = cameraId == 1 ? 0 : 1;
            }
        };
        //停止渲染
        onPause();
        //恢复渲染线程
        onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        close();//停止预览,释放Camera
    }

    private Camera mCamera;
    private Camera.Size pre, pic;

    @Override
    public boolean open(int cameraId) {
        //4.1获取Camera实例(开机摄像头硬件)
        /*API在SDK 2.3之前，是没有参数的，
            2.3以后支持多摄像头，
            所以开启前可以通过getNumberOfCameras先获取摄像头数目，
            再通过getCameraInfo得到需要开启的摄像头id，
            然后传入Open函数开启摄像头，
            假如摄像头开启成功则返回一个Camera对象，否则就抛出异常；
             我们这里就直接写了*/
        mCamera = Camera.open(cameraId);

        if (mCamera != null) {

            /**4.2
             * Camera对象中含有一个内部类Camera.Parameters.该类可以对Camera的特性进行定制
             * 在Parameters中设置完成后，需要调用Camera.setParameters()方法，相应的设置才会生效
             */

            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (BuildConfig.DEBUG) Log.d("CameraView", "autofocus success:" + success);
                }
            });
            Camera.Parameters param = mCamera.getParameters();

            //4.3设置预览分辨率和图片分辨率
            Camera.Size preSize = getPropPreviewSize
                    (param.getSupportedPreviewSizes(), // 选择合适的预览尺寸
                            mConfig.rate, //宽高比
                            mConfig.minPreviewWidth);//预览宽
            //(获得摄像区域的大小)预览分辨率
            param.setPreviewSize(preSize.width, preSize.height);
            param.setPreviewFrameRate(30);//每秒30帧  每秒从摄像头里面获得30个画面(最少25帧)
            param.setPictureFormat(PixelFormat.JPEG);//设置图片的格式为JPEG
            param.set("jpeg-quality", 85);//设置图片的质量为80，最大值为100
            param.setPictureSize(preSize.width, preSize.height);//(设置拍出来的屏幕大小)图片分辨率
            mCamera.setParameters(param);//赋给摄像头  设置完后一定要记得：(不设置：分辨率设置了完全没用，拍照时又变回去了)
            //4.4返回 预览图片的宽度和高度
            pre = param.getPreviewSize();
            //4.5返回 图片高度和宽度
            pic = param.getPictureSize();

            return true;
        }
        return false;
    }

    @Override //(获得摄像区域的大小)预览分辨率
    public Point getPreviewSize() {
        return new Point(pre.height, pre.width);
    }

    @Override //设置预览纹理
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //开始预览
    @Override
    public boolean preview() {
        if (mCamera != null) {
            //开始预览
            mCamera.startPreview();
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //总矩阵
    private float[] matrix = new float[16];
    ///第五步
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视口
        GLES20.glViewport(0, 0, width, height);
        //(获得摄像区域的大小)预览分辨率
        Point point = getPreviewSize();
        //得到
        Utils.getShowMatrix(matrix, point.x, point.y, width, height);
        if (cameraId == 1) {
            Utils.flip(matrix, true, false);
            Utils.rotate(matrix, 90);
        } else {
            Utils.rotate(matrix, 270);
        }
        Utils.setMatrix(matrix);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            //将纹理图像更新到图像流的最新帧。
            surfaceTexture.updateTexImage();
        }
        Utils.draw();
    }


    @Override
    public void setConfig(Config config) {
        this.mConfig = config;
    }


    @Override
    public boolean switchTo(int cameraId) {
        close();//停止预览,释放Camera
        open(cameraId);
        return false;
    }

    //拍照
    @Override
    public void takePhoto(TakePhotoCallback callback) {

    }

    //停止预览,释放Camera
    @Override
    public boolean close() {
        if (mCamera != null) {
            try {
                //停止预览
                mCamera.stopPreview();
                //释放Camera
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    //图片分辨率
    @Override
    public Point getPictureSize() {
        return new Point(pic.height, pic.width);
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            //接收到每一帧的预览数据
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override //预览数据 是data。
                public void onPreviewFrame(byte[] data, Camera camera) {
                    //得到预览数据
                    callback.onPreviewFrame(data, getPreviewSize().x, getPreviewSize().y);
                }
            });
        }
    }

    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        int i = 0;
        for (Camera.Size s : list) {
            //如果预览图片的高大于或等于设置的宽并且
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }

    private boolean equalRate(Camera.Size s, float rate) {
        //获取当前图片的宽高比
        float r = (float) (s.width) / (float) (s.height);
        //abs返回指定数字的绝对
        //如果r在[rate-0.03 , rate+0.03]范围内
        //返回True 否则返回false
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

}
