package com.app.fitsmile.appointment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.Doctor_options;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.PhotoConsultancyDetailResponse;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.PhotoConsultancyDetailResult;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.QuestionData;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.QuestionImages;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoConsultationDetailActivity extends BaseActivity implements RecyclerItemClickListener {
    List<QuestionData> mQuestionData = new ArrayList<>();
    List<QuestionImages> mQuestionImages = new ArrayList<>();
    PhotoPatientInfoAdapter mAdapter;
    PhotoPatientTeethImageAdapter mAdapterImageTeeth;
    RecyclerView mRecyclerQuestionData, mRecyclerImageTeeth;
    PhotoConsultancyDetailResult mPhotoDetailResult = new PhotoConsultancyDetailResult();
    String id;
    TextView mTextPatientName, mTextPatientEmail, mTextPatientPhone;
    public ArrayList<Doctor_options> mDoctorOptionsArrayList = new ArrayList<>();
    public TreatmentPlanShowAdapter mTreatmentPlanShowAdapter;
    private RecyclerView mRecyclerTreatmentOptions;
    TextView mTextTreatmentOption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_consultation_details);
        LocaleManager.setLocale(this);
        id = getIntent().getStringExtra(Constants.PATIENT_DETAIL_ID);
        setUpToolBar();
        initView();
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_consultations));
    }

    private void initView() {
        mRecyclerQuestionData = findViewById(R.id.recycler_patient_info);
        mTextPatientName = findViewById(R.id.textPatientName);
        mTextPatientEmail = findViewById(R.id.textPatientEmail);
        mTextPatientPhone = findViewById(R.id.textPatientNumber);
        mRecyclerImageTeeth = findViewById(R.id.recyclerImageTeeth);
        mRecyclerTreatmentOptions = findViewById(R.id.recycler_treatment_option);
        mTextTreatmentOption = findViewById(R.id.textTreatmentOptions);
        getPhotoConsultancyDetails();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        if (mPhotoDetailResult != null) {
            mTextPatientName.setText(mPhotoDetailResult.getPatient_firstname() + " " + mPhotoDetailResult.getPatient_lastname());
            mTextPatientEmail.setText(mPhotoDetailResult.getPatient_email());
            mTextPatientPhone.setText(mPhotoDetailResult.getPatient_mobile());
            mQuestionData = mPhotoDetailResult.getQuestion_data();
            mQuestionImages = mPhotoDetailResult.getQuestion_images();
            setAdapter();
        }

    }


    private void setAdapter() {
        mRecyclerQuestionData.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new PhotoPatientInfoAdapter(this, mQuestionData);
        mRecyclerQuestionData.setAdapter(mAdapter);
        mRecyclerImageTeeth.setLayoutManager(new GridLayoutManager(this, 5));
        mAdapterImageTeeth = new PhotoPatientTeethImageAdapter(this, mQuestionImages, this);
        mRecyclerImageTeeth.setAdapter(mAdapterImageTeeth);
        mRecyclerTreatmentOptions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    void getPhotoConsultancyDetails() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("id", id);
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        Call<PhotoConsultancyDetailResponse> mServicePhotoConsultancyList = mApiService.getPhotoConsultancyDetail(jsonObj);
        mServicePhotoConsultancyList.enqueue(new Callback<PhotoConsultancyDetailResponse>() {
            @Override
            public void onResponse(Call<PhotoConsultancyDetailResponse> call, Response<PhotoConsultancyDetailResponse> response) {
                Utils.closeProgressDialog();
                final PhotoConsultancyDetailResponse photoConsultancyListResponse = response.body();
                if (photoConsultancyListResponse != null && photoConsultancyListResponse.getStatus() == 401) {
                    Utils.closeProgressDialog();
                    Utils.showSessionAlert(PhotoConsultationDetailActivity.this);
                    return;
                }
                if (photoConsultancyListResponse != null && photoConsultancyListResponse.getStatus() == 1) {
                    mPhotoDetailResult = photoConsultancyListResponse.getData();
                    setData();
                    if (mPhotoDetailResult.getDoctor_options() != null && mPhotoDetailResult.getDoctor_options().size() > 0) {
                        mTextTreatmentOption.setVisibility(View.VISIBLE);
                        mRecyclerTreatmentOptions.setVisibility(View.VISIBLE);
                        mDoctorOptionsArrayList.addAll(mPhotoDetailResult.getDoctor_options());
                        mTreatmentPlanShowAdapter = new TreatmentPlanShowAdapter(PhotoConsultationDetailActivity.this, mDoctorOptionsArrayList);
                        mRecyclerTreatmentOptions.setAdapter(mTreatmentPlanShowAdapter);
                    } else {
                        mTextTreatmentOption.setVisibility(View.GONE);
                        mRecyclerTreatmentOptions.setVisibility(View.GONE);
                    }
                } else {
                    Utils.showToast(PhotoConsultationDetailActivity.this, getResources().getString(R.string.something_went_wrong));
                    Utils.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<PhotoConsultancyDetailResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    @Override
    public void setClicks(int pos, int open) {
        showPopUp(mQuestionImages.get(pos).getValue());
    }

    private void showPopUp(String Url) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_view_image, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();


        ImageView mImvProfile = alert.findViewById(R.id.imageSelfie);
        Picasso.get().load(Url).into(mImvProfile);

        ImageView close = alert.findViewById(R.id.imv_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }
}
