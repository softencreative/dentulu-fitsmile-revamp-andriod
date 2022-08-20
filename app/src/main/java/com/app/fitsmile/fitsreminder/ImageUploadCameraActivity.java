package com.app.fitsmile.fitsreminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.hardware.camera2.CameraMetadata;
//import android.hardware.camera2.CaptureRequest;
//import android.hardware.camera2.TotalCaptureResult;
//import android.hardware.camera2.params.StreamConfigurationMap;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;

import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.firebase_chat.ChatActivityFirebase;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.utils.ImageConverter;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.photoConsultation.Constants.CAMERA_INTENT;

public class ImageUploadCameraActivity extends BaseActivity implements TextureView.SurfaceTextureListener {
    private AutoFitTextureView mTextureView;
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private boolean mFlashOn = false;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    String ImageString;
    RelativeLayout mLayoutImage;
    ImageView frontCameraBtn, leftCameraBtn, rightCameraBtn, upCameraButton, downCameraButton;
    LinearLayout buttonLayout;
    AppCompatButton submit, cancelBtn;
    String currentPhotoPath;
    byte[] byteFront, byteLeft, byteRight, byteUp, byteDown;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private String mCameraId = CAMERA_BACK;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private int mSensorOrientation;
    private Size mPreviewSize;
    ImageView mImageSwitch;
    ImageView mImageZoom;
    CameraManager manager;
    Rect zoomRect = null;
    int minZoom = 1;
    float maxZoom = 5;
    int zoomLevel = 0;
    String mAlignerNo;
    int mFitSmileId;
    RelativeLayout cameraLayout;
    Boolean isSelectLeft = false, isSelectRight = false, isSelectFront = false, isSelectUp = false, isSelectDown = false;

    TextView mTextRightTeeth, mTextLeftTeeth, mTextFrontTeeth;
    ImageView mImageTeeth;
    String baseFront = "", baseLeft = "", baseRight = "", baseUp = "", baseDown = "";
    TextView mTextFaceCamera;
    private String mCameraFileName;
    private Uri mImageUri;

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        //  startBackgroundThread();
        //  startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_camera);
        LocaleManager.setLocale(this);
        cameraLayout = findViewById(R.id.camera_layout);
        frontCameraBtn = findViewById(R.id.frontCameraBtn);
        leftCameraBtn = findViewById(R.id.leftCameraBtn);
        rightCameraBtn = findViewById(R.id.rightCameraBtn);
        upCameraButton = findViewById(R.id.upCameraBtn);
        downCameraButton = findViewById(R.id.downCameraBtn);
        buttonLayout = findViewById(R.id.buttonLayout);
        cancelBtn = findViewById(R.id.cancel);
        mTextureView = findViewById(R.id.texture);
        submit = findViewById(R.id.submit);
        mTextureView.setSurfaceTextureListener(this);
        setUpToolBar();
        setClicks();
        mFitSmileId = getIntent().getIntExtra(AppConstants.TREATMENT_ID, 0);
        mAlignerNo = getIntent().getStringExtra(AppConstants.ALIGNER_NO);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    void setClicks() {
        mImageSwitch = findViewById(R.id.switchCamera);
        mTextFaceCamera = findViewById(R.id.textFront);
        mLayoutImage = findViewById(R.id.layoutImages);
        mImageZoom = findViewById(R.id.imageZoom);
        mTextFrontTeeth = findViewById(R.id.text_front_teeth);
        mTextRightTeeth = findViewById(R.id.text_right_teeth);
        mTextLeftTeeth = findViewById(R.id.text_left_teeth);
        mImageTeeth = findViewById(R.id.imageRound);
        mTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        findViewById(R.id.closeCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        findViewById(R.id.imageSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!baseFront.equals("") || !baseLeft.equals("") || !baseRight.equals("") || !baseUp.equals("") || !baseDown.equals("")) {
//                    uploadImageToServer(baseFront, baseLeft, baseRight, baseUp, baseDown);
//                }
                if (!byteFront.equals("") || !byteLeft.equals("") || !byteRight.equals("") || !byteUp.equals("") || !byteDown.equals("")) {
                    uploadMultipartImageToServer(byteFront, byteLeft, byteRight, byteUp, byteDown);
                } else {
                    Toast.makeText(ImageUploadCameraActivity.this, R.string.add_at_least_one_image, Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.flashOn).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mFlashOn = !mFlashOn;
                if (mFlashOn) {
                    mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                } else {
                    mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
                }

                updatePreview();
            }
        });

        //  isStoragePermissionGranted();

        mImageSwitch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        mImageZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setZoomLevel();
            }
        });

        mTextLeftTeeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundDefault();
                mTextLeftTeeth.setBackground(getResources().getDrawable(R.drawable.bg_radio_btn_one_selector));
                mImageTeeth.setImageResource(R.drawable.left_teeth);
                isSelectLeft = true;
            }
        });
        mTextRightTeeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackgroundDefault();
                mTextRightTeeth.setBackground(getResources().getDrawable(R.drawable.bg_radio_btn_one_selector));
                mImageTeeth.setImageResource(R.drawable.rightteeth);
                isSelectRight = true;
            }
        });
        mTextFrontTeeth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setBackgroundDefault();
                mTextFrontTeeth.setBackground(getResources().getDrawable(R.drawable.bg_radio_btn_one_selector));
                mImageTeeth.setImageResource(R.drawable.front_teeth);
                isSelectFront = true;
            }
        });

        frontCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDefaultCamera();
                isSelectFront = true;
