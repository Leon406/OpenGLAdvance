package ll.leon.com.opengladvance.fbo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 创建时间：2018/1/12
 *
 * @author Leon
 */
//第二步渲染器
//继承工具类，实现render接口
public class FBORender extends AFilter implements GLSurfaceView.Renderer {
    private Bitmap mBitmap;
    private ByteBuffer mBuffer;

    public FBORender(Resources res) {
        super(res);
    }

    //第三步：创建回调接口，和设置回调方法
    //3.01创建回调接口
    interface Callback {
        void onCall(ByteBuffer data);
    }

    private Callback mCallback;

    //3.02设置回调方法（第四步）
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    //3.03设置图片入口
    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }
//============================================================


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //第五步：获取着色器源码 并创建着色程序
        createProgramByAssetsFile("fbo/base_vertex.glsl",
                "fbo/gray_fragment.frag");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int[] fTexture = new int[2];

    @Override
    public void onDrawFrame(GL10 gl) {
        //第七步获取所需数据并绘制
        //7.0.01判断位图是否为空和被回收
        if (mBitmap != null && !mBitmap.isRecycled()) {
            //7.1.0创建帧缓冲
            createEnvi();
            ////绑定FrameBuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
            //纹理附着到帧缓冲中去
            //为FrameBuffer挂载Texture[1]来存储颜色
            GLES20.glFramebufferTexture2D(
                    // 创建的帧缓冲类型的目标，一般为GL_FRAMEBUFFER
                    GLES20.GL_FRAMEBUFFER
                    // 附着点，这里附着的事一个纹理，需要传入参数为一个颜色附着点
                    // GL_COLOR_ATTACHMENT0---->颜色缓冲
                    // GL_DEPTH_ATTACHMENT----->深度缓冲
                    // GL_STENCIL_ATTACHMENT--->模板缓冲。
                    //附着深度缓冲可以使用GL_DEPTH_ATTACHMENT作为附着类型，此时纹理的内部类型为GL_DEPTH_COMPONENT（32位深），
                    // 附着模板缓冲使用GL_STENCIL_ATTACHMENT附着点，对应文理类型为GL_STENCIL_INDEX。
                    , GLES20.GL_COLOR_ATTACHMENT0
                    // 希望附着的纹理类型
                    , GLES20.GL_TEXTURE_2D
                    // 附加的纹理对象ID
                    , fTexture[1]
                    // 一般设置为0
                    , 0);
            ////为FrameBuffer挂载fRender[0]来存储深度
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,//帧缓冲类型的目标
                    GLES20.GL_DEPTH_ATTACHMENT,// 附着点
                    GLES20.GL_RENDERBUFFER,// 必须为GL_RENDERBUFFER
                    fRender[0]); // 渲染缓冲区对象

            //设置视口
            GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            setTextureId(fTexture[0]);
            //绘制================
            draw();
            //=============
            //从帧缓存里读取一个像素块
            GLES20.glReadPixels(0, 0//定义图像区域左下角点的坐标
                    //图像的高度和宽度
                    , mBitmap.getWidth(), mBitmap.getHeight()
                    //所读象素数据元素的格式
                    , GLES20.GL_RGBA,
                    //数据类型
                    GLES20.GL_UNSIGNED_BYTE,//每个元素的数据类型
                    mBuffer);//图片
            if (mCallback != null) {
                mCallback.onCall(mBuffer);
            }
            deleteEnvi();
            mBitmap.recycle();
        }
    }

    public void createEnvi() {
        //7.1.01创建帧缓冲
        GLES20.glGenFramebuffers(1, fFrame, 0);
        GLES20.glGenRenderbuffers(1, fRender, 0);

        //7.1.02绑定Render Buffer(渲染缓冲区,  帧缓冲区对象)
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
        //7.1.03我们这里设置为深度的Render Buffer，并传入大小
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER
                , GLES20.GL_DEPTH_COMPONENT16,
                mBitmap.getWidth(), mBitmap.getHeight());//渲染缓存图像的像素维度

        //这里有一个关键的地方，也就是我们生成的渲染缓冲对像，它本身并不会自动分配内存空间。
        // 因此我们要调用OpenGL的函数来给它分配指定大小的内存空间，在这里，我们分配一个固定大小的深度缓显空间。
        //7.1.04将创建的渲染缓冲区挂载到帧缓冲区上
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);
        //7.1.05解绑Render Buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        //7.1.05设置纹理
        GLES20.glGenTextures(2, fTexture, 0);
        for (int i = 0; i < 2; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D//纹理被绑定的目标
                    , fTexture[i]);//纹理的名称
            if (i == 0) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D// 操作的目标类型
                        , 0,//执行细节级别,0是最基本的图像级别
                        GLES20.GL_RGBA,//指定纹理中的颜色组件。
                        mBitmap,//图像
                        0);// 边框，一般设为0
            } else {
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D// 操作的目标类型
                        , 0//执行细节级别
                        , GLES20.GL_RGBA
                        //指定纹理图像的宽度和高度，必须是2的n次方。
                        , mBitmap.getWidth(), mBitmap.getHeight()
                        , 0//指定边框的宽度。必须为0。
                        , GLES20.GL_RGBA//像素数据的颜色格式
                        , GLES20.GL_UNSIGNED_BYTE//指定像素数据的数据类型
                        , null);//指定内存中指向图像数据的指针
            }
            //设置纹理过滤函数
            setTextureParam();
        }
        //7.1.06创建图片字节缓冲
        mBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);
    }

    private void deleteEnvi() {
        ////删除纹理
        GLES20.glDeleteTextures(2, fTexture, 0);
        //删除Render Buffer
        GLES20.glDeleteRenderbuffers(1, fRender, 0);
        //删除Frame Buffer
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
    }

}
