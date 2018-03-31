package ll.leon.com.opengladvance;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


import ll.leon.com.opengladvance.camera.CameraActivity;
import ll.leon.com.opengladvance.demo.EarthActivity;
import ll.leon.com.opengladvance.fbo.FBOActivity;
import ll.leon.com.opengladvance.fog.FogActivity;
import ll.leon.com.opengladvance.image.ImageActivity;
import ll.leon.com.opengladvance.mix.MixActivity;
import ll.leon.com.opengladvance.mix3d.Mix3dActivity;
import ll.leon.com.opengladvance.mtl.MtlDemoActivity;
import ll.leon.com.opengladvance.obj.ObjActivity;
import ll.leon.com.opengladvance.wave.WaveActivity;
import ll.leon.com.opengladvance.ziptexture.ZipActivity;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 123);
    }

    public void loadObj(View view) {
        ObjActivity.start(this);
    }

    public void loadMtl(View view) {
        // MtlActivity.start(this);
        MtlDemoActivity.start(this);



    }

    public void shake(View view) {
        WaveActivity.start(this);
    }

    public void mix(View view) {
        MixActivity.start(this);
    }

    public void mix3D(View view) {
        Mix3dActivity.start(this);
    }

    public void image(View view) {
        ImageActivity.start(this);
    }

    public void mix2(View view) {
        EarthActivity.start(this);
    }

    public void fog(View view) {
        FogActivity.start(this);
    }

    public void zip(View view) {
        ZipActivity.start(this);
    }

    public void fbo(View view) {
        FBOActivity.start(this);
    }

    public void camera(View view) {
        CameraActivity.start(this);
    }
}
