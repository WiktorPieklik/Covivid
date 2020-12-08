package com.example.covivid.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.covivid.R;
import com.example.covivid.Utils.Common;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.requestFullScreenActivity(this);
        setContentView(R.layout.activity_splash);
        Animation fadeAnim = AnimationUtils.loadAnimation(this, R.anim.fade_scale);
        ImageView appName = findViewById(R.id.app_name);
        appName.setAnimation(fadeAnim);


        proceedToMainActivity(this);
    }

    private void proceedToMainActivity(SplashActivity activity)
    {
        new Thread(() -> {
            try {
                Thread.sleep(1150);
            }
            catch (InterruptedException ignored){ }
            finally {
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }
}