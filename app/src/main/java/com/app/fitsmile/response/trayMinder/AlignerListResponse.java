package com.app.fitsmile.response.trayMinder;

import java.util.List;

public class AlignerListResponse
{
    private int status;

    private String message;

    private List<AlignerListResult> data;

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
    public void setData(List<AlignerListResult> data){
        this.data = data;
    }
    public List<AlignerListResult> getData(){
        return this.data;
    }
}
