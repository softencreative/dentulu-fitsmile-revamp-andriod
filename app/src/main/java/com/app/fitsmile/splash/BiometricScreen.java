package com.app.fitsmile.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.login.LoginResponse;
import com.app.fitsmile.utils.CommanClass;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;

public class BiometricScreen extends BaseActivity implements View.OnClickListener {

    EditText etPassword;
    private static final String TAG = "";
    private BiometricPrompt mBiometricPrompt;
    private String mToBeSignedMessage;
    private static final String KEY_NAME = "test";
    TextView etEmail;
    String userId;
    Button TouchId;
    Button btnLogin, btnSelectLanguage;
    String placeAddress;
    private static final int PERMISSION_REQUEST_CODE = 500;
    TextView textViewNotYou;
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_screen);
        LocaleManager.setLocale(this);
        activity = this;
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        TouchId = findViewById(R.id.btn_login_touch);
        btnLogin = findViewById(R.id.btn_login);
        btnSelectLanguage = findViewById(R.id.btn_select_language);
        btnSelectLanguage.setOnClickListener(this);
        textViewNotYou = findViewById(R.id.tv_not_you);

        if (appPreference.getTouchId().equals(true)) {
            TouchId.setVisibility(View.VISIBLE);
        } else {
            TouchId.setVisibility(View.GONE);
        }

        TouchId.setOnClickListener(this);

        textViewNotYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BiometricScreen.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(this);


        etEmail.setText(appPreference.getEmail());

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, RECORD_AUDIO, CALL_PHONE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        AppController.getInstance().notification();

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();

        userId = status.getSubscriptionStatus().getUserId();


        boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();

        status.getPermissionStatus().getEnabled();

        status.getSubscriptionStatus().getSubscribed();
        status.getSubscriptionStatus().getUserSubscriptionSetting();
        status.getSubscriptionStatus().getUserId();
        status.getSubscriptionStatus().getPushToken();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAffinity();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            Log.e("latitude", "inside loung--" + LONGITUDE);
            addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {
                String l_address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String area = addresses.get(0).getSubLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                Log.d("haida", area + " " + city + " " + country);
                placeAddress = l_address;


            }

            //   Utils.showToast(getApplicationContext(),"");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strAdd;
    }

    void Login() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(BiometricScreen.this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }

        Utils.openProgressDialog(BiometricScreen.this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        String user_id = appPreference.getUserId();
        String password = etPassword.getText().toString();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", user_id);
        jsonObj.addProperty("password", password);
        jsonObj.addProperty("timeZone", TimeZone.getDefault().getID());
        jsonObj.addProperty("language", currentLanguage);
        Call<LoginResponse> mService = mApiService.sessionLogin(jsonObj);
        mService.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse login = response.body();
                if (login.getStatus().equals("1")) {

                    Utils.closeProgressDialog();
                    Utils.showToast(BiometricScreen.this, login.getMessage());
                    startNextScreenAfterAuthentication();
                } else if (login.getStatus().equals("401")) {
                    Utils.closeProgressDialog();
                    Utils.showSessionAlert(activity);
                } else {
                    Utils.closeProgressDialog();
                    Utils.showToast(BiometricScreen.this, login.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                if (!BiometricScreen.this.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();

            }
        });
    }

    private void startNextScreenAfterAuthentication() {
        Intent intent;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    @TargetApi(28)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                if (etPassword.getText().toString().isEmpty()) {
                    Utils.showToast(this, " Please enter your Password  ");
                } else {
                    Utils.hidekeyboard(v, BiometricScreen.this);
                    Login();

                }
                break;
            case R.id.btn_login_touch:
                //AuthenticateBioMetric();
                authenticateApp();
                break;

            case R.id.btn_select_language:
                CommanClass.openChangeLanguagePopup(BiometricScreen.this, "biometric");
                break;
        }
    }

    private void authenticateApp() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
            try {
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {
                    //textView.setText(getResources().getString(R.string.setting_label));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Intent intent;
                    intent = new Intent(BiometricScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Utils.showToast(BiometricScreen.this, getString(R.string.unlock_failed));
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                if (isDeviceSecure()) {
                    Utils.showToast(BiometricScreen.this, getResources().getString(R.string.device_is_secure));
                    authenticateApp();
                } else {
                    Utils.showToast(BiometricScreen.this, getString(R.string.security_device_cancelled));
                    //textView.setText(getResources().getString(R.string.security_device_cancelled));
                }
                break;
        }
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }

    private CancellationSignal getCancellationSignal() {
        // With this cancel signal, we can cancel biometric prompt operation
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //handle cancel result
                Log.i(TAG, "Canceled");
            }
        });
        return cancellationSignal;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        // Callback for biometric authentication result
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @RequiresApi(api = 28)
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                Log.i(TAG, "onAuthenticationSucceeded");
                super.onAuthenticationSucceeded(result);
                Signature signature = result.getCryptoObject().getSignature();
                try {
                    signature.update(mToBeSignedMessage.getBytes());
                    String signatureString = Base64.encodeToString(signature.sign(), Base64.URL_SAFE);
                    // Normally, ToBeSignedMessage and Signature are sent to the server and then verified
                    Log.i(TAG, "Message: " + mToBeSignedMessage);
                    Log.i(TAG, "Signature (Base64 EncodeD): " + signatureString);
                    if (getIntent().getStringExtra("status").equals("0")) {
                        finish();
                    } else {
                        Intent intent;
                        intent = new Intent(BiometricScreen.this, MainActivity.class);
                        startActivity(intent);

                    }
                    //
                    //  Utils.showToast(getApplicationContext(), mToBeSignedMessage + ":" + signatureString);
                } catch (SignatureException e) {
                    throw new RuntimeException();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        };
    }


    private boolean isSupportBiometricPrompt() {
        PackageManager packageManager = this.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private KeyPair generateKeyPair(String keyName, boolean invalidatedByBiometricEnrollment) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                KeyProperties.PURPOSE_SIGN)
                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512)
                // Require the user to authenticate with a biometric to authorize every use of the key
                .setUserAuthenticationRequired(true)
                // Generated keys will be invalidated if the biometric templates are added more to user device
                .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);

        keyPairGenerator.initialize(builder.build());

        return keyPairGenerator.generateKeyPair();
    }

    @Nullable
    private KeyPair getKeyPair(String keyName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(keyName)) {
            // Get public key
            PublicKey publicKey = keyStore.getCertificate(keyName).getPublicKey();
            // Get private key
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, null);
            // Return a key pair
            return new KeyPair(publicKey, privateKey);
        }
        return null;
    }

    @Nullable
    private Signature initSignature(String keyName) throws Exception {
        KeyPair keyPair = getKeyPair(keyName);

        if (keyPair != null) {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(keyPair.getPrivate());
            return signature;
        }
        return null;
    }

    @RequiresApi(api = 28)
    void RegisterBioMetric() {
        if (isSupportBiometricPrompt()) {
            Log.i(TAG, "Try registration");
            // Generate keypair and init signature
            Signature signature;
            try {
                // Before generating a key pair, we have to check enrollment of biometrics on the device
                // But, there is no such method on new biometric prompt API

                // Note that this method will throw an exception if there is no enrolled biometric on the device
                // This issue is reported to Android issue tracker
                // https://issuetracker.google.com/issues/112495828
                KeyPair keyPair = generateKeyPair(KEY_NAME, true);
                // Send public key part of key pair to the server, this public key will be used for authentication
                mToBeSignedMessage = new StringBuilder()
                        .append(Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.URL_SAFE))
                        .append(":")
                        .append(KEY_NAME)
                        .append(":")
                        // Generated by the server to protect against replay attack
                        .append("")
                        .toString();

                signature = initSignature(KEY_NAME);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Create biometricPrompt
            mBiometricPrompt = new BiometricPrompt.Builder(this)
                    .setDescription("Description")
                    .setTitle("Title")
                    .setSubtitle("Subtitle")
                    .setNegativeButton("Cancel", getMainExecutor(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i(TAG, "Cancel button clicked");
                        }
                    })
                    .build();
            CancellationSignal cancellationSignal = getCancellationSignal();
            BiometricPrompt.AuthenticationCallback authenticationCallback = getAuthenticationCallback();

            // Show biometric prompt
            if (signature != null) {
                Log.i(TAG, "Show biometric prompt");
                mBiometricPrompt.authenticate(new BiometricPrompt.CryptoObject(signature), cancellationSignal, getMainExecutor(), authenticationCallback);
            }
        }
    }

    @RequiresApi(api = 28)
    void AuthenticateBioMetric() {
        if (isSupportBiometricPrompt()) {
            Log.i(TAG, "Try authentication");

            // Init signature
            Signature signature;
            try {
                // Send key name and challenge to the server, this message will be verified with registered public key on the server
                mToBeSignedMessage = new StringBuilder()
                        .append(KEY_NAME)
                        .append(":")
                        // Generated by the server to protect against replay attack
                        .append("")
                        .toString();
                signature = initSignature(KEY_NAME);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Create biometricPrompt
            mBiometricPrompt = new BiometricPrompt.Builder(this)
                    .setDescription("Description")
                    .setTitle("Title")
                    .setSubtitle("Subtitle")
                    .setNegativeButton("Cancel", getMainExecutor(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i(TAG, "Cancel button clicked");
                        }
                    })
                    .build();
            CancellationSignal cancellationSignal = getCancellationSignal();
            BiometricPrompt.AuthenticationCallback authenticationCallback = getAuthenticationCallback();

            // Show biometric prompt
            if (signature != null) {
                Log.i(TAG, "Show biometric prompt");
                mBiometricPrompt.authenticate(new BiometricPrompt.CryptoObject(signature), cancellationSignal, getMainExecutor(), authenticationCallback);
            } else {
                RegisterBioMetric();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appPreference.getTouchId().equals(true)) {
            TouchId.setVisibility(View.VISIBLE);
        } else {
            TouchId.setVisibility(View.GONE);
        }
    }

}

