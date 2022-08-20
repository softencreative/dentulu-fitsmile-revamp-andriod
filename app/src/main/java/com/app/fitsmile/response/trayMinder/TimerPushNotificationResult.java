package com.app.fitsmile.response.trayMinder;

import com.google.gson.annotations.SerializedName;

public class TimerPushNotificationResult {
    @SerializedName("Name")
    private String name;

    @SerializedName("Time")
    private String time;

    public String getName() {
        return name;
    }


    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
