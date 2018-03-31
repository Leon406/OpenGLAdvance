package com.tz.rock.opengl_vip_beauty.activity;


import android.Manifest;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;


import com.tz.rock.opengl_vip_beauty.R;
import com.tz.rock.opengl_vip_beauty.interfaces.FrameCallback;
import com.tz.rock.opengl_vip_beauty.interfaces.Renderer;
import com.tz.rock.opengl_vip_beauty.render.TextureController;
import com.tz.rock.opengl_vip_beauty.utils.PermissionUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;

public class Camera2Activity extends AppCompatActivity {
    private SurfaceView mSurfaceView;
    private TextureController mController;
    private Renderer mRenderer;
    private int cameraId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.1权限获取（开启线程）
        PermissionUtils.askPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 10, initViewRunnable);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //1.2判断权限申请是否成功
        PermissionUtils.onRequestPermissionsResult(requestCode == 10, grantResults, initViewRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Camera2Activity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
    private Runnable initViewRunnable = new Runnable() {
        @Override
        public void run() {
            //TODO 设置数据源
            //1.2判断版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mRenderer = new Camera2Renderer();
            }
//            else {5.o以下版本
//                mRenderer = new Camera1Renderer();
//            }
            //1.3设置内容视图
            setContentView();
            //1.4创建SurfaceView
            mSurfaceView = (SurfaceView) findViewById(R.id.mSurface);
            //第二步：创建渲染器(1.5)
            mController = new TextureController(Camera2Activity.this);
            //1.6添加渲染效果的方法
            onFilterSet(mController);
            //1.7设置帧回调
            mController.setFrameCallback(720, 1280, new FrameCallback() {

                @Override
                public void onFrame(final byte[] bytes, long time) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //1.7.1创建新位图
                            Bitmap bitmap = Bitmap.createBitmap(720, 1280,//图片大小
                                    Bitmap.Config.ARGB_8888);//色彩的存储方法
                            //1.7.2将数据存放在byte数组中
                            ByteBuffer b = ByteBuffer.wrap(bytes);
                            //1.7.3从缓存器中拷贝像素值，从当前索引开始，覆盖位图中对应的像素值。
                            // 在缓存器中的数据不会被改变(不像setPixels()，会把32位去预存像素转换为该位图的格式)。
                            bitmap.copyPixelsFromBuffer(b);
                            //1.7.4图片保存
                            saveBitmap(bitmap);
                            //1.7.5释放资源
                            bitmap.recycle();
                        }
                    }).start();

                }
            });
            // 1.8为SurfaceView添加状态监听
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override//创建
                public void surfaceCreated(SurfaceHolder holder) {
                    mController.surfaceCreated(holder);
                    mController.setRenderer(mRenderer);
                }

                @Override//改变
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mController.surfaceChanged(width, height);
                }

                @Override//销毁
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mController.surfaceDestroyed();
                }
            });

        }
    };



    protected void setContentView() {
        setContentView(R.layout.activity_camera2);
    }

    //添加滤镜效果的方法
    protected void onFilterSet(TextureController controller) {

//        controller.addFilter(mAniFilter);
    }

    //拍照按钮监听
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mShutter:
                mController.takePhoto();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mController != null) {
            mController.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mController != null) {
            mController.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.destroy();
        }
    }


    //图片保存
    public void saveBitmap(Bitmap b) {
        //设置路径（SD卡位置+"/OpenGL_VIP/photo/"）
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OpenGL_VIP/photo/";
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Camera2Activity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Camera2Activity.this, "保存成功->" + jpegName, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private class Camera2Renderer implements Renderer {


        private Handler mHandler;
        private Size mPreviewSize;
        CameraManager mCameraManager;

        Camera2Renderer() {
            mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            //创建新线程
            HandlerThread mThread = new HandlerThread("camera2 ");
            //开启线程
            mThread.start();
            //mThread.getLooper()返回的就是我们在run方法中创建的mLooper。
            //那么Handler的构造呢，其实就是在Handler中持有一个指向该Looper.mQueue对象，
            // 当handler调用sendMessage方法时，其实就是往该mQueue中去插入一个message，
            // 然后Looper.loop()就会取出执行。
            mHandler = new Handler(mThread.getLooper());
        }


        CameraDevice mDevice;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            try {
                //关闭相机,清除CameraDevice
                if (mDevice != null) {
                    mDevice.close();
                    mDevice = null;
                }
                //得到相机的特性
                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
                //获取摄像头支持的配置属性
                StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //得到的输出尺寸
                Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
                //自定义规则，选个大小
                mPreviewSize = sizes[0];
                //设置大小
                mController.setDataSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                //打开相机
                mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {
                        //CameraDevice是连接在安卓设备上的单个相机的抽象表示,
                        // CameraDevice支持在高帧率下对捕获的图像进行细粒度控制和后期处理.
                        mDevice = camera;
                        try {

                            Surface surface = new Surface(mController.getTexture());
                            //// 创建作为拍照的CaptureRequest.Builder
                            final CaptureRequest.Builder builder = mDevice.createCaptureRequest
                                    (TEMPLATE_PREVIEW);
                            // surface作为CaptureRequest.Builder的目标
                            builder.addTarget(surface);

                            mController.getTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                            // 创建相机捕获会话，
                            // 第一个参数是捕获数据的输出Surface列表，
                            // 第二个参数是CameraCaptureSession的状态回调接口，
                            // 当它创建好后会回调onConfigured方法，
                            // 第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
                            mDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                                /*
                                * 当照相机设备完成配置时调用此方法， 可以开始处理捕获请求。
                                * */
                                @Override
                                public void onConfigured(CameraCaptureSession session) {
                                    try {
                                                /*CameraCaptureSession（）
                                                *当程序需要预览拍照时，都需要先通过该类的实例创建Session。
                                                * 而且不管预览还是拍照，也都是由该对象的方法进行控制的，
                                                * 其中控制预览的方法为setRepeatingRequest（）；控制拍照的方法为capture（）。
                                                * 为了监听CameraCaptureSession的创建过程，
                                                * 以及监听CameraCaptureSession的拍照过程，
                                                * Camera2 API为其提供了StateCallback，CaptureCallback等内部类
                                                * */
                                        //控制预览的方法
                                        session.setRepeatingRequest(builder.build(),//捕获的请求
                                                new CameraCaptureSession.CaptureCallback() {
                                                    @Override//捕获中（可以看到进展）
                                                    public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                                                        super.onCaptureProgressed(session, request, partialResult);
                                                    }

                                                    @Override//捕获完成
                                                    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                        super.onCaptureCompleted(session, request, result);
                                                        //刷新渲染
                                                        mController.requestRender();
                                                    }
                                                }, mHandler);
                                    } catch (CameraAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override//(配置失败)如果会话不能按请求配置，则调用此方法。
                                public void onConfigureFailed(CameraCaptureSession session) {

                                }
                            }, mHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override//相机断开连接会被调用
                    public void onDisconnected(CameraDevice camera) {
                        mDevice = null;
                    }

                    @Override//相机出错的时候调用
                    public void onError(CameraDevice camera, int error) {

                    }
                }, mHandler);
            } catch (SecurityException | CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }

        @Override
        public void onDestroy() {
            if (mDevice != null) {
                mDevice.close();
                mDevice = null;
            }
        }
    }

}
//    6.0以下版本
//    private class Camera1Renderer implements Renderer {
//
//        private Camera mCamera;
//
//        @Override
//        public void onDestroy() {
//            if (mCamera != null) {
//                mCamera.stopPreview();
//                mCamera.release();
//                mCamera = null;
//            }
//        }
//
//        @Override
//        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//            if (mCamera != null) {
//                mCamera.stopPreview();
//                mCamera.release();
//                mCamera = null;
//            }
//            mCamera = Camera.open(cameraId);
//            mController.setImageDirection(cameraId);
//            Camera.Size size = mCamera.getParameters().getPreviewSize();
//            mController.setDataSize(size.height, size.width);
//            try {
//                mCamera.setPreviewTexture(mController.getTexture());
//                mController.getTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//                    @Override
//                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                        mController.requestRender();
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mCamera.startPreview();
//        }
//
//        @Override
//        public void onSurfaceChanged(GL10 gl, int width, int height) {
//
//        }
//
//        @Override
//        public void onDrawFrame(GL10 gl) {
//
//        }
//
//    }