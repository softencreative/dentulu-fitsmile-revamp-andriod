package com.app.fitsmile.response.myaccount;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FAQResponse {

    @SerializedName("data")
    private List<FAQResult> faqData;


    @SerializedName("status")
    private String status;


    @SerializedName("message")
    private String message;


    @SerializedName("description")
    private String description;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public List<FAQResult> getFaqData(){
        return faqData;
    }



    public String getStatus(){
        return status;
    }
}
