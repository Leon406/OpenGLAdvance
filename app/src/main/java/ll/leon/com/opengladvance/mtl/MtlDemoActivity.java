package ll.leon.com.opengladvance.mtl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MtlDemoActivity extends AppCompatActivity {
    private FGLView mGLView;

    public static void start(Context context) {
        Intent starter = new Intent(context, MtlDemoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new FGLView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

}
