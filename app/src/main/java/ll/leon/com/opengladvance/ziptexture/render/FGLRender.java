package ll.leon.com.opengladvance.ziptexture.render;

import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ll.leon.com.opengladvance.ziptexture.AFilter;
import ll.leon.com.opengladvance.ziptexture.StateChangeListener;

/**
 * 创建时间：2018/01/11
 *
 * @author Leon
 */
//第三步：渲染器
public class FGLRender extends AFilter implements GLSurfaceView.Renderer {

    public static final String TAG = "DebugTAG";
    private int mGlHAlpha;
    private ZipInputStream mZipStream;
    private ZipEntry mZipEntry;
    private ByteBuffer headerBuffer;
    private GLSurfaceView view;
    public boolean infinitePlay;


    public int textureType = 0;      //默认使用Texture2D0

    //3.0.01初始化
    public FGLRender(GLSurfaceView view) {
        super(view.getResources());
        this.view = view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ///3.0.02设置背景色
        GLES20.glClearColor(0f, 0f, 0f, 0.0f);
        ///3.0.03获取并加载着色器 然后创建创建GL程序 的 方法
        createProgramByAssetsFile("shader/pkm_mul.vert", "shader/pkm_mul.frag");
        //3.1创建纹理
        createEtcTexture();
    }

