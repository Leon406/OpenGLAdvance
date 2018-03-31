package ll.leon.com.opengladvance.utils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * @author Leon
 * @Desc: GLSurfaceView 基类
 */

public abstract class BaseGLView10 extends GLSurfaceView implements GLSurfaceView.Renderer {

    protected final Context context;

    public BaseGLView10(Context context) {
        this(context, null);
    }

    public BaseGLView10(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;
    }

    public void init() {
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // 3D打开深度检测
        gl.glEnable(GL10.GL_DEPTH_TEST);
       // GLES20.glEnable(GLES20.GL_CULL_FACE);
        onCreate(gl);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        onChange(gl ,width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        onFrameDraw(gl);
    }

    protected abstract void onCreate(GL10 gl);

    protected abstract void onChange(GL10 gl, int width, int height);

    protected abstract void onFrameDraw(GL10 gl);

}
