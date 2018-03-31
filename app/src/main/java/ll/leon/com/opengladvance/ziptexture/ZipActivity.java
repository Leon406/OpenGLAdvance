package ll.leon.com.opengladvance.ziptexture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import ll.leon.com.opengladvance.BuildConfig;
import ll.leon.com.opengladvance.R;

public class ZipActivity extends AppCompatActivity {

    private FGLView mAniView;

    public static void start(Context context) {
        Intent starter = new Intent(context, ZipActivity.class);
        //starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);
        mAniView = (FGLView) findViewById(R.id.mAni);
        mAniView.start();
        mAniView.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onStateChanged(int lastState, int nowState) {
                if (nowState == STOP) {
                    if (BuildConfig.DEBUG) Log.d("ZipActivity", "stopped");
                    runOnUiThread(() -> mAniView.setVisibility(View.GONE));
//                    if (!mAniView.isPlay()) {
//                        mAniView.start();
//                    }
                }
            }
        });

    }

    public void present(View view) {
        mAniView.start();
        mAniView.setVisibility(View.VISIBLE);
    }
}