//                cameraLayout.setVisibility(View.VISIBLE);
//                buttonLayout.setVisibility(View.GONE);
//                mImageTeeth.setVisibility(View.VISIBLE);
//                mImageTeeth.setImageResource(R.drawable.front_teeth);
            }
        });


        leftCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDefaultCamera();
                isSelectLeft = true;
//                cameraLayout.setVisibility(View.VISIBLE);
//                buttonLayout.setVisibility(View.GONE);
//                mImageTeeth.setVisibility(View.VISIBLE);
//                mImageTeeth.setImageResource(R.drawable.left_teeth);
            }
        });
        rightCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openCameraApp();
                openDefaultCamera();
                isSelectRight = true;
//                cameraLayout.setVisibility(View.VISIBLE);
//                buttonLayout.setVisibility(View.GONE);
//                mImageTeeth.setVisibility(View.VISIBLE);
//                mImageTeeth.setImageResource(R.drawable.rightteeth);
            }
        });
        upCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDefaultCamera();
                isSelectUp = true;
//                cameraLayout.setVisibility(View.VISIBLE);
//                buttonLayout.setVisibility(View.GONE);
//                mImageTeeth.setVisibility(View.VISIBLE);
//                mImageTeeth.setImageResource(R.drawable.up_teeth);
            }
        });
        downCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDefaultCamera();
                isSelectDown = true;
