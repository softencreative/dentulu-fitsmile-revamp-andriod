package com.app.fitsmile.home;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.appointment.TrayMinderAppointmentAdapter;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.book.SearchDentistList;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.firebase_chat.ChatHistoryFirebaseActivity;
import com.app.fitsmile.fitsreminder.AdjustReminderActivity;
import com.app.fitsmile.fitsreminder.AlignerSmileProgressActivity;
import com.app.fitsmile.fitsreminder.FitsReminderActivity;
import com.app.fitsmile.fitsreminder.LocalData;
import com.app.fitsmile.fitsreminder.PieChartView;
import com.app.fitsmile.fitsreminder.ReminderProgressActivity;
import com.app.fitsmile.fitsreminder.adapter.TimerPushNotificationListAdapter;
import com.app.fitsmile.fitsreminder.adapter.TrayMinderDefaultAdapter;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.rewards.SettingResponse;
import com.app.fitsmile.response.trayMinder.TimerPushNotificationResult;
import com.app.fitsmile.response.trayMinder.TrayMinderDataResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements RecyclerItemClickListener {

    LinearLayout mLayoutOverview, mLayoutTimeline, mLayoutChat;
    LinearLayout mLayoutSmileProgress, mLayoutFindDentist;
    private Context context;
    private boolean loading = true;
    private TextView tvNodata;
    PieChartView pieChartView;
    private RelativeLayout mTimerLayout;
    private TextView mTextWearReminderTime;
    private String wearInTime;
    private TextView mTextWear;
    private TextView mTextWearNotReminderTime;
    private int seconds = 0;
    private boolean isBlinkGoal = false;
    private int outSeconds = 0;

    boolean isFABOpen = false;
    private boolean isBlink = false;
    private boolean isBlinkOut = false;
    private static final String TAG = FitsReminderActivity.class.getSimpleName();
    LocalData localData;

    // Handler to update the UI every second when the timer is running
    private final Handler mUpdateTimeHandler = new UIUpdateHandler(HomeFragment.this);
    private final Handler mUpdateOutTimeHandler = new UIUpdateHandlerOut(HomeFragment.this);

    // Message type for the handler
    private final static int MSG_UPDATE_TIME = 0;
    private final static int MSG_UPDATE_TIME_OUT = 0;

    private boolean running;

    private boolean wasRunning;
    String totalInTime, totalOutTime;
    String inTime, outTime;

    boolean isTimerOn = false;
    boolean isOutTimerOn = false;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    String wearTime, notWearTime, totalPendingTime;
    int fitsReminderId;
    TextView mTextHeader;
    TextView mTextBottomDays,textNameReminder;
    FloatingActionButton fabShowDialog, fabSettings, fabShare, fabReminder, fabChangeDefault;
    LinearLayout fabLayoutSettings, fabLayoutShare, fabLayoutReminder, fabLayoutChangeDefault;
    View fabBGLayout;
    RelativeLayout mLayoutChartScreen;
    RelativeLayout mLayoutPlay;
    TextView mTextPlay;
    String remindTime;
    CircleImageView mCirclePlay;
    private AlertDialog alertDialogReminder;
    private AlertDialog alertDialogDefaultReminder;
    RelativeLayout mLayoutReminder;
    private TextView mTextGoalTime;
    String mGoalTime, mGoalTimeApi;
    TrayMinderResult mReminderResult = new TrayMinderResult();
    String mAlignNo = "";


    TrayMinderAppointmentAdapter mAdapter;
    RecyclerView mRecyclerAppointment;
    List<TrayMinderResult> appointmentList;
    private boolean isAppointmentList = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LocaleManager.setLocale(getActivity());
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        LocaleManager.setLocale(context);
        initUI(view);
        setClicks();
        return view;
    }

    void initUI(View view) {
        appointmentList = new ArrayList<>();
        textNameReminder=view.findViewById(R.id.textNameReminder);
        mLayoutOverview = view.findViewById(R.id.layoutOverview);
        mLayoutTimeline = view.findViewById(R.id.layoutTimeline);
        mLayoutSmileProgress = view.findViewById(R.id.layoutSmileProgress);
        mLayoutChat = view.findViewById(R.id.layoutChat);
        mLayoutFindDentist = view.findViewById(R.id.layoutFindDentist);
        tvNodata = view.findViewById(R.id.nodata);
        pieChartView = view.findViewById(R.id.pieChart);
        mTimerLayout = (RelativeLayout) view.findViewById(R.id.layoutTimer);
        mTextWearReminderTime = (TextView) view.findViewById(R.id.textWearReminderTime);
        mTextWear = (TextView) view.findViewById(R.id.textWearIndicator);
        mTextWearNotReminderTime = (TextView) view.findViewById(R.id.textWearNotReminderTime);
        mTextHeader = (TextView) view.findViewById(R.id.textHeaderTrayMinder);
        mTextBottomDays = (TextView) view.findViewById(R.id.textBottomPieChart);
        fabLayoutReminder = view.findViewById(R.id.fabLayout1);
        fabLayoutShare = view.findViewById(R.id.fabLayout2);
        fabLayoutSettings = view.findViewById(R.id.fabLayout3);
        fabLayoutChangeDefault = view.findViewById(R.id.fabLayoutChangeDefault);
        fabShowDialog = view.findViewById(R.id.fab);
        fabReminder = view.findViewById(R.id.fab1);
        fabShare = view.findViewById(R.id.fab2);
        fabSettings = view.findViewById(R.id.fab3);
        fabChangeDefault = view.findViewById(R.id.fabChangeDefault);
        mLayoutChartScreen = view.findViewById(R.id.layoutChartScreen);
        mLayoutPlay = view.findViewById(R.id.layoutPlay);
        mTextPlay = view.findViewById(R.id.textPlay);
        mCirclePlay = view.findViewById(R.id.circlePlay);
        mLayoutReminder = view.findViewById(R.id.layoutReminder);
        mTextGoalTime = view.findViewById(R.id.textTodayGoal);
        fabBGLayout = view.findViewById(R.id.fabBGLayout);

        //set appointment list
        mRecyclerAppointment = view.findViewById(R.id.recycler_tray_minder);
        mAdapter = new TrayMinderAppointmentAdapter(context, appointmentList, this);
        mRecyclerAppointment.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerAppointment.setAdapter(mAdapter);


        mLayoutPlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!isTimerOn) {
                    inTime = simpleDateFormat.format(Calendar.getInstance().getTime());
                    setPieChartViewOnClick();
                    updateUIStartRun();
                    mTextWear.setText(R.string.wearing);
                    mTextPlay.setText(R.string.pause);
                    textNameReminder.setVisibility(View.GONE);
                    mTextPlay.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mCirclePlay.setBorderColor(getResources().getColor(R.color.colorPrimary));
                    mTextWear.setTextColor(getResources().getColor(R.color.colorPrimary));
                    screenVibrate();
                    isTimerOn = true;
                    setTextWithoutBlink(outSeconds, mTextWearNotReminderTime, 1);
                    updateReminderTiming("in");
                } else {
                    textNameReminder.setVisibility(View.VISIBLE);
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
                    updateReminderTiming("out");
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
        mLayoutOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!appPreference.getFitsReminderId().equals("")) {
                    Intent intent = new Intent(getActivity(), AdjustReminderActivity.class);
                    intent.putExtra(AppConstants.TREATMENT_ID, appPreference.getFitsReminderId());
                    intent.putExtra(AppConstants.OPEN_FROM, 1);
                    startActivity(intent);
                } else {
                    Utils.showToast(getActivity(), getResources().getString(R.string.no_fits_reminder_yet));
                }
            }
        });

        mLayoutTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!appPreference.getFitsReminderId().equals("")) {
                    Intent intent = new Intent(getActivity(), ReminderProgressActivity.class);
                    intent.putExtra(AppConstants.TREATMENT_ID, appPreference.getFitsReminderId());
                    intent.putExtra(AppConstants.ALIGNER_NO, mAlignNo);
                    intent.putExtra(AppConstants.OPEN_FROM, 1);
                    startActivity(intent);
                } else {
                    Utils.showToast(getActivity(), getResources().getString(R.string.no_fits_reminder_yet));
                }
            }
        });

        mLayoutSmileProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!appPreference.getFitsReminderId().equals("")) {
                    Intent intent = new Intent(getActivity(), AlignerSmileProgressActivity.class);
                    intent.putExtra(AppConstants.TREATMENT_ID, appPreference.getFitsReminderId());
                    intent.putExtra(AppConstants.ALIGNER_NO, mAlignNo);
                    startActivity(intent);
                } else {
                    Utils.showToast(getActivity(), getResources().getString(R.string.no_fits_reminder_yet));
                }
            }
        });

        mLayoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatHistoryFirebaseActivity.class);
                startActivity(intent);
            }
        });

        mLayoutFindDentist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchDentistList.class);
                intent.putExtra("isFromBookAppointment",false);
                startActivity(intent);
            }
        });
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
                Intent intent = new Intent(getActivity(), AdjustReminderActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, appPreference.getFitsReminderId());
                startActivity(intent);
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
                closeFABMenu();
                Intent intent = new Intent(getActivity(), AdjustReminderActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, String.valueOf(fitsReminderId));
                intent.putExtra("reminder_time", remindTime);
                startActivity(intent);

            }
        });

        fabLayoutChangeDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReminderList();
                closeFABMenu();

            }
        });
        fabChangeDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReminderList();
                closeFABMenu();

            }
        });

    }


    private void showFABMenu() {
        isFABOpen = true;
        fabLayoutChangeDefault.setVisibility(View.GONE);
        if (isTimerOn) {
            fabLayoutReminder.setVisibility(View.GONE);
            fabLayoutChangeDefault.animate().translationY(-getResources().getDimension(R.dimen._80sdp));
        } else {
            fabLayoutReminder.setVisibility(View.VISIBLE);
            fabLayoutReminder.animate().translationY(-getResources().getDimension(R.dimen._80sdp));
            fabLayoutChangeDefault.animate().translationY(-getResources().getDimension(R.dimen._200sdp));
        }
        fabLayoutSettings.setVisibility(View.GONE);
        fabLayoutShare.setVisibility(View.VISIBLE);
        fabShowDialog.animate().rotationBy(180);
        fabLayoutSettings.animate().translationY(-getResources().getDimension(R.dimen._50sdp));
        fabLayoutShare.animate().translationY(-getResources().getDimension(R.dimen._30sdp));
        fabBGLayout.setVisibility(View.VISIBLE);

    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fabShowDialog.animate().rotation(360);
        fabLayoutChangeDefault.animate().translationY(0);
        fabLayoutShare.animate().translationY(0);
        fabLayoutReminder.animate().translationY(0);
        fabLayoutSettings.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayoutChangeDefault.setVisibility(View.GONE);
                    fabLayoutReminder.setVisibility(View.GONE);
                    fabLayoutShare.setVisibility(View.GONE);
                    fabLayoutSettings.setVisibility(View.GONE);
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
        Utils.openProgressDialog(getActivity());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", appPreference.getFitsReminderId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderDataResponse> mService = mApiService.fetchFitsReminderDetails(jsonObject);
        mService.enqueue(new Callback<TrayMinderDataResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<TrayMinderDataResponse> call, Response<TrayMinderDataResponse> response) {
                Utils.closeProgressDialog();
             //   mLayoutChartScreen.setVisibility(View.VISIBLE);
                tvNodata.setVisibility(View.GONE);
                mRecyclerAppointment.setVisibility(View.GONE);
                fabShowDialog.setVisibility(View.VISIBLE);
                TrayMinderDataResponse mResponse = response.body();
                assert mResponse != null;
                TrayMinderResult mBody = mResponse.getData();
                mReminderResult = mResponse.getData();
                if (mResponse.getStatus() == 1) {

                    if (mBody.getStatus().equalsIgnoreCase("1")) {
                        mLayoutChartScreen.setVisibility(View.VISIBLE);
//                        Toast.makeText(context, "status 0", Toast.LENGTH_LONG).show();
//                    } else {
                        remindTime = mBody.getRemind_time();
                        mTextBottomDays.setText(mBody.getRemaining_days() +" "+ getResources().getString(R.string.days_remaining));
                        mGoalTimeApi = mBody.getGoal_time();
                        if (mBody.getToday_timming_log() != null) {
                            if (Integer.parseInt(mBody.getToday_timming_log().getDay()) < 10) {
                                mTextHeader.setText(mBody.getToday_timming_log().getDay() + " "+ getResources().getString(R.string.days_of_aligner)+ " #" + mBody.getToday_timming_log().getAligner_no());
                            } else {
                                mTextHeader.setText(mBody.getToday_timming_log().getDay()  + " "+ getResources().getString(R.string.days_of_aligner) +" #" + mBody.getToday_timming_log().getAligner_no());
                            }
                            mAlignNo = mBody.getToday_timming_log().getAligner_no();
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
                                    textNameReminder.setVisibility(View.GONE);
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
                                    textNameReminder.setVisibility(View.GONE);
                                    mTextPlay.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    mCirclePlay.setBorderColor(getResources().getColor(R.color.colorPrimary));
                                    mTextWear.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    isTimerOn = true;
                                    updateUIStartRun();
                                }
                            } else
                                {
                                if (totalInTime == null) {
                                    totalInTime = "00:00";
                                }
                                wearInTime = totalInTime;
                                mTextWearReminderTime.setText(totalInTime);
                                setPieChartView();
                                updateUIStartRunOut();
                            }
                            getOutSeconds();
                            setTime();
                        }
                        else {
                            mLayoutChartScreen.setVisibility(View.GONE);
                            tvNodata.setVisibility(View.VISIBLE);
                            fabShowDialog.setVisibility(View.GONE);
                            mRecyclerAppointment.setVisibility(View.GONE);
                        }

                    } else if(mBody.getStatus().equalsIgnoreCase("0") && mBody.getAssigned_id().equalsIgnoreCase(appPreference.getUserId())){
                        mLayoutChartScreen.setVisibility(View.GONE);
                        tvNodata.setVisibility(View.VISIBLE);
                        fabShowDialog.setVisibility(View.GONE);
                        mRecyclerAppointment.setVisibility(View.GONE);
                        getAppointmentList();
                    }
                    else {
                        mLayoutChartScreen.setVisibility(View.GONE);
                        tvNodata.setVisibility(View.VISIBLE);
                        fabShowDialog.setVisibility(View.GONE);
                        mRecyclerAppointment.setVisibility(View.GONE);
                    }
                } else if (mResponse.getStatus() == 401) {
                    Utils.showSessionAlert(getActivity());
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
        jsonObject.addProperty("id", appPreference.getFitsReminderId());
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
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
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

                } else if (mResponse.getStatus() == 401) {
                    Utils.showSessionAlert(getActivity());
                }

            }

            @Override
            public void onFailure(Call<TrayMinderDataResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    void screenVibrate() {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
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
        String textOut = getResources().getString(R.string.out);
//        if (view == 1) {
//            textView.setText(textOut +": "+ time);
//        } else
//            textView.setText(time);
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

    void getDailyGoalValue() {
        if (mGoalTime != null) {
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
            mTextGoalTime.setText(getResources().getString(R.string.today_goal_pending_time) +" "+ time);
            isBlinkGoal = !isBlinkGoal;
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
        String textOut = getResources().getString(R.string.out);
       // mTextWearNotReminderTime.setText(textOut +": "+ time);
    }

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
    public void onStart() {
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
    public void onResume() {
        super.onResume();
        if (!appPreference.getFitsReminderId().equals("")) {
            getReminderDetails();
        } else {
            mLayoutChartScreen.setVisibility(View.GONE);
            tvNodata.setVisibility(View.VISIBLE);
            fabShowDialog.setVisibility(View.GONE);
            mRecyclerAppointment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        updateUIStopRun();
        updateUIStopRunOut();
    }

    @Override
    public void onPause() {
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
                            minutes, secs);
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
            String textOut = getResources().getString(R.string.out);
            //mTextWearNotReminderTime.setText(textOut +": "+ time);
            isBlinkOut = !isBlinkOut;

            getDifference();
        }
    }

    void setTime() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time;
        if (isTimerOn) {
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
        if (open == 3) {
            if (isAppointmentList) {
                updateReminderStatus(pos, appointmentList.get(pos).getId(), "1");
            } else {
                updateDefaultReminder(String.valueOf(pos));
                alertDialogDefaultReminder.dismiss();
            }
        } else if (open == 4) {
            if (isAppointmentList) {
                updateReminderStatus(pos, appointmentList.get(pos).getId(), "2");
            }
        } else {
            Log.e("Mins", String.valueOf(open));
            alertDialogReminder.dismiss();
            updateAlignerReminder(String.valueOf(open));
        }

    }

    void updateReminderStatus(int pos, String id, String status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("status", status);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(getActivity());
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAppointmentStatus(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    // mTrayMinderList.get(pos).setStatus(status);
                   // && appPreference.getFitsReminderId().equals("")
                    if (status.equals("1") ) {
                        appPreference.setFitsReminderId(id);
                    }
                    isAppointmentList = false;
                    mRecyclerAppointment.setVisibility(View.GONE);
                    getReminderDetails();


                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(getActivity());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }


    void updateAlignerReminder(String mins) {
        Utils.openProgressDialog(getActivity());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", appPreference.getFitsReminderId());
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("mins", mins);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
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
                    Toast.makeText(getActivity(), mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(getActivity());
                }

            }

            @Override
            public void onFailure(Call<SettingResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<HomeFragment> activity;

        UIUpdateHandler(HomeFragment activity) {
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
                    activity.get().getDailyGoalValue();
                    sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
                }
            }
        }
    }


    static class UIUpdateHandlerOut extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<HomeFragment> activity;

        UIUpdateHandlerOut(HomeFragment activity) {
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


    void showDialogReminder() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reminder_notification, null);
        dialogBuilder.setView(dialogView);
        RecyclerView mRecycler = dialogView.findViewById(R.id.recyclerReminderTime);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
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
                timerPushNotificationResult.setTime((time[0] + " "+getResources().getString(R.string.hour)));
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
                timerPushNotificationResult.setTime((time[0] + " "+getResources().getString(R.string.hour)));
            } else {
                timerPushNotificationResult.setTime(time[0] + " "+getResources().getString(R.string.hour) +" " + time[1] + " mins");
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
                timerPushNotificationResult.setTime((time[0] + " "+getResources().getString(R.string.hour)));
            } else {
                timerPushNotificationResult.setTime(time[0] + " "+getResources().getString(R.string.hour) + " " + time[1] + " mins");
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
                String displayhour="";
                if (time[1].equalsIgnoreCase("01")){
                    displayhour=getResources().getString(R.string.hour);
                }
                else {
                    displayhour=getResources().getString(R.string.hours);
                }

                timerPushNotificationResult.setTime((time[0] + " "+displayhour));
            } else {
                timerPushNotificationResult.setTime(time[0] + " "+getResources().getString(R.string.hour) + " " + time[1] + " mins");
            }
        } else {
            timerPushNotificationResult.setName((myReminderType[3]));
            timerPushNotificationResult.setTime((myReminderTime[3]));
        }
        mListPush.add(timerPushNotificationResult);
        TimerPushNotificationListAdapter mAdapter = new TimerPushNotificationListAdapter(getActivity(), mListPush, this);
        mRecycler.setAdapter(mAdapter);
        dialogBuilder.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogReminder = dialogBuilder.create();
        alertDialogReminder.setTitle(R.string.aligner_reminder);
        alertDialogReminder.show();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    void shareImage() {
        mLayoutChartScreen.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mLayoutChartScreen.getDrawingCache());
        String pathofBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "FitsSmile", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }


    void showPopupSetDefault(List<TrayMinderResult> mListReminder) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_patient, null);
        dialogBuilder.setView(dialogView);
        RecyclerView mRecycler = dialogView.findViewById(R.id.recyclerPatient);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        TrayMinderDefaultAdapter mAdapter = new TrayMinderDefaultAdapter(getActivity(), mListReminder, this);
        mRecycler.setAdapter(mAdapter);
        dialogBuilder.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogDefaultReminder = dialogBuilder.create();
        alertDialogDefaultReminder.setTitle(R.string.change_default_aligner);
        alertDialogDefaultReminder.show();
    }

    void getAppointmentList() {
        if (appointmentList.size() > 0) {
            appointmentList.clear();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        // Utils.openProgressDialog(context);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderResponse> mService = mApiService.fetchFitsReminder(jsonObject);
        mService.enqueue(new Callback<TrayMinderResponse>() {
            @Override
            public void onResponse(Call<TrayMinderResponse> call, Response<TrayMinderResponse> response) {
                // Utils.closeProgressDialog();
                TrayMinderResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus() == 1) {
                    if (mResponse.getData().size() > 0) {
                        mLayoutChartScreen.setVisibility(View.GONE);
                        fabShowDialog.setVisibility(View.GONE);
                        tvNodata.setVisibility(View.GONE);
                        mRecyclerAppointment.setVisibility(View.VISIBLE);
                        appointmentList.addAll(mResponse.getData());
                        isAppointmentList = true;
                    } else {
                        tvNodata.setVisibility(View.VISIBLE);
                        mLayoutChartScreen.setVisibility(View.GONE);
                        fabShowDialog.setVisibility(View.GONE);
                        mRecyclerAppointment.setVisibility(View.GONE);
                        isAppointmentList = false;
                    }
                } else if (mResponse.getStatus() == 401) {
                    Utils.showSessionAlert(getActivity());
                }

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<TrayMinderResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }


    void getReminderList() {
        List<TrayMinderResult> mTrayMinderList = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(context);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderResponse> mService = mApiService.fetchFitsReminder(jsonObject);
        mService.enqueue(new Callback<TrayMinderResponse>() {
            @Override
            public void onResponse(Call<TrayMinderResponse> call, Response<TrayMinderResponse> response) {
                Utils.closeProgressDialog();
                TrayMinderResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus() == 1) {
                    if (mResponse.getData().size() > 0) {
                        for (int i = 0; i < mResponse.getData().size(); i++) {
                            if (!mResponse.getData().get(i).getId().equals(appPreference.getFitsReminderId())) {
                                mTrayMinderList.add(mResponse.getData().get(i));

                            }
                        }
                        if (mTrayMinderList.size() == 0) {
                            Utils.showToast(getActivity(), getResources().getString(R.string.no_reminder_change_default));
                        } else {
                            showPopupSetDefault(mTrayMinderList);
                        }
                    } else {
                        tvNodata.setVisibility(View.VISIBLE);
                    }
                } else if (mResponse.getStatus() == 401) {
                    Utils.showSessionAlert(getActivity());
                }

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<TrayMinderResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }


    void updateDefaultReminder(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(context);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateDefaultAppointment(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    appPreference.setFitsReminderId(id);
                    // Utils.showToast(getActivity(),mResponse.getMessage());
                    getReminderDetails();
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(getActivity());
                }


            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

}
