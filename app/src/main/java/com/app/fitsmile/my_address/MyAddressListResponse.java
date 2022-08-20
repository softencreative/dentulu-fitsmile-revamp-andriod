package com.app.fitsmile.my_address;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyAddressListResponse {

    @SerializedName("location_datas")
    private List<Address> datas;

    @SerializedName("address")
    private List<Address> address;

    @SerializedName("message")
    private String message;

    public List<Address> getAddress() {
        return address;
    }

    @SerializedName("status")
    private String status;

    public List<Address> getDatas() {
        return datas;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}