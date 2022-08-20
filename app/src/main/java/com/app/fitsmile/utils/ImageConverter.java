package com.app.fitsmile.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.app.fitsmile.common.Utils;

import java.io.ByteArrayOutputStream;

public class ImageConverter extends AsyncTask<Bitmap,Void,String> {

    private String question;

    public ImageConverter(String question)  {
        this.question=question;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        if (bitmaps!=null && bitmaps.length>0) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            /* return Base64.encodeToString(byteArray, Base64.DEFAULT);*/

            /*ByteBuffer dst= ByteBuffer.allocate(bitmaps[0].getByteCount());
            bitmaps[0].copyPixelsToBuffer( dst);
            byte[] byteArray=dst.array();*/
            return convertBitmapToBase64(byteArray);
        }else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Utils.closeProgressDialog();
        if (s!=null){
            question=s;
        }
    }  public static String convertBitmapToBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] compressFaceImage(byte[] faceImage, int targetH, int compressRate) {
        Bitmap bitmap = resizeBitmap(faceImage, targetH);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, stream);
        return stream.toByteArray();
    }

    private static Bitmap resizeBitmap(byte[] bytes, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmOptions);
        int originalWidth = bmOptions.outWidth;
        int originalHeight = bmOptions.outHeight;
        int targetW = (originalWidth * targetH) / originalHeight;
        return Bitmap.createScaledBitmap(convertByteArrayToBitmap(bytes), targetW, targetH, true);
    }

    public static Bitmap convertByteArrayToBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
    public static byte[] getByteArrayFromBase64(String base64){
        return Base64.decode(base64,Base64.DEFAULT);
    }

}
