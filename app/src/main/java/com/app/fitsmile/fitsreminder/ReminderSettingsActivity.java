package com.app.fitsmile.fitsreminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.adapter.AlarmSettingsAdapter;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.trayMinder.TimerPushNotificationResult;
import com.app.fitsmile.response.trayMinder.TrayMinderDataResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderSettingsActivity extends BaseActivity implements RecyclerItemClickListener {
    String fitsReminderId;
    AlarmSettingsAdapter mAdapter;
    RecyclerView mRecyclerAlarmSetting;
    private String strAlarmTime;
    TrayMinderResult mTrayMinderResult = new TrayMinderResult();
    List<TimerPushNotificationResult> mListPush = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_settings);
        LocaleManager.setLocale(this);
        fitsReminderId = getIntent().getStringExtra(AppConstants.TREATMENT_ID);
        setUpToolBar();
        initUI();
        getReminderDetails();
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.alarm_settings);
    }

    void initUI() {
        mRecyclerAlarmSetting = findViewById(R.id.recyclerAlarmSettings);

    }

    void setAdapter() {
        mAdapter = new AlarmSettingsAdapter(this, mListPush, this);
        mRecyclerAlarmSetting.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAlarmSetting.setAdapter(mAdapter);
    }

    void setData() {
        mListPush.clear();
        String[] myReminderType = getResources().getStringArray(R.array.ReminderType);
        String[] myReminderTime = getResources().getStringArray(R.array.ReminderTime);
        TimerPushNotificationResult timerPushNotificationResult = new TimerPushNotificationResult();
        if (!mTrayMinderResult.getBreakfast().equals("")) {
            timerPushNotificationResult.setName((myReminderType[0]));
            String[] time = mTrayMinderResult.getBreakfast().split(":");
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
        if (!mTrayMinderResult.getLunch().equals("")) {
            timerPushNotificationResult.setName((myReminderType[1]));
            String[] time = mTrayMinderResult.getLunch().split(":");
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
        if (!mTrayMinderResult.getDinner().equals("")) {
            timerPushNotificationResult.setName((myReminderType[2]));
            String[] time = mTrayMinderResult.getDinner().split(":");
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
        if (!mTrayMinderResult.getCleaner().equals("")) {
            timerPushNotificationResult.setName((myReminderType[3]));
            String[] time = mTrayMinderResult.getCleaner().split(":");
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
        setAdapter();
    }


   /* public void showPopupSelectSelfie() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReminderSettingsActivity.this);
        alertDialog.setTitle("Remind me to take my next teeth selfie in");
        List<String> weeks = new ArrayList<String>();
        weeks.add("1 week");
        weeks.add("2 weeks");
        weeks.add("3 weeks");
        weeks.add("4 weeks");
        weeks.add("5 weeks");
        final String[] strWeekName = new String[1];
        final CharSequence[] items = weeks.toArray(new String[weeks.size()]);
        alertDialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strWeekName[0] = items[which].toString();

            }
        });
        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }*/

    public void showPopupEditTime(int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReminderSettingsActivity.this);
        alertDialog.setTitle("Edit Alarm Time");
        List<String> weeks = new ArrayList<String>();
        weeks.add("5 mins");
        weeks.add("10 mins");
        weeks.add("15 mins");
        weeks.add("30 mins");
        weeks.add("45 mins");
        weeks.add("1 hour");
        weeks.add("1 hour 15 mins");
        weeks.add("1 hour 30 mins");
        weeks.add("2 hour");
        weeks.add("3 hour");
        final CharSequence[] items = weeks.toArray(new String[weeks.size()]);
        alertDialog.setSingleChoiceItems(items, 3, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strAlarmTime = items[which].toString();

            }
        });
        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dailyApiTime = "";
                if (strAlarmTime.contains("hour")) {
                    String[] dailyTime = strAlarmTime.split(" ");
                    if (Integer.parseInt(dailyTime[0]) < 10) {
                        dailyApiTime = "0" + dailyTime[0];
                    } else {
                        dailyApiTime = dailyTime[0];
                    }

                    if (dailyTime.length > 2) {
                        dailyApiTime = dailyApiTime + ":" + dailyTime[2];
                    } else {
                        dailyApiTime = dailyApiTime + ":" + "00";
                    }
                } else {
                    String[] dailyTime = strAlarmTime.split(" ");
                    if (Integer.parseInt(dailyTime[0]) < 10) {
                        dailyApiTime = "00:0" + dailyTime[0];
                    } else {
                        dailyApiTime = "00:" + dailyTime[0];
                    }
                }
                if (pos == 0) {
                    updateTimeValue(dailyApiTime, mTrayMinderResult.getLunch(), mTrayMinderResult.getDinner(), mTrayMinderResult.getCleaner());
                }
                if (pos == 1) {
                    updateTimeValue(mTrayMinderResult.getBreakfast(), dailyApiTime, mTrayMinderResult.getDinner(), mTrayMinderResult.getCleaner());
                }
                if (pos == 2) {
                    updateTimeValue(mTrayMinderResult.getBreakfast(), mTrayMinderResult.getLunch(), dailyApiTime, mTrayMinderResult.getCleaner());
                }
                if (pos == 3) {
                    updateTimeValue(mTrayMinderResult.getBreakfast(), mTrayMinderResult.getLunch(), mTrayMinderResult.getDinner(), dailyApiTime);
                }
                Log.e("Time", dailyApiTime);


            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @SuppressLint("SetTextI18n")
    void showPopupEditName() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_edit_name, null);
        dialogBuilder.setView(dialogView);
        EditText editAlarmName = dialogView.findViewById(R.id.et_alarm_name);
        editAlarmName.setText("BreakFast");
        dialogBuilder.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setPositiveButton(R.string.capital_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialogPatient = dialogBuilder.create();
        alertDialogPatient.setTitle(R.string.edit_alarm_name);
        alertDialogPatient.show();
    }

    @Override
    public void setClicks(int pos, int open) {
        showPopupEditTime(pos);
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
                if (mResponse.getStatus() == 1) {
                    mTrayMinderResult = mResponse.getData();
                    setData();
                } else if (mResponse.getStatus() == 401) {
                   Utils.showSessionAlert(ReminderSettingsActivity.this);
                }
            }

            @Override
            public void onFailure(Call<TrayMinderDataResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }


    void updateTimeValue(String breakfastTime, String lunchTime, String dinnerTime, String clearTime) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", fitsReminderId);
        jsonObject.addProperty("breakfast", breakfastTime);
        jsonObject.addProperty("lunch", lunchTime);
        jsonObject.addProperty("dinner", dinnerTime);
        jsonObject.addProperty("cleaner", clearTime);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAlarmData(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().getStatus().equals("1")) {
                    getReminderDetails();
                } else if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(ReminderSettingsActivity.this);
                }
            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

}