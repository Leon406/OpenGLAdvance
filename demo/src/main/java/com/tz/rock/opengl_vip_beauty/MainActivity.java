package com.tz.rock.opengl_vip_beauty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tz.rock.opengl_vip_beauty.activity.Camera2Activity;
import com.tz.rock.opengl_vip_beauty.activity.Camera3Activity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:  startActivity(new Intent(MainActivity.this, Camera2Activity.class));
                break;
            case R.id.button3:  startActivity(new Intent(MainActivity.this, Camera3Activity.class));
                break;

        }
    }
}
