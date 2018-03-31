package ll.leon.com.opengladvance.mix3d;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;

import ll.leon.com.opengladvance.R;

public class Mix3dActivity extends AppCompatActivity {


    private Switch sw;
    private GLView gv;

   public static void start(Context context) {
       Intent starter = new Intent(context, Mix3dActivity.class);
       context.startActivity(starter);
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix3d);
        gv=findViewById(R.id.gv);

    }



    @Override
    protected void onStop() {
        super.onStop();
        gv.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gv.onResume();
    }


}
