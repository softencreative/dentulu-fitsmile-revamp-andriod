package com.app.fitsmile.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.utils.LocaleManager;

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(">>>Test>>>", ">>>>>>Splash activity start>>");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        LocaleManager.setLocale(this);
        ((AppController)getApplication()).notification();
        int SPLASH_TIME_OUT = 3000;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                openNextActivity();
            }
        }, SPLASH_TIME_OUT);

    }

    private void openNextActivity() {
        if (appPreference.getloginvalue().equals("0")) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
       else if (appPreference.getloginvalue().equals("")) {
            Intent intent = new Intent(SplashActivity.this, SliderActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(SplashActivity.this, SliderActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
