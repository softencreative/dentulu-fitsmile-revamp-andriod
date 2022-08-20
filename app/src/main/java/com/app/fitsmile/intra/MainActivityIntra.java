package com.app.fitsmile.intra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.app.fitsmile.R;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.intra.utils.CameraSessionManager;
import com.app.fitsmile.intra.utils.HandlerParams;
import com.app.fitsmile.intra.utils.PathConfig;
import com.app.fitsmile.intra.utils.StreamSelf;
import com.app.fitsmile.intra.utils.SurfaceView;
import com.app.fitsmile.utils.LocaleManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivityIntra extends CheckPermissonActivity {
    private static final String TAG = "TestMainActivityIntra";

    private SurfaceView mSurfaceView;

    private StreamSelf mStreamSelf;

    private int screenWidth;
    private int screenHeight;

    private int recTime;
    private int second;
    private Timer showTimer;
    private ShowTimerTask showTimerTask;

    private ImageView iv_Main_Background;
    private ImageView iv_Main_TakePhoto;
    private ImageView iv_Main_TakeVideo;
    private ImageView iv_Main_Playback;
    private ImageView iv_Main_Setting;
    private TextView txt_Main_RecordTime;

    private boolean isSearching = false;

    private long mKeyTime;
    private boolean isShowView = true;

    private static final int SCAN_OK = 0;

    List<String> photoList = new ArrayList<String>();
    List<String> videoList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intra);
        LocaleManager.setLocale(this);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //DisplayMetrics dm = this.getResources().getDisplayMetrics();
        //screenWidth = dm.widthPixels;
        //screenHeight = dm.heightPixels;
        //Log.d(TAG, screenWidth + "*" + screenHeight);
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mStreamSelf = new StreamSelf(getApplicationContext(), mSurfaceView, handler);
        widgetInit();
        showTimer = new Timer();
        AppController.cmdSocket.setHandler(handler);
    }

    private void widgetInit() {
        getPhotoVideoList(new File(PathConfig.getRootPath() + PathConfig.PHOTOS_PATH), new File(PathConfig.getRootPath() + PathConfig.VIDEOS_PATH));

        iv_Main_Background  = (ImageView) findViewById(R.id.iv_main_background);
        iv_Main_TakePhoto   = (ImageView) findViewById(R.id.iv_Main_TakePhoto);
        iv_Main_TakeVideo   = (ImageView) findViewById(R.id.iv_Main_TakeVideo);
        iv_Main_Playback    = (ImageView) findViewById(R.id.iv_Main_Playback);
        iv_Main_Setting     = (ImageView) findViewById(R.id.iv_Main_Setting);
        txt_Main_RecordTime = (TextView) findViewById(R.id.txt_Record_Time);

        iv_Main_TakePhoto.setOnClickListener(clickListener);
        iv_Main_TakeVideo.setOnClickListener(clickListener);
        iv_Main_Playback.setOnClickListener(clickListener);
        iv_Main_Setting.setOnClickListener(clickListener);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mStreamSelf.startStream();
        AppController.cmdSocket.startRunKeyThread();
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.iv_Main_TakePhoto:
                    mStreamSelf.takePhoto();
                    break;
                case R.id.iv_Main_TakeVideo:
                    mStreamSelf.takeRecord();
                    break;
                case R.id.iv_Main_Playback:
                    //finish();
                    startIntent(MainActivityIntra.this, PlaybackActivity.class);
                    break;
                case R.id.iv_Main_Setting:
                    startIntent(MainActivityIntra.this, SettingIntraActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case HandlerParams.RECORD_START:
                    initTime();
                    txt_Main_RecordTime.setVisibility(View.VISIBLE);
                    iv_Main_TakeVideo.setImageResource(R.drawable.main_takevideo_recording);
                    txt_Main_RecordTime.setText("REC:" + String.format("%02d", 0) + ":" + String.format("%02d", 0) + ":" + String.format("%02d", 0));
                    startShowTimer();
                    break;
                case HandlerParams.RECORD_STOP:
                    clearTime();
                    iv_Main_TakeVideo.setImageResource(R.drawable.main_takevideo_state);
                    txt_Main_RecordTime.setVisibility(View.INVISIBLE);
                    break;
                case HandlerParams.SDCARD_FULL:
                    Utils.showToast(MainActivityIntra.this, getString(R.string.SD_card_full));
                    break;
                case HandlerParams.UPDATE_RECORDTIME:
                    setTime();
                    break;
                case HandlerParams.HAS_GET_STREAM:
                    iv_Main_Background.setVisibility(View.GONE);
                    break;
                case HandlerParams.REMOTE_TAKE_PHOTO:
                    mStreamSelf.takePhoto();
                    break;
                case HandlerParams.REMOTE_TAKE_RECORD:
                    if (mStreamSelf.getVideoWidth() > 1280) {
                        Utils.showToast(MainActivityIntra.this, getString(R.string.video_resolution));
                        break;
                    }
                    mStreamSelf.takeRecord();
                    break;
                case HandlerParams.REMOTE_GET_IP:
                    if(msg.obj != null) {
                        //String oldText = txt_Main_IpList.getText().toString();
                        //txt_Main_IpList.setText(oldText + "\n" + msg.obj.toString());
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    });

    private void setTime() {
        Time t = new Time();
        t.setToNow();
        int s = t.second;
        if (second != s) {
            second = s;
            recTime++;
            int time_h = recTime / 3600;
            int time_m = (recTime % 3600) / 60;
            int time_s = recTime % 60;
            txt_Main_RecordTime.setText("REC:" + String.format("%02d", time_h) + ":" + String.format("%02d", time_m) + ":" + String.format("%02d", time_s));
        }

    }

    private void initTime() {
        recTime = 0;
        Time time = new Time();
        time.setToNow();
        second = time.second;
    }

    private void clearTime() {
        second = 0;
    }

    // what the timer task do
    private class ShowTimerTask extends TimerTask {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            handler.sendEmptyMessage(HandlerParams.UPDATE_RECORDTIME);
        }
    }

    // start the timer task
    private void startShowTimer() {
        if (showTimer != null) {
            if (showTimerTask != null) {
                showTimerTask.cancel();
            }
        }

        showTimerTask = new ShowTimerTask();
        showTimer.schedule(showTimerTask, 500, 500); // 500ms one time
    }

    private void startIntent(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        startActivity(intent);
        //overridePendingTransition(R.anim.left_out, R.anim.left_out);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mStreamSelf.stopStream();
        AppController.cmdSocket.stopRunKeyThread();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mStreamSelf.destroy();
    }

    private void getPhotoVideoList(final File photoPath, final File videoPath) {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                // photo list
                photoList.clear();
                photoList = PathConfig.getImagesList(photoPath);

                // video list
                videoList.clear();
                videoList = PathConfig.getVideosList(videoPath);
                Log.e(TAG, "images size:" + photoList.size() + " videos size:" + videoList.size());
                CameraSessionManager.getInstance(MainActivityIntra.this).setCount(videoList.size(), photoList.size());
                handler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }



	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
}
