package com.app.fitsmile.firebase_chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.common.Utils.isVideoFile;

public class AWSUtils {

    public static String getMimeType(String url) {
        try {
            String type;
            String extension = url.substring(url.lastIndexOf(".") + 1);
            Log.i("extension", "ext : " + extension);
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            return type;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyExif(String oldPath, String newPath) throws IOException {
        ExifInterface oldExif = new ExifInterface(oldPath);
        String[] attributes = new String[]
                {
                        ExifInterface.TAG_APERTURE,
                        ExifInterface.TAG_DATETIME,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_FLASH,
                        ExifInterface.TAG_FOCAL_LENGTH,
                        ExifInterface.TAG_GPS_ALTITUDE,
                        ExifInterface.TAG_GPS_ALTITUDE_REF,
                        ExifInterface.TAG_GPS_DATESTAMP,
                        ExifInterface.TAG_GPS_LATITUDE,
                        ExifInterface.TAG_GPS_LATITUDE_REF,
                        ExifInterface.TAG_GPS_LONGITUDE,
                        ExifInterface.TAG_GPS_LONGITUDE_REF,
                        ExifInterface.TAG_GPS_PROCESSING_METHOD,
                        ExifInterface.TAG_GPS_TIMESTAMP,
                        ExifInterface.TAG_IMAGE_LENGTH,
                        ExifInterface.TAG_IMAGE_WIDTH,
                        ExifInterface.TAG_ISO,
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.TAG_WHITE_BALANCE
                };

        ExifInterface newExif = null;
        try {
            newExif = new ExifInterface(newPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String attribute : attributes) {
            String value = oldExif.getAttribute(attribute);
            if (value != null)
                if (newExif != null) {
                    newExif.setAttribute(attribute, value);
                }
        }
        if (newExif != null) {
            newExif.saveAttributes();
        }
    }

    public void upload(BaseActivity activity, String filename, boolean isFile, FirebaseUploadFilesListener uploadFilesListener) {
        if (isFile) {
            uploadDocumentToServer(activity, filename, uploadFilesListener);
        } else {
            uploadMediaToServer(activity, filename, uploadFilesListener);
        }
    }

    private void uploadMediaToServer(BaseActivity activity, String filename, FirebaseUploadFilesListener uploadListener) {
        ApiInterface mApiService = activity.retrofit.create(ApiInterface.class);
        Call<UploadMediaResponse> mService;
        File file = new File(filename);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getMimeType(filename)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("media", file.getName(), requestFile);

        mService = mApiService.uploadFirebaseMedia(body);
        mService.enqueue(new Callback<UploadMediaResponse>() {
            @Override
            public void onResponse(Call<UploadMediaResponse> call, Response<UploadMediaResponse> response) {
                UploadMediaResponse res = response.body();
                if (res != null) {
                    if (res.getStatus().equals("1")) {
                        if (uploadListener != null) {
                            uploadListener.onUploadSuccess(res.getData(), false);
                        }
                    } else {
                        if (uploadListener != null) {
                            uploadListener.onUploadError(res.getMessage(), false);
                        }
                    }
                } else {
                    if (uploadListener != null) {
                        uploadListener.onUploadError("No response", false);
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadMediaResponse> call, Throwable t) {
                call.cancel();
                if (uploadListener != null) {
                    uploadListener.onUploadError(t.getMessage(), false);
                }
            }
        });
    }

    private void uploadDocumentToServer(BaseActivity activity, String filename, FirebaseUploadFilesListener uploadListener) {
        ApiInterface mApiService = activity.retrofit.create(ApiInterface.class);
        Call<UploadDocumentResponse> mService;

        File file = new File(filename);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getMimeType(filename)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("document", file.getName(), requestFile);

        mService = mApiService.uploadFirebaseDocs(body);
        mService.enqueue(new Callback<UploadDocumentResponse>() {
            @Override
            public void onResponse(Call<UploadDocumentResponse> call, Response<UploadDocumentResponse> response) {
                UploadDocumentResponse res = response.body();
                if (res != null) {
                    if (res.getStatus().equals("1")) {
                        if (uploadListener != null) {
                            uploadListener.onUploadSuccess(res.getData(), true);
                        }
                    } else {
                        if (uploadListener != null) {
                            uploadListener.onUploadError(res.getMessage(), true);
                        }
                    }
                } else {
                    if (uploadListener != null) {
                        uploadListener.onUploadError("No response", true);
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadDocumentResponse> call, Throwable t) {
                call.cancel();
                if (uploadListener != null) {
                    uploadListener.onUploadError(t.getMessage(), true);
                }
            }
        });
    }

    public void getMediaUrlFromServer(BaseActivity activity,String imageName, FirebaseGetFilesUrlListener getFilesUrlListener) {
        ApiInterface mApiService = activity.retrofit.create(ApiInterface.class);
        Call<GetMediaResponse> mService;
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("media_name", imageName);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(activity).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        mService = mApiService.getFirebaseMedia(jsonObj);
        mService.enqueue(new Callback<GetMediaResponse>() {
            @Override
            public void onResponse(Call<GetMediaResponse> call, Response<GetMediaResponse> response) {
                GetMediaResponse res = response.body();
                if (res != null) {
                    if (res.getStatus().equals("1")) {
                        if (getFilesUrlListener != null) {
                            getFilesUrlListener.onGetUrl(res.getData());
                        }
                    } else {
                        if (getFilesUrlListener != null) {
                            getFilesUrlListener.onError(res.getMessage());
                        }
                    }
                } else {
                    if (getFilesUrlListener != null) {
                        getFilesUrlListener.onError("No response");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetMediaResponse> call, Throwable t) {
                call.cancel();
                if (getFilesUrlListener != null) {
                    getFilesUrlListener.onError(t.getMessage());
                }
            }
        });
    }

    public void getDocumentUrlFromServer(BaseActivity activity,String imageName, FirebaseGetFilesUrlListener getFilesUrlListener) {
        ApiInterface mApiService = activity.retrofit.create(ApiInterface.class);
        Call<GetDocumentResponse> mService;
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("document_name", imageName);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(activity).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        mService = mApiService.getFirebaseDocs(jsonObj);
        mService.enqueue(new Callback<GetDocumentResponse>() {
            @Override
            public void onResponse(Call<GetDocumentResponse> call, Response<GetDocumentResponse> response) {
                GetDocumentResponse res = response.body();
                if (res != null) {
                    if (res.getStatus().equals("1")) {
                        if (getFilesUrlListener != null) {
                            getFilesUrlListener.onGetUrl(res.getData());
                        }
                    } else {
                        if (getFilesUrlListener != null) {
                            getFilesUrlListener.onError(res.getMessage());
                        }
                    }
                } else {
                    if (getFilesUrlListener != null) {
                        getFilesUrlListener.onError("No response");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetDocumentResponse> call, Throwable t) {
                call.cancel();
                if (getFilesUrlListener != null) {
                    getFilesUrlListener.onError(t.getMessage());
                }
            }
        });
    }

    public File compressFile(String filename, boolean isFile) {
        File file = new File(filename);
        if (isVideoFile(file.getPath()) || isFile) {
            return file;
        }
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 50;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            String path = file.getPath();
            path = path.replace(file.getName(), "compressed" + filename.substring(filename.lastIndexOf(".")));
            File compressedFile = new File(path);
            compressedFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(compressedFile);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            copyExif(file.getPath(), compressedFile.getPath());

            return compressedFile;
        } catch (Exception e) {
            return null;
        }
    }
}
