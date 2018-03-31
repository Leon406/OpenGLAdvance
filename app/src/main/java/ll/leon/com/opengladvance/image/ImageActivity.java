package ll.leon.com.opengladvance.image;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;

import ll.leon.com.opengladvance.R;


public class ImageActivity extends AppCompatActivity {



    private Switch sw;
    private FrameLayout container;
    private GLView glView;

    public static void start(Context context) {
        Intent starter = new Intent(context, ImageActivity.class);
       // starter.putExtra();
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        sw = ((Switch) findViewById(R.id.sw));
        container = ((FrameLayout) findViewById(R.id.container));

        glView = new GLView(this,  R.drawable.fengj);
        container.addView(glView);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                glView.image.isHalf =isChecked;
//                gv.setTextureId(R.drawable.fengj);

            }
        });
    }

    private int selected;
    public void choose(final View view) {

       final String [] items = new String[] {"原图","灰度","暖色调","冷色调","浮雕","对比度增强","放大镜",
               "马赛克","图像扭曲","图像颠倒","膨胀","腐蚀","普通模糊","高斯模糊"};
        new AlertDialog.Builder(this).setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                glView.image.one =which;
                selected = which;
                dialog.dismiss();
                ((Button) view).setText(items[which]);
            }
        })
           .show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }
}
