package com.app.fitsmile.insurance;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.insurance.InsuranceResponse;
import com.app.fitsmile.response.myaccount.Updateprofile;
import com.app.fitsmile.utils.ImagePickerActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.BuildConfig.BASE_URL;


public class InsuranceActivity extends BaseActivity {

    private EditText tvInsuranceNumber;
    InputStream is;
    ArrayList<String> arrayList = new ArrayList<>();
    TextView tvInsuranceProvider;

    private ImageView iv_insurance_front;
    private ImageView iv_insurance_back;
    private ImageView iv_licence_front;
    private ImageView iv_licence_back;
    MultipartBody.Part body1 = null;
    MultipartBody.Part body2 = null;
    MultipartBody.Part body3 = null;
    MultipartBody.Part body4 = null;

    byte[] ivInsuranceFront, ivInsuranceBack, ivLicenceFront, ivLicenceback;

    String InsuranceNumber = "";
    String images = "";
    String image_name = "";
    String driving_license = "";
    String insurance_back = "";

    ArrayAdapter arrayAdapter;

    String insurance = "";
    String driving_license_back = "";
    String SpinnerValue = "";
    Boolean profileImage = false;
    private Button btnUploadInsurance;
    Activity activity;
    boolean status1 = false;
    boolean status2 = false;
    boolean status3 = false;
    boolean status4 = false;
    CardView mCardImburse;

    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);
        LocaleManager.setLocale(this);
        activity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("Insurance");

        tvInsuranceProvider = findViewById(R.id.textSelectProvider);
        tvInsuranceNumber = (EditText) findViewById(R.id.editInsuranceNumber);
        iv_insurance_front = (ImageView) findViewById(R.id.iv_insurance_front);
        iv_insurance_back = (ImageView) findViewById(R.id.iv_insurance_back);
        iv_licence_front = (ImageView) findViewById(R.id.iv_licence_front);
        iv_licence_back = (ImageView) findViewById(R.id.iv_licence_back);
        mCardImburse = findViewById(R.id.select_reimburse);

        btnUploadInsurance = (Button) findViewById(R.id.btn_upload_insurance);


        getInsuranceInfo();

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

        btnUploadInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvInsuranceProvider.getText().toString().isEmpty() || tvInsuranceProvider.getText().toString().equals("Select Insurance Provider")) {
                    Utils.showToast(InsuranceActivity.this, getResources().getString(R.string.insurance_provider_required));
                } else if (tvInsuranceNumber.getText().toString().isEmpty()) {
                    Utils.showToast(InsuranceActivity.this, getResources().getString(R.string.insurance_number_required));
                } else if (!status1 || !status2) {
                    Utils.showToast(InsuranceActivity.this, getResources().getString(R.string.card_Attachment_required));
                } else if (!status3 || !status4) {
                    Utils.showToast(InsuranceActivity.this, getResources().getString(R.string.driving_licence_required));
                } else {
                    uploadFile();
                }
            }
        });

        mCardImburse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectReimburse();
            }
        });
    }

    public void selectInsuranceProvider() {
        final CharSequence[] insuranceList = arrayList.toArray(new String[arrayList.size()]);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(InsuranceActivity.this);
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


    void getInsuranceInfo() {
        Utils.openProgressDialog(activity);
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
                Utils.closeProgressDialog();
                InsuranceResponse mResponse=response.body();
                try {
                    if(mResponse.getStatus().equals("1")) {
                        String tvInsuranceProviderText = mResponse.getInsurance_type();
                        if (mResponse.getInsurance_type().equals("")){
                            tvInsuranceProvider.setText(R.string.select_insurance_provider);
                        }else {
                            tvInsuranceProvider.setText(mResponse.getInsurance_type());
                        }
                        InsuranceNumber =  mResponse.getInsurance_number();
                        tvInsuranceNumber.setText(mResponse.getInsurance_number());

                        insurance = mResponse.getInsurance();
                        driving_license = mResponse.getDriving_license();
                        insurance_back = mResponse.getInsurance_back();
                        driving_license_back = mResponse.getDriving_license_back();

                        if (!insurance.equals("")){
                            status1=true;
                            Picasso.get().load(insurance).placeholder(R.drawable.image_placeholder).into(iv_insurance_front);
                        }
                        if (!insurance_back.equals("")) {
                            status2=true;
                            Picasso.get().load(insurance_back).placeholder(R.drawable.image_placeholder).into(iv_insurance_back);
                        }
                        if (!driving_license.equals("")){
                            status3=true;
                            Picasso.get().load(driving_license).placeholder(R.drawable.image_placeholder).into(iv_licence_front);}

                        if (!driving_license_back.equals("")){
                            status4=true;
                            Picasso.get().load(driving_license_back).placeholder(R.drawable.image_placeholder).into(iv_licence_back);
                        }

                       // JSONArray jsonArray = mResponse.getInsurance_type_master();
                        for (int i = 0; i<mResponse.getInsurance_type_master().size(); i++){
                            arrayList.add(mResponse.getInsurance_type_master().get(i).getName());
                        }
                    }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                        Utils.showSessionAlert(InsuranceActivity.this);
                    }

                } catch (Exception ex) {
                    System.out.println("EXCPTION IN SUCCESS OF LOGIN REQUEST : " + ex.toString());
                }
            }

            @Override
            public void onFailure(Call<InsuranceResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }


    private void uploadFile() {
        Utils.openProgressDialog(activity);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        RequestBody requestFile = null;
        RequestBody requestFile2 = null;
        RequestBody requestFile3 = null;
        RequestBody requestFile4 = null;

        if (profileImage) {
            Bitmap bitmap1 = ((BitmapDrawable) iv_insurance_front.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            ivInsuranceFront = baos.toByteArray();

            Bitmap bitmap2 = ((BitmapDrawable) iv_insurance_back.getDrawable()).getBitmap();
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
            ivInsuranceBack = baos2.toByteArray();

            Bitmap bitmap3 = ((BitmapDrawable) iv_licence_front.getDrawable()).getBitmap();
            ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
            bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, baos3);
            ivLicenceFront = baos3.toByteArray();

            Bitmap bitmap4 = ((BitmapDrawable) iv_licence_back.getDrawable()).getBitmap();
            ByteArrayOutputStream baos4 = new ByteArrayOutputStream();
            bitmap4.compress(Bitmap.CompressFormat.JPEG, 100, baos4);
            ivLicenceback = baos4.toByteArray();

            requestFile = RequestBody.create(MediaType.parse("image/jpeg"), ivInsuranceFront);
            requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"), ivInsuranceBack);
            requestFile3 = RequestBody.create(MediaType.parse("image/jpeg"), ivLicenceFront);
            requestFile4 = RequestBody.create(MediaType.parse("image/jpeg"), ivLicenceback);

            body1 = MultipartBody.Part.createFormData("insurance", "image.jpg", requestFile);
            body2 = MultipartBody.Part.createFormData("insurance_back", "image.jpg", requestFile2);
            body3 = MultipartBody.Part.createFormData("driving_license", "image.jpg", requestFile3);
            body4 = MultipartBody.Part.createFormData("driving_license_back", "image.jpg", requestFile4);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), appPreference.getUserId());

        RequestBody insurancepro = RequestBody.create(MediaType.parse("text/plain"), SpinnerValue);
        RequestBody insurancenum = RequestBody.create(MediaType.parse("text/plain"), tvInsuranceNumber.getText().toString());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        RequestBody language = RequestBody.create(MediaType.parse("text/plain"), currentLanguage);
        Call<Updateprofile> mService = null;
        mService = mApiService.updateSignaturea(body1, body2, body3, body4, user_id, insurancepro, insurancenum,language);
        mService.enqueue(new Callback<Updateprofile>() {
            @Override
            public void onResponse(Call<Updateprofile> call, retrofit2.Response<Updateprofile> response) {
                if (response.isSuccessful()) {

                    Utils.closeProgressDialog();
                    Updateprofile updateprofile = response.body();
                    if (response.body().getStatus().equals("1")) {
                        Utils.showToast(InsuranceActivity.this, updateprofile.getMessage());
                    }else if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                        Utils.showSessionAlert(InsuranceActivity.this);
                    }
                    else {
                        Utils.showToast(InsuranceActivity.this, "Update Failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<Updateprofile> call, Throwable t) {
                Utils.showToast(InsuranceActivity.this, t.getMessage());


                Utils.closeProgressDialog();

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setProfilePicture(String string) {
        Dexter.withActivity(activity)
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
        Intent intent = new Intent(activity, ImagePickerActivity.class);
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
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                    loadProfile(bitmap, uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    public void selectReimburse() {
        List<Spanned> mListReImburse = new ArrayList<>();
        String sourceString = "<b>" + "1. Obtain Itemized Receipt." + "</b> " + "An itemized receipt can be obtained after the completion of your visit. An Itemized receipt lists all the procedures provided as well as the pricing.";
        mListReImburse.add(Html.fromHtml(sourceString));

        String sourceString2 = "<b>" + "2. Grab your Claim Form. " + "</b> " + "You’ll need to fill out a health insurance claim form. You can find our claim form on our website under the “Forms” section. This form requires basic health information such as your insurance information, your reason for the visit, etc.";
        mListReImburse.add(Html.fromHtml(sourceString2));

        String sourceString3 = "<b>" + "3. Make a Copy" + "</b> " + " Make sure to make a copy of your Claim Form for record purposes. This same Claim Form will be submitted to the Insurance Agency to reimburse your visit. Making copies will be beneficial for both you and the Insurance Agency should there be issues for any reason.";
        mListReImburse.add(Html.fromHtml(sourceString3));

        String sourceString4 = "<b>" + "4. Review & Send." + "</b> " + "Read and review the itemized receipt, collect and attach all required documents and send in the claim to get reimbursed. Claims generally take about 30 business" +
                " days to process. We recommend calling the number on the back of your Insurance card a week after submission to ensure all " +
                "required documents were received. To expedite the process, you can email the claim - the email address for the Insurance Agency " +
                "can be found on the form.";
        mListReImburse.add(Html.fromHtml(sourceString4));

        final CharSequence[] insuranceList = mListReImburse.toArray(new Spanned[mListReImburse.size()]);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(InsuranceActivity.this);
        dialogBuilder.setTitle(R.string.insurance_reimbursed);
        dialogBuilder.setItems(insuranceList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

            }
        });
        android.app.AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

}