    public int width;
    public int height;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        //3.2.1开启混合
        GLES20.glEnable(GLES20.GL_BLEND);
        //3.2.2设置混合因子
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

    }

    long time = 0;
    //多长时间走一步
    private int timeStep = 50;
    public boolean isPlay = false;

    @Override
    public void onDrawFrame(GL10 gl) {
        //3.2.3清除颜色和和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //3.2.4使用着色程序
        GLES20.glUseProgram(mProgram);
        //3.3.0绑定纹理
        onBindTexture();
        time = System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        long s = System.currentTimeMillis() - startTime;
        /**
         * 启用顶点坐标和纹理坐标进行绘制
         */
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
        //开启一个定时器，定时requestRender，加载下一帧压缩纹理。
        if (isPlay) {//public boolean isPlay = false;
            if (s < timeStep) {
                try {
                    Thread.sleep(timeStep - s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            view.requestRender();
        } else {
            //接口回调
            changeState(StateChangeListener.PLAYING, StateChangeListener.STOP);
        }
    }

    //===================================================
    private int type = TYPE_CENTERINSIDE;
    private float[] SM = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    protected void onBindTexture() {
        //3.3.1封装压缩的ETC1纹理的实用程序类。
        ETC1Util.ETC1Texture t = getNextTexture();
        ETC1Util.ETC1Texture tAlpha = getNextTexture();
        if (t != null && tAlpha != null) {
            getMatrix(SM, type, t.getWidth(), t.getHeight(), width, height);
            matrix = SM;
            /**
             * 设置其他扩展数据
             */
            GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);
            //激活纹理单元
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

            //加载ETC1纹理
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D
                    , 0//纹理层次
                    , 0//边框大小。通常为0.
                    , GLES20.GL_RGB//格式使用ETC1纹理压缩，如果不支持。必须gl_rgb。
                    //要使用的类型如果ETC1纹理压缩不支持。
                    // 可以gl_unsigned_short_5_6_5，
                    // 这意味着每个像素的16位或gl_unsigned_byte，这意味着每像素24位。
                    , GLES20.GL_UNSIGNED_SHORT_5_6_5
                    , t);//ETC1Util.ETC1Texture
            GLES20.glUniform1i(mHTexture, textureType);
            //=============
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + textureType);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB, GLES20
                    .GL_UNSIGNED_SHORT_5_6_5, tAlpha);
            mGlHAlpha = GLES20.glGetUniformLocation(mProgram, "vTextureAlpha");
            GLES20.glUniform1i(mGlHAlpha, 1 + textureType);
        } else {
            if (infinitePlay) {
                start();
            } else {
                isPlay = false;
            }
        }
    }


    //================================
    public ETC1Util.ETC1Texture getNextTexture() {
        //获取封装压缩的ETC1纹理
        //3.3.2判断是否有压缩实体
        if (hasElements()) {
            try {
                //3.3.3创建纹理
                ETC1Util.ETC1Texture e = createTexture(mZipStream);
                return e;
            } catch (IOException e1) {
                Log.e(TAG, "err->" + e1.getMessage());
                e1.printStackTrace();
            }
        }
        return null;
    }

    ////3.3.2判断是压缩实体
    private boolean hasElements() {
        try {
            if (mZipStream != null) {
                //如果不空，就得到一个压缩实体
                mZipEntry = mZipStream.getNextEntry();
                if (mZipEntry != null) {
                    //如果压缩实体不为空就返回 true
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从包含PKM格式压缩纹理的输入流创建一个新的ETC1Texture。
     *
     * @param输入包含PKM格式压缩纹理的输入流。
     * @返回从输入流中读取的ETC2Texture。
     * @抛出IOException
     */
    private ETC1Util.ETC1Texture createTexture(InputStream input) throws IOException {

        int width = 0;
        int height = 0;
        //3.3.4创建字节数组
        byte[] ioBuffer = new byte[4096];
        {
            //3.3.5如果读取的ETC1的头大小不一样，就无法读取pkm文件
            if (input.read(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE) != ETC1.ETC_PKM_HEADER_SIZE) {
                throw new IOException("Unable to read PKM file header.");
            }
            //3.3.6判断字节缓冲是不是空的，如果是空的，就创建，并设置字节顺序
            if (headerBuffer == null) {
                headerBuffer = ByteBuffer.allocateDirect(ETC1.ETC_PKM_HEADER_SIZE)
                        .order(ByteOrder.nativeOrder());
            }
            //3.3.7把读取的内容添加到缓冲中，从0开始读取
            headerBuffer.put(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE).position(0);
            //3.3.8检查PKM头是否正确格式化
            if (!ETC1.isValid(headerBuffer)) {
                throw new IOException("Not a PKM file.");
            }
            //3.3.9从PKM头部读取图像宽度和高度。
            width = ETC1.getWidth(headerBuffer);
            height = ETC1.getHeight(headerBuffer);
        }
        //3.4.0得到编码图像数据的大小。
        int encodedSize = ETC1.getEncodedDataSize(width, height);
        //3.4.1把得到的数据加载到字节缓冲中
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(ByteOrder.nativeOrder());
        int len;
        while ((len = input.read(ioBuffer)) != -1) {
            dataBuffer.put(ioBuffer, 0, len);
        }
        dataBuffer.position(0);
        //3.4.2返回压缩的ETC1纹理
        return new ETC1Util.ETC1Texture(width, height, dataBuffer);
    }
//==================================================
//==================================================

    private String path = "assets/zip/cc.zip";

    public boolean open() {
        Log.e(TAG, path + " open");
        if (path == null) return false;
        try {
            if (path.startsWith("assets/")) {
                InputStream s = view.getResources().getAssets().open(path.substring(7));
                mZipStream = new ZipInputStream(s);
            } else {
                File f = new File(path);
                Log.e(TAG, path + " is File exists->" + f.exists());
                mZipStream = new ZipInputStream(new FileInputStream(path));
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "eee-->" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //==========================================================================
    public void start() {
        if (!isPlay) {
            isPlay = true;
            changeState(StateChangeListener.STOP, StateChangeListener.START);
            open();
            view.requestRender();
        }
    }

    public void stop() {
        if (isPlay) {
            isPlay = false;
            changeState(StateChangeListener.START, StateChangeListener.STOP);
            view.requestRender();
        }
    }


    private StateChangeListener mStateChangeListener;

    public void setStateChangeListener(StateChangeListener listener) {
        this.mStateChangeListener = listener;
    }

    private void changeState(int lastState, int nowState) {
        if (this.mStateChangeListener != null) {
            this.mStateChangeListener.onStateChanged(lastState, nowState);
        }
    }

}
