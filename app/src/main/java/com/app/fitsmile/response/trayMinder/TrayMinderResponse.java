package com.app.fitsmile.response.trayMinder;

import java.util.List;

public class TrayMinderResponse {


        private int status;

        private String message;

        private List<TrayMinderResult> data;

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
        public void setData(List<TrayMinderResult> data){
            this.data = data;
        }
        public List<TrayMinderResult> getData(){
            return this.data;
        }
    }

