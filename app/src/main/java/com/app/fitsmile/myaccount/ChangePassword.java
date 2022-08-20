package com.app.fitsmile.myaccount;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

public class ChangePassword extends BaseActivity {

    private Button mBtnUpdate;
    private EditText mEtCurrentPwd;
    private EditText mEtNewPwd;
    private EditText mEtConfirmPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        LocaleManager.setLocale(this);
        setUpToolBar();
        init();
    }

    private void init() {
        mBtnUpdate = findViewById(R.id.btn_update);
        mEtCurrentPwd = findViewById(R.id.et_current_password);
        mEtNewPwd = findViewById(R.id.et_new_password);
        mEtConfirmPwd = findViewById(R.id.et_confirm_password);

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mEtCurrentPwd.getText().toString().isEmpty()){
                    Utils.showToast(actCon, getString(R.string.please_enter_your_current_password));
                }
                else if(mEtCurrentPwd.getText().toString().equals(mEtNewPwd.getText().toString())){
                    Utils.showToast(actCon, getString(R.string.new_pwd_should_not_same_as_current_pwd));
                }
                else if(mEtNewPwd.getText().toString().isEmpty()){
                    Utils.showToast(actCon, getString(R.string.enter_your_new_pwd));
                }
                else if(!mEtNewPwd.getText().toString().equals(mEtConfirmPwd.getText().toString())){
                    Utils.showToast(actCon, getString(R.string.new_confirm_pwd_not_match));
                }
                else {
                    passwordEdit(mEtCurrentPwd.getText().toString(),mEtConfirmPwd.getText().toString());
                }
            }
        });


    }


    void passwordEdit(String old , String confirm) {
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("old_password", old);
        jsonObj.addProperty("new_password", confirm);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.ChangePassword(jsonObj);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                Utils.closeProgressDialog();
                CommonResponse ChangePasswordEdit = response.body();
                if (ChangePasswordEdit.getStatus().equals("1")) {
                    Utils.showToast(actCon, ChangePasswordEdit.getMessage());
                    finish();
                } else if (ChangePasswordEdit.getStatus().equals("401")) {
                    Utils.showSessionAlert(actCon);
                }  else {
                    Utils.showToast(actCon, ChangePasswordEdit.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                call.cancel();
                Utils.closeProgressDialog();
            }
        });
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_change_password));
    }
}
