package ll.leon.com.opengladvance.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import ll.leon.com.opengladvance.utils.MatrixHelper;

public class EarthActivity extends AppCompatActivity {

    private MySurfaceView mGLSurfaceView;

    public static void start(Context context) {
        Intent starter = new Intent(context, EarthActivity.class);
       // starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
               //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //初始化GLSurfaceView
        mGLSurfaceView = new MySurfaceView(this);
        //切换到主界面
        setContentView(mGLSurfaceView);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
        MatrixHelper.threadFlag = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        MatrixHelper.threadFlag = false;
    }
}



