package com.app.fitsmile.appointment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.AdjustReminderActivity;
import com.app.fitsmile.fitsreminder.AlarmReceiver;
import com.app.fitsmile.fitsreminder.AlignerSmileProgressActivity;
import com.app.fitsmile.fitsreminder.FitsReminderActivity;
import com.app.fitsmile.fitsreminder.LocalData;
import com.app.fitsmile.fitsreminder.NotificationScheduler;
import com.app.fitsmile.fitsreminder.PieChartView;
import com.app.fitsmile.fitsreminder.ReminderProgressActivity;
import com.app.fitsmile.fitsreminder.adapter.TimerPushNotificationListAdapter;
import com.app.fitsmile.response.rewards.SettingResponse;
import com.app.fitsmile.response.trayMinder.TimerPushNotificationResult;
import com.app.fitsmile.response.trayMinder.TrayMinderDataResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;


import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReminderDetailsActivity extends BaseActivity implements RecyclerItemClickListener {
    PieChartView pieChartView;
    private RelativeLayout mTimerLayout;
    private TextView mTextWearReminderTime;
    private String wearInTime;
    private TextView mTextWear;
    private TextView mTextWearNotReminderTime;
    private int seconds = 0;
    private int outSeconds = 0;
    private int goalSeconds = 0;

    boolean isFABOpen = false;
    private boolean isBlink = false;
    private boolean isBlinkOut = false;
    private boolean isBlinkGoal = false;
    private static final String TAG = FitsReminderActivity.class.getSimpleName();
    LocalData localData;

    // Handler to update the UI every second when the timer is running
    private final Handler mUpdateTimeHandler = new UIUpdateHandler(this);
    private final Handler mUpdateOutTimeHandler = new UIUpdateHandlerOut(this);

    // Message type for the handler
    private final static int MSG_UPDATE_TIME = 0;
    private final static int MSG_UPDATE_TIME_OUT = 0;

    private boolean running;

    private boolean wasRunning;
    String totalInTime, totalOutTime;
    String inTime, outTime;
    String mGoalTime, mGoalTimeApi;

    boolean isTimerOn = false;
    boolean isOutTimerOn = false;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    String wearTime, notWearTime, totalPendingTime;
    int fitsReminderId;
    TextView mTextHeader;
    TextView mTextBottomDays;
    FloatingActionButton fabShowDialog, fabSettings, fabShare, fabReminder,fabTimeLine,fabSmileProgress;
    LinearLayout fabLayoutSettings, fabLayoutShare, fabLayoutReminder,fabLayoutTimeLine,fabLayoutSmileProgress;
    View fabBGLayout;
    RelativeLayout mLayoutChartScreen;
    RelativeLayout mLayoutPlay;
    TextView mTextPlay;
    String remindTime;
    CircleImageView mCirclePlay;
    private AlertDialog alertDialogReminder;
    private TextView mTextGoalTime;
    private boolean isStartResume = false;
    TrayMinderResult mReminderResult = new TrayMinderResult();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tray_minder_details);
        LocaleManager.setLocale(this);
        fitsReminderId = getIntent().getIntExtra("id", 0);
        localData = new LocalData(this);
        initUI();
        // setPieChartView();
        setUpToolBar();
        setClicks();

    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.appointment_details));
    }

    void initUI() {
        pieChartView = findViewById(R.id.pieChart);
        mTimerLayout = (RelativeLayout) findViewById(R.id.layoutTimer);
        mTextWearReminderTime = (TextView) findViewById(R.id.textWearReminderTime);
        mTextWear = (TextView) findViewById(R.id.textWearIndicator);
        mTextWearNotReminderTime = (TextView) findViewById(R.id.textWearNotReminderTime);
        mTextHeader = (TextView) findViewById(R.id.textHeaderTrayMinder);
        mTextBottomDays = (TextView) findViewById(R.id.textBottomPieChart);
        fabLayoutReminder = findViewById(R.id.fabLayout1);
        fabLayoutShare = findViewById(R.id.fabLayout2);
        fabLayoutSettings = findViewById(R.id.fabLayout3);
        fabLayoutTimeLine = findViewById(R.id.fabLayoutTimeline);
        fabLayoutSmileProgress = findViewById(R.id.fabLayoutSmileProgress);
        fabShowDialog = findViewById(R.id.fab);
        fabReminder = findViewById(R.id.fab1);
        fabShare = findViewById(R.id.fab2);
        fabSettings = findViewById(R.id.fab3);
        fabTimeLine = findViewById(R.id.fabtimeline);
        fabSmileProgress = findViewById(R.id.fabSmileProgress);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        mLayoutChartScreen = findViewById(R.id.layoutChartScreen);
        mLayoutPlay = findViewById(R.id.layoutPlay);
        mTextPlay = findViewById(R.id.textPlay);
        mCirclePlay = findViewById(R.id.circlePlay);
        mTextGoalTime = findViewById(R.id.textTodayGoal);
        getReminderDetails();


        mLayoutPlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!isTimerOn) {
                    inTime = simpleDateFormat.format(Calendar.getInstance().getTime());
                    updateUIStartRun();
                    setPieChartViewOnClick();
                    mTextWear.setText(R.string.wearing);
                    mTextPlay.setText(R.string.pause);
                    mTextPlay.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mCirclePlay.setBorderColor(getResources().getColor(R.color.colorPrimary));
                    mTextWear.setTextColor(getResources().getColor(R.color.colorPrimary));
                    screenVibrate();
                    isTimerOn = true;
                    updateUIStopRunOut();
                    setTextWithoutBlink(outSeconds, mTextWearNotReminderTime, 1);
                    updateReminderTiming(getResources().getString(R.string.in));
                } else {
                    outTime = simpleDateFormat.format(Calendar.getInstance().getTime());
                    updateUIStartRunOut();
                    isTimerOn = false;
                    setPieChartView();
                    mTextPlay.setText(R.string.resume);
                    mTextPlay.setTextColor(getResources().getColor(R.color.red_light));
                    mTextWear.setText(R.string.not_wearing);
                    mCirclePlay.setBorderColor(getResources().getColor(R.color.red_light));
                    screenVibrate();
                    mTextWear.setTextColor(getResources().getColor(R.color.red));
                    //    timerService.stopTimer();
                    updateUIStopRun();
                    wearTime = mTextWearReminderTime.getText().toString();
                    setTextWithoutBlink(seconds, mTextWearReminderTime, 2);
                    updateReminderTiming(getResources().getString(R.string.out));
                    showDialogReminder();
                }

            }
        });

        mTextHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void setClicks() {
        fabShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                shareImage();

            }
        });
        fabReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showDialogReminder();

            }
        });
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                Intent intent = new Intent(ReminderDetailsActivity.this, AdjustReminderActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, String.valueOf(fitsReminderId));
                isStartResume = true;
                startActivityForResult(intent,200);
            }
        });


        fabLayoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                shareImage();
            }
        });
        fabLayoutReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                showDialogReminder();

            }
        });
        fabLayoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderDetailsActivity.this, AdjustReminderActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, String.valueOf(fitsReminderId));
                isStartResume = true;
                startActivityForResult(intent,200);
                closeFABMenu();

            }
        });
        fabLayoutSmileProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderDetailsActivity.this, AlignerSmileProgressActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, String.valueOf(fitsReminderId));
                intent.putExtra(AppConstants.ALIGNER_NO, mReminderResult.getToday_timming_log().getAligner_no());
                isStartResume = true;
                startActivity(intent);
                closeFABMenu();

            }
        });
        fabLayoutTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderDetailsActivity.this, AdjustReminderActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, String.valueOf(fitsReminderId));
                intent.putExtra(AppConstants.ALIGNER_NO, mReminderResult.getToday_timming_log().getAligner_no());
                isStartResume = true;
                startActivity(intent);
                closeFABMenu();

            }
        });

        fabSmileProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderDetailsActivity.this, AlignerSmileProgressActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, String.valueOf(fitsReminderId));
                intent.putExtra(AppConstants.ALIGNER_NO, mReminderResult.getToday_timming_log().getAligner_no());
                isStartResume = true;
                startActivity(intent);
                closeFABMenu();

            }
        });
        fabTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderDetailsActivity.this, ReminderProgressActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, String.valueOf(fitsReminderId));
                intent.putExtra(AppConstants.ALIGNER_NO, mReminderResult.getToday_timming_log().getAligner_no());
                isStartResume = true;
                startActivity(intent);
                closeFABMenu();

            }
        });
    }


    private void showFABMenu() {
        isFABOpen = true;
        if (isTimerOn) {
            fabLayoutReminder.setVisibility(View.GONE);
        } else {
            fabLayoutReminder.setVisibility(View.VISIBLE);
            fabLayoutReminder.animate().translationY(-getResources().getDimension(R.dimen._250sdp));
        }
        fabLayoutSettings.setVisibility(View.VISIBLE);
        fabLayoutShare.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fabLayoutTimeLine.setVisibility(View.VISIBLE);
        fabLayoutSmileProgress.setVisibility(View.VISIBLE);
        fabShowDialog.animate().rotationBy(180);
        fabLayoutSettings.animate().translationY(-getResources().getDimension(R.dimen._50sdp));
        fabLayoutTimeLine.animate().translationY(-getResources().getDimension(R.dimen._100sdp));
        fabLayoutSmileProgress.animate().translationY(-getResources().getDimension(R.dimen._150sdp));
        fabLayoutShare.animate().translationY(-getResources().getDimension(R.dimen._200sdp));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fabShowDialog.animate().rotation(360);
        fabLayoutShare.animate().translationY(0);
        fabLayoutReminder.animate().translationY(0);
        fabLayoutTimeLine.animate().translationY(0);
        fabLayoutSmileProgress.animate().translationY(0);
        fabLayoutSettings.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayoutReminder.setVisibility(View.GONE);
                    fabLayoutShare.setVisibility(View.GONE);
                    fabLayoutSettings.setVisibility(View.GONE);
                    fabLayoutSmileProgress.setVisibility(View.GONE);
                    fabLayoutTimeLine.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    void getReminderDetails() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", fitsReminderId);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderDataResponse> mService = mApiService.fetchFitsReminderDetails(jsonObject);
        mService.enqueue(new Callback<TrayMinderDataResponse>() {
            @Override
            public void onResponse(Call<TrayMinderDataResponse> call, Response<TrayMinderDataResponse> response) {
                Utils.closeProgressDialog();
                TrayMinderDataResponse mResponse = response.body();
                assert mResponse != null;
                TrayMinderResult mBody = mResponse.getData();
                assert mBody != null;
                mReminderResult = mResponse.getData();
                if (mResponse.getStatus() == 1) {
                    remindTime = mBody.getRemind_time();
                    mTextBottomDays.setText(String.format("%s"+R.string.remaining_days, mBody.getRemaining_days()));
                    mGoalTimeApi = mBody.getGoal_time();
                    if (mBody.getToday_timming_log() != null) {
                        if (Integer.parseInt(mBody.getToday_timming_log().getDay()) < 10) {
                            mTextHeader.setText(String.format("%s"+R.string.days_of_aligner_1+"#%s", mBody.getToday_timming_log().getDay(), mBody.getToday_timming_log().getAligner_no()));
                        } else {
                            mTextHeader.setText(String.format("%s"+R.string.days_of_aligner_1+"#%s", mBody.getToday_timming_log().getDay(), mBody.getToday_timming_log().getAligner_no()));
                        }
                        inTime = mBody.getToday_timming_log().getIn_time();
                        outTime = mBody.getToday_timming_log().getOut_time();
                        if (mBody.getToday_timming_log().getTotal_in_time() != null) {
                            totalInTime = mBody.getToday_timming_log().getTotal_in_time();
                        } else
                            totalInTime = "00:00";
                        if (mBody.getToday_timming_log().getTotal_out_time() != null) {
                            totalOutTime = mBody.getToday_timming_log().getTotal_out_time();
                        } else
                            totalOutTime = "00:00";
                    }

                    if (mBody.getTimer_on().equals("1")) {
                        isTimerOn = true;
                    } else {
                        isTimerOn = false;
                    }
                    if (isTimerOn) {
                        if (mBody.getToday_timming_log() != null) {
                            inTime = mBody.getToday_timming_log().getIn_time();
                            outTime = mBody.getToday_timming_log().getOut_time();
                            getTotalInTime(mBody.getToday_timming_log().getIn_time(), totalInTime);
                            setPieChartViewOnClick();
                            mTextWear.setText(R.string.wearing);
                            mTextPlay.setText(R.string.pause);
                            mTextPlay.setTextColor(getResources().getColor(R.color.colorPrimary));
                            mCirclePlay.setBorderColor(getResources().getColor(R.color.colorPrimary));
                            mTextWear.setTextColor(getResources().getColor(R.color.colorPrimary));
                            isTimerOn = true;
                            updateUIStartRun();
                        } else {
                            wearInTime = totalInTime;
                            mTextWearReminderTime.setText(totalInTime);
                            setPieChartViewOnClick();
                            mTextWear.setText(R.string.wearing);
                            mTextPlay.setText(R.string.pause);
                            mTextPlay.setTextColor(getResources().getColor(R.color.colorPrimary));
                            mCirclePlay.setBorderColor(getResources().getColor(R.color.colorPrimary));
                            mTextWear.setTextColor(getResources().getColor(R.color.colorPrimary));
                            isTimerOn = true;
                            updateUIStartRun();
                        }
                    } else {
                        wearInTime = totalInTime;
                        mTextWearReminderTime.setText(totalInTime);
                        setPieChartView();
                        updateUIStartRunOut();
                    }
                    getOutSeconds();
                    //   getGoalSeconds();
                    setTime();
                }else if (mResponse.getStatus()==401) {
                  Utils.showSessionAlert(ReminderDetailsActivity.this);
                }
            }

            @Override
            public void onFailure(Call<TrayMinderDataResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    private void getTotalInTime(String in_time, String totalInTime) {
        try {
            Date currentTime = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().getTime()));
            Date inTime = simpleDateFormat.parse(in_time);
            String elapsedTime = simpleDateFormat.format(getTimeDifference(inTime, currentTime));
            wearInTime = getTimeAfterAdd(elapsedTime, totalInTime);
            mTextWearReminderTime.setText(wearInTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void updateReminderTiming(String type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", fitsReminderId);
        jsonObject.addProperty("type", type);
        if (inTime != null) {
            jsonObject.addProperty("in_time", inTime);
        } else
            jsonObject.addProperty("in_time", "00:00");
        if (outTime != null) {
            jsonObject.addProperty("out_time", outTime);
        } else
            jsonObject.addProperty("out_time", "00:00");

        jsonObject.addProperty("total_in_time", totalInTime);
        jsonObject.addProperty("total_out_time", totalOutTime);
        if (isTimerOn)
            jsonObject.addProperty("timer_on", "1");
        else
            jsonObject.addProperty("timer_on", " ");

        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderDataResponse> mService = mApiService.updateFitsReminder(jsonObject);
        mService.enqueue(new Callback<TrayMinderDataResponse>() {
            @Override
            public void onResponse(Call<TrayMinderDataResponse> call, Response<TrayMinderDataResponse> response) {
                Utils.closeProgressDialog();
                TrayMinderDataResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus() == 1) {

                }else if (mResponse.getStatus()==401) {
                   Utils.showSessionAlert(ReminderDetailsActivity.this);
                }

            }

            @Override
            public void onFailure(Call<TrayMinderDataResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    void screenVibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    void setPieChartViewOnClick() {
        getDifference();
        float[] f = {getPercentage(wearTime), getPercentage(totalPendingTime), getPercentage(notWearTime)};
        int[] colorArray = {R.color.colorPrimary, R.color.profile_grey, R.color.light_grey_color};
        pieChartView.setDataPoints(f);
        pieChartView.setCenterColor(R.color.colorLightGreen);
        pieChartView.setSliceColor(colorArray);

    }

    void setTextWithoutBlink(int seconds, TextView textView, int view) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        String time;

        time = String
                .format(Locale.getDefault(),
                        "%02d:%02d", hours,
                        minutes);
        if (view == 1) {
            textView.setText("Out " + time);
        } else
            textView.setText(time);
    }

    void setPieChartView() {
        getDifference();
        float[] f = {getPercentage(wearTime), getPercentage(totalPendingTime), getPercentage(notWearTime)};
        int[] colorArray = {R.color.light_grey_color, R.color.profile_grey, R.color.redChart};
        pieChartView.setDataPoints(f);
        pieChartView.setCenterColor(R.color.pink);
        pieChartView.setSliceColor(colorArray);
    }

    float getPercentage(String date) {
        String[] split = date.split(":");

        return (Float.parseFloat(split[0]) * 3600 + Float.parseFloat(split[1]) * 60) / 864;
    }


    void getDifference() {
        try {
            Date startDate = simpleDateFormat.parse("00:00");
            Date maxDate = simpleDateFormat.parse("24:00");
            String currentTime = simpleDateFormat.format(Calendar.getInstance().getTime());
            Date endDate = simpleDateFormat.parse(currentTime);
            Date spendTime = simpleDateFormat.parse(wearInTime);
            Date goalTime = simpleDateFormat.parse(mGoalTimeApi);
            Date pastTime = getTimeDifference(startDate, endDate);
            wearTime = getTimeDifferenceString(startDate, spendTime);
            notWearTime = getTimeDifferenceString(spendTime, pastTime);
            totalPendingTime = getTimeDifferenceString(pastTime, maxDate);
            assert goalTime != null;
            if (spendTime.getTime() < goalTime.getTime()) {
                mGoalTime = getTimeDifferenceString(spendTime, goalTime);
                if (mGoalTime.equals(mGoalTimeApi)) {
                    mTextGoalTime.setText(R.string.today_goal_time_completed);
                } else {
                    getDailyGoalValue();
                }
            } else {
                mTextGoalTime.setText(R.string.today_goal_time_completed);
            }


        } catch (ParseException e) {
            Log.e("Error", e.getMessage());
        }
    }

    void getOutSeconds() {
        String[] split = notWearTime.split(":");
        int timeSec = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        outSeconds = timeSec * 60;
        String[] splitWear = wearTime.split(":");
        int timeSecWear = Integer.parseInt(splitWear[0]) * 60 + Integer.parseInt(splitWear[1]);
        seconds = timeSecWear * 60;
        int hours = outSeconds / 3600;
        int minutes = (outSeconds % 3600) / 60;
        int secs = outSeconds % 60;
        String time;

        time = String
                .format(Locale.getDefault(),
                        "%02d:%02d", hours,
                        minutes);

        mTextWearNotReminderTime.setText(R.string.out+ time);
    }

    void getDailyGoalValue() {
        String[] split = mGoalTime.split(":");
        int timeSec = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        int goalSecond = timeSec * 60;
        int hours = goalSecond / 3600;
        int minutes = (goalSecond % 3600) / 60;
        int secs = goalSecond % 60;
        String time;
        if (isTimerOn) {
            if (isBlinkGoal) {
                time = String
                        .format(Locale.getDefault(),
                                "%02d:%02d", hours,
                                minutes);
            } else {
                time = String
                        .format(Locale.getDefault(),
                                "%02d %02d", hours,
                                minutes);
            }
        } else {
            time = String
                    .format(Locale.getDefault(),
                            "%02d:%02d", hours,
                            minutes);
        }
         mTextGoalTime.setText(R.string.today_goal_pending_time + time);
        isBlinkGoal = !isBlinkGoal;
    }


   /* void getGoalSeconds() {
        String[] split = mGoalTime.split(":");
        int timeSec = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        outSeconds = timeSec * 60;
        int hours = outSeconds / 3600;
        int minutes = (outSeconds % 3600) / 60;
        String time;

        time = String
                .format(Locale.getDefault(),
                        "%02d:%02d", hours,
                        minutes);

        mTextGoalTime.setText("Today Goal Time " + time);
    }*/

    Date getTimeDifference(Date startDate, Date endDate) {
        try {
            long difference = endDate.getTime() - startDate.getTime();
            if (difference < 0) {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
            }
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            int sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min)) / (1000);
            Log.i("log_tag", "Hours: " + hours + ", Mins: " + min + ", Secs: " + sec);
            return simpleDateFormat.parse(hours + ":" + min);
        } catch (ParseException e) {
            Log.e("Error", e.getMessage());
        }
        return null;
    }

    String getTimeAfterAdd(String firstTime, String secondTime) {
        int firstSeconds = 0, seconds2 = 0, totalSeconds = 0;
        String[] split = firstTime.split(":");
        int timeSec = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        firstSeconds = timeSec * 60;
        String[] split2 = secondTime.split(":");
        int timeSec2 = Integer.parseInt(split2[0]) * 60 + Integer.parseInt(split2[1]);
        seconds2 = timeSec2 * 60;
        totalSeconds = firstSeconds + seconds2;
        seconds = totalSeconds;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        return String
                .format(Locale.getDefault(),
                        "%02d:%02d", hours,
                        minutes);
    }

    String getTimeDifferenceString(Date startDate, Date endDate) {
        //  try {
        long difference = endDate.getTime() - startDate.getTime();

        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        int sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min)) / (1000);
        Log.i("log_tag", "Hours: " + hours + ", Mins: " + min + ", Secs: " + sec);
        return hours + ":" + min;

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting and binding service");
        }
    /*    Intent i = new Intent(this, TimerService.class);
        i.putExtra("Seconds", seconds);
        startService(i);
        bindService(i, mConnection, 0);*/
       /* Intent timerOut = new Intent(this, OutTimerService.class);
        timerOut.putExtra("Seconds", outSeconds);
        startService(timerOut);
        bindService(timerOut, mConnectionOut, 0);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (isStartResume) {
            updateUIStartRun();
            updateUIStartRunOut();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUIStopRun();
        updateUIStopRunOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUIStopRun();
        updateUIStopRunOut();
    }


    private void updateUIStartRun() {
        mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
    }


    private void updateUIStopRun() {
        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
    }


    private void updateUIStartRunOut() {
        mUpdateOutTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME_OUT);
    }


    private void updateUIStopRunOut() {
        mUpdateOutTimeHandler.removeMessages(MSG_UPDATE_TIME_OUT);
    }


    private void updateUITimer() {
        if (isTimerOn) {
            seconds++;
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;
            String time;
            totalInTime = String
                    .format(Locale.getDefault(),
                            "%02d:%02d", hours,
                            minutes, secs);
            wearInTime = totalInTime;
            if (isBlink) {
                time = String
                        .format(Locale.getDefault(),
                                "%02d:%02d", hours,
                                minutes);
            } else {
                time = String
                        .format(Locale.getDefault(),
                                "%02d %02d", hours,
                                minutes);
            }
            mTextWearReminderTime.setText(time);
            isBlink = !isBlink;
            getDifference();
            setPieChartViewOnClick();
        }
    }

    private void updateUITimerOut() {
        if (!isTimerOn) {
            outSeconds++;
            int hours = outSeconds / 3600;
            int minutes = (outSeconds % 3600) / 60;
            int secs = outSeconds % 60;
            String time;
            totalOutTime = String
                    .format(Locale.getDefault(),
                            "%02d:%02d", hours,
                            minutes);
            if (isBlinkOut) {
                time = String
                        .format(Locale.getDefault(),
                                "%02d:%02d", hours,
                                minutes);
            } else {
                time = String
                        .format(Locale.getDefault(),
                                "%02d %02d", hours,
                                minutes);
            }
            mTextWearNotReminderTime.setText("Out " + time);
            isBlinkOut = !isBlinkOut;

            getDifference();
        }
    }

    void setTime() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time;
        if (isBlink) {
            time = String
                    .format(Locale.getDefault(),
                            "%02d %02d", hours,
                            minutes);
        } else {
            time = String
                    .format(Locale.getDefault(),
                            "%02d:%02d", hours,
                            minutes);
        }
        mTextWearReminderTime.setText(time);
        isBlink = !isBlink;
    }

    @Override
    public void setClicks(int pos, int open) {
        Log.e("Mins", String.valueOf(open));
        alertDialogReminder.dismiss();
        updateAlignerReminder(String.valueOf(open));
    }


    static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<ReminderDetailsActivity> activity;

        UIUpdateHandler(ReminderDetailsActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            if (MSG_UPDATE_TIME == message.what) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "updating time");
                }
                if (activity != null) {
                    activity.get().updateUITimer();
                    if (activity.get().mGoalTime != null && !activity.get().mGoalTime.equals("Today Goal Time Completed")) {
                        activity.get().getDailyGoalValue();
                    }
                    sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
                }
            }
        }
    }


    static class UIUpdateHandlerOut extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<ReminderDetailsActivity> activity;

        UIUpdateHandlerOut(ReminderDetailsActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            if (MSG_UPDATE_TIME_OUT == message.what) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "updating time");
                }
                try {
                    if (activity != null) {
                        activity.get().updateUITimerOut();
                        sendEmptyMessageDelayed(MSG_UPDATE_TIME_OUT, UPDATE_RATE_MS);
                    }
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    void showDialogReminder() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reminder_notification, null);
        dialogBuilder.setView(dialogView);
        RecyclerView mRecycler = dialogView.findViewById(R.id.recyclerReminderTime);
        mRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        List<TimerPushNotificationResult> mListPush = new ArrayList<>();
        String[] myReminderType = getResources().getStringArray(R.array.ReminderType);
        String[] myReminderTime = getResources().getStringArray(R.array.ReminderTime);
        TimerPushNotificationResult timerPushNotificationResult = new TimerPushNotificationResult();
        if (!mReminderResult.getBreakfast().equals("")) {
            timerPushNotificationResult.setName((myReminderType[0]));
            String[] time = mReminderResult.getBreakfast().split(":");
            if (time[0].equals("00")) {
                timerPushNotificationResult.setTime((time[1] + " mins"));
            } else if (time[1].equals("00")) {
                timerPushNotificationResult.setTime((time[0] + " hour"));
            } else {
                timerPushNotificationResult.setTime(time[0] + " hour " + time[1] + " mins");
            }
        } else {
            timerPushNotificationResult.setName((myReminderType[0]));
            timerPushNotificationResult.setTime((myReminderTime[0]));
        }
        mListPush.add(timerPushNotificationResult);
        timerPushNotificationResult = new TimerPushNotificationResult();
        if (!mReminderResult.getLunch().equals("")) {
            timerPushNotificationResult.setName((myReminderType[1]));
            String[] time = mReminderResult.getLunch().split(":");
            if (time[0].equals("00")) {
                timerPushNotificationResult.setTime((time[1] + " mins"));
            } else if (time[1].equals("00")) {
                timerPushNotificationResult.setTime((time[0] + " hour"));
            } else {
                timerPushNotificationResult.setTime(time[0] + " hour " + time[1] + " mins");
            }
        } else {
            timerPushNotificationResult.setName((myReminderType[1]));
            timerPushNotificationResult.setTime((myReminderTime[1]));
        }
        mListPush.add(timerPushNotificationResult);
        timerPushNotificationResult = new TimerPushNotificationResult();
        if (!mReminderResult.getDinner().equals("")) {
            timerPushNotificationResult.setName((myReminderType[2]));
            String[] time = mReminderResult.getDinner().split(":");
            if (time[0].equals("00")) {
                timerPushNotificationResult.setTime((time[1] + " mins"));
            } else if (time[1].equals("00")) {
                timerPushNotificationResult.setTime((time[0] + " hour"));
            } else {
                timerPushNotificationResult.setTime(time[0] + " hour " + time[1] + " mins");
            }
        } else {
            timerPushNotificationResult.setName((myReminderType[2]));
            timerPushNotificationResult.setTime((myReminderTime[2]));
        }
        mListPush.add(timerPushNotificationResult);
        timerPushNotificationResult = new TimerPushNotificationResult();
        if (!mReminderResult.getCleaner().equals("")) {
            timerPushNotificationResult.setName((myReminderType[3]));
            String[] time = mReminderResult.getCleaner().split(":");
            if (time[0].equals("00")) {
                timerPushNotificationResult.setTime((time[1] + " mins"));
            } else if (time[1].equals("00")) {
                timerPushNotificationResult.setTime((time[0] + " hour"));
            } else {
                timerPushNotificationResult.setTime(time[0] + " hour " + time[1] + " mins");
            }
        } else {
            timerPushNotificationResult.setName((myReminderType[3]));
            timerPushNotificationResult.setTime((myReminderTime[3]));
        }
        mListPush.add(timerPushNotificationResult);

        TimerPushNotificationListAdapter mAdapter = new TimerPushNotificationListAdapter(this, mListPush, this);
        mRecycler.setAdapter(mAdapter);
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogReminder = dialogBuilder.create();
        alertDialogReminder.setTitle(R.string.aligner_reminder);
        alertDialogReminder.show();
    }


    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_reminder, null);
        dialogBuilder.setView(dialogView);
        final int[] selectHour = {0};
        final int[] selectMin = {0};

        final LoopView loopView = dialogView.findViewById(R.id.loopMin);
        final LoopView loopHour = dialogView.findViewById(R.id.loopHour);
        loopView.setNotLoop();

        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        List<String> minuteList = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minuteList.add("0" + i + " min");
            } else {
                minuteList.add(i + " min");
            }
        }
        List<String> hourList = new ArrayList<>();
        hourList.add("0 hr");
        hourList.add("1 hr");
        hourList.add("2 hr");
        hourList.add("3 hr");
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {


            }
        });
        loopView.setItems(minuteList);
        loopHour.setItems(hourList);
        dialogBuilder.setNegativeButton("No Reminder", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectMin[0] = loopView.getSelectedItem();
                selectHour[0] = loopHour.getSelectedItem();
                localData.set_hour(selectMin[0]);
                localData.set_min(selectHour[0]);
                NotificationScheduler.setReminder(ReminderDetailsActivity.this, AlarmReceiver.class, localData.get_hour(), localData.get_min());
                dialog.dismiss();
            }
        });
        AlertDialog alertDialogPatient = dialogBuilder.create();
        alertDialogPatient.setTitle(R.string.remind_me_to_wear);
        alertDialogPatient.show();
    }

    void shareImage() {
        mLayoutChartScreen.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mLayoutChartScreen.getDrawingCache());
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "FitsSmile", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }

    void updateAlignerReminder(String mins) {
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", fitsReminderId);
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("mins", mins);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingResponse> mService = mApiService.updateAlignerReminder(jsonObject);
        mService.enqueue(new Callback<SettingResponse>() {
            @Override
            public void onResponse(Call<SettingResponse> call, Response<SettingResponse> response) {
                Utils.closeProgressDialog();
                SettingResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    Toast.makeText(actCon, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(ReminderDetailsActivity.this);
                }

            }

            @Override
            public void onFailure(Call<SettingResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==200){
            getReminderDetails();
        }
    }
}