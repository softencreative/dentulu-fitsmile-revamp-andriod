package com.app.fitsmile.my_address;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Update_addressuser implements Serializable {

    @SerializedName("status")

    String status;

    @SerializedName("message")

    String message;


    @SerializedName("address_id")

    String address_id;


    public String getStatus() {

        return status;

    }

    public void setStatus(String status) {

        this.status = status;

    }

    public String getMessage() {

        return message;

    }

    public void setMessage(String message) {

        this.message = message;

    }


    public String getStatus_code() {

        return address_id;

    }

    public void setStatus_code(String status_code) {

        this.address_id = status_code;

    }

}