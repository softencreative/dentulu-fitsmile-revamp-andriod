package com.app.fitsmile.photoConsultation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.app.fitsmile.common.Utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageConverterAsync extends AsyncTask<Bitmap,Void,String> {

    private Question question;

    public ImageConverterAsync( Question question)  {
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
            question.setBase64Encoded(s);
        }
    }

    private String convertBitmapToBase64(byte[] bytes) {
       return Base64.encodeToString(compressFaceImage(bytes,
               1080,90), Base64.DEFAULT);
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

    private  byte[] convertBitmapToByteArray(Bitmap bitmap){
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] byteArray = byteBuffer.array();
        return byteArray;
    }
}
