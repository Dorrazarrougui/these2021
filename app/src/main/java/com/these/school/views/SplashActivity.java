package com.these.school.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.these.school.R;

public class SplashActivity extends AppCompatActivity {
    ImageView logo, loader;
    public static boolean endTrans = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(1000));
            getWindow().setSharedElementReturnTransition(new ChangeBounds().setDuration(500));
        }
        setContentView(R.layout.activity_splash);
        //supportFinishAfterTransition();

        mAuth = FirebaseAuth.getInstance();

        logo = findViewById(R.id.logo);
        loader = findViewById(R.id.loader);

        ObjectAnimator topAnimation = ObjectAnimator.ofFloat(logo, "y", 50f);
        topAnimation.setDuration(2000);

        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(loader, "rotation", 0f, 360f);
        rotateAnimation.setDuration(3000);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(loader, View.ALPHA, 0.1f, 1.0f);
        alphaAnimation.setDuration(1500);
        rotateAnimation.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotateAnimation, alphaAnimation, topAnimation);
        animatorSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser==null){
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}