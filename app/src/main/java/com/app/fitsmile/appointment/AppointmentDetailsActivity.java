package com.app.fitsmile.appointment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.CheckDoctorAvailablePojo;
import com.app.fitsmile.response.VideoListPojo;
import com.app.fitsmile.response.appointmentdetails.VideoDetailPojo;
import com.app.fitsmile.response.appointmentdetails.VideoDetailPojoData;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;
import static com.app.fitsmile.common.Utils.getChatIntent;
import static com.app.fitsmile.photoConsultation.Constants.ERROR_IN_RESPONSE;

public class AppointmentDetailsActivity extends BaseActivity {
    Activity activity;
    TextView tvAppointmentId, tvDateTime, tvDentistName, tvService, tvDuration, tvDiscountAmt;
    TextView tvPaidAmt, tvStatus, tvTotalPayment;
    ImageView chatImageView, imvCall;
    String strAppointmentID = "";
    public String strCompareDate = "";
    public String strWaitingDuration = "";
    long seconds;
    public String video_id = "";
    public String booking_date = "";
    Date book_date;
    Date current_date;
    private VideoDetailPojo appointmentDetailResponse = null;
    String mAppointmentId, mAppointmentType;
    LinearLayout mLayoutCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_appointment_details);
        LocaleManager.setLocale(this);
        activity = this;
        setUpToolBar();
        mAppointmentId = getIntent().getStringExtra(AppConstants.APPOINTMENT_ID);
        mAppointmentType = getIntent().getStringExtra(AppConstants.APPOINTMENT_TYPE);
        initUI();
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.appointment_details));
    }

    private void initUI() {
        tvAppointmentId = findViewById(R.id.tv_appointment_id);
        tvDateTime = findViewById(R.id.tv_date_time);
        tvService = findViewById(R.id.tv_service);
        tvDentistName = findViewById(R.id.tv_dentist_name);
        tvDuration = findViewById(R.id.tv_duration);
        tvDiscountAmt = findViewById(R.id.tv_discount_amt);
        tvPaidAmt = findViewById(R.id.tv_paid_amt);
        tvStatus = findViewById(R.id.tv_status_of_consult);
        chatImageView = findViewById(R.id.imv_chat);
        tvTotalPayment = findViewById(R.id.tv_total_amount);
        imvCall = findViewById(R.id.imv_call);
        mLayoutCall = findViewById(R.id.layout_videoCall);
        callAppointmentDetails(mAppointmentId, mAppointmentType);

    }

    private void callAppointmentDetails(String strAppointmentId, String strAppointmentType) {
        if (Utils.isOnline(activity)) {
            if (!strAppointmentId.isEmpty())
                getAppointmentDetails(strAppointmentId, strAppointmentType);
        } else {
            Utils.showToast(activity, getString(R.string.please_check_your_network));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    private void getAppointmentDetails(String Appointment_ID, String strAppointmentType) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(activity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("appointment_id", Appointment_ID);
        jsonObj.addProperty("type", strAppointmentType);
        jsonObj.addProperty("by_code", "0");
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<VideoDetailPojo> mService = mApiService.getVideoCallDetail(jsonObj);
        mService.enqueue(new Callback<VideoDetailPojo>() {
            @Override
            public void onResponse(Call<VideoDetailPojo> call, Response<VideoDetailPojo> response) {
                Utils.closeProgressDialog();
                VideoDetailPojo res = response.body();
                if (res != null) {
                    if (res.getStatus().equals(ONE)) {
                        setData(res);
                    } else if (res.getStatus().equals(FOUR_ZERO_ONE)) {
                        Utils.showSessionAlert(AppointmentDetailsActivity.this);
                    } else {
                        Utils.showToast(activity, Utils.getStr(res.getMessage()));
                    }
                } else {
                    Utils.showToast(activity, ERROR_IN_RESPONSE);
                }
            }

            @Override
            public void onFailure(Call<VideoDetailPojo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(activity, ERROR_IN_RESPONSE);
                call.cancel();
            }
        });
    }

    private void setData(VideoDetailPojo res) {
        if (res != null && res.getDatas() != null) {
            appointmentDetailResponse = res;
            VideoDetailPojoData data = res.getDatas();
            tvAppointmentId.setText(Utils.getStr(data.getRef_id()));
            String strDate = Utils.dateFormat(data.getBooking_date(), Utils.inputOnlyDateSmall, Utils.dateDisplayFormatFull);
            tvDateTime.setText(Utils.getStr(strDate) + "\n" + Utils.getStr(data.getBooking_time()));
            tvService.setText(Utils.getStr(data.getSpeacialist_category()));
            tvDentistName.setText(Utils.getStr(data.getSpecialist_name()));
            tvStatus.setText(Utils.getStr(data.getAppointment_status()));
            if (data.getCan_make_call().equals("0") || data.getIs_cancelled().equals("1")) {
                mLayoutCall.setVisibility(View.GONE);
            }

            if (!Utils.getStr(data.getDuration()).isEmpty() || Utils.getStr(data.getDuration()).equals("0")) {
                tvDuration.setVisibility(View.VISIBLE);
                tvDuration.setText(Utils.getStr(data.getDuration() + " " + getString(R.string.mins_value)));
            } else {
                tvDuration.setText(Utils.getStr("0 " + getString(R.string.mins_value)));
            }

            if (!data.getDiscount_coupon().isEmpty()) {
                tvDiscountAmt.setText(Utils.displayPrice(data.getDiscount_coupon()));
            } else {
                tvDiscountAmt.setText(Utils.displayPrice("0"));
            }

            if (!data.getTotal_amount().equalsIgnoreCase("")) {
                tvPaidAmt.setText(Utils.displayPrice(data.getTotal_amount()));
                tvTotalPayment.setText(Utils.displayPrice(data.getAmount()));
            } else {
                tvPaidAmt.setText(Utils.displayPrice("0"));
                tvTotalPayment.setText(Utils.displayPrice("0"));
            }
            chatImageView.setOnClickListener(v -> startActivity(getChatIntent(activity, res.getDatas().getFirebase_uid(), res.getDatas().getSpecialist_name(), "")));

            mLayoutCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (data != null && !Utils.getStr(data.getDoctor_id()).isEmpty()) {
                        checkDoctorAvailable(Utils.getStr(data.getDoctor_id()), Utils.getStr(data.getId()));
                    }
                }
            });
        }
    }

    private void checkDoctorAvailable(String doctorId, String appointmentID) {
        Utils.openProgressDialog(activity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("doctor_id", doctorId);
        jsonObj.addProperty("appointment_id", appointmentID);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CheckDoctorAvailablePojo> mService = mApiService.getCheckDoctorPojo(jsonObj);
        mService.enqueue(new Callback<CheckDoctorAvailablePojo>() {
            @Override
            public void onResponse(Call<CheckDoctorAvailablePojo> call, Response<CheckDoctorAvailablePojo> response) {
                Utils.closeProgressDialog();
                CheckDoctorAvailablePojo doctorListPojo = response.body();
                if (Utils.getStr(doctorListPojo.getStatus()).equalsIgnoreCase("1")) {
                    strAppointmentID = doctorListPojo.getVideo_id();
                    String channel_id = Utils.random(10);
                    video_id = doctorListPojo.getVideo_id();
                    //booking_date = doctorListPojo.getBooking_date();
                    strCompareDate = doctorListPojo.getCompare_date();
                    strWaitingDuration = doctorListPojo.getWaiting_duration();
                    SimpleDateFormat sdf = new SimpleDateFormat(Utils.outputDate);
                    String currentDateandTime = sdf.format(new Date());
                    try {
                        book_date = convertDate(Utils.getNewDate(activity, strCompareDate, Utils.inputDate, Utils.outputDate));
                        current_date = new SimpleDateFormat(Utils.outputDate).parse(currentDateandTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (book_date.compareTo(current_date) > 0) {
                        seconds = (current_date.getTime() - book_date.getTime()) / 1000;
                    } else if (book_date.compareTo(current_date) < 0) {
                        seconds = (current_date.getTime() - book_date.getTime()) / 1000;
                        int WaitingDuration = Integer.parseInt(strWaitingDuration);
                        if (seconds > WaitingDuration) {
                            videoCallActivity(channel_id, doctorId);
                        } else {
                         /*   Intent intent=new Intent(mActivity, CallNowActivity.class);
                            intent.putExtra("doctor_id", doctorId);
                            intent.putExtra("seconds", seconds);
                            startActivity(intent);*/
                        }
                    }

                } else if (doctorListPojo.getStatus().equalsIgnoreCase(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(AppointmentDetailsActivity.this);
                } else if (Utils.getStr(doctorListPojo.getStatus()).equalsIgnoreCase("0")) {
                    Utils.showToast(activity, doctorListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CheckDoctorAvailablePojo> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    void videoCallActivity(final String channel_id, final String doctor_id) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("channel_id", channel_id);
        jsonObj.addProperty("caller_id", appPreference.getUserId());
        jsonObj.addProperty("receiver_id", doctor_id);
        jsonObj.addProperty("is_emergency", "0");
        jsonObj.addProperty("limitedDuration ", "1");
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<VideoListPojo> mService = mApiService.sendVideoCallInvitation(jsonObj);
        mService.enqueue(new Callback<VideoListPojo>() {
            @Override
            public void onResponse(Call<VideoListPojo> call, Response<VideoListPojo> response) {
                VideoListPojo videoCallPojoData = response.body();
                if (videoCallPojoData.getStatus().equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(AppointmentDetailsActivity.this);
                } else {
                    String strToken = videoCallPojoData.getToken();
                    String strChannelId = videoCallPojoData.getChannel_id();
                    String strUsrId = videoCallPojoData.getUidstr();
                    String strFirstName = videoCallPojoData.getDatas().getFirstname();
                    String strLastName = videoCallPojoData.getDatas().getLastname();

                    appPreference.setAgoraCallDetails(strToken, strChannelId,
                            appPreference.getUserId(), doctor_id, strFirstName, strLastName, strAppointmentID,
                            (((appointmentDetailResponse != null && Utils.getStr(appointmentDetailResponse.getDatas().getSpecialist_category()).equals("1"))) ? "1" : "0"),
                            "1");
                    Intent intent = new Intent(activity, VideoCallActivity.class);
                    intent.putExtra("view_status", "sender");
                    startActivity(intent);

                }
            }

            @Override
            public void onFailure(Call<VideoListPojo> call, Throwable t) {
                Utils.showToast(activity, getString(R.string.please_check_your_network));
                call.cancel();
            }
        });
    }

    public Date convertDate(String date123) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(Utils.outputDate);
        try {
            date = format.parse(date123);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
