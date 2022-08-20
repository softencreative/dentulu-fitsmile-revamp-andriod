package com.app.fitsmile.myaccount;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.SettingsResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends BaseActivity {
    CheckBox mCheckTouch, mCheckSms, mCheckAligner;
    TextView mTextUpdate;
    int isTouch = 0, isSms = 0, isAligner = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LocaleManager.setLocale(this);
        setUpToolBar();
        initUI();
        setClicks();
        getNotificationSettings();
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_settings));
    }


    void initUI() {
        mCheckAligner = findViewById(R.id.checkAligner);
        mCheckSms = findViewById(R.id.checkSms);
        mCheckTouch = findViewById(R.id.checkTouch);
        mTextUpdate=findViewById(R.id.textUpdate);
    }

    void setClicks() {
        mCheckTouch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    isTouch = 1;
                    mCheckTouch.setChecked(true);
                } else {
                    mCheckTouch.setChecked(false);
                    isTouch = 0;
                }
            }
        });

        mCheckSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    isSms = 1;
                    mCheckSms.setChecked(true);
                } else {
                    mCheckSms.setChecked(false);
                    isSms = 0;
                }
            }
        });

        mCheckAligner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    isAligner = 1;
                    mCheckAligner.setChecked(true);
                } else {
                    mCheckAligner.setChecked(false);
                    isAligner = 0;
                }
            }
        });
        mTextUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotificationSettings();
            }
        });
    }

    void getNotificationSettings() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_ID, appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.fetchNotificationSettings(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                if (mResponse.getStatus().equals("1")) {
                    setData(mResponse.getData());
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(Settings.this);
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    void setNotificationSettings() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_ID, appPreference.getUserId());
        jsonObject.addProperty("aligner", String.valueOf(isAligner));
        jsonObject.addProperty("sms", String.valueOf(isSms));
        jsonObject.addProperty("touch_id", String.valueOf(isTouch));
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateNotification(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    if (isTouch==1){
                        appPreference.setTouchId(true);
                    }else {
                        appPreference.setTouchId(false);
                    }
                    Utils.showToast(Settings.this,mResponse.getMessage());
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(Settings.this);
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    void setData(SettingsResult settingsResult) {
        if (settingsResult.getTouch_id().equals("1")) {
            mCheckTouch.setChecked(true);
            isTouch = 1;
        } else {
            isTouch = 0;
            mCheckTouch.setChecked(false);
        }
        if (settingsResult.getSms().equals("1")) {
            mCheckSms.setChecked(true);
            isSms = 1;
        } else {
            isSms = 0;
            mCheckSms.setChecked(false);
        }
        if (settingsResult.getAligner().equals("1")) {
            mCheckAligner.setChecked(true);
            isAligner = 1;
        } else {
            isAligner = 0;
            mCheckAligner.setChecked(false);
        }
    }
}
