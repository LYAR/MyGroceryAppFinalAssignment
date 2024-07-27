package com.example.mygroceryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mygroceryapp.authentication.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView ivWelcome;
    LinearLayout llBottom;
    Animation fromTop;
    Animation fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivWelcome = findViewById(R.id.ivWelcome);
        llBottom = findViewById(R.id.llBottom);

        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        ivWelcome.setAnimation(fromTop);
        llBottom.setAnimation(fromBottom);

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        };
        thread.start();

    }
}