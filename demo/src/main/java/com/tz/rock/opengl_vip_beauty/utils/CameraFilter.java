package com.tz.rock.opengl_vip_beauty.utils;

import android.content.res.Resources;
import android.hardware.Camera;

//照相机滤色镜
public class CameraFilter extends OesFilter {

    public CameraFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void initBuffer() {
        super.initBuffer();
//        movie();
    }

    //切换镜头
    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        /*
        * 根据镜头使用不同的使用不同的纹理坐标
        * （纹理坐标是一样的）
        * */
        if (getFlag() == Camera.CameraInfo.CAMERA_FACING_FRONT) {    //前置摄像头
            cameraFront();
        } else if (getFlag() == Camera.CameraInfo.CAMERA_FACING_BACK) {   //后置摄像头
            cameraBack();
        }
    }

    private void cameraFront() {
        float[] coord = new float[]{
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
        };
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

    private void cameraBack() {
        float[] coord = new float[]{
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
        };
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }
    //
//    private void movie() {
//        float[] coord = new float[]{
//                0.0f, 0.0f,
//                0.0f, 1.0f,
//                1.0f, 0.0f,
//                1.0f, 1.0f,
//        };
//        mTexBuffer.clear();
//        mTexBuffer.put(coord);
//        mTexBuffer.position(0);
//    }
}
