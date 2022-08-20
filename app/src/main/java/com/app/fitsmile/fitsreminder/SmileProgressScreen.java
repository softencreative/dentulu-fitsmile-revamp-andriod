package com.app.fitsmile.fitsreminder;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.adapter.TeethImageAdapter;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.trayMinder.SmileProgressImageResult;
import com.app.fitsmile.response.trayMinder.SmileProgressImages;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DiscreteScrollView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.utils.ImageConverter.convertBitmapToBase64;

public class SmileProgressScreen extends BaseActivity implements DiscreteScrollView.OnItemChangedListener<TeethImageAdapter.ViewHolder>,
        DiscreteScrollView.ScrollStateChangeListener<TeethImageAdapter.ViewHolder> {

    FloatingActionButton fabShowDialog, fabOpenCamera, fabOpenGallery;
    LinearLayout fabLayoutOpenCamera, fabLayoutOpenGallery;
    View fabBGLayout;
    boolean isFABOpen = false;
    ImageView mImageTeeth, mImageRound;
    ImageView mImageEdit, mImageDelete;
    byte[] mImageByteArray;
    private int PICK_IMAGE = 4;
    String mFitSmileId, mAlignerNo;
    TextView mTextNoData;
    List<SmileProgressImages> mListImages = new ArrayList<>();
    ConstraintLayout mLayoutImages;
    RecyclerView mRecyclerAligner;
    TeethImageAdapter mAdapter;
    TextView mTextCenterNumber;
    int curPos = 0;
    TextView mTextAlignerNumber, mTextImageDate;
    ImageView mImageShare;
    ConstraintLayout mLayoutImagesShare;
    SmileProgressImageResult mSmileImages = new SmileProgressImageResult();
    ImageView mImageSmileProgressDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_teeth_image);
        LocaleManager.setLocale(this);
        mFitSmileId = getIntent().getStringExtra(AppConstants.TREATMENT_ID);
        mAlignerNo = getIntent().getStringExtra(AppConstants.ALIGNER_NO);
        mSmileImages = (SmileProgressImageResult) getIntent().getSerializableExtra(AppConstants.REMINDER_MODEL);
        setUpToolBar();
        initUI();
        setClicks();
        //    fetchAlignerImages();
        setData();
        setAdapter();
        setImageData(0);
        mTextNoData.setVisibility(View.GONE);
        mLayoutImages.setVisibility(View.VISIBLE);
    }


    void setData() {
        if (!mSmileImages.getCenter_image_url().equals("")) {
            SmileProgressImages smileProgressImages = new SmileProgressImages();
            smileProgressImages.setAlignerNo(mSmileImages.getAligner_no());
            smileProgressImages.setImageDate(mSmileImages.getCreated_date());
            smileProgressImages.setImageUrl(mSmileImages.getCenter_image_url());
            smileProgressImages.setImageName("Front");
            smileProgressImages.setId(mSmileImages.getId());
            mListImages.add(smileProgressImages);
        }
        if (!mSmileImages.getRight_image_url().equals("")) {
            SmileProgressImages smileProgressImages = new SmileProgressImages();
            smileProgressImages.setAlignerNo(mSmileImages.getAligner_no());
            smileProgressImages.setImageDate(mSmileImages.getCreated_date());
            smileProgressImages.setImageUrl(mSmileImages.getRight_image_url());
            smileProgressImages.setImageName("Right");
            smileProgressImages.setId(mSmileImages.getId());
            mListImages.add(smileProgressImages);
        }
        if (!mSmileImages.getLeft_image_url().equals("")) {
            SmileProgressImages smileProgressImages = new SmileProgressImages();
            smileProgressImages.setAlignerNo(mSmileImages.getAligner_no());
            smileProgressImages.setImageDate(mSmileImages.getCreated_date());
            smileProgressImages.setImageUrl(mSmileImages.getLeft_image_url());
            smileProgressImages.setImageName("Left");
            smileProgressImages.setId(mSmileImages.getId());
            mListImages.add(smileProgressImages);
        }
        if (!mSmileImages.getFourth_image_url().equals("")) {
            SmileProgressImages smileProgressImages = new SmileProgressImages();
            smileProgressImages.setAlignerNo(mSmileImages.getAligner_no());
            smileProgressImages.setImageDate(mSmileImages.getCreated_date());
            smileProgressImages.setImageUrl(mSmileImages.getFourth_image_url());
            smileProgressImages.setImageName(" Up ");
            smileProgressImages.setId(mSmileImages.getId());
            mListImages.add(smileProgressImages);
        }
        if (!mSmileImages.getFifth_image_url().equals("")) {
            SmileProgressImages smileProgressImages = new SmileProgressImages();
            smileProgressImages.setAlignerNo(mSmileImages.getAligner_no());
            smileProgressImages.setImageDate(mSmileImages.getCreated_date());
            smileProgressImages.setImageUrl(mSmileImages.getFifth_image_url());
            smileProgressImages.setImageName("Down");
            smileProgressImages.setId(mSmileImages.getId());
            mListImages.add(smileProgressImages);
        }
    }


    /*void fetchAlignerImages(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", mFitSmileId);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SmileProgressImageResponse> mService = mApiService.fetchAlignerImage(jsonObject);
        mService.enqueue(new Callback<SmileProgressImageResponse>() {
            @Override
            public void onResponse(Call<SmileProgressImageResponse> call, Response<SmileProgressImageResponse> response) {
                Utils.closeProgressDialog();
                mListImages.clear();
                SmileProgressImageResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus()==1) {
                    if (mResponse.getData().size() > 0) {
                        mTextNoData.setVisibility(View.GONE);
                        mLayoutImages.setVisibility(View.VISIBLE);
                        mListImages.addAll(mResponse.getData());
                    }else {
                        mTextNoData.setVisibility(View.VISIBLE);
                        mLayoutImages.setVisibility(View.GONE);
                    }
                }else {
                    mTextNoData.setVisibility(View.VISIBLE);
                    mLayoutImages.setVisibility(View.GONE);
                }
                if(mListImages.size()>0){
                    mLayoutImages.setVisibility(View.VISIBLE);
                    curPos=0;
                    setImageData();
                }
               mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<SmileProgressImageResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                mTextNoData.setVisibility(View.VISIBLE);
                mLayoutImages.setVisibility(View.GONE);
            }
        });

    }*/

    void initUI() {
        mImageTeeth = findViewById(R.id.imageSelfie);
        mImageRound = findViewById(R.id.imageRound);
        mImageEdit = findViewById(R.id.imageEdit);
        fabLayoutOpenCamera = findViewById(R.id.fabLayout1);
        fabLayoutOpenGallery = findViewById(R.id.fabLayout2);
        fabShowDialog = findViewById(R.id.fab);
        fabOpenCamera = findViewById(R.id.fab1);
        fabOpenGallery = findViewById(R.id.fab2);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        mTextNoData = findViewById(R.id.nodata);
        mLayoutImages = findViewById(R.id.layoutImages);
        mRecyclerAligner = findViewById(R.id.recyclerPhotosIndex);
        mTextCenterNumber = findViewById(R.id.textAlignerImageNumber);
        mImageDelete = findViewById(R.id.imageDelete);
        mTextImageDate = findViewById(R.id.textImageDate);
        mImageShare = findViewById(R.id.imageShare);
        mLayoutImagesShare = findViewById(R.id.layoutImagesShare);
        mImageSmileProgressDelete = findViewById(R.id.imageSmileImageDelete);
        mImageSmileProgressDelete.setVisibility(View.GONE);
        //mTextImageDate.setText(R.string.lbl_image_title);
    }

    void setAdapter() {
        mAdapter = new TeethImageAdapter(SmileProgressScreen.this, mListImages);
        DiscreteScrollView scrollView = findViewById(R.id.picker);
        scrollView.setAdapter(mAdapter);
        scrollView.addOnItemChangedListener(this);
        scrollView.addScrollStateChangeListener(this);
    }

    void setImageData(int pos) {
        mTextCenterNumber.setText(mListImages.get(pos).getImageName());
        //mTextImageDate.setText(Utils.getNewDate(this, mListImages.get(pos).getImageDate(), Utils.inputDate, Utils.outputDateAligner) + " | " + mAlignerNo);
        Picasso.get()
                .load(mListImages.get(pos).getImageUrl())
                .transform(new CropTransformation(1080, 972, CropTransformation.GravityHorizontal.CENTER,
                        CropTransformation.GravityVertical.CENTER))
                .into(mImageTeeth);
    }

    void setClicks() {
        fabShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
        fabLayoutOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                startActivityForResult(new Intent(actCon, TeethSefieActivity.class), 2);
            }
        });
        fabOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                Intent intent = new Intent(actCon, ImageUploadCameraActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, mFitSmileId);
                intent.putExtra(AppConstants.ALIGNER_NO, mAlignerNo);
                startActivityForResult(intent, 2);
            }
        });
        mImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent=new Intent(actCon, ImageEditActivity.class);
                intent.putExtra("type",1);
                intent.putExtra(AppConstants.TREATMENT_ID,mFitSmileId);
                intent.putExtra(AppConstants.ALIGNER_NO,mAlignerNo);
                intent.putExtra("imageModel",mListImages.get(curPos));
                startActivityForResult(intent,3);*/
            }
        });

        mImageSmileProgressDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlert();
            }
        });

        fabLayoutOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
            }
        });
        fabOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                Intent chooserIntent = Intent.createChooser(i, "Select Image");

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        mImageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });
    }

    void showDeleteAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_these_Images))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImageToServer();
                    }
                }).setNegativeButton(getString(R.string.no), null).show();
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

    private void showFABMenu() {
        isFABOpen = true;
        fabLayoutOpenCamera.setVisibility(View.VISIBLE);
        fabLayoutOpenGallery.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fabShowDialog.animate().rotationBy(180);
        fabLayoutOpenCamera.animate().translationY(-getResources().getDimension(R.dimen._50sdp));
        fabLayoutOpenGallery.animate().translationY(-getResources().getDimension(R.dimen._100sdp));
    }

    private Bitmap cropImage(Bitmap bitmap, View reference, View frame) {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        int heightOriginal = displaySize.y;
        int widthOriginal = displaySize.x;
        int heightFrame = reference.getHeight();
        int widthFrame = reference.getWidth();
        int leftFrame = reference.getLeft();
        int topFrame = reference.getTop();
        int heightReal = bitmap.getHeight();
        int widthReal = bitmap.getWidth();
        int widthFinal = widthFrame * widthReal / widthOriginal;
        int heightFinal = heightFrame * heightReal / heightOriginal;
        int leftFinal = leftFrame * widthReal / widthOriginal;
        int topFinal = topFrame * heightReal / heightOriginal;
        if (widthFinal > bitmap.getWidth()) {
            widthFinal = bitmap.getWidth();
        }
        if (heightFinal > bitmap.getHeight()) {
            heightFinal = bitmap.getHeight();
        }
        return Bitmap.createBitmap(
                bitmap,
                leftFinal, topFinal, widthFinal, heightFinal
        );
       /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFinal.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                stream
        ); //100 is the best quality possibe
        return stream.toByteArray();*/
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fabShowDialog.animate().rotation(360);
        fabLayoutOpenCamera.animate().translationY(0);
        fabLayoutOpenGallery.animate().translationY(0);
        fabLayoutOpenGallery.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayoutOpenCamera.setVisibility(View.GONE);
                    fabLayoutOpenGallery.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 || requestCode == 3 && resultCode == RESULT_OK) {
            // fetchAlignerImages();
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            onSelectFromGalleryResult(data);

        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        if (data != null) {
            try {
                Utils.openProgressDialog(this);
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                new ImageConverter().execute(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onCurrentItemChanged(@Nullable TeethImageAdapter.ViewHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onScrollStart(@NonNull TeethImageAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull TeethImageAdapter.ViewHolder currentItemHolder, int adapterPosition) {
        curPos = adapterPosition;
        Picasso.get()
                .load(mListImages.get(curPos).getImageUrl())
                .transform(new CropTransformation(mImageTeeth.getWidth(), mImageTeeth.getHeight(), CropTransformation.GravityHorizontal.CENTER,
                        CropTransformation.GravityVertical.CENTER))
                .into(mImageTeeth);
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable TeethImageAdapter.ViewHolder currentHolder, @Nullable TeethImageAdapter.ViewHolder newCurrent) {
        mTextCenterNumber.setText(mListImages.get(newPosition).getImageName());
        //mTextImageDate.setText(Utils.getNewDate(this, mListImages.get(newPosition).getImageDate(), Utils.inputDate, Utils.outputDateAligner) + " | " + mListImages.get(curPos).getAlignerNo());

    }

    public class ImageConverter extends AsyncTask<Bitmap, Void, String> {


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
                /* return Base64.encodeToString(byteArray, Base64.DEFAULT);*/

            /*ByteBuffer dst= ByteBuffer.allocate(bitmaps[0].getByteCount());
            bitmaps[0].copyPixelsToBuffer( dst);
            byte[] byteArray=dst.array();*/
                return convertBitmapToBase64(byteArray);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Utils.closeProgressDialog();
            if (s != null) {
                Intent intent = new Intent(actCon, ImageEditActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra(AppConstants.TREATMENT_ID, mFitSmileId);
                intent.putExtra(AppConstants.ALIGNER_NO, mAlignerNo);
                appPreference.setImageArray(s);
                startActivityForResult(intent, 3);

            }
        }
    }


    void deleteImageToServer() {
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("image_id", mListImages.get(curPos).getId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.deleteAlignerImage(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    Utils.showToast(SmileProgressScreen.this, mResponse.getMessage());
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(SmileProgressScreen.this);
                }  else {
                    Utils.showToast(SmileProgressScreen.this, mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    void shareImage() {
        mLayoutImagesShare.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mLayoutImagesShare.getDrawingCache());
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "FitsSmile", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }

}