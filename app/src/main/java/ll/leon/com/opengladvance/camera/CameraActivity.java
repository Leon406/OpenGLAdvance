package ll.leon.com.opengladvance.camera;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ll.leon.com.opengladvance.R;


/**
 * Description:
 */
public class CameraActivity extends AppCompatActivity {

    private CameraView mCameraView;
    public static void start(Context context) {
        Intent starter = new Intent(context, CameraActivity.class);
       // starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("") ;
        setContentView(R.layout.activity_camera);
        mCameraView = (CameraView) findViewById(R.id.mCameraView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("切换摄像头").setTitle("切换摄像头").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String name = item.getTitle().toString();
        if (name.equals("切换摄像头")) {
            mCameraView.switchCamera();
        }
        return super.onOptionsItemSelected(item);
    }
}
