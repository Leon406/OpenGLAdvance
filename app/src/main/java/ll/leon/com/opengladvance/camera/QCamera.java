package ll.leon.com.opengladvance.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

/**
 * 第一步：
 * 思考：照相机都有哪些基本功能
 */
public interface QCamera {
    /*
 * 1.首先调用Camera的open（）方法打开摄像机...
 *   private Camera camera;
 *   camera=Camera.open(0);
 * 2.调用Camera.setParameters()方法获取摄像机的参数...
 *   Parameters param=camera.setParameters();
 * 3.设置摄像机的拍照参数来配置拍照信息...
 *   param.setPreviewSize(display.getWidth(),display.getHeight());设置预览大小..
 *   param.setPreviewFrameRate(4)..以每秒四帧显示图像信息...
 *   param.setPictureFormat(PixelFormat.JPEG);设置图片的格式...
 *   param.set("jpeg-quality",85);设置图片的质量，最高为100...
 *   parameters.setPictureSize(screenWidth,screenHeight);设置照片的大小...
 * 4.param.setParamters(param);将参数传递给相机，使相机可以指定相应的参数来完成拍摄...
 * 5.使用setPreview(SurfaceView)设置使用哪个SurfaceView来显示要预览的景象...
 *   MainActivity.this.cma.setPreView(SurfaceHolder holder)...防止主线程阻塞..因此另外开启线程...
 *   MainActivity.this.cma.startPreView();开始预览...
 *   MainActivity.this.cma.autoFocus(afcb);
 * 6.进行拍照，然后获取拍到的图片进行保存...
 *   cma.takePicture(sc,pc,jpgcall);获取图片...
 * 7.结束预览释放资源...
 *   cma.stopPreView();
 *   cma.release();释放资源..
 * 8.在AndroidManifest设置权限...
 *   <uses-feature android:name="android.hardware.camera" />
 *   <uses-feature android:name="android.hardware.camera.autofocus"/>
 *   <uses-permission android:name="android.permission.CAMERA"/>
 *   <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
 *   <uses-permission android:name="android.permission.WRITE_EXTENAL_STORAGE"/>
 * */

    //开启相机
    boolean open(int cameraId);

    //摄像头切换
    boolean switchTo(int cameraId);

    //照相
    void takePhoto(TakePhotoCallback callback);

    //开始预览
    boolean preview();

    //设置预览纹理
    void setPreviewTexture(SurfaceTexture texture);

    //(获得摄像区域的大小)预览分辨率
    Point getPreviewSize();

    //得到图片分辨率
    Point getPictureSize();

    //停止预览,释放Camera
    boolean close();
    //预览框回调
    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }
    //设置预览帧回调
    void setOnPreviewFrameCallback(PreviewFrameCallback callback);

    class Config {
        public float rate; //宽高比
        public int minPreviewWidth; //最小预览宽
        public int minPictureWidth;//最小预览高
    }

    //设置配置
    void setConfig(Config config);

    //拍照的回调
    interface TakePhotoCallback {
        void onTakePhoto(byte[] bytes, int width, int height);
    }


}
