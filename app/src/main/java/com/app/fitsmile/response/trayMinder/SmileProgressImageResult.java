package com.app.fitsmile.response.trayMinder;

import java.io.Serializable;

public class SmileProgressImageResult implements Serializable
{
    private String id;

    private String fits_smile_id;

    private String aligner_id;

    private String timming_logs_id;

    private String name;

    private String  created_date;

    private String aligner_name;

    private String aligner_no;

    private String image_url;
    private String left_image_url;
    private String right_image_url;
    private String center_image_url;
    private String fourth_image_url;
    private String fifth_image_url;



    private String mBitmapGallery;

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
    public void setAligner_id(String aligner_id){
        this.aligner_id = aligner_id;
    }
    public String getAligner_id(){
        return this.aligner_id;
    }
    public void setTimming_logs_id(String timming_logs_id){
        this.timming_logs_id = timming_logs_id;
    }
    public String getTimming_logs_id(){
        return this.timming_logs_id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setCreated_date(String created_date){
        this.created_date = created_date;
    }
    public String getCreated_date(){
        return this.created_date;
    }
    public void setAligner_name(String aligner_name){
        this.aligner_name = aligner_name;
    }
    public String getAligner_name(){
        return this.aligner_name;
    }
    public void setAligner_no(String aligner_no){
        this.aligner_no = aligner_no;
    }
    public String getAligner_no(){
        return this.aligner_no;
    }
    public void setImage_url(String image_url){
        this.image_url = image_url;
    }
    public String getImage_url(){
        return this.image_url;
    }

    public String getBitmapGallery() {
        return mBitmapGallery;
    }

    public void setBitmapGallery(String mBitmapGallery) {
        this.mBitmapGallery = mBitmapGallery;
    }

    public String getRight_image_url() {
        return right_image_url;
    }

    public void setRight_image_url(String right_image_url) {
        this.right_image_url = right_image_url;
    }

    public String getLeft_image_url() {
        return left_image_url;
    }

    public void setLeft_image_url(String left_image_url) {
        this.left_image_url = left_image_url;
    }

    public String getCenter_image_url() {
        return center_image_url;
    }

    public void setCenter_image_url(String center_image_url) {
        this.center_image_url = center_image_url;
    }

    public String getFourth_image_url() {
        return fourth_image_url;
    }

    public void setFourth_image_url(String fourth_image_url) {
        this.fourth_image_url = fourth_image_url;
    }

    public String getFifth_image_url() {
        return fifth_image_url;
    }

    public void setFifth_image_url(String fifth_image_url) {
        this.fifth_image_url = fifth_image_url;
    }
}