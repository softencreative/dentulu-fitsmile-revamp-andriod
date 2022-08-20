package com.app.fitsmile.invitation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.myaccount.TermsConditionsActivity;
import com.app.fitsmile.response.rewards.SettingResponse;
import com.app.fitsmile.response.rewards.SettingResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationActivity extends BaseActivity {
    ImageView mShareImage;
    TextView mTextCouponCode, mTextTerms, mTextEarnAmount;
    Toolbar mToolBar;
    AppBarLayout mAppBarLayout;
    String mEarnAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        LocaleManager.setLocale(this);
        setToolBar();
        initUI();
        getShareInfo();
        setClicks();
    }


    private void setToolBar() {
        //setup toolbar
        mToolBar = findViewById(R.id.toolbar);
        mAppBarLayout = findViewById(R.id.appBar);
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolBar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        getSupportActionBar().setTitle(getResources().getString(R.string.invite_friends));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle arrow click
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initUI() {
        mShareImage =findViewById(R.id.shareImage);
        mTextCouponCode =findViewById(R.id.textCouponCode);
        mTextTerms = findViewById(R.id.textTerms);
        mTextEarnAmount =findViewById(R.id.textEarnAmount);
    }


    void setClicks() {
        String referralCode = appPreference.getreferalcode();
        mShareImage.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = R.string.discount_code_message + "$" +mEarnAmount + " discount when you register! Enjoy 😃 The code is:" + referralCode + "'";
            String shareSub = getResources().getString(R.string.use_below_code);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });
        mTextCouponCode.setText(referralCode);
        mTextCouponCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("couponCode", referralCode);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Utils.showToast(this, getResources().getString(R.string.coupon_code_copied));
            }
        });
        mTextTerms.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsConditionsActivity.class);
            startActivity(intent);
        });
    }

    void getShareInfo() {
        Utils.openProgressDialog(this);
        ApiInterface mApiService = AppController.getInstance().getClient().create(ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Call<SettingResponse> mService = mApiService.getShare(jsonObject);
        mService.enqueue(new Callback<SettingResponse>() {
            @Override
            public void onResponse(Call<SettingResponse> call, Response<SettingResponse> response) {
                Utils.closeProgressDialog();
                if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(InvitationActivity.this);
                }else {
                    ArrayList<SettingResult> mSettingList = response.body().getSettings();
                    for (int i = 0; i < mSettingList.size(); i++) {
                        if (mSettingList.get(i).getCode().equals("sharing_amount")) {
                            String value = mSettingList.get(i).getValue();
                            mEarnAmount = value;
                            mTextEarnAmount.setText(R.string.earn+" $" + value);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<SettingResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }


}


