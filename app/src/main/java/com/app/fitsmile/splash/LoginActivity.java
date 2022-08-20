package com.app.fitsmile.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.firebase_chat.InitializeListener;
import com.app.fitsmile.response.login.LoginResponse;
import com.app.fitsmile.utils.CommanClass;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.ONE;
import static com.app.fitsmile.app.AppConstants.ZERO;
import static com.app.fitsmile.common.Utils.closeProgressDialog;
import static com.app.fitsmile.common.Utils.showToast;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtnLogin, btnSelectLanguage;
    private String mStrDeviceIP = "";
    private String mStrDeviceName = "";
    private EditText mEtEmail;
    private EditText mEtPwd;
    private final String mStrEmailPattern = "[a-zA-Z0-9._+-]+@[a-z]+\\.+[a-z]+";
    private TextView mTvForgotPassword;
    private TextView mTvNewUser;
    CheckBox mCheckTouchID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LocaleManager.setLocale(this);
        initView();

    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void initView() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mStrDeviceIP = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        mStrDeviceName = android.os.Build.MODEL;

        mBtnLogin = findViewById(R.id.btn_login);
        mEtEmail = findViewById(R.id.email);
        mEtPwd = findViewById(R.id.password);
        mTvForgotPassword = findViewById(R.id.tv_forget_pwd);
        mTvNewUser = findViewById(R.id.tv_new_user);
        mCheckTouchID = findViewById(R.id.checkTouch);

        TextView tvLogin = findViewById(R.id.txt_login);
        btnSelectLanguage = findViewById(R.id.btn_select_language);
        btnSelectLanguage.setOnClickListener(this);


        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                String text = "OneSignal UserID:\n" + userId + "\n\n";
                if (registrationId != null)
                    text += "Google Registration Id:\n" + registrationId;
                else
                    text += "Google Registration Id:\nCould not subscribe for push";
            }
        });

        mBtnLogin.setOnClickListener(this);
        mTvForgotPassword.setOnClickListener(this);
        mTvNewUser.setOnClickListener(this);
        mCheckTouchID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    appPreference.setTouchId(true);
                } else {
                    appPreference.setTouchId(false);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                validateLogin();
                break;
            case R.id.tv_forget_pwd:
                moveToForgot();
                break;
            case R.id.tv_new_user:
                moveToRegister();
                break;
            case R.id.btn_select_language:
                CommanClass.openChangeLanguagePopup(LoginActivity.this, "login");
                break;

        }
    }

    private void moveToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void validateLogin() {
        if (mEtEmail.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.please_enter_email_address));
//        } else if (!mEtEmail.getText().toString().matches(mStrEmailPattern)) {
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString()).matches()) {
            showToast(LoginActivity.this, getResources().getString(R.string.invalid_email));
        } else if (mEtPwd.getText().toString().isEmpty()) {
            showToast(this, getResources().getString(R.string.please_enter_password));
        } else {
            Utils.hidekeyboard(mEtPwd, LoginActivity.this);
            if (Utils.isOnline(LoginActivity.this)) {
                invokeLoginApi(mEtEmail.getText().toString(), mEtPwd.getText().toString());
            } else {
                showToast(LoginActivity.this, getResources().getString(R.string.please_check_your_network));
            }
        }
    }

    private void moveToHome(int loginCount, String id) {
        //loginCount == 1 &&
//        if (id.equals("")) {
//            Intent intent = new Intent(LoginActivity.this, FitsReminderActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(AppConstants.OPEN_FROM, 1);
//            startActivity(intent);
//            finish();
//        } else {
        // LocaleManager.setLocale(this);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        // }
    }

    private void moveToForgot() {
        Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
        startActivity(intent);
    }

    private void invokeLoginApi(String email, String pwd) {
        Utils.openProgressDialog(actCon);

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String notificationId = status.getSubscriptionStatus().getUserId();
        String currentLanguage = "";
        LocaleManager.getLanguagePref(this);
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("email", email);
        jsonObj.addProperty("password", pwd);
        jsonObj.addProperty("notification_id", notificationId);
        jsonObj.addProperty("deviceIP", mStrDeviceIP);
        jsonObj.addProperty("deviceName", mStrDeviceName);
        jsonObj.addProperty("timeZone", TimeZone.getDefault().getID());
        jsonObj.addProperty("platform", 2);
        jsonObj.addProperty("language", currentLanguage);

        Log.e("onesignalnotificationid", notificationId);

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LoginResponse> service = apiInterface.login(jsonObj);
        service.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null) {
                    LoginResponse login = response.body();
                    if (Utils.getStr(login.getStatus()).equals(ONE) && login.getOtpStatus().equals(ZERO)) {
                        saveData(login, email);
                        ((AppController) getApplication()).initAfterLogin(new InitializeListener() {
                            @Override
                            public void onInitialized() {
                                closeProgressDialog();
                                moveToHome(login.getLoginCount(), login.getFitMinderId());
                            }

                            @Override
                            public void onError(String message) {
                                closeProgressDialog();
                                showToast(LoginActivity.this, message);
                                moveToHome(login.getLoginCount(), login.getFitMinderId());

                            }
                        });
                    } else {
                        showToast(actCon, "" + Utils.getStr(login.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                closeProgressDialog();
                call.cancel();
            }
        });
    }

    private void saveData(LoginResponse login, String email) {
        appPreference.setloginvalue("0");
        appPreference.setUserId(login.getUserId());
        appPreference.setreferalcode(login.getReferCode());
        appPreference.setUserName(login.getFirstname());
        appPreference.setLastName(login.getLastname());
        appPreference.setFirebaseCustomToken(login.getFirebaseCustomToken());
        appPreference.setFirebaseUID(login.getFirebaseUid());
        appPreference.setDentalOfficeFirebaseUID(login.getDentalOfficeFirebaseUid());
        appPreference.setToken(login.getToken());
        appPreference.setIsAuthentication(login.getSessionToken());
        appPreference.setImage(login.getImage());
        appPreference.setEmail(email);
        appPreference.setisLoggedIn(true);
        appPreference.setFitsReminderId(login.getFitMinderId());
        appPreference.setRewardPoint(login.getRewardAmount());
        appPreference.setByCode(login.getByCode());
        appPreference.setProviderId(login.getProviderId());
        appPreference.setProviderLevel(login.getProviderLevel());
        appPreference.setDentalOfficeLogo(login.getLogo());
        appPreference.setLoginCount(login.getLoginCount());
    }
}
