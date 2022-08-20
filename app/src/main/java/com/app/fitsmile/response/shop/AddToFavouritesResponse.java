package com.app.fitsmile.response.shop;

public class AddToFavouritesResponse {
    private String status;
    private String message;
    private Boolean datas;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getDatas() {
        return datas;
    }

    public void setDatas(Boolean datas) {
        this.datas = datas;
    }
}
