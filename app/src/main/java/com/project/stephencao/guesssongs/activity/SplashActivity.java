package com.project.stephencao.guesssongs.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.*;
import android.widget.ImageView;
import com.project.stephencao.guesssongs.R;
import com.project.stephencao.guesssongs.util.ConstantValues;

import java.io.*;

public class SplashActivity extends AppCompatActivity {
    private ImageView mImageView, mStarImageView;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mImageView = findViewById(R.id.iv_splash_activity);
        mStarImageView = findViewById(R.id.iv_splash_activity_star);
        prepareMusicResource();
        initAnimation();
        initStarAnimation();
    }

    private void initStarAnimation() {

    }

    /**
     * copy all audio files to users sd card when installing the app
     */
    private void prepareMusicResource() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String string : ConstantValues.AUDIO_INFO) {
                    File file = new File(Environment.getExternalStorageDirectory(), string);
                    if (file.exists()) {
                        return;
                    } else {
                        try {
                            InputStream inputStream = getAssets().open(string);
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                            int len = 0;
                            byte[] buffer = new byte[1024];
                            while ((len = inputStream.read(buffer)) != -1) {
                                bufferedOutputStream.write(buffer, 0, len);
                            }
                            bufferedOutputStream.close();
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(3000);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f,
                1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(3000);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(5000);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mStarImageView.setBackgroundResource(R.drawable.animation_star);
                animationDrawable = (AnimationDrawable) mStarImageView.getBackground();
                animationDrawable.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationDrawable.stop();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mImageView.startAnimation(animationSet);
    }
}
