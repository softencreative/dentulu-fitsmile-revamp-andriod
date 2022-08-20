package com.app.fitsmile.response.login;

import com.google.gson.annotations.SerializedName;

public class DentalOfficeResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private LoginResponse data;

    public LoginResponse getData() {
        return data;
    }

    public String getMessage(){
        return message;
    }

    public String getStatus(){
        return status;
    }
}
