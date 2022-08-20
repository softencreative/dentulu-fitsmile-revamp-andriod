package com.app.fitsmile.home;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.myaccount.FAQResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermsPrivacyActivity extends BaseActivity {
    TextView mTextPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_privacy);
        LocaleManager.setLocale(this);
      //  initUI();
        setToolBar();
        //getPrivacyPolicy();
    }

    void initUI() {
       // mTextPrivacy = findViewById(R.id.textPrivacy);
    }

    private void setToolBar() {
        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getString(R.string.terms_amp_policy));
    }

    void getPrivacyPolicy() {
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<FAQResponse> mService = mApiService.getTerms(jsonObject);
        mService.enqueue(new Callback<FAQResponse>() {
            @Override
            public void onResponse(Call<FAQResponse> call, Response<FAQResponse> response) {
                Utils.closeProgressDialog();
                FAQResponse mResponse = response.body();
                if (mResponse.getStatus().equals(AppConstants.ONE)) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        mTextPrivacy.setText(Html.fromHtml(mResponse.getDescription(), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        mTextPrivacy.setText(Html.fromHtml(mResponse.getDescription()));
                    }
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(TermsPrivacyActivity.this);
                }

            }

            @Override
            public void onFailure(Call<FAQResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }
}