package com.app.fitsmile.book;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.utils.LocaleManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class UpdateSignature extends BaseActivity {

    Activity activity;
    EditText fullName;
    private String booking_id = "0";
    public static String path = "";
    Bitmap bitmap;
    public static byte[] byteArray;
    public static String paths = "";
    private static final String IMAGE_DIRECTORY = "/signdemo";
    String isService = "";

    TextView save,cancel;

    Boolean isSignature = false;
    String strType = "";

    TextView tv_signature_view;
    String fullUserName= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        LocaleManager.setLocale(this);
        activity = this;
        fullName = findViewById(R.id.fullName);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("Signature");
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        isService = getIntent().getStringExtra("servicebooking");
        booking_id = getIntent().getStringExtra("booking_id");
        strType = getIntent().getStringExtra("type");
        if (strType != null && strType.contains("bycode")) {
            save.setText(R.string.sign);
        }

        tv_signature_view = findViewById(R.id.tv_signature_view);

        String userName = appPreference.getUserName()+" "+appPreference.getLastName();
        fullName.setText(userName);
        tv_signature_view.setText(userName);
        Utils.setSignatureText(activity, tv_signature_view);


        tv_signature_view.setMovementMethod(new ScrollingMovementMethod());

        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //updateNameListener.updateFullName(fullName.getText().toString());
                fullUserName = fullName.getText().toString();
                tv_signature_view.setText(fullUserName);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullName.getText().toString().isEmpty()) {
                    Utils.showToast(UpdateSignature.this, "Please enter your name");
                } else {
                    tv_signature_view.buildDrawingCache();
                    bitmap = tv_signature_view.getDrawingCache();
                    path = saveImage(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byteArray = baos.toByteArray();
                    bitmap.recycle();
                    if (isService.equals("0")) {
                        uploadFile();
                    } else {
                        VideoAppointmentCheckout.setSignFlag=true;
                        finish();
                    }
                }
            }

        });
    }



    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY /*iDyme folder*/);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            Log.d("hhhhh", wallpaperDirectory.toString());
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(UpdateSignature.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            paths = f.getAbsolutePath();
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
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

    private void uploadFile() {
        Utils.openProgressDialog(this);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), appPreference.getUserId());
        RequestBody booking_ids = RequestBody.create(MediaType.parse("text/plain"), "0");
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        RequestBody language = RequestBody.create(MediaType.parse("text/plain"), currentLanguage);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.getSignature(body, user_id, booking_ids,language);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    Utils.closeProgressDialog();
                    CommonResponse updateprofile = response.body();
                    if (updateprofile != null && Utils.getStr(updateprofile.getStatus()).equals("1")) {
                        Utils.showToast(UpdateSignature.this, updateprofile.getMessage());
//                        Myaccounts();
                        VideoAppointmentCheckout.setSignFlag = true;
                        finish();

                    } else if (updateprofile != null && Utils.getStr(updateprofile.getStatus()).equals("401")) {
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(UpdateSignature.this, "Update Failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Utils.showToast(UpdateSignature.this, t.getMessage());
                Utils.closeProgressDialog();
            }
        });
    }
}


