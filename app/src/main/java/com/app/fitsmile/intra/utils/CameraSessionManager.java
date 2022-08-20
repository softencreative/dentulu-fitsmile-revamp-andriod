package com.app.fitsmile.intra.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CameraSessionManager {

    public int i,j;

    private static CameraSessionManager instance;
    private static String PREF_NAME     = "";

    private final String VIDEO_COUNT    = "video_count";
    private final String PHOTO_COUNT    = "photo_count";

    SharedPreferences pref;
    Editor editor;
    Context context;
    private int PRIVATE_MODE = 0;

    // Constructor
    public CameraSessionManager(Context context) {
        this.context = context;
        PREF_NAME = context.getPackageName();
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Thread safe singleton
    public synchronized static CameraSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new CameraSessionManager(context);
        }
        return instance;
    }

    public void setCount(int video_count, int photo_count) {
        editor.putInt(VIDEO_COUNT, video_count);
        editor.putInt(PHOTO_COUNT, photo_count);
        editor.commit();
    }

    public boolean verifySession() {
        boolean validToken = false;
        int token = getPHOTO_COUNT();
        if (token==0) {
            validToken = true;
        }
        return validToken;
    }


    public int getVIDEO_COUNT() {
        return pref.getInt(VIDEO_COUNT, 0);
    }

    public int getPHOTO_COUNT() {
        return pref.getInt(PHOTO_COUNT, 0);
    }
}