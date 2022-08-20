package com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail;

import android.net.Uri;

import java.io.Serializable;

public class RecodedVideoModel implements Serializable {
    private Uri videoUri;
    private boolean isPlay;
    private String base64;
    private String videoUrl;

    public RecodedVideoModel() {
    }

    public RecodedVideoModel(Uri videoUri, boolean isPlay,String base64) {
        this.videoUri = videoUri;
        this.isPlay = isPlay;
        this.base64=base64;
    }

    public RecodedVideoModel(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "RecodedVideoModel{" +
                "videoUri=" + videoUri +
                ", isPlay=" + isPlay +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
