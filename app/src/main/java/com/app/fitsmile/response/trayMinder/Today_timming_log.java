package com.app.fitsmile.response.trayMinder;

import java.io.Serializable;

public class Today_timming_log implements Serializable
{
    private String id;

    private String fits_smile_id;

    private String date;

    private String day;

    private String in_time;
    private String total_in_time;

    private String out_time;
    private String total_out_time;

    private  String aligner_no;

    private String aligner_id;

    private String updated_date;

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
    public void setUpdated_date(String updated_date){
        this.updated_date = updated_date;
    }
    public String getUpdated_date(){
        return this.updated_date;
    }

    public String getTotal_in_time() {
        return total_in_time;
    }

    public void setTotal_in_time(String total_in_time) {
        this.total_in_time = total_in_time;
    }

    public String getTotal_out_time() {
        return total_out_time;
    }

    public void setTotal_out_time(String total_out_time) {
        this.total_out_time = total_out_time;
    }

    public String getAligner_no() {
        return aligner_no;
    }

    public void setAligner_no(String aligner_no) {
        this.aligner_no = aligner_no;
    }

    public String getAligner_id() {
        return aligner_id;
    }

    public void setAligner_id(String aligner_id) {
        this.aligner_id = aligner_id;
    }
}
