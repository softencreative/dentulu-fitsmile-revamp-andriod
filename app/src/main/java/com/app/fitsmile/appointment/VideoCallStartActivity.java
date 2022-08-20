package com.app.fitsmile.appointment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.videocall.VideoCallPojo;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

public class VideoCallStartActivity extends BaseActivity {
    public static Activity activity;
    Uri defaultRintoneUri;
    Ringtone defaultRington;
    TextView tvCALL;
    ImageView call, decline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_start);
        LocaleManager.setLocale(this);
        activity = this;
        Log.d(">>>Test>>>", ">>>>>>VideoCall Start activity>>");
        tvCALL = findViewById(R.id.name);
        call = findViewById(R.id.attenn);
        decline = findViewById(R.id.decline);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvCALL.setText(AppController.Name);

        AppController.isVideoCallComing = false;
        Log.d(">>>Test>>>", ">>>>>>isVideoCallComing>>" + AppController.isVideoCallComing);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectCallActivity();
                //finish();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoCallStartActivity.this, VideoCallActivity.class);
                intent.putExtra("view_status", "receiver");
                startActivity(intent);
                defaultRington.stop();
                finish();
            }
        });

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();

// to release screen lock
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();


        defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        defaultRington = RingtoneManager.getRingtone(getApplicationContext(), defaultRintoneUri);
        defaultRington.play();
        seTimer();
    }

    void rejectCallActivity() {
        String str1 = appPreference.getUserId();
        String str2 = appPreference.getAGORA_RECEIVER();
        if (str1.equalsIgnoreCase(str2)) {
            str2 = appPreference.getAGORA_SENDER();
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", str1);
        jsonObj.addProperty("caller_id", str2);
        jsonObj.addProperty("to_id", str2);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<VideoCallPojo> mService = mApiService.rejectVideoCall(jsonObj);
        mService.enqueue(new Callback<VideoCallPojo>() {
            @Override
            public void onResponse(Call<VideoCallPojo> call, retrofit2.Response<VideoCallPojo> response) {
                VideoCallPojo ChangePasswordedit = response.body();
                if (ChangePasswordedit.getStatus().equals("1")) {
                    defaultRington.stop();
                    finish();
                } else if (ChangePasswordedit.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(VideoCallStartActivity.this);
                }
            }

            @Override
            public void onFailure(Call<VideoCallPojo> call, Throwable t) {
                call.cancel();
            }
        });
    }

    void seTimer() {
        new CountDownTimer(45000, 1000) {

            public void onTick(long millisUntilFinished) {

                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                if (activity != null)
                    finish();
            }

        }.start();

    }
}

