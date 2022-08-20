package com.app.fitsmile.myaccount;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.profile.ProfileResponse;
import com.app.fitsmile.response.profile.UpdateProfile;
import com.app.fitsmile.utils.ImagePickerActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;

public class EditProfile extends BaseActivity implements View.OnClickListener {
    Boolean isUpdate=false;
    private EditText dob;
    private final int DEFAULT_DATE = 0;
    private int mYear = DEFAULT_DATE, mMonth = DEFAULT_DATE, mDay = DEFAULT_DATE;
    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;
    private EditText mEtPhone;
    private TextView mTvMale;
    private TextView mTvFemale;
    private TextView mTvOther;
    private CountryCodePicker mCountryCodePicker;
    private String gender_preference = "";
    private CircleImageView mProfileImage;
    public static final int REQUEST_IMAGE = 100;
    private byte[] image;
    private boolean newProfileImage = false;
    private Button mBtnUpdateProfile;
    private EditText mEtPassword;
    private CircleImageView mImvAddImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        LocaleManager.setLocale(this);
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
        getSupportActionBar().setTitle(getResources().getString(R.string.title_edit_profile));
    }

    private void initView() {
        dob = findViewById(R.id.et_dob);
        mEtFirstName = findViewById(R.id.et_first_name);
        mEtLastName = findViewById(R.id.et_last_name);
        mEtEmail = findViewById(R.id.et_email);
        mEtPhone = findViewById(R.id.et_phone);
        mTvMale = findViewById(R.id.tv_male);
        mTvFemale = findViewById(R.id.tv_female);
        mTvOther = findViewById(R.id.tv_other);
        mCountryCodePicker = findViewById(R.id.ccp);
        mCountryCodePicker.changeDefaultLanguage(CountryCodePicker.Language.SPANISH);
        mProfileImage = findViewById(R.id.profile_image);
        mBtnUpdateProfile = findViewById(R.id.btn_update_profile);
        mEtPassword = findViewById(R.id.et_password);
        mImvAddImage = findViewById(R.id.imv_add_image);
        mEtPassword.setText("*******");
        mEtPassword.setOnClickListener(this);
        mBtnUpdateProfile.setOnClickListener(this);
        mTvMale.setOnClickListener(this);
        mTvFemale.setOnClickListener(this);
        mTvOther.setOnClickListener(this);
        mImvAddImage.setOnClickListener(this);
        dob.setOnClickListener(v -> {
            if (!isDateSelected()) {
                Calendar currentDate = Calendar.getInstance();
                mYear = currentDate.get(Calendar.YEAR);
                mMonth = currentDate.get(Calendar.MONTH);
                mDay = currentDate.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog mDatePicker = new DatePickerDialog(actCon, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                mDay = selectedDay;
                mMonth = selectedMonth;
                mYear = selectedYear;
                showDate();
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            mDatePicker.show();
            mDatePicker.setOnCancelListener(dialogInterface -> {
                clearSelectedDate();
            });
        });


        if (Utils.isOnline(actCon)) {
            invokeProfileApi();
        } else {
            Utils.showToast(actCon, getString(R.string.please_check_your_network));
        }

    }

    private void clearSelectedDate() {
        mDay = DEFAULT_DATE;
        mMonth = DEFAULT_DATE;
        mYear = DEFAULT_DATE;
        showDate();
    }

    private boolean isDateSelected() {
        return mYear != DEFAULT_DATE && mMonth != DEFAULT_DATE && mDay != DEFAULT_DATE;
    }

    private String getSelectedFormattedDate() {
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, mYear);
        myCalendar.set(Calendar.MONTH, mMonth);
        myCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        return sdf.format(myCalendar.getTime());
    }

    private String getSelectedDateToSend() {
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.YEAR, mYear);
        myCalendar.set(Calendar.MONTH, mMonth);
        myCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        return sdf.format(myCalendar.getTime());
    }

    private void showDate() {
        if (isDateSelected()) {
            dob.setText(getSelectedFormattedDate());
        } else {
            dob.setText("");
        }
    }

    private void invokeProfileApi() {
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ProfileResponse> service = apiInterface.profile(jsonObj);
        service.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null) {
                    ProfileResponse profile = response.body();
                    if (Utils.getStr(profile.getStatus()).equals(ONE)) {
                        bindDataToView(profile);
                    } else if (Utils.getStr(profile.getStatus()).equals(FOUR_ZERO_ONE)) {
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(actCon, "" + Utils.getStr(profile.getMessage()));
                    }
                }
            }


            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    private String modifyNumber(String num, String code) {
        if (num.startsWith(code)) {
            num = num.replaceFirst(code, "");
        }
        return num;
    }

    private void bindDataToView(ProfileResponse profile) {
        mEtFirstName.setText(Utils.getStr(profile.getDatas().getFirstname()));
        mEtLastName.setText(Utils.getStr(profile.getDatas().getLastname()));
        mEtEmail.setText(Utils.getStr(profile.getDatas().getEmail()));
        String mobile = Utils.getStr(profile.getDatas().getMobile());
        String mobile_code = Utils.getStr(profile.getDatas().getMobileCode());
        if (!mobile_code.equals("")) {
            mCountryCodePicker.setCountryForNameCode(mobile_code);
            String countryCode = mCountryCodePicker.getSelectedCountryCode();
            mEtPhone.setText(modifyNumber(mobile, countryCode));
        } else {
            mobile_code = Utils.defaultCountrycode;
            mCountryCodePicker.setCountryForNameCode(mobile_code);
            String countryCode = mCountryCodePicker.getSelectedCountryCode();
            mEtPhone.setText(modifyNumber(mobile, countryCode));
        }

        gender_preference = Utils.getStr(profile.getDatas().getGender());
        if (gender_preference.equalsIgnoreCase("male")) {
            gender_preference = getResources().getString(R.string.male);
            mTvMale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTvMale.setTextColor(getResources().getColor(R.color.white));
            mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
            mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
            mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
            mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (gender_preference.equalsIgnoreCase("female")) {
            gender_preference = getResources().getString(R.string.female);
            mTvFemale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTvFemale.setTextColor(getResources().getColor(R.color.white));
            mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
            mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
            mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
            mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            gender_preference = getResources().getString(R.string.other);
            mTvOther.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTvOther.setTextColor(getResources().getColor(R.color.white));
            mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
            mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
            mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
            mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        String date = Utils.getStr(profile.getDatas().getBirthdayDate());
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        showDate();

        if (Utils.getStr(profile.getDatas().getImage()).isEmpty()) {
            mProfileImage.setImageResource(R.drawable.ic_profile_placeholder_side);
        } else {
            Picasso.get()
                    .load(Utils.getStr(profile.getDatas().getImage()))
                    .placeholder(R.drawable.ic_profile_placeholder_side)
                    .into(mProfileImage);
        }

    }

    private void invokeUpdateProfileAPi() {
        String str_mobile_number = mCountryCodePicker.getSelectedCountryCode()+ mEtPhone.getText().toString();
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        RequestBody requestFile = null;

        if (newProfileImage) {
            Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            image = baos.toByteArray();
            requestFile = RequestBody.create(MediaType.parse("image/jpeg"), image);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), appPreference.getUserId());
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), mEtFirstName.getText().toString());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), mEtLastName.getText().toString());
        RequestBody language = RequestBody.create(MediaType.parse("text/plain"), currentLanguage);

        String gender_type = "";
        if (gender_preference.equalsIgnoreCase("Hombre")) {
            gender_type = "Male";
        } else if (gender_preference.equalsIgnoreCase("Mujer")) {
            gender_type = "Female";
        } else if (gender_preference.equalsIgnoreCase("Otro")) {
            gender_type = "Non Binary";
        }
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), gender_type);
        RequestBody emailId = RequestBody.create(MediaType.parse("text/plain"), mEtEmail.getText().toString());
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), str_mobile_number);
        RequestBody mobile_code = RequestBody.create(MediaType.parse("text/plain"), mCountryCodePicker.getSelectedCountryNameCode());
        MultipartBody.Part body = null;
        if (newProfileImage) {
            body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        }
        String dob = "";
        if (isDateSelected()) {
            dob = getSelectedDateToSend();
        }
        RequestBody date = RequestBody.create(MediaType.parse("text/plain"), dob);
        Call<UpdateProfile> mService;
        if (newProfileImage) {
            mService = mApiService.profileUpdateWithImage(body, user_id, name, last_name, gender, emailId, phone, mobile_code, language,date);
        } else {
            mService = mApiService.profileUpdateWithoutImage(user_id, name, last_name, gender, emailId, phone, mobile_code,language, date);
        }

        mService.enqueue(new Callback<UpdateProfile>() {
            @Override
            public void onResponse(Call<UpdateProfile> call, retrofit2.Response<UpdateProfile> response) {
                if (response.isSuccessful()) {
                    isUpdate=true;
                    Utils.closeProgressDialog();
                    UpdateProfile updateprofile = response.body();
                    if (updateprofile != null && Utils.getStr(updateprofile.getStatus()).equals("1")) {
                        appPreference.setUserName(mEtFirstName.getText().toString());
                        Utils.showToast(actCon, getString(R.string.profile_updated_successfully));
                        try {
                            appPreference.setImage(updateprofile.getImage());
                            Picasso.get()
                                    .load(updateprofile.getImage())
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .into(MainActivity.imvUserPhoto);
                            Picasso.get()
                                    .load(updateprofile.getImage())
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .into(MyAccount.mProfileImage);
                        } catch (Exception e) {

                        }
                    } else if (updateprofile != null && Utils.getStr(updateprofile.getStatus()).equals("401")) {
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(actCon, getString(R.string.updated_failed));
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateProfile> call, Throwable t) {
                Utils.showToast(actCon, t.getMessage());
                Utils.closeProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_profile:
                if (mEtFirstName.getText().toString().isEmpty()) {
                    Utils.showToast(this, getResources().getString(R.string.please_enter_first_name));
                } else if (mEtLastName.getText().toString().isEmpty()) {
                    Utils.showToast(this, getResources().getString(R.string.please_enter_last_name));
                } else if (!isDateSelected()) {
                    Utils.showToast(this, getResources().getString(R.string.please_select_date_of_birth));
                } else if (mEtEmail.getText().toString().isEmpty()) {
                    Utils.showToast(this, getResources().getString(R.string.please_enter_email_address));
                } else if (mEtPhone.getText().toString().isEmpty()) {
                    Utils.showToast(this, getResources().getString(R.string.enter_phone_no));
                } else if (Utils.getStr(gender_preference).isEmpty()) {
                    Utils.showToast(this,  getResources().getString(R.string.please_select_gender));
                } else {
                    invokeUpdateProfileAPi();
                }

                break;
            case R.id.tv_male:
                gender_preference = getResources().getString(R.string.male);
                mTvMale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvMale.setTextColor(getResources().getColor(R.color.white));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.tv_female:
                gender_preference = getResources().getString(R.string.female);
                mTvFemale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvFemale.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.tv_other:
                gender_preference = getResources().getString(R.string.other);
                mTvOther.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvOther.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.et_password:
                startActivity(new Intent(actCon, ChangePassword.class));
                break;
            case R.id.imv_add_image:
                Dexter.withActivity(this)
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
                break;
        }
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
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
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    Picasso.get().load(uri.toString()).into(mProfileImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    image = baos.toByteArray();
                    newProfileImage = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(isUpdate){
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
