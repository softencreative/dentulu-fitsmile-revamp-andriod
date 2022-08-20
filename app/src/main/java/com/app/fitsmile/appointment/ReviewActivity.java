package com.app.fitsmile.appointment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends BaseActivity {
    Activity activity;
    String strDoctorId ="";
    String ratingupdat ="";
    RatingBar rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_feedback);
        LocaleManager.setLocale(this);
        activity = this;
        initUI();
    }

    private void initUI() {
        strDoctorId = getIntent().getExtras().getString("dentist_id");
        final EditText comments = findViewById(R.id.comments);
        rb = findViewById(R.id.simpleRatingBar);

        ImageView iv_close = findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Button submitButton = (Button)findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(activity)) {
                    String comment = comments.getText().toString();
                    int ratings = (int) rb.getRating();
                    if (Float.parseFloat(String.valueOf(ratings)) > 0) {
                        ratingupdat = String.valueOf(ratings);
                        String feedbackuserid = appPreference.getUserId();
                        feedbackSubmit(comment, feedbackuserid, ratingupdat, strDoctorId);
                    } else {
                        Utils.showToast(getApplicationContext(), getString(R.string.please_provide_rating));
                    }
                } else{
                    Utils.showToast(activity, getString(R.string.network_error));
                }
            }
        });
    }

    void feedbackSubmit(String comments, String id, String rating,  String doctor_id) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("comment", comments);
        jsonObj.addProperty("user_id", id);
        jsonObj.addProperty("rating", rating);
        jsonObj.addProperty("doctor_id", doctor_id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.getVideoCallFeedBACK(jsonObj);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                CommonResponse SaveReview = response.body();
                Utils.closeProgressDialog();
                if (SaveReview.getStatus().equals("1")) {
                    Utils.showToast(activity, "" + SaveReview.getMessage());
                    onBackPressed();
                }else if (SaveReview.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(ReviewActivity.this);
                }
            }
            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
