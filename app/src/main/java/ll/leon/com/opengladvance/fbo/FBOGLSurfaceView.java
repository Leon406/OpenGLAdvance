package ll.leon.com.opengladvance.fbo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.nio.ByteBuffer;


public class FBOGLSurfaceView extends GLSurfaceView implements FBORender.Callback {

    private FBORender renderer;
    private int mBmpWidth;
    private int mBmpHeight;
    public FBOGLSurfaceView(Context context) {
        this(context,null);
    }

    public FBOGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        setEGLContextClientVersion(2);
        renderer = new FBORender(context.getResources());
        setRenderer(renderer);

        renderer.setCallback(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setBitmap(Bitmap bitmap) {
        mBmpWidth = bitmap.getWidth();
        mBmpHeight = bitmap.getHeight();
        renderer.setBitmap(bitmap);
    }
    @Override
    public void onCall(ByteBuffer data) {

        //ByteBuffer转Bitmap 耗时操作, 建议在子线程执行
        post(new Runnable() {
            @Override
            public void run() {
                Log.e("LeonDebug", "callback success");
                //创建新位图
                Bitmap bitmap = Bitmap.createBitmap(mBmpWidth, mBmpHeight, Bitmap.Config.ARGB_8888);
                //从缓存器中拷贝像素值，从当前索引开始，覆盖位图中对应的像素值
                bitmap.copyPixelsFromBuffer(data);
                if (listener != null) {
                    listener.onFinishRender(bitmap);
                }
                data.clear();
            }
        });

    }

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener{
        void onFinishRender(Bitmap bitmap);
    }
}
