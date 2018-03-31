package com.tz.rock.opengl_vip_beauty.utils;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

//特效处理的Filter
public class TextureFilter extends AFilter {

    private CameraFilter mFilter;
    private int width = 0;
    private int height = 0;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private int[] mCameraTexture = new int[1];

    private SurfaceTexture mSurfaceTexture;
    private float[] mCoordOM = new float[16];

    public TextureFilter(Resources mRes) {
        super(mRes);
        //创建照相机滤色镜
        mFilter = new CameraFilter(mRes);
    }


    public SurfaceTexture getTexture() {
        return mSurfaceTexture;
    }

    @Override
    public void setFlag(int flag) {
        mFilter.setFlag(flag);
    }

    @Override
    protected void initBuffer() {

    }

    @Override
    public void setMatrix(float[] matrix) {
        mFilter.setMatrix(matrix);
    }

    @Override
    public int getOutputTexture() {
        return fTexture[0];
    }

    @Override
    public void draw() {
        boolean a = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if (a) {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mCoordOM);
            mFilter.setCoordMatrix(mCoordOM);
        }
        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        GLES20.glViewport(0, 0, width, height);
        mFilter.setTextureId(mCameraTexture[0]);
        mFilter.draw();
        Log.e("wuwang", "textureFilter draw");
        EasyGlUtils.unBindFrameBuffer();

        if (a) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
    }

    @Override
    public void create() {
        mFilter.create();
        createOesTexture();
        mSurfaceTexture = new SurfaceTexture(mCameraTexture[0]);
    }

    @Override
    public void setSize(int width, int height) {
        mFilter.setSize(width, height);
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            //创建FrameBuffer和Texture
            deleteFrameBuffer();
            GLES20.glGenFramebuffers(1, fFrame, 0);
            EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, width, height);
        }
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }

    private void createOesTexture() {
        GLES20.glGenTextures(1, mCameraTexture, 0);
    }

}
