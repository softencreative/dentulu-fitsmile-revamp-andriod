package com.app.fitsmile.response.shop;

import java.util.List;

public class FavouriteProductsResponse {
    private String status;

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

    public List<ProductResult> getDatas() {
        return datas;
    }

    public void setDatas(List<ProductResult> datas) {
        this.datas = datas;
    }

    private String message;
    private List<ProductResult> datas;

}
