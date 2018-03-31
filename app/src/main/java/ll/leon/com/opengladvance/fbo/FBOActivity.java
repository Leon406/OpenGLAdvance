package ll.leon.com.opengladvance.fbo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import ll.leon.com.opengladvance.R;

public class FBOActivity extends AppCompatActivity {

    private FBOGLSurfaceView fbo;

    public static void start(Context context) {
        Intent starter = new Intent(context, FBOActivity.class);
        context.startActivity(starter);
    }

    private ImageView mImage;
    private String mImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);
        fbo = findViewById(R.id.fboView);
        fbo.setListener(this::saveBitmap);
        mImage = findViewById(R.id.mImage);
        ActivityCompat.requestPermissions(FBOActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            Toast.makeText(FBOActivity.this, " 成功", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void onClick(View view) {
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            //得到图片绝对路径
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            //根据图片绝对路径查询
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            //指向查询结果的第一个位置。
            c.moveToFirst();
            //得到当前列的索引
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            //得到当前列的列的值（图路径）
            mImgPath = c.getString(columnIndex);
            Log.e("LeonDebug", "img->" + mImgPath);

            Bitmap bmp = BitmapFactory.decodeFile(mImgPath);

            fbo.setBitmap(bmp);
            fbo.requestRender();
            c.close();
        }
    }

    //图片保存
    public void saveBitmap(final Bitmap b) {
        String path = mImgPath.substring(0, mImgPath.lastIndexOf("/") + 1);
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FBOActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FBOActivity.this, "保存成功->" + jpegName, Toast.LENGTH_SHORT).show();
                mImage.setImageBitmap(b);
            }
        });

    }

}