//                cameraLayout.setVisibility(View.VISIBLE);
//                buttonLayout.setVisibility(View.GONE);
//                mImageTeeth.setVisibility(View.VISIBLE);
//                mImageTeeth.setImageResource(R.drawable.down_teeth);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!baseFront.equals("") || !baseLeft.equals("") || !baseRight.equals("") || !baseUp.equals("") || !baseDown.equals("")) {
//                    uploadImageToServer(baseFront, baseLeft, baseRight, baseUp, baseDown);
//                } else {
//                    Toast.makeText(ImageUploadCameraActivity.this, R.string.add_at_least_one_image, Toast.LENGTH_SHORT).show();
//                }

                if (!byteFront.equals("") || !byteLeft.equals("") || !byteRight.equals("") || !byteUp.equals("") || !byteDown.equals("")) {
                    uploadMultipartImageToServer(byteFront, byteLeft, byteRight, byteUp, byteDown);
                } else {
                    Toast.makeText(ImageUploadCameraActivity.this, R.string.add_at_least_one_image, Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void openDefaultCamera() {
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_INTENT);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case Constants.CAMERA_INTENT:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    Drawable img = getResources().getDrawable(R.drawable.ic_green_tick);
                    img.setBounds(0, 0, 60, 60);
                    if (isSelectFront) {
//                       baseFront = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
//                        String basestring=convertBitmapToBase64(byteArray);
//                        baseFront = "data:image/png;base64," + basestring;
                        mTextFrontTeeth.setCompoundDrawables(null, null, img, null);
                        byteFront = compressFaceImage(byteArray, 1080, 90);
                        String fileSize = "";
                        if (byteFront.length >= 1024) {
                            //KB or more
                            long rxKb = byteFront.length / 1024;
                             fileSize=String.valueOf(rxKb) + "kb";
                            if (rxKb >= 1024) {
                                //MB or more
                                long rxMB = rxKb / 1024;
                                fileSize=String.valueOf(rxMB) + "mb";
                                if (rxMB >= 1024) {
                                    //GB or more
                                    long rxGB = rxMB / 1024;
                                    fileSize=String.valueOf(rxGB) + "gb";
                                }
                            }
                        }
                        Log.d(">>>front image size>>>",fileSize);

                        frontCameraBtn.setImageBitmap(photo);
                        cameraLayout.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);
                        isSelectFront = false;
                    } else if (isSelectLeft) {
                        mTextLeftTeeth.setCompoundDrawables(null, null, img, null);
//                       baseLeft = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
//                        String basestring=convertBitmapToBase64(byteArray);
//                       baseLeft ="data:image/png;base64," + basestring;
                        byteLeft = compressFaceImage(byteArray, 1080, 90);
                        leftCameraBtn.setImageBitmap(photo);
                        cameraLayout.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);
                        isSelectLeft = false;

                        String fileSize = "";
                        if (byteLeft.length >= 1024) {
                            //KB or more
                            long rxKb = byteLeft.length / 1024;
                            fileSize=String.valueOf(rxKb) + "kb";
                            if (rxKb >= 1024) {
                                //MB or more
                                long rxMB = rxKb / 1024;
                                fileSize=String.valueOf(rxMB) + "mb";
                                if (rxMB >= 1024) {
                                    //GB or more
                                    long rxGB = rxMB / 1024;
                                    fileSize=String.valueOf(rxGB) + "gb";
                                }
                            }
                        }
                        Log.d(">>>left image size>>>",fileSize);
                    } else if (isSelectRight) {
                        mTextRightTeeth.setCompoundDrawables(null, null, img, null);
//                        baseRight = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
//                        String basestring=convertBitmapToBase64(byteArray);
//                       baseRight ="data:image/png;base64," + basestring;
                        byteRight = compressFaceImage(byteArray, 1080, 90);
                        rightCameraBtn.setImageBitmap(photo);
                        cameraLayout.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);
                        isSelectRight = false;
                        String fileSize = "";
                        if (byteRight.length >= 1024) {
                            //KB or more
                            long rxKb = byteRight.length / 1024;
                            fileSize=String.valueOf(rxKb) + "kb";
                            if (rxKb >= 1024) {
                                //MB or more
                                long rxMB = rxKb / 1024;
                                fileSize=String.valueOf(rxMB) + "mb";
                                if (rxMB >= 1024) {
                                    //GB or more
                                    long rxGB = rxMB / 1024;
                                    fileSize=String.valueOf(rxGB) + "gb";
                                }
                            }
                        }
                        Log.d(">>>right image size>>>",fileSize);

                    } else if (isSelectUp) {
                        mTextRightTeeth.setCompoundDrawables(null, null, img, null);
//                        baseUp = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
//                        String basestring=convertBitmapToBase64(byteArray);
//                        baseUp ="data:image/png;base64," +  basestring;
                        byteUp = compressFaceImage(byteArray, 1080, 90);
                        upCameraButton.setImageBitmap(photo);
                        cameraLayout.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);
                        isSelectUp = false;

                        String fileSize = "";
                        if (byteUp.length >= 1024) {
                            //KB or more
                            long rxKb = byteUp.length / 1024;
                            fileSize=String.valueOf(rxKb) + "kb";
                            if (rxKb >= 1024) {
                                //MB or more
                                long rxMB = rxKb / 1024;
                                fileSize=String.valueOf(rxMB) + "mb";
                                if (rxMB >= 1024) {
                                    //GB or more
                                    long rxGB = rxMB / 1024;
                                    fileSize=String.valueOf(rxGB) + "gb";
                                }
                            }
                        }
                        Log.d(">>>up image size>>>",fileSize);

                    } else if (isSelectDown) {
                        mTextRightTeeth.setCompoundDrawables(null, null, img, null);
//                        baseDown = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
//                        String basestring=convertBitmapToBase64(byteArray);
//                        baseDown ="data:image/png;base64," +  basestring;
                        byteDown = compressFaceImage(byteArray, 1080, 90);
                        downCameraButton.setImageBitmap(photo);
                        cameraLayout.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);
                        isSelectDown = false;

                        String fileSize = "";
                        if (byteDown.length >= 1024) {
                            //KB or more
                            long rxKb = byteDown.length / 1024;
                            fileSize=String.valueOf(rxKb) + "kb";
                            if (rxKb >= 1024) {
                                //MB or more
                                long rxMB = rxKb / 1024;
                                fileSize=String.valueOf(rxMB) + "mb";
                                if (rxMB >= 1024) {
                                    //GB or more
                                    long rxGB = rxMB / 1024;
                                    fileSize=String.valueOf(rxGB) + "gb";
                                }
                            }
                        }
                        Log.d(">>>down image size>>>",fileSize);
                    }
                    break;

            }

        }
    }

    private String convertBitmapToBase64(byte[] bytes) {
        return Base64.encodeToString(compressFaceImage(bytes,
                1080, 90), Base64.DEFAULT);
    }

    public byte[] compressFaceImage(byte[] faceImage, int targetH, int compressRate) {
        Bitmap bitmap = resizeBitmap(faceImage, targetH);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, stream);
        return stream.toByteArray();
    }

    private Bitmap resizeBitmap(byte[] bytes, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmOptions);
        int originalWidth = bmOptions.outWidth;
        int originalHeight = bmOptions.outHeight;
        int targetW = (originalWidth * targetH) / originalHeight;
        return Bitmap.createScaledBitmap(convertByteArrayToBitmap(bytes), targetW, targetH, true);
    }

    private Bitmap convertByteArrayToBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    //    private Bitmap compressImage(Bitmap image) {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//Compression quality, here 100 means no compression, the storage of compressed data to baos
