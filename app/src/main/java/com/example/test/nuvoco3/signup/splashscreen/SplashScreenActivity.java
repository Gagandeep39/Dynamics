package com.example.test.nuvoco3.signup.splashscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.nuvoco3.BuildConfig;
import com.example.test.nuvoco3.R;
import com.example.test.nuvoco3.signup.LoginActivity;

import static com.example.test.nuvoco3.helpers.Contract.SPLASH_SCREEN_DURATION;

public class SplashScreenActivity extends AppCompatActivity {
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    TextView mTextViewVersion;
    ImageView mImageView;

    static {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_splash_screen);

        mImageView = findViewById(R.id.imgLogo);


        if (Build.VERSION.SDK_INT >= 25) {
            mImageView.setBackgroundResource(R.drawable.ic_dynamics);
        } else {
            mImageView.setBackgroundResource(R.drawable.ic_dynamics_backcompatible);
        }





        mTextViewVersion = findViewById(R.id.textViewVersion);
        mTextViewVersion.setText(getString(R.string.company_info) +
                "\n" + versionName +
                "\n" + getString(R.string.college_project));

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_SCREEN_DURATION);


    }
    }
