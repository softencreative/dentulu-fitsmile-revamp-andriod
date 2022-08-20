package com.app.fitsmile.fitsreminder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.adapter.AlignerSmileAdapter;
import com.app.fitsmile.response.trayMinder.AlignerListResponse;
import com.app.fitsmile.response.trayMinder.AlignerListResult;
import com.app.fitsmile.response.trayMinder.SmileProgressImageResponse;
import com.app.fitsmile.response.trayMinder.SmileProgressImageResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlignerSmileProgressActivity extends BaseActivity implements RecyclerItemClickListener {
    RecyclerView mRecyclerSmile;
    AlignerSmileAdapter mAdapter;
    TextView mTextSelectAligner;
    String currentAligner;
    TextView mTextNoData;
    String mFitSmileId;
    private List<AlignerListResult> mListAligner=new ArrayList<>();
    private List<SmileProgressImageResult> mListAlignerImages=new ArrayList<>();
    private String checkedItemString;
    FloatingActionButton mAddImage;
    String alignNoUpload="";
    private int fitsSmileId;
    int mOpenFrom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aligner_smile_progress);
        LocaleManager.setLocale(this);
        setUpToolBar();
         initUI();
         setAdapter();
         setClicks();
         getAlignerImageList();
    }

    void setAdapter(){
        mRecyclerSmile.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new AlignerSmileAdapter(this,mListAlignerImages,this);
        mRecyclerSmile.setAdapter(mAdapter);
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

    void initUI(){
        mRecyclerSmile=findViewById(R.id.recycler_smile);
        mTextSelectAligner=findViewById(R.id.textSelectAligner);
        currentAligner=getIntent().getStringExtra(AppConstants.ALIGNER_NO);
        mFitSmileId=getIntent().getStringExtra(AppConstants.TREATMENT_ID);
        mTextSelectAligner.setText(R.string.aligner+" #"+ currentAligner);
        mTextNoData=findViewById(R.id.nodata);
        mAddImage=findViewById(R.id.fab);
        mAddImage.setVisibility(View.GONE);

        if(getIntent()!=null){
            mOpenFrom = getIntent().getIntExtra(AppConstants.OPEN_FROM, 0);
            if (mOpenFrom == 3) {
                alignNoUpload = getIntent().getStringExtra(AppConstants.ALIGNER_NO);
                fitsSmileId = getIntent().getIntExtra(AppConstants.APPOINTMENT_ID, 0);
                showPopUpAlignerImages();
            }

        }

    }



    void setClicks(){
        mTextSelectAligner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUpdateAlignerNumber();
            }
        });
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(actCon, ImageUploadCameraActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID,mFitSmileId);
                intent.putExtra(AppConstants.ALIGNER_NO,currentAligner);
                startActivityForResult(intent,2);
            }
        });
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
        Utils.openProgressDialog(this);
        mService.enqueue(new Callback<AlignerListResponse>() {
            @Override
            public void onResponse(Call<AlignerListResponse> call, Response<AlignerListResponse> response) {
                Utils.closeProgressDialog();
                AlignerListResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus()==1) {
                    mListAligner.addAll(mResponse.getData());
                    getAlignerImageList();
                }else if (mResponse.getStatus()==401) {
                    Utils.showSessionAlert(AlignerSmileProgressActivity.this);
                } else {
                    Utils.showToast(AlignerSmileProgressActivity.this,mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<AlignerListResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }




    void getAlignerImageList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", mFitSmileId);
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);

        Call<SmileProgressImageResponse> mService = mApiService.fetchAlignerImage(jsonObject);
        mService.enqueue(new Callback<SmileProgressImageResponse>() {
            @Override
            public void onResponse(Call<SmileProgressImageResponse> call, Response<SmileProgressImageResponse> response) {
                Utils.closeProgressDialog();
                mListAlignerImages.clear();
                SmileProgressImageResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus()==1) {
                    if (mResponse.getData().size() > 0) {
                        mTextNoData.setVisibility(View.GONE);
                        mListAlignerImages.addAll(mResponse.getData());
                        mRecyclerSmile.setVisibility(View.VISIBLE);
                    }else {
                        mTextNoData.setVisibility(View.VISIBLE);
                        mRecyclerSmile.setVisibility(View.GONE);
                    }
                }else if (mResponse.getStatus()==401) {
                    Utils.showSessionAlert(AlignerSmileProgressActivity.this);
                }  else {
                    mTextNoData.setVisibility(View.VISIBLE);
                    mRecyclerSmile.setVisibility(View.GONE);
                }
                setAdapter();

            }

            @Override
            public void onFailure(Call<SmileProgressImageResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                mTextNoData.setVisibility(View.VISIBLE);
                mRecyclerSmile.setVisibility(View.GONE);
            }
        });
    }

    private void showPopUpAlignerImages() {
        Intent intent = new Intent(actCon, ImageUploadCameraActivity.class);
        intent.putExtra(AppConstants.TREATMENT_ID, fitsSmileId);
        intent.putExtra(AppConstants.ALIGNER_NO, alignNoUpload);
        startActivityForResult(intent,2);

        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_upload_aligner_image, null);
        dialogBuilder.setView(dialogView);

        TextView mTextDialog = dialogView.findViewById(R.id.textUploadAligner);
        String firstLine = getResources().getString(R.string.upload_image_of_aligner) + alignNoUpload + " ";
        mTextDialog.setText(firstLine + getResources().getString(R.string.for_provider_track_progress));

        dialogBuilder.setPositiveButton(R.string.upload_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(actCon, ImageUploadCameraActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, fitsSmileId);
                intent.putExtra(AppConstants.ALIGNER_NO, alignNoUpload);
                startActivityForResult(intent,2);
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = dialogBuilder.create();
        alert.show();*/
    }

    public void dialogUpdateAlignerNumber() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AlignerSmileProgressActivity.this);
        alertDialog.setTitle(R.string.was_wearing);
        List<String> mAlignerList = new ArrayList<String>();
        int checkItem=0;
        for (int i=0;i<mListAligner.size();i++){
            mAlignerList.add(mListAligner.get(i).getName());
            if (currentAligner.equals(mListAligner.get(i).getAligner_no())){
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
        alertDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTextSelectAligner.setText(R.string.aligner+" #"+checkedItemString);
                currentAligner=checkedItemString;
                getAlignerImageList();

            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }



    @Override
    public void setClicks(int pos, int open) {
        if (open == 1) {
            Intent intent = new Intent(actCon, SmileProgressScreen.class);
            intent.putExtra(AppConstants.TREATMENT_ID, mFitSmileId);
            intent.putExtra(AppConstants.ALIGNER_NO, currentAligner);
            intent.putExtra(AppConstants.REMINDER_MODEL, mListAlignerImages.get(pos));
            startActivityForResult(intent, 3);
        }
        if (open==2){
            showPopUp(mListAlignerImages.get(pos).getRight_image_url());
        }
        if (open==3){
            showPopUp(mListAlignerImages.get(pos).getLeft_image_url());
        }
        if (open==4){
            showPopUp(mListAlignerImages.get(pos).getCenter_image_url());
        }
        if (open == 5){
            showPopUp(mListAlignerImages.get(pos).getFourth_image_url());
        }
        if (open == 6){
            showPopUp(mListAlignerImages.get(pos).getFifth_image_url());
        }
        if (open==7){
            SmileProgressImageResult item=mListAlignerImages.get(pos);
            alignNoUpload=item.getAligner_no();
            fitsSmileId=Integer.parseInt(appPreference.getFitsReminderId());
            showPopUpAlignerImages();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2 && resultCode==RESULT_OK){
            getAlignerImageList();
        }
        if (requestCode==3 && resultCode==RESULT_OK){
            getAlignerImageList();
        }
    }
    private void showPopUp(String Url) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_view_image, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();


        ImageView mImvProfile = alert.findViewById(R.id.imageSelfie);


        Picasso.get()
                .load(Url)
                .transform(new CropTransformation(1050, 970, CropTransformation.GravityHorizontal.CENTER,
                        CropTransformation.GravityVertical.CENTER))
                .into(mImvProfile);

        ImageView close = alert.findViewById(R.id.imv_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });


    }
}