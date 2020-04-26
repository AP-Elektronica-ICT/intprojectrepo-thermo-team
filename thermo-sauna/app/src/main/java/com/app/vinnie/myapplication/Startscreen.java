package com.app.vinnie.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Startscreen extends AppCompatActivity {

    private static int SPLASH_SCREEN=2500;
    ImageView mlogo;
    TextView mprojectname;
    Animation top, bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_screen);
        mlogo =findViewById(R.id.Logo);
        mprojectname = findViewById(R.id.projectname);

        top = AnimationUtils.loadAnimation(this, R.anim.top);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);

        mlogo.setAnimation(top);
        mprojectname.setAnimation(bottom);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();

            }
        }, SPLASH_SCREEN);

    }
}
