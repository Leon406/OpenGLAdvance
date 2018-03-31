package ll.leon.com.opengladvance.ziptexture;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import ll.leon.com.opengladvance.ziptexture.render.FGLRender;


public class FGLView extends GLSurfaceView {

    private FGLRender render;

    public FGLView(Context context) {
        this(context, null);
    }

    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //第二步初始化
        init();
    }

    //第二步初始化
    private void init() {
        setEGLContextClientVersion(2);//版本号

        setZOrderOnTop(true);//遮挡其他控件的项目背景：
        getHolder().setFormat(PixelFormat.TRANSLUCENT);//半透明
        //Android设备往往支持多种EGL配置，可以使用不同数目的通道(channel)，
        // 在这里可以指定每个通道具有不同数目的位(bits)深度。
        // 注意：要在渲染器工作之前就应该指定EGL的配置。
        // GLSurfaceView默认EGL配置的像素格式为RGB_656，16位的深度缓存(depth buffer)，
        // 默认不开启遮罩缓存(stencil buffer)。
        //如果你要选择不同的EGL配置，请使用setEGLConfigChooser方法中的一种。
        setEGLConfigChooser(8, 8, 8, 8//颜色缓存为RGBA，位数都为8
                , 16,//depth缓存位数为16
                0);//stencil缓存位数为0
        render = new FGLRender(this);
        setRenderer(render);
        setRenderMode(RENDERMODE_WHEN_DIRTY);//渲染模式
    }

    public void start() {
        render.start();
    }

    public void startInfinite() {
        render.infinitePlay = true;
        render.start();
    }

    public void stop() {
        render.infinitePlay = false;
        render.stop();
    }

    public boolean isPlay() {
        return render.isPlay;
    }

    public void setStateChangeListener(StateChangeListener listener) {
        render.setStateChangeListener(listener);
    }
}
