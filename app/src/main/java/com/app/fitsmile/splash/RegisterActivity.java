package com.app.fitsmile.splash;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.login.LoginResponse;
import com.app.fitsmile.response.register.RegistrationResponse;
import com.app.fitsmile.utils.CommanClass;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.ONE;
import static com.app.fitsmile.app.AppConstants.ZERO;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnRegister,btnSelectLanguage;
    private EditText mEtFirstName;
    private EditText mEtLastName;
    private static EditText mEtDOB;
    private EditText mEtEmail;
    private EditText mEtPwd;
    private EditText mEtConfirmPwd;
    private EditText mEtMobile;
    private CountryCodePicker ccp;
    static String dateApi="";

    private TextView mTvMale, mTvFemale, mTvOther;
    private String mStrGender = "";

    /*Country Code*/
    String strCountryCode = "US";
    String strMobileCode = "1";
    boolean flagCountry = false;

    String mStrDeviceIP="";
    String mStrDeviceName="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        LocaleManager.setLocale(this);
        init();
    }

    private void init() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mStrDeviceIP = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        mStrDeviceName = android.os.Build.MODEL;

        mBtnRegister = findViewById(R.id.btn_register);
        mEtFirstName = findViewById(R.id.et_first_name);
        mEtLastName = findViewById(R.id.et_last_name);
        mEtDOB = findViewById(R.id.et_dob);
        mTvMale = findViewById(R.id.tv_male);
        mTvFemale = findViewById(R.id.tv_female);
        mTvOther = findViewById(R.id.tv_other);
        mEtEmail = findViewById(R.id.et_email);
        mEtMobile = findViewById(R.id.et_phone);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtConfirmPwd = findViewById(R.id.et_confirm_pwd);
        btnSelectLanguage=findViewById(R.id.btn_select_language);
        btnSelectLanguage.setOnClickListener(this);
        ccp = findViewById(R.id.ccp);

        mTvMale.setOnClickListener(this);
        mTvFemale.setOnClickListener(this);
        mTvOther.setOnClickListener(this);
        mEtDOB.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                flagCountry = true;
                strCountryCode = ccp.getSelectedCountryNameCode();
                strMobileCode = ccp.getSelectedCountryCode();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_male:
                mStrGender = getResources().getString(R.string.male);
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_bottom_white));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_white_stroke));
                mTvFemale.setTextColor(getResources().getColor(R.color.white));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_white_stroke));
                mTvOther.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_female:
                mStrGender = getResources().getString(R.string.female);
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_bottom_white));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_white_stroke));
                mTvMale.setTextColor(getResources().getColor(R.color.white));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_white_stroke));
                mTvOther.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_other:
                mStrGender = getResources().getString(R.string.other);
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_bottom_white));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_white_stroke));
                mTvMale.setTextColor(getResources().getColor(R.color.white));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_white_stroke));
                mTvFemale.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.et_dob:
                DialogFragment dFragment = new DatePickerFragment();
                dFragment.show(getSupportFragmentManager(), "Date Picker");
                break;
            case R.id.btn_register:
                if (validateSignUp()) {
                    Utils.openProgressDialog(actCon);
                    String mobile_number = strMobileCode + mEtMobile.getText().toString();
                    register(mEtFirstName.getText().toString(), mEtLastName.getText().toString(), dateApi, mStrGender,
                            mEtEmail.getText().toString(), mEtPwd.getText().toString(), mobile_number, "", "", "");
                }
                break;

            case R.id.btn_select_language:
                CommanClass.openChangeLanguagePopup(RegisterActivity.this,"register");
                break;

        }
    }


    private boolean validateSignUp() {
        if (mEtFirstName.getText().toString().isEmpty()) {
            Utils.showToast(this, getString(R.string.please_enter_first_name));
            return false;
        } else if (mEtLastName.getText().toString().isEmpty()) {
            Utils.showToast(this, getString(R.string.please_enter_last_name));
            return false;
        } else if (dateApi.isEmpty()) {
            Utils.showToast(this, getString(R.string.please_select_date_of_birth));
            return false;
        } else if (mStrGender.isEmpty()) {
            Utils.showToast(this, getString(R.string.please_select_gender));
            return false;
        } else if (mEtEmail.getText().toString().isEmpty()) {
            Utils.showToast(this, getString(R.string.please_enter_email_address));
            return false;
        } else if (!Utils.isValidEmail(mEtEmail.getText().toString())) {
            Utils.showToast(this, getString(R.string.invalid_email));
            return false;
        } else if (mEtPwd.getText().toString().isEmpty()) {
            Utils.showToast(this, getString(R.string.please_enter_password));
            return false;
        } else if (mEtConfirmPwd.getText().toString().isEmpty()) {
            Utils.showToast(this, getString(R.string.please_enter_confirm_password));
            return false;
        } else if (!mEtPwd.getText().toString().equals(mEtConfirmPwd.getText().toString())) {
            Utils.showToast(this, getString(R.string.password_does_not_match));
            return false;
        } else if (mEtMobile.getText().toString().isEmpty()) {
            Utils.showToast(this, getString(R.string.please_enter_mobile_number));
            return false;
        }
        return true;
    }

    public void register(String Name, String lastName, String dob, String gender, final String Email, final String Password, String Phoneno, String notificationId, String here, String what) {
        String currentLanguage = "";
        LocaleManager.getLanguagePref(this);
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("name", Name);
        jsonObj.addProperty("lastname", lastName);
        jsonObj.addProperty("dob", dob);
        jsonObj.addProperty("gender", gender);
        jsonObj.addProperty("email", Email);
        jsonObj.addProperty("password", Password);
        jsonObj.addProperty("mobile", Phoneno);
        jsonObj.addProperty("notification_id", notificationId);
        jsonObj.addProperty("hear", here);
        jsonObj.addProperty("reason", what);
        jsonObj.addProperty("mobile_code", strCountryCode);
        jsonObj.addProperty("language", currentLanguage);


        ApiInterface mApiService =retrofit.create(ApiInterface.class);
        Call<RegistrationResponse> mService = mApiService.register(jsonObj);
        mService.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                Utils.closeProgressDialog();
                RegistrationResponse registration = response.body();
                if (Utils.getStr(registration.getStatus()).equalsIgnoreCase("1")) {
                    successPopup(registration.getMessage(), Email, Password);
                } else {
                    Utils.showToast(actCon, Utils.getStr(registration.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    public void successPopup(String message, final String email, final String pwd) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_common, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvContent = dialogView.findViewById(R.id.tvContent);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);
        TextView tvYes = dialogView.findViewById(R.id.tvYes);

        tvTitle.setText(getString(R.string.app_name));
        tvContent.setText(message);

        tvNo.setVisibility(View.GONE);
        tvYes.setText(getString(R.string.got_it));

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                invokeLoginApi(email, pwd);
            }
        });
    }

    private void moveToHome() {
        Intent intent = new Intent(actCon, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void invokeLoginApi(String email, String pwd) {
        String currentLanguage = "";
        LocaleManager.getLanguagePref(this);
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }

        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("email", email);
        jsonObj.addProperty("password", pwd);
        jsonObj.addProperty("notification_id", "");
        jsonObj.addProperty("deviceIP", mStrDeviceIP);
        jsonObj.addProperty("deviceName", mStrDeviceName);
        jsonObj.addProperty("timeZone", TimeZone.getDefault().getID());
        jsonObj.addProperty("language", currentLanguage);

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LoginResponse> service = apiInterface.login(jsonObj);
        service.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Utils.closeProgressDialog();
                if(response.body()!=null) {
                    LoginResponse login = response.body();
                    if(Utils.getStr(login.getStatus()).equals(ONE)&& login.getOtpStatus().equals(ZERO)){
                        saveDataAndMoveToHome(login,email);
                    }else{
                        Utils.showToast(actCon, "" + Utils.getStr(login.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    private void saveDataAndMoveToHome(LoginResponse login, String email) {

        appPreference.setloginvalue("0");
        appPreference.setUserId(login.getUserId());
        appPreference.setreferalcode(login.getReferCode());
        appPreference.setUserName(login.getFirstname());
        appPreference.setToken(login.getToken());
        appPreference.setIsAuthentication(login.getSessionToken());
        appPreference.setImage(login.getImage());
        appPreference.setEmail(email);
        appPreference.setisLoggedIn(true);
        appPreference.setByCode(login.getByCode());
        appPreference.setProviderId(login.getProviderId());
        appPreference.setProviderLevel(login.getProviderLevel());
        appPreference.setDentalOfficeLogo(login.getLogo());

        moveToHome();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            calendar.add(Calendar.YEAR, -70);
            dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chosenDate = cal.getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            dateApi = df.format(chosenDate);
            mEtDOB.setText(df2.format(chosenDate));
        }
    }

}
