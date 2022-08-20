package com.app.fitsmile.splash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends BaseActivity {

    private Button mBtnResetPassword;
    private TextView mTvGoBack;
    private EditText mEtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        LocaleManager.setLocale(this);
        init();
    }

    private void init() {
        mBtnResetPassword = findViewById(R.id.btn_reset_password);
        mEtEmail = findViewById(R.id.email);
        mTvGoBack = findViewById(R.id.tv_go_back);

        mTvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtEmail.getText().toString().trim().isEmpty()){
                    Utils.showToast(actCon, getString(R.string.email_id_required));
                } else{
                    if (Utils.isOnline(getApplicationContext())) {
                        if (!mEtEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                            mEtEmail.setError(getString(R.string.invalid_email_address));
                        }else {
                            forgotPassword(mEtEmail.getText().toString());
                        }
                    } else{
                        Utils.showToast(actCon, getString(R.string.please_check_your_network));
                    }
                }
            }
        });
    }

    private void forgotPassword(String email) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("email", email);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.forgotPassword(jsonObj);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Utils.closeProgressDialog();
                CommonResponse forgotPassword = response.body();
                Utils.showToast(actCon, ""+forgotPassword.getMessage());
                finish();
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }
}
