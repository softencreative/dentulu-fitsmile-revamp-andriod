package com.app.fitsmile.fitsreminder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderDataResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdjustReminderActivity extends BaseActivity {
    ConstraintLayout mLayoutTotalAligner, mLayoutSwitchAligner, mLayoutAlarmSettings;
    String strTotalAligners = "";
    int checkedDays = 0;
    TextView mTextTotalAligner;
    TrayMinderResult mTrayMinderResult = new TrayMinderResult();
    TextView mTextCurrentAligner, mTextEachAlignerDays, mTextDailyGoal, mTextSwitchAligner, mTextProvider;
    TextView mTextDaysLeft;
    ConstraintLayout mLayoutWearEachAligner, mLayoutDailyGoal, mLayoutChangeAligner, mLayoutProgress;
    boolean isChange = false;
    private String strDaysCurrentGoal = "";
    int goalCheck = -1;
    TextView mTextUserName;
    CircleImageView mUserImage;
    String mFitsSmileId;
    TextView mTextTotalWear, mTextCompletePercentage;
    ProgressBar mProgressBar;
    Boolean isAddImage=false;
    String lastAlignerNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_reminder);
        LocaleManager.setLocale(this);
        initUI();
        //setClicks();
        getReminderDetails();
        setUpToolBar();
    }
    private WebChromeClient getChromeClient() {
        final ProgressDialog progressDialog = new ProgressDialog(AdjustReminderActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        };
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.overview);
    }

    void initUI() {
        mLayoutTotalAligner = findViewById(R.id.layoutTotalAligner);
        mLayoutSwitchAligner = findViewById(R.id.layoutChangeAligner);
        mLayoutWearEachAligner = findViewById(R.id.layoutWearEachAligner);
        mLayoutChangeAligner = findViewById(R.id.layoutCurrentAligner);
        mLayoutDailyGoal = findViewById(R.id.layoutDailyGoal);
        mTextTotalAligner = findViewById(R.id.textTotalAligner);
        mTextCurrentAligner = findViewById(R.id.textCurrentAligner);
        mTextDailyGoal = findViewById(R.id.textDailyGoal);
        mTextEachAlignerDays = findViewById(R.id.textWearEachAlignerDays);
        mTextSwitchAligner = findViewById(R.id.textSwitchAlignerTime);
        mTextDaysLeft = findViewById(R.id.textDaysLeft);
        mTextProvider = findViewById(R.id.textProviderName);
        mTextUserName = findViewById(R.id.textPatientName);
        mUserImage = findViewById(R.id.imagePatient);
        mLayoutAlarmSettings = findViewById(R.id.layoutAlarmSettings);
        mFitsSmileId = getIntent().getStringExtra(AppConstants.TREATMENT_ID);
        mTextTotalWear = findViewById(R.id.textPatientWear);
        mTextCompletePercentage = findViewById(R.id.textComplete);
        mLayoutProgress = findViewById(R.id.layoutReminderProgress);
        mProgressBar = findViewById(R.id.customProgress);
        ScrollView sv = findViewById(R.id.scrollView);
        sv.post(new Runnable() {
            public void run() {
                sv.fullScroll(sv.FOCUS_UP);
            }
        });
        //https://player.vimeo.com/video/46926279
        //https://www.youtube.com/embed/yAoLSRbwxL8

    }

    public String getHTML(String videoId) {
        String html = "<iframe class=\"youtube-player\" " + "style=\"border: 0; width: 100%; height: 100%;"
                + "padding:0px; margin:0px\" " + "id=\"ytplayer\" type=\"text/html\" "
                + "src=\"http://www.youtube.com/embed/" + videoId
                + "?&theme=dark&autohide=2&modestbranding=1&showinfo=0&autoplay=1\fs=0\" frameborder=\"10\" "
                + "allowfullscreen autobuffer " + "controls onclick=\"this.play()\">\n" + "</iframe>\n";
        return html;
    }

    public String getVimeoHTML(String videoId) {
        String html = "<iframe class=\"youtube-player\" " + "style=\"border: 0; width: 100%; height: 100%;"
                + "padding:0px; margin:0px\" " + "id=\"ytplayer\" type=\"text/html\" "
                + "src=\"https://player.vimeo.com/video/" + videoId
                + "?&theme=dark&autohide=2&modestbranding=1&showinfo=0&autoplay=1\fs=0\" frameborder=\"10\" "
                + "allowfullscreen autobuffer " + "controls onclick=\"this.play()\">\n" + "</iframe>\n";
        return html;
    }
    void setVideoData(String url,String type) {
        //String frameVideo = "<html><body> <br><iframe width=\"auto\" height=\"auto\"  + \"src=\\\"http://www.youtube.com/embed/\" + videoId frameborder=\"0\" allowfullscreen></iframe></body></html>";

        WebView displayYoutubeVideo = (WebView) findViewById(R.id.mWebView);
        displayYoutubeVideo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = displayYoutubeVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        displayYoutubeVideo.setWebChromeClient(getChromeClient());
        if (type.equals("youtube")) {
            displayYoutubeVideo.loadData(getHTML(url), "text/html", "utf-8");
        }else {
            displayYoutubeVideo.loadData(getVimeoHTML(url), "text/html", "utf-8");
        }
    }

    void setClicks() {
        mLayoutTotalAligner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alignerNumberPopup();
            }
        });
        mLayoutSwitchAligner.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        mLayoutWearEachAligner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdjustReminderActivity.this, AdjustAlignerDaysActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, mTrayMinderResult.getId());
                intent.putExtra(AppConstants.ALIGNER_NO, mTrayMinderResult.getToday_timming_log().getAligner_no());
                intent.putExtra(AppConstants.ALIGNER_DAY, mTrayMinderResult.getToday_timming_log().getDay());
                startActivity(intent);
            }
        });
        mLayoutDailyGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alignGoalPopup(goalCheck);

            }
        });
        mLayoutChangeAligner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTrayMinderResult.getToday_timming_log().getAligner_no().equals(mTrayMinderResult.getNo_of_aligners())) {
                    showPopUpAligner();
                }

            }
        });

        mLayoutAlarmSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrayMinderResult != null) {
                    Intent intent = new Intent(AdjustReminderActivity.this, ReminderSettingsActivity.class);
                    intent.putExtra(AppConstants.TREATMENT_ID, mTrayMinderResult.getId());
                    startActivity(intent);
                }
            }
        });
        mLayoutProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrayMinderResult != null) {
                    Intent intent = new Intent(AdjustReminderActivity.this, ReminderProgressActivity.class);
                    intent.putExtra(AppConstants.TREATMENT_ID, mTrayMinderResult.getId());
                    intent.putExtra(AppConstants.ALIGNER_NO, mTextCurrentAligner.getText().toString());
                    startActivity(intent);
                }
            }
        });

    }

    void updateReminderTime(String time) {
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", mTrayMinderResult.getId());
        jsonObject.addProperty("remind_time", time);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateRemindTime(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse settingsResponse = response.body();
                assert settingsResponse != null;
                if (settingsResponse.getStatus().equals("1")) {
                    isChange = true;
                    Toast.makeText(AdjustReminderActivity.this, settingsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (settingsResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(AdjustReminderActivity.this);
                }
            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }

        });
    }

    void updateDailyGoal(String dailyGoal) {
        String[] dailyTime = dailyGoal.split(" Hour");
        String dailyApiTime = "";
        if (Integer.parseInt(dailyTime[0]) < 10) {
            dailyApiTime = "0" + dailyTime[0] + ":00";
        } else {
            dailyApiTime = dailyTime[0] + ":00";
        }
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", mTrayMinderResult.getId());
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("goal_time", dailyApiTime);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateDailyGoal(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse settingsResponse = response.body();
                assert settingsResponse != null;
                if (settingsResponse.getStatus().equals(AppConstants.ONE)) {
                    isChange = true;
                    Toast.makeText(AdjustReminderActivity.this, settingsResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (settingsResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(AdjustReminderActivity.this);
                }
            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_open_timer, null);
        dialogBuilder.setView(dialogView);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        String[] remind = mTrayMinderResult.getRemind_time().split(":");
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, Integer.parseInt(remind[0]));
        now.set(Calendar.MINUTE, Integer.parseInt(remind[1]));
        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = now.get(java.util.Calendar.MINUTE);
        timePicker.setIs24HourView(false);
        timePicker.setHour(hour);
        timePicker.setMinute(minute);
        final String[] timeFrom = {hour + ":" + minute};
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                timeFrom[0] = hour + ":" + minute;
            }
        });

        dialogBuilder.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setPositiveButton(R.string.capital_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String time = setDateFormat(timeFrom[0]);
                updateReminderTime(time);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialogPatient = dialogBuilder.create();
        alertDialogPatient.setTitle(R.string.remind_me_to_switch_aligner);
        alertDialogPatient.show();
    }


    private void showDialogAddImages(String alignNo) {
        int currentAligner = Integer.parseInt(alignNo);
        currentAligner--;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_images, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        int finalCurrentAligner = currentAligner;
        dialogBuilder.setPositiveButton(R.string.add_images, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(actCon, ImageUploadCameraActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, mFitsSmileId);
                intent.putExtra(AppConstants.ALIGNER_NO, String.valueOf(finalCurrentAligner));
                startActivityForResult(intent, 2);
                dialog.dismiss();
            }
        });

        AlertDialog alertDialogPatient = dialogBuilder.create();
        alertDialogPatient.show();
    }

    void setData() {
        if (mTrayMinderResult != null) {
            mTextTotalAligner.setText(mTrayMinderResult.getNo_of_aligners());
            String[] split = mTrayMinderResult.getGoal_time().split(":");
            goalCheck = Integer.parseInt(split[0]);
            mTextDailyGoal.setText(String.format("%s "+getResources().getString(R.string.hours), split[0]));
            mTextEachAlignerDays.setText(String.format("%s "+getResources().getString(R.string.days), mTrayMinderResult.getNo_of_days()));
            if (mTrayMinderResult.getToday_timming_log()!=null) {
                if (mTrayMinderResult.getNext_aligner_no().equals("0")) {
                    mTextCurrentAligner.setText(String.format("%s / %s", mTrayMinderResult.getToday_timming_log().getAligner_no(), mTrayMinderResult.getToday_timming_log().getAligner_no()));
                } else {
                    mTextCurrentAligner.setText(mTrayMinderResult.getToday_timming_log().getAligner_no());
                }
            }
            setDateFormat(mTrayMinderResult.getRemind_time());
            if (mTrayMinderResult.getTotal_wear_time() != null) {
                setDateFormatComplete(mTrayMinderResult.getTotal_wear_time());
            } else {
                mTextTotalWear.setText("0 "+getResources().getString(R.string.day));
            }
            mTextDaysLeft.setText(String.format("%s "+ getResources().getString(R.string.days_left), mTrayMinderResult.getRemaining_days()));
            checkedDays = Integer.parseInt(mTrayMinderResult.getNo_of_aligners());
            mTextProvider.setText(mTrayMinderResult.getDoctor_name());
            mTextUserName.setText(mTrayMinderResult.getFamily_member_name());
            mTextCompletePercentage.setText(String.format("%s%% "+ getResources().getString(R.string.completed_with_treatment), mTrayMinderResult.getFits_minder_percentage()));

            mProgressBar.setProgress(Integer.parseInt(mTrayMinderResult.getFits_minder_percentage()));
            if (Utils.getStr(mTrayMinderResult.getImage()).isEmpty()) {
                mUserImage.setImageResource(R.drawable.ic_profile_placeholder);
            } else {
                Picasso.get()
                        .load(Utils.getStr(mTrayMinderResult.getImage()))
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(mUserImage);
            }
            if (!mTrayMinderResult.getVideo_link().equals("")){
                String splitLink[]=mTrayMinderResult.getVideo_link().split("/");
                String video_id= splitLink[splitLink.length-1];
                setVideoData(video_id,mTrayMinderResult.getVideo_type());
                Log.e("split","value");
            }
        }
    }

    void getReminderDetails() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", mFitsSmileId);
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
                    if (isAddImage){
                        showDialogAddImages(lastAlignerNo);
                    }
                } else if (mResponse.getStatus() == 401) {
                    Utils.showSessionAlert(AdjustReminderActivity.this);
                } else {
                    Toast.makeText(AdjustReminderActivity.this, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TrayMinderDataResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    public void alignerNumberPopup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdjustReminderActivity.this);
        alertDialog.setTitle(getString(R.string.slide_3_title));
        List<String> genders = new ArrayList<String>();
        int daysPass = Integer.parseInt(mTrayMinderResult.getToday_timming_log().getAligner_no()) + 1;
        for (int i = daysPass; i < 151; i++) {
            genders.add("" + i);
        }

        final CharSequence[] items = genders.toArray(new String[genders.size()]);
        int days = checkedDays;
        days = days - daysPass;
        alertDialog.setSingleChoiceItems(items, days, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strTotalAligners = items[which].toString();
                checkedDays = which;
            }
        });
        alertDialog.setPositiveButton(R.string.capital_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateDefaultReminder(mTrayMinderResult.getId());
            }
        });
        alertDialog.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedDays = Integer.parseInt(mTrayMinderResult.getNo_of_aligners());
                dialog.dismiss();

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    void updateDefaultReminder(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("no_of_aligners", strTotalAligners);
        jsonObject.addProperty("id", id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAlignerTotalNumber(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    isChange = true;
                    mTextTotalAligner.setText(String.valueOf(strTotalAligners));
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(AdjustReminderActivity.this);
                }


            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    String setDateFormat(String time) {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = fmt.parse(time);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat fmtApi = new SimpleDateFormat("HH:mm:ss");
        assert date != null;
        String formattedTime = "";
        formattedTime = fmtOut.format(date);
        String mTimeReturn = fmtApi.format(date);
        mTextSwitchAligner.setText(formattedTime);
        return mTimeReturn;
    }

    void setDateFormatComplete(String time) {
        String[] timeArray = time.split(":");
        if (timeArray[0].equalsIgnoreCase("00") || timeArray[0].equalsIgnoreCase("01")){
            mTextTotalWear.setText(timeArray[0] + " " +getResources().getString(R.string.hour)+" " + timeArray[1] + " mins");
        }
        else {
            mTextTotalWear.setText(timeArray[0] + " " +getResources().getString(R.string.hours)+" " + timeArray[1] + " mins");
        }

    }

    public void alignGoalPopup(int goalChecked) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdjustReminderActivity.this);
        alertDialog.setTitle(R.string.select_daily_goal);
        List<String> genders = new ArrayList<String>();
        for (int i = 1; i < 25; i++) {
            genders.add(i + getResources().getString(R.string.hour));
        }
        final CharSequence[] items = genders.toArray(new String[genders.size()]);
        goalChecked--;
        alertDialog.setSingleChoiceItems(items, goalChecked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strDaysCurrentGoal = items[which].toString();
            }
        });
        alertDialog.setPositiveButton(R.string.capital_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!strDaysCurrentGoal.equals("")) {
                    mTextDailyGoal.setText(strDaysCurrentGoal);
                    updateDailyGoal(strDaysCurrentGoal);
                }
            }
        });
        alertDialog.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void showPopUpAligner() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_change_reminder, null);
        dialogBuilder.setView(dialogView);
        TextView mTextAlignerChange = dialogView.findViewById(R.id.textAlignerDetails);
        String first = "Change from ";
        String AlignerCurrent = "Aligner #" + mTrayMinderResult.getToday_timming_log().getAligner_no();
        String AlignerNext = "Aligner #" + mTrayMinderResult.getNext_aligner_no();
        String sec = "<font color='#6A8681'>" + AlignerCurrent + "</font>";
        String third = "<font color='#6A8681'>" + AlignerNext + "</font>";

        String to = " to ";

        mTextAlignerChange.setText(Html.fromHtml(first + sec + to + third));

        String finalNextAligner = mTrayMinderResult.getNext_aligner_no();
        dialogBuilder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                changeAligner(mFitsSmileId, finalNextAligner);
            }
        });

        dialogBuilder.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = dialogBuilder.create();
        alert.show();
    }

    void changeAligner(String id, String alignerNumber) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("aligner_no", alignerNumber);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.changeAligner(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    isChange = true;
                    mTextCurrentAligner.setText(String.valueOf(alignerNumber));
                    lastAlignerNo=alignerNumber;
                    isAddImage=true;
                    getReminderDetails();

                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(AdjustReminderActivity.this);
                }


            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }
}