package com.app.fitsmile.response.trayMinder;

import java.util.List;

public class SmileProgressImageResponse
{
    private int status;

    private String message;

    private List<SmileProgressImageResult> data;

    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setData(List<SmileProgressImageResult> data){
        this.data = data;
    }
    public List<SmileProgressImageResult> getData(){
        return this.data;
    }
}