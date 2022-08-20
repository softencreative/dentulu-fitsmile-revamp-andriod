package com.app.fitsmile.my_address;

import com.google.gson.annotations.SerializedName;

public class DeleteAddressResponse {
    @SerializedName("status")
    String Status;
    @SerializedName("message")
    String Message;

    public String getMessage() {
        return Message;
    }

    public String getStatus() {
        return Status;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
