package ll.leon.com.opengladvance.wave;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ll.leon.com.opengladvance.R;

public class WaveActivity extends AppCompatActivity {

    private WaveGLView mGLSurfaceView;
    private long i;

    public static void start(Context context) {
        Intent starter = new Intent(context, WaveActivity.class);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_effect2);
        mGLSurfaceView =findViewById(R.id.egv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    public void chooseDirection(View view) {
        mGLSurfaceView.texRect.currIndex = (int) (++i %3);
    }
}
