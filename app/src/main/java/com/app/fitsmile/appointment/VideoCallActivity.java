package com.app.fitsmile.appointment;

import android.Manifest;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.videocall.VideoCallPojo;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.internal.RtcEngineMessage;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import retrofit2.Call;
import retrofit2.Callback;

public class VideoCallActivity extends BaseActivity {

    private static final String TAG = VideoCallActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;

    String strToken = "";
    String strChannel = "";
    String strUser = "";
    String strFirstName = "";
    String strLastName = "";
    String strViewStatus = "";

    TextView tv_fname, tv_lname, tv_call_time;
    TextView tv_name, tv_time;
    FrameLayout app_icon, caller_info;
    String countTime;

    public static boolean testing = false;
    public static boolean decline = false;

    public static Activity activity;

    int sec = 0;
    int seconds = 0;
    String is_video_appointment = "0";
    CountDownTimer count_down_timer;
    CountDownTimer call_duration_count_down_timer;

    boolean callStatus = false;
    boolean timer = false;
    String strEmergency = "";
    String strLimited = "";
    int startTimer = 0;
    int oldTimer = 0;
    int limit_timer = 0;
    private static final int PROJECTION_REQ_CODE = 1 << 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);
        LocaleManager.setLocale(this);
        activity = this;
        initUI();
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
    }

    private void initUI() {
        callTimerStart();
        getVideoCallInfo();
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);
        tv_fname = findViewById(R.id.tv_fname);
        tv_lname = findViewById(R.id.tv_lname);
        tv_call_time = findViewById(R.id.tv_call_time);
        app_icon = findViewById(R.id.app_icon);
        caller_info = findViewById(R.id.caller_info);

    }

    void setData() {
        strFirstName = appPreference.getAGORA_FNAME();
        strLastName = appPreference.getAGORA_LNAME();
        tv_fname.setText("Dr " + strFirstName);
        tv_lname.setText(strLastName);

        strEmergency = appPreference.getAGORA_EMERGENCY();
        strLimited = appPreference.getAGORA_LIMITED();

        if (strLimited.equalsIgnoreCase("1")) {
            tv_time.setVisibility(View.VISIBLE);
        } else {
            tv_time.setVisibility(View.GONE);
        }
        strViewStatus = getIntent().getStringExtra("view_status");
        if (strViewStatus.equalsIgnoreCase("receiver")) {
            tv_call_time.setText(R.string.connecting);
        }
        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callStatus) {
                    disConnectCall();
                } else {
                    rejectCallActivity();
                    finish();
                }
            }
        });
    }

    private void callTimerStart() {
        count_down_timer = new CountDownTimer(45000, 1000) { /* 300000, 1000*/
            public void onTick(long millisUntilFinished) {
                seconds++;
            }

            public void onFinish() {
                Utils.showToast(activity, getString(R.string.dentist_dit_not_attend_the_call));
                rejectCallActivity();
                finish();
            }
        }.start();
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("screenShare_reason", String.valueOf(reason));
                   // onRemoteUserLeft();
                    if(reason== io.agora.rtc.IRtcEngineEventHandler.UserOfflineReason.USER_OFFLINE_QUIT){
                        onRemoteUserLeft();
                    }
                    else if(reason== ClientRole.CLIENT_ROLE_BROADCASTER){
                        Toast.makeText(actCon, "CLIENT_ROLE_BROADCASTER", Toast.LENGTH_LONG).show();
                    }
                    else if(reason== ClientRole.CLIENT_ROLE_AUDIENCE){
                        Toast.makeText(actCon, "CLIENT_ROLE_AUDIENCE", Toast.LENGTH_LONG).show();
                        setupRemoteVideo(uid);
                    }
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        /**remove local preview*/
                      //  fl_local.removeAllViews();
                        /***/
                        MediaProjectionManager mpm = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                        Intent intent = mpm.createScreenCaptureIntent();
                        startActivityForResult(intent, PROJECTION_REQ_CODE);
                    } else {
                        //showAlert(getString(R.string.lowversiontip));
                    }
                }
            });
        }
    };

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PROJECTION_REQ_CODE && resultCode == RESULT_OK) {
//            try {
//                DisplayMetrics metrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_WIDTH, metrics.widthPixels);
//                data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_HEIGHT, metrics.heightPixels);
//                data.putExtra(ExternalVideoInputManager.FLAG_SCREEN_DPI, (int) metrics.density);
//                data.putExtra(ExternalVideoInputManager.FLAG_FRAME_RATE, DEFAULT_SHARE_FRAME_RATE);
//
//                setVideoConfig(ExternalVideoInputManager.TYPE_SCREEN_SHARE, metrics.widthPixels, metrics.heightPixels);
//                mService.setExternalVideoInput(ExternalVideoInputManager.TYPE_SCREEN_SHARE, data);
//            }
//            catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }



    private void setupRemoteVideo(int uid) {
        int count = mRemoteContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = mRemoteContainer.getChildAt(i);
            Log.e("Tag", String.valueOf(v.getTag()));
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
                v.setTag(null);
            }
        }


        if (view != null) {
            return;
        }

        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        app_icon.setVisibility(View.VISIBLE);
        callStatus = true;
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_FIT, uid));
        mRemoteView.setTag(uid);

        testing = true;

        caller_info.setVisibility(View.VISIBLE);
        tv_name.setText(getResources().getString(R.string.dr) +" "+ appPreference.getAGORA_FNAME() + " " + appPreference.getAGORA_LNAME());
        //CallNowActivity.activity.finish();
        if (count_down_timer != null) {
            count_down_timer.cancel();
        }

        if (!timer) {
            startTimer = startTimer * 1000;
            timer= true;
        }

        if (limit_timer == 0) { // if the limit_timer is 0 then there is no time limit for the video call so we are not running timer thread.
            tv_time.setText("");
            return;
        }

        call_duration_count_down_timer = new CountDownTimer(startTimer, 1000) {
            public void onTick(long millisUntilFinished) {
                countTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                sec++;

                tv_time.setText(countTime);

                if (countTime.equals( "00:00")) {
                    tv_time.setVisibility(View.GONE);
                    disConnectCall();
                }

            }
            public void onFinish() {
                if (countTime == "00:00") {
                    disConnectCall();
                }
            }
        }.start();

    }

    private void onRemoteUserLeft() {
        removeRemoteVideo();
        disConnectCall();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.length>0){

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }else {

            }
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showToast(getApplicationContext(), msg);
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
        setData();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void joinChannel() {
        strToken = appPreference.getAGORA_TOKEN();
        strChannel = appPreference.getAGORA_CHANNEL();
        strUser = appPreference.getUserId();//CameraSessionManager.getInstance(this).getAGORA_RECEIVER();
        if (TextUtils.isEmpty(strToken)) {
            strToken = null; // default, no strToken
        }
        mRtcEngine.joinChannel(strToken, strChannel, "Extra Optional Data", Integer.parseInt(strUser));
        videoCallUpdateStatus("videoCallConsultationStarted");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //CommonClass.showToast(activity,"Pause called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        RtcEngine.destroy();
        if (count_down_timer != null) {
            count_down_timer.cancel();
        }
        if (call_duration_count_down_timer != null) {
            call_duration_count_down_timer.cancel();
        }

    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
        }
        showButtons(!mCallEnd);
    }


    public void disConnectCall() {
        if (!testing && decline) {
            rejectCallActivity();
        } else if (!testing) {
            rejectCallActivity();
        } else {
            videoCallUpdateStatus("updateDoctorServiceCompleted");
        }
    }


    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
        appPreference.clearAgoraCallDetails();
        testing = true;
        disConnectCall();
    }

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }



    void videoCallUpdateStatus(final String videocallStatus) {
        StringRequest request = new StringRequest(Request.Method.POST, BuildConfig.BASE_URL + videocallStatus,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals(null)) {
                            if (videocallStatus.equals("updateDoctorServiceCompleted")) {
                                /*DoctorListActivity.isEnd = true;*/
                                finish();
                                Intent intent = new Intent(activity, ReviewActivity.class);
                                intent.putExtra("dentist_id", appPreference.getAGORA_RECEIVER());
                                startActivity(intent);
                            } else {
//                                DoctorListActivity.isEnd = false;
                                testing = true;
                            }
                        } else {
                            Log.e("Your Array Response", "Data Null");
                        }
                    }

                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", appPreference.getToken());
                params.put("Dentulu-Userid", appPreference.getUserId());
                params.put("Sessiontoken", appPreference.getIsAuthentication());
                return params;
            }

            @Override
            public byte[] getBody() {
                try {
                    JSONObject params = new JSONObject();
                    try {
                        if (videocallStatus.equals("videoCallConsultationStarted")) {
                            params.put("user_id", appPreference.getUserId());
                            params.put("doctor_id", appPreference.getAGORA_RECEIVER());
                            params.put("video_call_id", appPreference.getAGORA_VIDEO_ID());
                            params.put("is_video_appointment", is_video_appointment);
                        } else {
                            params.put("user_id", appPreference.getUserId());
                            params.put("doctor_id", appPreference.getAGORA_RECEIVER());
                            params.put("video_call_id", appPreference.getAGORA_VIDEO_ID());
                            params.put("record_id", "");
                            sec = sec + oldTimer;
                            params.put("duration", String.valueOf(sec));
                            params.put("is_video_appointment", is_video_appointment);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String requestBody = params.toString();
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }

        };

        AppController.getInstance().addToRequestQueue(request);
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
                VideoCallPojo videoCallPojo = response.body();
                if (videoCallPojo.getStatus().equals("1")) {
                    finish();
                }else if (videoCallPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(VideoCallActivity.this);
                }
            }

            @Override
            public void onFailure(Call<VideoCallPojo> call, Throwable t) {
                call.cancel();
            }
        });
    }


    public void getVideoCallInfo() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("channel", appPreference.getAGORA_CHANNEL());
        jsonObj.addProperty("appointment_id", appPreference.getAGORA_VIDEO_ID());
        jsonObj.addProperty("category", "2");
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<VideoCallPojo> mService = mApiService.getVideoCallInfo(jsonObj);
        mService.enqueue(new Callback<VideoCallPojo>() {
            @Override
            public void onResponse(Call<VideoCallPojo> call, retrofit2.Response<VideoCallPojo> response) {
                VideoCallPojo videoCallPojo = response.body();
                if (videoCallPojo.getStatus().equals("1")) {
                    oldTimer = Integer.parseInt(videoCallPojo.getOld_duration());
                    startTimer = Integer.parseInt(videoCallPojo.getDuration());
                    limit_timer = Integer.parseInt(Utils.getStr(videoCallPojo.getLimited()).isEmpty() ? "1" : Utils.getStr(videoCallPojo.getLimited()));
                    if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                            checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                            checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
                        initEngineAndJoinChannel();
                    }
                }else if (videoCallPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(VideoCallActivity.this);
                }
            }

            @Override
            public void onFailure(Call<VideoCallPojo> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Display d = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        int width = p.x;
        int height = p.y;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational ratio = new Rational(width, height);
            PictureInPictureParams.Builder pip_Builder = new PictureInPictureParams.Builder();
            pip_Builder.setAspectRatio(ratio).build();
            enterPictureInPictureMode(pip_Builder.build());
        }
    }
}
