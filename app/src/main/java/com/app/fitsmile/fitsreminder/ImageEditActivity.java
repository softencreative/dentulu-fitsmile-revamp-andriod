package com.app.fitsmile.fitsreminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.trayMinder.AlignerListResponse;
import com.app.fitsmile.response.trayMinder.AlignerListResult;
import com.app.fitsmile.response.trayMinder.SmileProgressImageResult;
import com.app.fitsmile.utils.ImageConverter;
import com.app.fitsmile.utils.LocaleManager;
import com.app.fitsmile.utils.TouchImageView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageEditActivity extends BaseActivity {

    Bitmap bitmapImage;
    Bitmap passBitmap;
    float cont = 1f;
    float bright = 0f;
    float sat = 1f;
    TouchImageView tuneDisplay;
    ImageView mImageUpdate;
    int type;
    SmileProgressImageResult mSmileProgressResult;
    String mFitSmileId,mAlignerNo;
    TextView mTextAlignerNumber,mTextImageDate;
    private String checkedItemString;
    private List<AlignerListResult>mListAligner=new ArrayList<>();
    ImageView mImageClose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone);
        LocaleManager.setLocale(this);
        tuneDisplay =findViewById(R.id.tunedDisplay);
        mImageUpdate=findViewById(R.id.imageSave);
        mTextImageDate=findViewById(R.id.textDate);
        mTextAlignerNumber=findViewById(R.id.textAlignerNumber);
        type=getIntent().getIntExtra("type",0);
        mFitSmileId=getIntent().getStringExtra(AppConstants.TREATMENT_ID);
        mAlignerNo=getIntent().getStringExtra(AppConstants.ALIGNER_NO);
        mImageClose=findViewById(R.id.closeImagEdit);
        mSmileProgressResult=(SmileProgressImageResult) getIntent().getSerializableExtra("imageModel");
        getAlignerList();
        if (type==1) {
            Picasso.get().load(mSmileProgressResult.getImage_url()).into(tuneDisplay);
            mTextImageDate.setText(Utils.getNewDate(this,mSmileProgressResult.getCreated_date(),Utils.inputDate,Utils.outputDateAligner));
            mTextAlignerNumber.setText("#"+ mSmileProgressResult.getAligner_no());
            Utils.openProgressDialog(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new LoadAsync().execute(mSmileProgressResult.getImage_url());
                }
            },2000);
        }else {
            mTextAlignerNumber.setVisibility(View.GONE);
            mTextImageDate.setVisibility(View.GONE);
            Utils.openProgressDialog(this);
            byte[] bytes=ImageConverter.getByteArrayFromBase64(appPreference.getImageArray());
            bitmapImage=ImageConverter.convertByteArrayToBitmap(bytes);
            tuneDisplay.setImageBitmap(bitmapImage);
            Utils.closeProgressDialog();
        }
            setClicks();

    }

    void setClicks(){

        ((SeekBar)findViewById(R.id.brightnessBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bright = ((255f/50f)*i)-255f;
                tuneDisplay.setImageBitmap(changeBitmapContrastBrightness(cont,bright,sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ((SeekBar)findViewById(R.id.contrastBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                cont = i*(0.1f);
                tuneDisplay.setImageBitmap(changeBitmapContrastBrightness(cont,bright,sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        ((SeekBar)findViewById(R.id.saturationBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sat = (float)i/256f;
                tuneDisplay.setImageBitmap(changeBitmapContrastBrightness(cont,bright,sat));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mImageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openProgressDialog(ImageEditActivity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (type == 1) {
                            editImageToServer();
                        }else if (type == 3) {
                            updateAlignerNumber();
                        }else {
                            uploadImageToServer();
                        }
                    }
                },1000);

            }
        });
        mTextAlignerNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=3;
                dialogUpdateAlignerNumber();
            }
        });

        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void uploadImageToServer(){
        passBitmap= ((BitmapDrawable)tuneDisplay.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        passBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String image="data:image/png;base64,"+ImageConverter.convertBitmapToBase64(byteArray);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", mFitSmileId);
        jsonObject.addProperty("aligner_no", mAlignerNo);
        jsonObject.addProperty("image", image);

        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.uploadAlignerImage(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    Utils.showToast(ImageEditActivity.this,mResponse.getMessage());
                    Intent intent=new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(ImageEditActivity.this);
                }  else {
                    Utils.showToast(ImageEditActivity.this,mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
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
        getSupportActionBar().setTitle(R.string.smile_progress);
    }



    void editImageToServer(){
        passBitmap= ((BitmapDrawable)tuneDisplay.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        passBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String image="data:image/png;base64,"+ImageConverter.convertBitmapToBase64(byteArray);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("image_id",mSmileProgressResult.getId());
        jsonObject.addProperty("image", image);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAlignerImage(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    Utils.showToast(ImageEditActivity.this,mResponse.getMessage());
                    Intent intent=new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(ImageEditActivity.this);
                }  else {
                    Utils.showToast(ImageEditActivity.this,mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }


    private Bitmap changeBitmapContrastBrightness(float contrast, float brightness,float saturation)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bitmapImage.getWidth(), bitmapImage.getHeight(), bitmapImage.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bitmapImage, 0, 0, paint);
        cm.setSaturation(saturation);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(ret,0,0,paint);
        return ret;
    }

    public class ImageConverterAsync extends AsyncTask<Bitmap,Void,String> {
        public ImageConverterAsync() {

        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            if (bitmaps != null && bitmaps.length > 0) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return ImageConverter.convertBitmapToBase64(byteArray);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (s != null) {
                        Utils.closeProgressDialog();
                        Intent intent = new Intent();
                        appPreference.setImageArray(s);
                        intent.putExtra("imageArray", 1);
                        setResult(RESULT_OK, intent);
                        Log.e("bitmap", "value");
                        finish();
                    }
                }
            });

        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {

            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class LoadAsync extends AsyncTask<String, Bitmap, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURL(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            bitmapImage=bitmap;
            Utils.closeProgressDialog();
        }
    }



    public void dialogUpdateAlignerNumber() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ImageEditActivity.this);
        alertDialog.setTitle("I was wearing...");
        List<String> mAlignerList = new ArrayList<String>();
        int checkItem=0;
        for (int i=0;i<mListAligner.size();i++){
            mAlignerList.add(mListAligner.get(i).getName());
            if (mAlignerNo.equals(mListAligner.get(i).getAligner_no())){
                checkItem=i;
            }
        }
        final CharSequence[] items = mAlignerList.toArray(new String[mAlignerList.size()]);

        alertDialog.setSingleChoiceItems(items, checkItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItemString=mListAligner.get(which).getAligner_no();

            }
        });
        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTextAlignerNumber.setText("# "+checkedItemString);

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    void getAlignerList(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",mFitSmileId);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<AlignerListResponse> mService = mApiService.fetchAlignerList(jsonObject);
        mService.enqueue(new Callback<AlignerListResponse>() {
            @Override
            public void onResponse(Call<AlignerListResponse> call, Response<AlignerListResponse> response) {
                Utils.closeProgressDialog();
                AlignerListResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus()==1) {
                    mListAligner.addAll(mResponse.getData());
                }else if (mResponse.getStatus()==401) {
                    Utils.showSessionAlert(ImageEditActivity.this);
                } else {
                    Utils.showToast(ImageEditActivity.this,mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<AlignerListResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    void updateAlignerNumber(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("image_id",mSmileProgressResult.getId());
        jsonObject.addProperty("aligner_no",checkedItemString);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAlignerNumber(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    Utils.showToast(ImageEditActivity.this,mResponse.getMessage());
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                  Utils.showSessionAlert(ImageEditActivity.this);
                } else {
                    Utils.showToast(ImageEditActivity.this,mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

}
