package com.app.fitsmile.book;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.book.stripe.StripePayment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.appointmentlist.ResponseCheckPromoCode;
import com.app.fitsmile.response.date.AppointmentDateResponse;
import com.app.fitsmile.response.date.DatesItem;
import com.app.fitsmile.response.doctime.DocAvailableTimeResponse;
import com.app.fitsmile.response.doctime.TimingsItem;
import com.app.fitsmile.response.healthrec.ResponseViewHealthRecord;
import com.app.fitsmile.response.insurance.InsuranceResponse;
import com.app.fitsmile.response.tempschedule.ReScheduleBookingpojoo;
import com.app.fitsmile.response.videofee.VideoCallFeeResponse;
import com.app.fitsmile.utils.ImagePickerActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;
import static com.app.fitsmile.photoConsultation.Constants.ERROR_IN_RESPONSE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class VideoAppointmentCheckout extends BaseActivity implements View.OnClickListener {

    private Button btnComplete;
    private Button btnConfirmDate, btnCancelDate;
    private TextView tvAgree, textApply;
    private TextView tvChooseDate;
    private BottomSheetBehavior bottomSheetBehaviorDate;
    private LinearLayout llDateChooseBottomSheet;
    private RadioGroup rgDate, rgTime;
    private TextView tvAddHealthRecord;
    private ImageView imvCompletedHealthRecord, imvEditCompletedHealthRecord;

    private String isHealthRecordUpdated = "1";
    private boolean strHealthRecordStatus = false;
    boolean strSignatureStatus = false;

    private LinearLayoutCompat llPayment;

    private String strFinalAmount = "0";
    private String strTotalAmount = "0";
    private String strBookDate = "";
    String SpinnerValue = "";
    Boolean profileImage = false;
    private String strBookTime = "";

    private TextView mTvVideoCallFee, mTvSummaryTotalFee, mTvFinalFee, mTvDiscountFee;
    private TextView mTvCard;

    /*For location*/
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String latitude = "", longitude = "";

    AlertDialog alert;

    public String videoId = "";
    private ResponseViewHealthRecord resHealthRecord = null;
    private LinearLayoutCompat llCompletedHealthRecord;
    private LinearLayoutCompat mLayoutAddInsurance;
    private TextView mTvExpireDays;
    TextView tvInsuranceProvider;
    private EditText tvInsuranceNumber;
    EditText mEditCoupon;

    private ImageView iv_insurance_front;
    private ImageView iv_insurance_back;
    private ImageView iv_licence_front;
    private ImageView iv_licence_back;
    String InsuranceNumber = "";
    String images = "";
    String image_name = "";
    String driving_license = "";
    String insurance_back = "";

    ArrayAdapter arrayAdapter;

    String insurance = "";
    String driving_license_back = "";


    public static boolean setSignFlag = false;
    MultipartBody.Part body = null;
    private String strCategory = "2";  // 1 emergency appointment 2 video schedule appointment.
    public String strDentistID = "";
    private String strPaymentInsurance = "0";
    private String strCouponCode = "";
    private boolean isCouponApplied = false;
    boolean strDateTime = false;

    /*card*/
    String strCardID = "";
    String strCustomerID = "";
    String cardends = "";

    List<TimingsItem> timeList = new ArrayList<TimingsItem>();
    List<DatesItem> daysList = new ArrayList<DatesItem>();
    SwitchMaterial switchTouchID;
    boolean insuranceFlag = false;
    boolean status1 = false;
    boolean status2 = false;
    boolean status3 = false;
    boolean status4 = false;
    ArrayList<String> arrayList = new ArrayList<>();
    public static final int REQUEST_IMAGE = 100;
    MultipartBody.Part body1 = null;
    MultipartBody.Part body2 = null;
    MultipartBody.Part body3 = null;
    MultipartBody.Part body4 = null;
    byte[] ivInsuranceFront, ivInsuranceBack, ivLicenceFront, ivLicenceback;
    LinearLayoutCompat mLayoutApplyCode, mLayoutEnterCode;
    ImageView mImageCouponCancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_checkout);
        LocaleManager.setLocale(this);
        setUpToolBar();
        initView();
    }

    private void initView() {
        btnComplete = findViewById(R.id.btn_completed);
        tvAgree = findViewById(R.id.tv_agree);
        textApply = findViewById(R.id.textApply);
        tvChooseDate = findViewById(R.id.tv_choose_date);
        btnConfirmDate = findViewById(R.id.btn_confirm);
        btnCancelDate = findViewById(R.id.btn_cancel);
        tvAddHealthRecord = findViewById(R.id.tv_add_health_record);
        rgDate = findViewById(R.id.rg_date);
        rgTime = findViewById(R.id.rg_time);
        imvCompletedHealthRecord = findViewById(R.id.view_completed_medical_history);
        imvEditCompletedHealthRecord = findViewById(R.id.imv_edit_health_record);
        llPayment = findViewById(R.id.ll_payment);
        mTvVideoCallFee = findViewById(R.id.tv_fee);
        mTvSummaryTotalFee = findViewById(R.id.tv_summary_total_fee);
        mTvFinalFee = findViewById(R.id.tv_final_fee);
        mTvDiscountFee = findViewById(R.id.tv_discount_fee);
        mTvCard = findViewById(R.id.tv_card);
        llCompletedHealthRecord = findViewById(R.id.ll_completed_health_record);
        mTvExpireDays = findViewById(R.id.tv_expire_days);
        switchTouchID = findViewById(R.id.switchTouchID);
        mLayoutAddInsurance = findViewById(R.id.layoutAddInsurance);
        tvInsuranceProvider = findViewById(R.id.textSelectProvider);
        tvInsuranceNumber = (EditText) findViewById(R.id.editInsuranceNumber);
        iv_insurance_front = (ImageView) findViewById(R.id.iv_insurance_front);
        iv_insurance_back = (ImageView) findViewById(R.id.iv_insurance_back);
        iv_licence_front = (ImageView) findViewById(R.id.iv_licence_front);
        iv_licence_back = (ImageView) findViewById(R.id.iv_licence_back);
        mEditCoupon = findViewById(R.id.edit_coupon);
        mLayoutApplyCode = findViewById(R.id.layoutApplyCoupon);
        mLayoutEnterCode = findViewById(R.id.layoutEnterCoupon);
        mImageCouponCancel = findViewById(R.id.imv_cancel_coupon);

        tvAgree.setOnClickListener(this);
        btnConfirmDate.setOnClickListener(this);
        btnCancelDate.setOnClickListener(this);
        tvChooseDate.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
        tvAddHealthRecord.setOnClickListener(this);
        imvCompletedHealthRecord.setOnClickListener(this);
        imvEditCompletedHealthRecord.setOnClickListener(this);
        llPayment.setOnClickListener(this);
        textApply.setOnClickListener(this);
        mImageCouponCancel.setOnClickListener(this);

        llDateChooseBottomSheet = findViewById(R.id.ll_parent_choose_date);
        bottomSheetBehaviorDate = BottomSheetBehavior.from(llDateChooseBottomSheet);
        bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_HIDDEN);
        tvInsuranceProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectInsuranceProvider();
            }
        });

        iv_insurance_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images = "1";
                setProfilePicture(images);
            }
        });

        iv_insurance_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images = "2";
                setProfilePicture(images);
            }
        });

        iv_licence_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images = "3";
                setProfilePicture(images);
            }
        });

        iv_licence_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images = "4";
                setProfilePicture(images);
            }
        });

        strDentistID = Utils.getStringFromIntent(getIntent(), "DENTIST_ID");

        // getVideoCallFees();
        getCompletedHealthRecord(appPreference.getUserId());
        setSwitchInsurance();
        getInsuranceInfo();

    }

    private void getDoctorAvailableDate() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("doctor_id", strDentistID);
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<AppointmentDateResponse> mService = mApiService.getDentistAvailableDate(jsonObj);
        mService.enqueue(new Callback<AppointmentDateResponse>() {
            @Override
            public void onResponse(Call<AppointmentDateResponse> call, Response<AppointmentDateResponse> response) {
                AppointmentDateResponse dateResponse = response.body();
                Utils.closeProgressDialog();
                if (Utils.getStr(dateResponse.getStatus()).equals(ONE)) {
                    if (dateResponse.getDates() != null && !dateResponse.getDates().isEmpty()) {
                        daysList = dateResponse.getDates();
                        addRadioButtons(dateResponse.getDates());
                    }

                    if (bottomSheetBehaviorDate.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                } else if (Utils.getStr(dateResponse.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    Utils.showToast(actCon, dateResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AppointmentDateResponse> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    public void addRadioButtons(List<DatesItem> number) {
        rgDate = findViewById(R.id.rg_date);
        rgDate.removeAllViews();
        rgTime = findViewById(R.id.rg_time);
        rgTime.removeAllViews();
        rgDate.setOrientation(LinearLayout.HORIZONTAL);
        //
        for (int i = 0; i <= number.size() - 1; i++) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(actCon.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.radio_button, null);

// fill in any details dynamically here
            RadioButton rdbtn = (RadioButton) v.findViewById(R.id.rb_date);
// insert into main view
//            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.insert_point);
//            insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            String date = Utils.getDateFromTimestamp(number.get(i).getTimestamp());
            rdbtn.setText(date);
            rdbtn.setTag(i);
            rdbtn.setId(View.generateViewId());
            rdbtn.setOnClickListener(this);
            rgDate.addView(rdbtn);
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_video_appointment));
    }

    private void showPopUp(String appointmentId) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_appointment_success, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        TextView mTvThanks = alert.findViewById(R.id.tv_thanks);
        TextView mTvDateTime = alert.findViewById(R.id.tv_date_time);
        TextView mTvPaidAmt = alert.findViewById(R.id.tv_paid_amt);
        TextView mTvName = alert.findViewById(R.id.tv_name);
        TextView mTvAppointmentId = alert.findViewById(R.id.tv_appointment);
        CircleImageView mImvProfile = alert.findViewById(R.id.profile_image);
        mTvDateTime.setText(strBookDate + " " + strBookTime);
        mTvPaidAmt.setText(Utils.getStr(mTvFinalFee.getText().toString()));
        mTvName.setText(appPreference.getFirstName());
        mTvAppointmentId.setText(appointmentId);

        if (Utils.getStr(appPreference.getImage()).isEmpty()) {
            mImvProfile.setImageResource(R.drawable.ic_profile_placeholder);
        } else {
            Picasso.get()
                    .load(Utils.getStr(appPreference.getImage()))
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(mImvProfile);
        }

        ImageView close = alert.findViewById(R.id.imv_close);
        mTvThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                moveToHome();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                moveToHome();
            }
        });


    }

    private void moveToHome() {
        Intent intent = new Intent(actCon, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_agree:
                Intent intentSign = new Intent(actCon, WebConsentForm.class);
                startActivity(intentSign);
                break;
            case R.id.btn_completed:
                validateAppointment();
                break;
            case R.id.tv_choose_date:
                rgTime.setVisibility(View.GONE);
                strDateTime = false;
                strBookTime = "";
                strBookDate = "";
                getDoctorAvailableDate();
                break;
            case R.id.btn_confirm:
                submitChooseDate();
                break;
            case R.id.btn_cancel:
                bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.imv_edit_health_record:
            case R.id.tv_add_health_record:
                Intent intent = new Intent(actCon, HealthHistory.class);
                intent.putExtra("book_type", "new_emergency");
                startActivityForResult(intent, 200);
                break;
            case R.id.view_completed_medical_history:
                showPopUpCompleteHealthRecord();
                break;
            case R.id.ll_payment:
                Intent i = new Intent(actCon, StripePayment.class);
                startActivityForResult(i, 1);
                break;
            case R.id.textApply:
                if (Utils.getStr(mEditCoupon.getText().toString().trim()).isEmpty()) {
                    Utils.showToast(actCon, "Please enter coupon code");
                    return;
                }
                getCouponCodeApply(Utils.getStr(mEditCoupon.getText().toString().trim()));
                break;
            case R.id.rb_date:

                break;
            case R.id.rb_time:

                break;
            case R.id.imv_cancel_coupon:
                ClearCouponAndResetValue();
                break;
            default:
                if (view instanceof RadioButton) {
//                    Utils.showToast(actCon, ((RadioButton) view).getText().toString());
                    if (((RadioButton) view).getText().toString().length() > 10) {
//                        Utils.showToast(actCon, ((RadioButton) view).getText().toString());
                        int position = (int) ((RadioButton) view).getTag();
                        strBookDate = daysList.get(position).getDate();
                        getScheduleTimings(daysList.get(position).getDate());
                    } else {
                        int positionTime = (int) ((RadioButton) view).getTag();
                        strBookTime = timeList.get(positionTime).getTime();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 200) {
                boolean status = data.getBooleanExtra("status", true);
                String str_message = data.getStringExtra("str_message");
                strHealthRecordStatus = status;
                tvAddHealthRecord.setText(str_message);
                isHealthRecordUpdated = "1";
                getCompletedHealthRecord(appPreference.getUserId());
                strHealthRecordStatus = true;
            }

            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    String status = data.getStringExtra("status");
                    if (status.equals("1")) {
                        strCardID = data.getStringExtra("cardId");
                        strCustomerID = data.getStringExtra("customer_id");
                        cardends = data.getStringExtra("cardends");
                        mTvCard.setText(cardends);
                    } else if (StripePayment.newdelete.equals(strCardID)) {
                        strCardID = "";
                        strCustomerID = "";
                        mTvCard.setText("Add Card");
                    }
                }
            }
            if (requestCode == REQUEST_IMAGE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getParcelableExtra("path");
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        loadProfile(bitmap, uri.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void getVideoCallFees() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        //Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<VideoCallFeeResponse> mService = mApiService.getVideoCallFees(jsonObj);
        mService.enqueue(new Callback<VideoCallFeeResponse>() {
            @Override
            public void onResponse(Call<VideoCallFeeResponse> call, retrofit2.Response<VideoCallFeeResponse> response) {
              //  Utils.closeProgressDialog();
                VideoCallFeeResponse videoCallFee = response.body();

                if (Utils.getStr(videoCallFee.getStatus()).equals(ONE)) {
                    strFinalAmount = Utils.getStr(videoCallFee.getVideoCallFee());
                    strTotalAmount = Utils.getStr(videoCallFee.getVideoCallFee());
                    mTvVideoCallFee.setText("$ " + Utils.getStr(videoCallFee.getVideoCallFee()));
                    mTvSummaryTotalFee.setText("$ " + Utils.getStr(videoCallFee.getVideoCallFee()));
                    mTvFinalFee.setText("$ " + Utils.getStr(videoCallFee.getVideoCallFee()));
                    Utils.showToast(actCon, videoCallFee.getMessage());
                } else if (Utils.getStr(videoCallFee.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    strFinalAmount = "0";
                    strTotalAmount = "0";
                    mTvVideoCallFee.setText("$ " + Utils.getStr(strFinalAmount));
                    mTvSummaryTotalFee.setText("$ " + Utils.getStr(strFinalAmount));
                    mTvFinalFee.setText("$ " + Utils.getStr(strFinalAmount));
                    Utils.showToast(actCon, videoCallFee.getMessage());
                }

            }

            @Override
            public void onFailure(Call<VideoCallFeeResponse> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    private void showDatePopUp() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_choose_appointment_date, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();


    }

    private void showPopUpCompleteHealthRecord() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_view_medical_record, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        TextView tvTitleHealth = alert.findViewById(R.id.title_completed_health_his);
        ImageView close = alert.findViewById(R.id.imv_close);
        RecyclerView recyHealthRecord = alert.findViewById(R.id.recy_health_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(actCon, LinearLayoutManager.VERTICAL, false);
        recyHealthRecord.setLayoutManager(linearLayoutManager);
        ShowHealthRecordAdapter showHealthRecordAdapter = new ShowHealthRecordAdapter(actCon);
        recyHealthRecord.setAdapter(showHealthRecordAdapter);
        showHealthRecordAdapter.setCommonList(resHealthRecord.getData());


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });


    }

    void submitChooseDate() {
        if (Utils.getStr(strBookDate).isEmpty()) {
            Utils.showToast(actCon, getString(R.string.please_choose_date));
            return;
        }

        if (Utils.getStr(strBookTime).isEmpty()) {
            Utils.showToast(actCon, getString(R.string.please_choose_time));
            return;
        }
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("booking_id", " ");
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("booking_date", strBookDate);
        jsonObj.addProperty("booking_time", strBookTime);
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ReScheduleBookingpojoo> mService = mApiService.scheduleDateTime(jsonObj);

        mService.enqueue(new Callback<ReScheduleBookingpojoo>() {
            @Override
            public void onResponse(Call<ReScheduleBookingpojoo> call, Response<ReScheduleBookingpojoo> response) {
                Utils.closeProgressDialog();
                ReScheduleBookingpojoo res = response.body();
                if (Utils.getStr(res.getStatus()).equals(ONE)) {
                    Utils.showToast(actCon, "" + Utils.getStr(res.getMessage()));
                    bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    String displayDate = Utils.dateFormat(strBookDate, Utils.inputOnlyDateSmall, Utils.dateDisplayFormat);
                    tvChooseDate.setText(displayDate + " & " + strBookTime);
                    strDateTime = true;
                } else if (Utils.getStr(res.getStatus()).equals(FOUR_ZERO_ONE)) {
                    bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_HIDDEN);
                    Utils.showSessionAlert(actCon);
                    strBookDate = "";
                    strBookTime = "";
                    strDateTime = false;
                } else {
                    bottomSheetBehaviorDate.setState(BottomSheetBehavior.STATE_HIDDEN);
                    strBookDate = "";
                    strBookTime = "";
                    strDateTime = false;
                    Utils.showToast(actCon, "" + res.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ReScheduleBookingpojoo> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    private void validateAppointment() {
        if (strDentistID.equalsIgnoreCase("")) {
            Utils.showToast(actCon, getString(R.string.error_please_select_dentist));
        } else if (insuranceFlag && (tvInsuranceProvider.getText().toString().equalsIgnoreCase("")
                || tvInsuranceNumber.getText().toString().equalsIgnoreCase(""))) {
            Utils.showToast(actCon, getString(R.string.insurace_add_number));
        } else if (insuranceFlag && (iv_insurance_front.getDrawable() == null || iv_insurance_back.getDrawable() == null)) {
            Utils.showToast(actCon, getString(R.string.add_insurance_images));
        } else if (insuranceFlag && (iv_licence_front.getDrawable() == null || iv_licence_back.getDrawable() == null)) {
            Utils.showToast(actCon, getString(R.string.driver_licence_image));
        } else if (!strDateTime && strBookDate.isEmpty()) {
            Utils.showToast(actCon, getString(R.string.error_please_add_date_time));
        } else if (!strHealthRecordStatus) {
            Utils.showToast(actCon, getString(R.string.error_please_add_health_history));
        } else if (!strSignatureStatus) {
            Utils.showToast(actCon, getString(R.string.error_please_agree_add_digital_Signature));
        } else if (!strFinalAmount.equals("0") && strCardID.isEmpty()) {
            Utils.showToast(actCon, getString(R.string.error_please_add_card));
        } else {
            makeAppointment();
        }
    }


    void makeAppointment() {
        Utils.openProgressDialog(actCon);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<BookingSuccessPojo> mService = null;

        RequestBody requestFile = null;
        RequestBody requestFile1 = null;
        RequestBody requestFile2 = null;
        RequestBody requestFile3 = null;
        RequestBody requestFile4 = null;

        requestFile = RequestBody.create(MediaType.parse("image/jpeg"), UpdateSignature.byteArray);
        body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        if (insuranceFlag) {

            Bitmap bitmap = ((BitmapDrawable) iv_insurance_front.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            ivInsuranceFront = baos.toByteArray();

            Bitmap bitmap1 = ((BitmapDrawable) iv_insurance_back.getDrawable()).getBitmap();
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
            ivInsuranceBack = baos2.toByteArray();

            Bitmap bitmap2 = ((BitmapDrawable) iv_licence_front.getDrawable()).getBitmap();
            ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos3);
            ivLicenceFront = baos3.toByteArray();

            Bitmap bitmap3 = ((BitmapDrawable) iv_licence_back.getDrawable()).getBitmap();
            ByteArrayOutputStream baos4 = new ByteArrayOutputStream();
            bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, baos4);
            ivLicenceback = baos4.toByteArray();


            requestFile1 = RequestBody.create(MediaType.parse("image/jpeg"), ivInsuranceFront);
            requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"), ivInsuranceBack);
            requestFile3 = RequestBody.create(MediaType.parse("image/jpeg"), ivLicenceFront);
            requestFile4 = RequestBody.create(MediaType.parse("image/jpeg"), ivLicenceback);

            body1 = MultipartBody.Part.createFormData("insurance", "image.jpg", requestFile1);
            body2 = MultipartBody.Part.createFormData("insurance_back", "image.jpg", requestFile2);
            body3 = MultipartBody.Part.createFormData("driving_license", "image.jpg", requestFile3);
            body4 = MultipartBody.Part.createFormData("driving_license_back", "image.jpg", requestFile4);
        }

        body1 = null;
        body2 = null;
        body3 = null;
        body4 = null;

        RequestBody rb_specialist_category = RequestBody.create(MediaType.parse("multipart/form-data"), strCategory);
        RequestBody rb_user_id = RequestBody.create(MediaType.parse("multipart/form-data"), appPreference.getUserId());
        RequestBody rb_doctor_id = RequestBody.create(MediaType.parse("multipart/form-data"), strDentistID);
        RequestBody rb_card_id = RequestBody.create(MediaType.parse("multipart/form-data"), strCardID);
        RequestBody rb_customer_id = RequestBody.create(MediaType.parse("multipart/form-data"), strCustomerID);
        RequestBody rb_test_live = RequestBody.create(MediaType.parse("multipart/form-data"), BuildConfig.DEBUG ? "1" : "0");
        RequestBody rb_patient_id = RequestBody.create(MediaType.parse("multipart/form-data"), appPreference.getUserId());
        RequestBody rb_with_insurance = RequestBody.create(MediaType.parse("multipart/form-data"), strPaymentInsurance);
        RequestBody rb_coupon = RequestBody.create(MediaType.parse("multipart/form-data"), strCouponCode);
        RequestBody rb_paidAmount = RequestBody.create(MediaType.parse("multipart/form-data"), strFinalAmount);
        RequestBody rb_totalAmount = RequestBody.create(MediaType.parse("multipart/form-data"), strTotalAmount);
        RequestBody rb_category = RequestBody.create(MediaType.parse("multipart/form-data"), "2");
        RequestBody rb_latitude = RequestBody.create(MediaType.parse("multipart/form-data"), latitude);
        RequestBody rb_longitude = RequestBody.create(MediaType.parse("multipart/form-data"), longitude);
        RequestBody rb_debug = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody rb_by_code = RequestBody.create(MediaType.parse("multipart/form-data"), "1");
        RequestBody rb_health_record_updated = RequestBody.create(MediaType.parse("multipart/form-data"), isHealthRecordUpdated);
        RequestBody rb_platform = RequestBody.create(MediaType.parse("multipart/form-data"), "2"); // 1 web 2 android 3 ios
        RequestBody rb_insurance_pro = null;
        RequestBody rb_insurance_num = null;
        if (insuranceFlag) {
            rb_insurance_pro = RequestBody.create(MediaType.parse("text/plain"), tvInsuranceProvider.getText().toString());
            rb_insurance_num = RequestBody.create(MediaType.parse("text/plain"), tvInsuranceNumber.getText().toString());
        } else {
            rb_insurance_pro = RequestBody.create(MediaType.parse("text/plain"), "");
            rb_insurance_num = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        JSONArray jsonObject1 = new JSONArray(Utils.allergiesList);
        JSONArray jsonObject2 = new JSONArray(Utils.otherAllergiesList);
        JSONArray jsonObject3 = new JSONArray(Utils.medicationsList);
        JSONArray jsonObject4 = new JSONArray(Utils.diagnosedList);
        JSONArray jsonObject5 = new JSONArray(Utils.medicalProceduresList);
        JSONArray jsonObject6 = new JSONArray(Utils.memberDiagnosedList);

        RequestBody rbJsonObject1 = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jsonObject1));
        RequestBody rbJsonObject2 = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jsonObject2));
        RequestBody rbJsonObject3 = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jsonObject3));
        RequestBody rbJsonObject4 = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jsonObject4));
        RequestBody rbJsonObject5 = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jsonObject5));
        RequestBody rbJsonObject6 = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jsonObject6));


        RequestBody rb_booking_date = RequestBody.create(MediaType.parse("multipart/form-data"), strBookDate);
        RequestBody rb_booking_time = RequestBody.create(MediaType.parse("multipart/form-data"), strBookTime);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        RequestBody language = RequestBody.create(MediaType.parse("text/plain"), currentLanguage);
        if (insuranceFlag) {
            mService = mApiService.makeVideoCallAppointmentExistDentistFlow(body, body1, body2,
                    body3, body4, rb_user_id, rb_doctor_id,
                    rb_card_id, rb_customer_id, rb_test_live, rb_patient_id, rb_with_insurance, rb_specialist_category,
                    rb_coupon,
                    rb_totalAmount, rb_paidAmount, rb_category, rb_latitude, rb_longitude, rb_insurance_pro,
                    rb_insurance_num, rb_booking_date, rb_booking_time,
                    rbJsonObject1, rbJsonObject2, rbJsonObject3, rbJsonObject4, rbJsonObject5, rbJsonObject6, rb_debug,
                    rb_by_code, rb_health_record_updated, language, rb_platform);
        } else {
            mService = mApiService.makeVideoCallAppointmentExistDentistFlow(body, null, null,
                    null, null, rb_user_id, rb_doctor_id,
                    rb_card_id, rb_customer_id, rb_test_live, rb_patient_id, rb_with_insurance, rb_specialist_category,
                    rb_coupon,
                    rb_totalAmount, rb_paidAmount, rb_category, rb_latitude, rb_longitude, rb_insurance_pro,
                    rb_insurance_num, rb_booking_date, rb_booking_time,
                    rbJsonObject1, rbJsonObject2, rbJsonObject3, rbJsonObject4, rbJsonObject5, rbJsonObject6, rb_debug,
                    rb_by_code, rb_health_record_updated, language, rb_platform);
        }
        mService.enqueue(new Callback<BookingSuccessPojo>() {
            @Override
            public void onResponse(Call<BookingSuccessPojo> call, Response<BookingSuccessPojo> response) {
                Utils.closeProgressDialog();
                try {
                    BookingSuccessPojo bookingPojoClass = response.body();
                    if (bookingPojoClass != null && bookingPojoClass.getStatus() != null) {
                        if (Utils.getStr(bookingPojoClass.getStatus()).equals("1")) {
                            videoId = bookingPojoClass.getVideo_id();
                            showPopUp("#DENVB000" + videoId);

                        } else if (Utils.getStr(bookingPojoClass.getStatus()).equals("401")) {
                            Utils.showSessionAlert(actCon);
                        } else {
                            Utils.showToast(actCon, Utils.getStr(bookingPojoClass.getMessage().trim()));
                        }

                    } else {
                        Utils.showToast(actCon, ERROR_IN_RESPONSE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showToast(actCon, ERROR_IN_RESPONSE);
                }
            }

            @Override
            public void onFailure(Call<BookingSuccessPojo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(actCon, ERROR_IN_RESPONSE);
                call.cancel();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (alert == null || !alert.isShowing()) {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                OnGPS();
            } else {
                getLocation();
            }
        }

        if (setSignFlag) {
            strSignatureStatus = true;
            tvAgree.setText(getString(R.string.i_agree_consent_signed));
            setSignFlag = false;
        }
    }


    private void OnGPS() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_common, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        alert = dialogBuilder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvContent = dialogView.findViewById(R.id.tvContent);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);
        TextView tvYes = dialogView.findViewById(R.id.tvYes);

        tvTitle.setText("GPS");
        tvContent.setText(R.string.enable_gps);
        tvNo.setText(R.string.cancel);
        tvYes.setText(R.string.yes);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                finish();
            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                actCon, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                actCon, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    latitude = String.valueOf(l.getLatitude());
                    longitude = String.valueOf(l.getLongitude());
                    bestLocation = l;
                }
            }

            if (Utils.getStr(latitude).isEmpty()) {
                getFusedLocation();
            }
        }
    }

    private void getFusedLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void getCompletedHealthRecord(String patient_id) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ResponseViewHealthRecord> mService;
        mService = mApiService.getMedicalRecordView(jsonObj);
        mService.enqueue(new Callback<ResponseViewHealthRecord>() {
            @Override
            public void onResponse(Call<ResponseViewHealthRecord> call, Response<ResponseViewHealthRecord> response) {
                Utils.closeProgressDialog();
                ResponseViewHealthRecord res = response.body();
                if (res.getStatus().equals("1")) {
                    if (res.getData().size() == 0) {
                        resHealthRecord = res;
                        isHealthRecordUpdated = "1";
                        tvAddHealthRecord.setAlpha(1f);
                        tvAddHealthRecord.setEnabled(true);
                        strHealthRecordStatus = false;
                        llCompletedHealthRecord.setVisibility(View.GONE);
                        tvAddHealthRecord.setVisibility(View.VISIBLE);
                    } else {
                        resHealthRecord = res;
                        tvAddHealthRecord.setAlpha(0.5f);
                        tvAddHealthRecord.setEnabled(false);
                        tvAddHealthRecord.setVisibility(View.GONE);
                        isHealthRecordUpdated = "0";
                        strHealthRecordStatus = true;
                        if (Utils.getStr(String.valueOf(res.getExpire_on())).equals("1")) {
                            mTvExpireDays.setText(getString(R.string.your_health_history_will) + " " + String.valueOf(res.getExpire_on()) + " " + getString(R.string.day));
                        } else {
                            mTvExpireDays.setText(getString(R.string.your_health_history_will) + " " + String.valueOf(res.getExpire_on()) + " " + getString(R.string.days));
                        }
                        llCompletedHealthRecord.setVisibility(View.VISIBLE);
                    }

                } else if (res.getStatus().equals("401")) {
                    Utils.showSessionAlert(actCon);
                } else {
                    resHealthRecord = res;
                    isHealthRecordUpdated = "1";
                    tvAddHealthRecord.setAlpha(1f);
                    tvAddHealthRecord.setEnabled(true);
                    strHealthRecordStatus = false;
                    llCompletedHealthRecord.setVisibility(View.GONE);
                    tvAddHealthRecord.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponseViewHealthRecord> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void getScheduleTimings(String date) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        timeList.clear();
        removeRadioButton(timeList);
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("doctor_id", strDentistID);
        jsonObj.addProperty("schedule_date", date);
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<DocAvailableTimeResponse> mService;
        mService = mApiService.availableVideoCallDoctorTiming(jsonObj);
        mService.enqueue(new Callback<DocAvailableTimeResponse>() {
            @Override
            public void onResponse(Call<DocAvailableTimeResponse> call, Response<DocAvailableTimeResponse> response) {
                timeList.clear();
                DocAvailableTimeResponse scheduleDatePojo = response.body();
                //min_seconds = scheduleDatePojo.getMin_seconds();

                if (scheduleDatePojo != null && scheduleDatePojo.getStatus().equals("401")) {
                    Utils.closeProgressDialog();
                    Utils.showSessionAlert(actCon);
                    return;
                }
                if (scheduleDatePojo != null && scheduleDatePojo.getTimings() != null) {
                    for (int i = 0; i < scheduleDatePojo.getTimings().size(); i++) {
                        String available = scheduleDatePojo.getTimings().get(i).getAvailable();
                        if (available.equalsIgnoreCase("1")) {
                            timeList.add(scheduleDatePojo.getTimings().get(i));
                        }
                    }
                    Collections.sort(timeList, new Comparator<TimingsItem>() {

                        @Override
                        public int compare(TimingsItem o1, TimingsItem o2) {
                            try {
                                return new SimpleDateFormat("hh:mm a").parse(o1.getTime()).compareTo(new SimpleDateFormat("hh:mm a").parse(o2.getTime()));
                            } catch (ParseException e) {
                                return 0;
                            }
                        }
                    });
                    System.out.println(timeList);
                    //   timeList = scheduleDatePojo.getTimings();
                    if (!timeList.isEmpty()) {
                        btnConfirmDate.setVisibility(View.VISIBLE);
                        addRadioButtonsForTime(timeList);
                    } else {
                        Toast.makeText(actCon, R.string.no_timing_available, Toast.LENGTH_SHORT).show();
                        btnConfirmDate.setVisibility(View.GONE);
                    }

                }
                Utils.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<DocAvailableTimeResponse> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    public void removeRadioButton(List<TimingsItem> time) {
        rgTime.setVisibility(View.VISIBLE);
        rgTime.setOrientation(LinearLayout.HORIZONTAL);
        rgTime.removeAllViews();
        //
        for (int i = 0; i <= time.size() - 1; i++) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(actCon.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.radio_button_time, null);

// fill in any details dynamically here
            RadioButton rdbtn = (RadioButton) v.findViewById(R.id.rb_time);
// insert into main view
//            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.insert_point);
//            insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            String date = Utils.getStr(time.get(i).getTime());
            rdbtn.setText(date);
            rdbtn.setTag(i);
            rdbtn.setId(View.generateViewId());
            rdbtn.setOnClickListener(this);
            rgTime.removeAllViews();
        }
    }

    public void addRadioButtonsForTime(List<TimingsItem> time) {
        rgTime.setVisibility(View.VISIBLE);
        rgTime.setOrientation(LinearLayout.HORIZONTAL);
        //
        for (int i = 0; i <= time.size() - 1; i++) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(actCon.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.radio_button_time, null);

// fill in any details dynamically here
            RadioButton rdbtn = (RadioButton) v.findViewById(R.id.rb_time);
// insert into main view
//            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.insert_point);
//            insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            String date = Utils.getStr(time.get(i).getTime());
            rdbtn.setText(date);
            rdbtn.setTag(i);
            rdbtn.setId(View.generateViewId());
            rdbtn.setOnClickListener(this);
            rgTime.addView(rdbtn);
        }
    }

    void setSwitchInsurance() {
        switchTouchID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (switchTouchID.isChecked()) {
                    strPaymentInsurance = "1";
                    mLayoutAddInsurance.setVisibility(View.VISIBLE);
                    insuranceFlag = true;
                } else {
                    strPaymentInsurance = "0";
                    mLayoutAddInsurance.setVisibility(View.GONE);
                    insuranceFlag = false;
                }
            }
        });
    }

    void getInsuranceInfo() {
//        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<InsuranceResponse> mService = mApiService.getInsurance(jsonObj);
        mService.enqueue(new Callback<InsuranceResponse>() {
            @Override
            public void onResponse(Call<InsuranceResponse> call, Response<InsuranceResponse> response) {
                InsuranceResponse mResponse = response.body();
                Utils.closeProgressDialog();
                if (mResponse.getStatus().equals("1")) {
                    String tvInsuranceProviderText = mResponse.getInsurance_type();
                    tvInsuranceProvider.setText(mResponse.getInsurance_type());

                    InsuranceNumber = mResponse.getInsurance_number();
                    tvInsuranceNumber.setText(mResponse.getInsurance_number());

                    insurance = mResponse.getInsurance();
                    driving_license = mResponse.getDriving_license();
                    insurance_back = mResponse.getInsurance_back();
                    driving_license_back = mResponse.getDriving_license_back();

                    if (!insurance.equals("")) {
                        status1 = true;
                        Picasso.get().load(insurance).placeholder(R.drawable.image_placeholder).into(iv_insurance_front);
                    }
                    if (!insurance_back.equals("")) {
                        status2 = true;
                        Picasso.get().load(insurance_back).placeholder(R.drawable.image_placeholder).into(iv_insurance_back);
                    }
                    if (!driving_license.equals("")) {
                        status3 = true;
                        Picasso.get().load(driving_license).placeholder(R.drawable.image_placeholder).into(iv_licence_front);
                    }

                    if (!driving_license_back.equals("")) {
                        status4 = true;
                        Picasso.get().load(driving_license_back).placeholder(R.drawable.image_placeholder).into(iv_licence_back);
                    }

                    // JSONArray jsonArray = mResponse.getInsurance_type_master();
                    if (mResponse.getInsurance_type_master() != null) {
                        for (int i = 0; i < mResponse.getInsurance_type_master().size(); i++) {
                            arrayList.add(mResponse.getInsurance_type_master().get(i).getName());
                        }
                    }
                } else if (mResponse.getStatus().equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(VideoAppointmentCheckout.this);
                }

            }

            @Override
            public void onFailure(Call<InsuranceResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    public void selectInsuranceProvider() {
        final CharSequence[] insuranceList = arrayList.toArray(new String[arrayList.size()]);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(VideoAppointmentCheckout.this);
        dialogBuilder.setTitle(R.string.select_insurance_provider);
        dialogBuilder.setItems(insuranceList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = insuranceList[item].toString();  //Selected item in listview
                tvInsuranceProvider.setText(selectedText);
                SpinnerValue = arrayList.get(item);
            }
        });
        android.app.AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    public void setProfilePicture(String string) {
        Dexter.withActivity(actCon)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchic_cameraaIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchic_cameraaIntent() {
        Intent intent = new Intent(actCon, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(actCon, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void loadProfile(Bitmap bitmap, String url) {
        Log.d(">>>>>>>>>>>>>>>>", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>image>>>>>>>>>>>>>>" + images);
        if (images.equals("1")) {
            status1 = true;
            profileImage = true;
            Picasso.get().load(url).into(iv_insurance_front);
            iv_insurance_front.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
        if (images.equals("2")) {
            profileImage = true;
            status2 = true;
            Picasso.get().load(url).into(iv_insurance_back);
            iv_insurance_back.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
        if (images.equals("3")) {
            profileImage = true;
            status3 = true;
            Picasso.get().load(url).into(iv_licence_front);
            iv_licence_front.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
        if (images.equals("4")) {
            profileImage = true;
            status4 = true;
            Picasso.get().load(url).into(iv_licence_back);
            iv_licence_back.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(actCon);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });

        builder.setNegativeButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void getCouponCodeApply(String couponCode) {
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("coupon_code", couponCode);
        jsonObj.addProperty("doctor_id", strDentistID);
        jsonObj.addProperty("category", "2");
        jsonObj.addProperty("specialist_category", "1");
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ResponseCheckPromoCode> mService;
        mService = mApiService.checkCoupon(jsonObj);
        mService.enqueue(new Callback<ResponseCheckPromoCode>() {
            @Override
            public void onResponse(Call<ResponseCheckPromoCode> call, Response<ResponseCheckPromoCode> response) {
                Utils.closeProgressDialog();
                ResponseCheckPromoCode res = response.body();
                if (res != null && res.getStatus() != null) {
                    if (res.getStatus().equals("1")) {
                        Utils.showToast(actCon, res.getMessage());
                        strCouponCode = couponCode;
                        setCouponAppiledAmt(res);
                    } else if (res.getStatus().equals("401")) {
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(actCon, res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseCheckPromoCode> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setCouponAppiledAmt(ResponseCheckPromoCode res) {
        if (res != null) {
            mLayoutEnterCode.setVisibility(View.GONE);
            mLayoutApplyCode.setVisibility(View.VISIBLE);
            if (Integer.parseInt(res.getOffer_value()) > Integer.parseInt(strTotalAmount)) {
                mTvDiscountFee.setText("$ " + Utils.getStr(strTotalAmount));
                mTvFinalFee.setText("$0");
                strFinalAmount = "0";
            } else {
                mTvDiscountFee.setText("$ " + Utils.getStr(res.getOffer_value()));
                mTvFinalFee.setText("$ " + Utils.getDiscount(strTotalAmount, res.getOffer_value()));
                strFinalAmount = Utils.getDiscount(strTotalAmount, res.getOffer_value());
            }
        }

    }

    private void ClearCouponAndResetValue() {
        strCouponCode = "";
        mEditCoupon.setText("");
        mLayoutApplyCode.setVisibility(View.GONE);
        mLayoutEnterCode.setVisibility(View.VISIBLE);
        mTvDiscountFee.setText("$" + "0");
        mTvFinalFee.setText("$" + strTotalAmount);
        strFinalAmount = strTotalAmount;
        isCouponApplied = false;
    }


}