//        int options = 90;
//        while (baos.toByteArray().length / 1024 > 400) {  //Loop if compressed picture is greater than 400kb, than to compression
//            baos.reset();//Reset baos is empty baos
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//The compression options%, storing the compressed data to the baos
//            options -= 10;//Every time reduced by 10
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//The storage of compressed data in the baos to ByteArrayInputStream
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//The ByteArrayInputStream data generation
//        return bitmap;
//    }
    @SuppressLint("UseCompatLoadingForDrawables")
    void setBackgroundDefault() {
        mTextLeftTeeth.setBackground(getResources().getDrawable(R.drawable.bg_radio_btn_one_selector_select));
        mTextRightTeeth.setBackground(getResources().getDrawable(R.drawable.bg_radio_btn_one_selector_select));
        mTextFrontTeeth.setBackground(getResources().getDrawable(R.drawable.bg_radio_btn_one_selector_select));
        isSelectFront = false;
        isSelectLeft = false;
        isSelectFront = false;
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    private void setUpCameraOutputs(int width, int height) {
        Activity activity = this;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }


                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            Surface surface = new Surface(texture);

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (null == mCameraDevice) {
                                return;
                            }

                            mCaptureSession = cameraCaptureSession;
                            try {
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = ImageUploadCameraActivity.this;
            if (null != activity) {
                activity.finish();
            }
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        setUpCameraOutputs(width, height);
        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera(width, height);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        closeCamera();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    protected void takePicture() {

        if (null == mCameraDevice) {
            Log.e("Error", "cameraDevice is null");
            return;
        }
        Activity activity = this;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 600;
            int height = 2000;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(1200, 2000, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            if (mFlashOn) {
                captureBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            }
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            /*if(zoomRect !=null){
                Log.e("ZOOM RECT",zoomRect.toString() + ":::ZOOM RECT SET");
                captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect);
            }*/


            final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    try {

                        Image image = null;

                        image = reader.acquireLatestImage();


                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        //save(bytes);
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Matrix matrix = new Matrix();
                        if (mCameraId.equals(CAMERA_FRONT)) {
                            matrix.postRotate(270);
                        } else {
                            matrix.postRotate(90);
                        }


                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);

                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                                stream
                        ); //100 is the best quality possibe

                        if (image != null) {
                            image.close();
                        }
                        Utils.openProgressDialog(activity);
                        Drawable img = getResources().getDrawable(R.drawable.ic_green_tick);
                        img.setBounds(0, 0, 60, 60);
                        if (isSelectFront) {
                            baseFront = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
                            mTextFrontTeeth.setCompoundDrawables(null, null, img, null);
                            frontCameraBtn.setImageBitmap(rotatedBitmap);
                            cameraLayout.setVisibility(View.GONE);
                            buttonLayout.setVisibility(View.VISIBLE);
                            isSelectFront = false;
                        } else if (isSelectLeft) {
                            mTextLeftTeeth.setCompoundDrawables(null, null, img, null);
                            baseLeft = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
                            leftCameraBtn.setImageBitmap(rotatedBitmap);
                            cameraLayout.setVisibility(View.GONE);
                            buttonLayout.setVisibility(View.VISIBLE);
                            isSelectLeft = false;
                        } else if (isSelectRight) {
                            mTextRightTeeth.setCompoundDrawables(null, null, img, null);
                            baseRight = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
                            rightCameraBtn.setImageBitmap(rotatedBitmap);
                            cameraLayout.setVisibility(View.GONE);
                            buttonLayout.setVisibility(View.VISIBLE);
                            isSelectRight = false;
                        } else if (isSelectUp) {
                            mTextRightTeeth.setCompoundDrawables(null, null, img, null);
                            baseUp = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
                            upCameraButton.setImageBitmap(rotatedBitmap);
                            cameraLayout.setVisibility(View.GONE);
                            buttonLayout.setVisibility(View.VISIBLE);
                            isSelectUp = false;
                        } else if (isSelectDown) {
                            mTextRightTeeth.setCompoundDrawables(null, null, img, null);
                            baseDown = "data:image/png;base64," + ImageConverter.convertBitmapToBase64(stream.toByteArray());
                            downCameraButton.setImageBitmap(rotatedBitmap);
                            cameraLayout.setVisibility(View.GONE);
                            buttonLayout.setVisibility(View.VISIBLE);
                            isSelectDown = false;
                        }
                        Utils.closeProgressDialog();
                        //     uploadImageToServer("data:image/png;base64,"+ImageConverter.convertBitmapToBase64(stream.toByteArray()));
                    } catch (Exception e) {

                    }
                    /* saveImage(rotatedBitmap);*/
                }
            };
            reader.setOnImageAvailableListener(readerListener, null);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //Toast.makeText(TelscopeActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    if (mTextureView.isAvailable()) {
                        openCamera(mTextureView.getWidth(), mTextureView.getHeight());
                    } else {
                        mTextureView.setSurfaceTextureListener(ImageUploadCameraActivity.this);
                    }
                }
            };
            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Log.e("Error", "onPause");
        closeCamera();
        //  stopBackgroundThread();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
    }

    protected void updatePreview() {
        if (null == mCameraDevice) {
            Log.e("TAG", "updatePreview error, return");
        }


        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        if (zoomRect != null) {
            mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);

                } catch (CameraAccessException e) {
                    Log.e("TAG", "Failed to start camera preview because it couldn't access camera", e);
                } catch (IllegalStateException e) {
                    Log.e("TAG", "Failed to start camera preview.", e);
                }
            }
        }, 500);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void switchCamera() {
        if (mCameraId.equals(CAMERA_FRONT)) {
            mTextFaceCamera.setVisibility(View.VISIBLE);
            mImageSwitch.setImageResource(R.drawable.ic_back_camera);
            mCameraId = CAMERA_BACK;
            closeCamera();
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            }
        } else if (mCameraId.equals(CAMERA_BACK)) {
            mTextFaceCamera.setVisibility(View.INVISIBLE);
            mImageSwitch.setImageResource(R.drawable.ic_front_cam);
            mCameraId = CAMERA_FRONT;
            closeCamera();
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }
    }

    public void setZoomLevel() {
        if (zoomLevel == 0) {
            mImageZoom.setImageResource(R.drawable.ic_zoom_out);
            zoomLevel = 10;
            setCurrentZoom(zoomLevel);
        } else {
            mImageZoom.setImageResource(R.drawable.ic_zoom_in);
            zoomLevel = 0;
            setCurrentZoom(zoomLevel);
        }
    }

    public void setCurrentZoom(float zoomLevel) {
        zoomRect = getZoomRect(zoomLevel);
        if (zoomRect != null) {
            try {
                //you can try to add the synchronized object here
                mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect);
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), captureCallbackListener, mBackgroundHandler);
            } catch (Exception e) {
                Log.e("TAG", "Error updating preview: ", e);
            }
            this.zoomLevel = (int) zoomLevel;
        }
    }

    private Rect getZoomRect(float zoomLevel) {
        try {
            CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
            float maxZoom = (characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)) * 10;
            Rect activeRect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            if ((zoomLevel <= maxZoom) && (zoomLevel > 1)) {
                int minW = (int) (activeRect.width() / maxZoom);
                int minH = (int) (activeRect.height() / maxZoom);
                int difW = activeRect.width() - minW;
                int difH = activeRect.height() - minH;
                int cropW = difW / 100 * (int) zoomLevel;
                int cropH = difH / 100 * (int) zoomLevel;
                cropW -= cropW & 3;
                cropH -= cropH & 3;
                return new Rect(cropW, cropH, activeRect.width() - cropW, activeRect.height() - cropH);
            } else if (zoomLevel == 0) {
                return new Rect(0, 0, activeRect.width(), activeRect.height());
            }
            return null;
        } catch (Exception e) {
            Log.e("TAG", "Error during camera init");
            return null;
        }
    }

    void uploadImageToServer(String imageFront, String imageLeft, String imageRight, String imageUp, String imageDown) {
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", String.valueOf(mFitSmileId));
        jsonObject.addProperty("aligner_no", mAlignerNo);
        if (!imageFront.equals("")) {
            jsonObject.addProperty("center_image", imageFront);
        }
        if (!imageLeft.equals("")) {
            jsonObject.addProperty("left_image", imageLeft);
        }
        if (!imageRight.equals("")) {
            jsonObject.addProperty("right_image", imageRight);
        }
        if (!imageUp.equals("")) {
            jsonObject.addProperty("fourth_image", imageUp);
        }
        if (!imageDown.equals("")) {
            jsonObject.addProperty("fifth_image", imageDown);
        }

        // Utils.closeProgressDialog();
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
                    Utils.showToast(ImageUploadCameraActivity.this, getResources().getString(R.string.progress_success));
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(ImageUploadCameraActivity.this);
                } else {
                    Utils.showToast(ImageUploadCameraActivity.this, mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
//                Utils.showToast(ImageUploadCameraActivity.this, "error occur");
                Utils.showToast(ImageUploadCameraActivity.this, call.toString());
            }
        });
    }

    void uploadMultipartImageToServer(byte[] imageFront, byte[] imageLeft, byte[] imageRight, byte[] imageUp, byte[] imageDown) {
        Utils.openProgressDialog(this);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }

        ApiInterface mApiService = retrofit.create(ApiInterface.class);

        RequestBody requestFileFront = RequestBody.create(MediaType.parse("image/jpeg"), imageFront);
        RequestBody requestFileLeft = RequestBody.create(MediaType.parse("image/jpeg"), imageLeft);
        RequestBody requestFileRight = RequestBody.create(MediaType.parse("image/jpeg"), imageRight);
        RequestBody requestFileUp = RequestBody.create(MediaType.parse("image/jpeg"), imageUp);
        RequestBody requestFileDown = RequestBody.create(MediaType.parse("image/jpeg"), imageDown);

        RequestBody patient_id = RequestBody.create(MediaType.parse("text/plain"), appPreference.getUserId());
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(mFitSmileId));
        RequestBody aligner_no = RequestBody.create(MediaType.parse("text/plain"), mAlignerNo);
        RequestBody language = RequestBody.create(MediaType.parse("text/plain"), currentLanguage);
        MultipartBody.Part bodyFront = MultipartBody.Part.createFormData("center_image", "image.jpg", requestFileFront);
        MultipartBody.Part bodyLeft = MultipartBody.Part.createFormData("left_image", "image.jpg", requestFileLeft);
        MultipartBody.Part bodyRight = MultipartBody.Part.createFormData("right_image", "image.jpg", requestFileRight);
        MultipartBody.Part bodyUp = MultipartBody.Part.createFormData("fourth_image", "image.jpg", requestFileUp);
        MultipartBody.Part bodyDown = MultipartBody.Part.createFormData("fifth_image", "image.jpg", requestFileDown);

        Call<SettingsResponse> mService = mApiService.uploadAlignerImageData(bodyFront, bodyLeft, bodyRight, bodyUp, bodyDown, patient_id, id, aligner_no, language);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    Utils.showToast(ImageUploadCameraActivity.this, getResources().getString(R.string.progress_success));
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(ImageUploadCameraActivity.this);
                } else {
                    Utils.showToast(ImageUploadCameraActivity.this, mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
//                Utils.showToast(ImageUploadCameraActivity.this, "error occur");
                Utils.showToast(ImageUploadCameraActivity.this, call.toString());
            }
        });
    }
}
