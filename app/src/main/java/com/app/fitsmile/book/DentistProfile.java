package com.app.fitsmile.book;

import  android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.dentisdetails.AvailableSlotsItem;
import com.app.fitsmile.response.dentisdetails.ResponseDentistDetails;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.BuildConfig.BASE_URL;

public class DentistProfile extends BaseActivity implements View.OnClickListener {

    List<AvailableSlotsItem> availableSlots = new ArrayList<AvailableSlotsItem>();
    private CircleImageView mDentistProfileImage;
    private TextView mTvDentistName;
    private TextView mTvMemberOf,mTvDentalSchool,mTvLanguageSpoken,mTvInfoDescription;
    private TextView mTvPracticeLocation;
    private RatingBar mRatingBar;
    private DocAvailableTimeAdapter docAvailableTimeAdapter;
    private TextView mTvAvailableTime, mTvLicense, mTvScheduleNow, mTvBio;
    private RelativeLayout mRelativeAvailableTime, mRelativeLicenseNo,mRelativeBio;
    private TextView tvNoAvailableTime, tvNoLicenseNumber;
    private LinearLayoutManager linearLayManagerAvailableTime;
    private RecyclerView mRecyLicenseNumber;
    private LinearLayoutManager linearLayManagerTeledentistry;
    private LinearLayoutManager linearLayManagerLicenseNumber;
    private LicenseAdapter licenseAdapter;
    private TextView mTvSpecialist;
    private RecyclerView mRecyAvailableTime;
    public String strDentistID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_profile);
        LocaleManager.setLocale(this);
        setUpToolBar();
        init();
    }

    private void init() {
        mDentistProfileImage = findViewById(R.id.dentist_profile_image);
        mTvDentistName = findViewById(R.id.tv_dentist_name);
        mTvMemberOf = findViewById(R.id.tv_member_of);
        mTvDentalSchool = findViewById(R.id.medical_school);
        mTvBio = findViewById(R.id.tv_bio);
        mTvInfoDescription = findViewById(R.id.tv_info_description);
        mTvLanguageSpoken = findViewById(R.id.tv_language_spoken);
        mTvPracticeLocation = findViewById(R.id.tv_practice_location);
        mRatingBar = findViewById(R.id.rating);
        mRelativeAvailableTime = findViewById(R.id.relative_available_time);
        mRelativeBio = findViewById(R.id.relative_bio);
        mTvAvailableTime = findViewById(R.id.tv_available_time);
        tvNoAvailableTime = findViewById(R.id.tv_no_available_time);
        tvNoLicenseNumber = findViewById(R.id.tv_no_license_number);
        mRecyAvailableTime = findViewById(R.id.recy_available_time);
        mRecyLicenseNumber = findViewById(R.id.recy_license_number);
        mTvLicense = findViewById(R.id.tv_license);
        mRelativeLicenseNo = findViewById(R.id.relative_license);
        mTvSpecialist = findViewById(R.id.tv_specialist);
        mTvScheduleNow = findViewById(R.id.tv_schedule_now);

        strDentistID = Utils.getStringFromIntent(getIntent(),"DENTIST_ID");

        mTvBio.setOnClickListener(this);
        mTvAvailableTime.setOnClickListener(this);
        mTvLicense.setOnClickListener(this);
        mTvScheduleNow.setOnClickListener(this);

       /* mTvBio.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_up_arrow), null);
        mTvAvailableTime.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_up_arrow), null);
        mTvLicense.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_up_arrow), null);*/

        setAdapter();
        getDoctorInfo(strDentistID);
    }

    private void setAdapter() {

        linearLayManagerAvailableTime = new LinearLayoutManager(actCon, LinearLayoutManager.VERTICAL, false);
        mRecyAvailableTime.setLayoutManager(linearLayManagerAvailableTime);
        linearLayManagerLicenseNumber = new LinearLayoutManager(actCon, LinearLayoutManager.VERTICAL, false);
        mRecyLicenseNumber.setLayoutManager(linearLayManagerLicenseNumber);

        docAvailableTimeAdapter = new DocAvailableTimeAdapter(actCon);
        mRecyAvailableTime.setAdapter(docAvailableTimeAdapter);

        licenseAdapter = new LicenseAdapter(actCon);
        mRecyLicenseNumber.setAdapter(licenseAdapter);
    }


    void getDoctorInfo(final String doctor_id) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("doctor_id", doctor_id);
        jsonObj.addProperty("category", 2);
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ResponseDentistDetails> mService = mApiService.getDoctorDetails(jsonObj);
        mService.enqueue(new Callback<ResponseDentistDetails>() {
            @Override
            public void onResponse(Call<ResponseDentistDetails> call, Response<ResponseDentistDetails> response) {
                Utils.closeProgressDialog();
                final ResponseDentistDetails doctorProfilePojo = response.body();
                if (doctorProfilePojo.getStatus().equals(AppConstants.ONE)) {

                    if (doctorProfilePojo.getAvailableSlots() != null && !doctorProfilePojo.getAvailableSlots().isEmpty()) {
                        mRecyAvailableTime.setVisibility(View.VISIBLE);
                        tvNoAvailableTime.setVisibility(View.GONE);
                        availableSlots.addAll(doctorProfilePojo.getAvailableSlots());
                    } else {
                        tvNoAvailableTime.setVisibility(View.VISIBLE);
                        mRecyAvailableTime.setVisibility(View.GONE);
                    }

                    if (doctorProfilePojo.getInfo() != null) {
                        if (doctorProfilePojo.getInfo() != null && !Utils.getStr(doctorProfilePojo.getInfo().getMember()).isEmpty()) {
                            mTvMemberOf.setText(Utils.getStr(doctorProfilePojo.getInfo().getMember()));
                        } else {
                            mTvMemberOf.setText(R.string.not_mentioned);
                        }

                        if (doctorProfilePojo.getInfo() != null && !Utils.getStr(doctorProfilePojo.getInfo().getRate()).isEmpty()) {
                            mTvPracticeLocation.setText(Utils.getStr(doctorProfilePojo.getInfo().getRate()));
                        } else {
                            mTvPracticeLocation.setText(R.string.not_mentioned);
                        }
                        if (doctorProfilePojo.getInfo().getMedicalSchool().isEmpty()) {
                            mTvDentalSchool.setText(R.string.not_mentioned);
                        } else {
                            mTvDentalSchool.setText(doctorProfilePojo.getInfo().getMedicalSchool());

                        }
                        if (doctorProfilePojo.getInfo().getLanguage().isEmpty()) {
                            mTvLanguageSpoken.setText(R.string.not_mentioned);
                        } else {
                            mTvLanguageSpoken.setText(doctorProfilePojo.getInfo().getLanguage());
                        }

                        if (doctorProfilePojo.getInfo().getInfoDescription().isEmpty()) {
                            mTvInfoDescription.setText(R.string.not_mentioned);
                        } else {
                            mTvInfoDescription.setText(doctorProfilePojo.getInfo().getInfoDescription());
                        }
                        if (doctorProfilePojo.getInfo() != null && !Utils.getStr(doctorProfilePojo.getInfo().getSpecialist()).isEmpty()) {
                            mTvSpecialist.setText(Utils.getStr(doctorProfilePojo.getInfo().getSpecialist()));
                        } else {
                            mTvSpecialist.setText(R.string.not_mentioned);
                        }

/*
                if (doctorProfilePojo.getInfo().getRatingCount().isEmpty()){
                    tvRatingCount.setText("");
                } else {
                    tvRatingCount.setText("("+(doctorProfilePojo.getInfo().getRatingCount())+")");
                }*/

                        String strDrName = getResources().getString(R.string.dr)+ " " + Utils.getStr(doctorProfilePojo.getInfo().getFirstname()) + " " + Utils.getStr(doctorProfilePojo.getInfo().getLastname()) + " " + Utils.getStr(doctorProfilePojo.getInfo().getQualification());
                        mTvDentistName.setText(strDrName);
//                tvDentistqualification.setText(doctorProfilePojo.getInfo().getSpecialist());

                        mRatingBar.setRating(Float.parseFloat(doctorProfilePojo.getInfo().getStar()));
                        if (!doctorProfilePojo.getInfo().getImage_url().equals("")) {
                            Picasso.get()
                                    .load(doctorProfilePojo.getInfo().getImage_url())
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .into(mDentistProfileImage);
                        }

                        String strTimeZone = doctorProfilePojo.getInfo().getTimezone();
                        String strDefaultTimeZone = String.valueOf(TimeZone.getDefault());
                        if (strTimeZone.equalsIgnoreCase(strDefaultTimeZone)) {
                            docAvailableTimeAdapter.setCommonList(availableSlots, "1", strTimeZone);
                        } else {
                            docAvailableTimeAdapter.setCommonList(availableSlots, "2", strTimeZone);
                        }

               /* if(doctorProfilePojo.getVideocallServices()!=null && doctorProfilePojo.getVideocallServices().isEmpty()){
                    tvNoTeledentistryService.setVisibility(View.VISIBLE);
                    mRecyTeleDentistryService.setVisibility(View.GONE);
                }else{
                    teledentistryAdapter.setCommonList(doctorProfilePojo.getVideocallServices());
                    tvNoTeledentistryService.setVisibility(View.GONE);
                    mRecyTeleDentistryService.setVisibility(View.VISIBLE);
                }*/
                    }
                    if (doctorProfilePojo.getLicenses() != null && !doctorProfilePojo.getLicenses().isEmpty()) {
                        licenseAdapter.setCommonList(doctorProfilePojo.getLicenses());
                        tvNoLicenseNumber.setVisibility(View.GONE);
                        mRecyLicenseNumber.setVisibility(View.VISIBLE);
                    } else {
                        tvNoLicenseNumber.setVisibility(View.VISIBLE);
                        mRecyLicenseNumber.setVisibility(View.GONE);
                    }
                }else if (doctorProfilePojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(DentistProfile.this);
                }

            }

            @Override
            public void onFailure(Call<ResponseDentistDetails> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();

                }
                call.cancel();
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
        getSupportActionBar().setTitle(getResources().getString(R.string.title_dentist_profile));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.tv_bio:
                if (mRelativeBio.getVisibility() == View.VISIBLE) {
                    mRelativeBio.setVisibility(View.GONE);
                    mTvBio.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_down_arrow), null);
                } else {
                    mTvBio.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_up_arrow), null);
                    mRelativeBio.setVisibility(View.VISIBLE);
                }
                break;
                case R.id.tv_available_time:
                if (mRelativeAvailableTime.getVisibility() == View.VISIBLE) {
                    mRelativeAvailableTime.setVisibility(View.GONE);
                    mTvAvailableTime.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_down_arrow), null);
                } else {
                    mTvAvailableTime.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_up_arrow), null);
                    mRelativeAvailableTime.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_license:
                if (mRelativeLicenseNo.getVisibility() == View.VISIBLE) {
                    mRelativeLicenseNo.setVisibility(View.GONE);
                    mTvLicense.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_down_arrow), null);
                } else {
                    mTvLicense.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_up_arrow), null);
                    mRelativeLicenseNo.setVisibility(View.VISIBLE);
                }
                break;*/
            case R.id.tv_schedule_now:
                Intent intent = new Intent(actCon,VideoAppointmentCheckout.class);
                intent.putExtra("DENTIST_ID",Utils.getStr(strDentistID));
                startActivity(intent);
                break;
        }
    }
}
