package com.app.fitsmile.response.trayMinder;

import java.io.Serializable;

public class Logs implements Serializable
{
    private String id;

    private String fits_smile_id;

    private String date;

    private String day;

    private String in_time;

    private String out_time;

    private String created_date;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setFits_smile_id(String fits_smile_id){
        this.fits_smile_id = fits_smile_id;
    }
    public String getFits_smile_id(){
        return this.fits_smile_id;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public void setDay(String day){
        this.day = day;
    }
    public String getDay(){
        return this.day;
    }
    public void setIn_time(String in_time){
        this.in_time = in_time;
    }
    public String getIn_time(){
        return this.in_time;
    }
    public void setOut_time(String out_time){
        this.out_time = out_time;
    }
    public String getOut_time(){
        return this.out_time;
    }
    public void setCreated_date(String created_date){
        this.created_date = created_date;
    }
    public String getCreated_date(){
        return this.created_date;
    }
}
