package com.app.fitsmile.response.profile;

import com.google.gson.annotations.SerializedName;

public class UpdateProfile {
    @SerializedName("status")
    String Status;
    @SerializedName("message")
    String Message;
    @SerializedName("image")
    String image;

    @SerializedName("birthday_date")
    String birthday_date;

    public String getBirthday_date() {
        return birthday_date;
    }

    public void setBirthday_date(String birthday_date) {
        this.birthday_date = birthday_date;
    }

    public String getAnniversary_date() {
        return anniversary_date;
    }

    public void setAnniversary_date(String anniversary_date) {
        this.anniversary_date = anniversary_date;
    }

    @SerializedName("anniversary_date")
    String anniversary_date;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
    public UpdateProfile(String status, String message) {
        Status = status;
        Message = message;

    }



    public UpdateProfile(String Status) {

        this.Status = Status;

    }

    public String getStatus() {

        return Status;

    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
}
