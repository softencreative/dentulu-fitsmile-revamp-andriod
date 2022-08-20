package com.app.fitsmile.response.trayMinder;

public class AlignerListResult {
        private String id;

        private String fits_smile_id;

        private String name;

        private String aligner_no;

        private String no_of_days;

        private String is_completed;

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
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setAligner_no(String aligner_no){
            this.aligner_no = aligner_no;
        }
        public String getAligner_no(){
            return this.aligner_no;
        }
        public void setNo_of_days(String no_of_days){
            this.no_of_days = no_of_days;
        }
        public String getNo_of_days(){
            return this.no_of_days;
        }
        public void setIs_completed(String is_completed){
            this.is_completed = is_completed;
        }
        public String getIs_completed(){
            return this.is_completed;
        }
    }
