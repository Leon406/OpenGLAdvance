package ll.leon.com.opengladvance.mix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import javax.microedition.khronos.opengles.GL10;

import ll.leon.com.opengladvance.R;

import static javax.microedition.khronos.opengles.GL10.GL_DST_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_DST_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA_SATURATE;

public class MixActivity extends AppCompatActivity {

    private int selectdDst;

    public static void start(Context context) {
        Intent starter = new Intent(context, MixActivity.class);
        //   starter.putExtra();
        context.startActivity(starter);
    }

    MySurfaceView mGLSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mGLSurfaceView = new MySurfaceView(this);
//        mGLSurfaceView.requestFocus();//获取焦点
//        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控

//        setContentView(mGLSurfaceView);
        setContentView(R.layout.activity_mix);
        mGLSurfaceView=  findViewById(R.id.gv);
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

    int selectdSrc;
    String[] dstArray = new String[]{"1","0","src color",
            "1 - src","src alpha","1-src alpha",
            "dst alpha","1-dst alpha"
    };
    int[] dstA = new int[]{GL10.GL_ONE, GL10.GL_ZERO, GL10.GL_SRC_COLOR,
            GL10.GL_ONE_MINUS_SRC_COLOR, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_DST_ALPHA, GL_ONE_MINUS_DST_ALPHA};


    public void dst(final View view) {

        new AlertDialog.Builder(this)

                .setSingleChoiceItems(dstArray, selectdDst, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectdDst = which;
                        mGLSurfaceView.setDst(dstA[which]);
                        ((TextView) view).setText(dstArray[which]);
                    }
                }).show();

    }

    String[] srcArray = new String[]{"1","0","dst color",
            "1 - dst","src alpha","1-dst alpha",
            "dst alpha","1-dst alpha","src alpha saturation"

    };
    int[] srcA = new int[]{GL10.GL_ONE, GL10.GL_ZERO, GL10.GL_DST_COLOR,
            GL10.GL_ONE_MINUS_DST_COLOR, GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA,
            GL_DST_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_SRC_ALPHA_SATURATE};

    public void src(final View v) {
        new AlertDialog.Builder(this)

                .setSingleChoiceItems(srcArray, selectdSrc, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectdSrc = which;
                        mGLSurfaceView.setSrc(srcA[which]);
                        ((TextView) v).setText(srcArray[which]);
                    }
                }).show();
    }
}