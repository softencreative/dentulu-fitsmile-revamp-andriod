package com.app.fitsmile.response.trayMinder;

public class TrayMinderDataResponse {


        private int status;

        private String message;

        private TrayMinderResult data;

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
        public void setData(TrayMinderResult data){
            this.data = data;
        }
        public TrayMinderResult getData(){
            return this.data;
        }
    }

