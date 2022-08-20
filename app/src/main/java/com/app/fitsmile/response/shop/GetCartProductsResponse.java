package com.app.fitsmile.response.shop;

import java.util.List;

public class GetCartProductsResponse {
    public List<ProductResult> getDatas() {
        return datas;
    }

    public void setDatas(List<ProductResult> datas) {
        this.datas = datas;
    }

    private List<ProductResult> datas;
    private String message;
    private String base_url;
    private String status;
    private String total_amount;


    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    @Override
    public String toString() {
        return "ClassPojo [datas = "+datas+", message = "+message+", status = "+status+"]";
    }



}

