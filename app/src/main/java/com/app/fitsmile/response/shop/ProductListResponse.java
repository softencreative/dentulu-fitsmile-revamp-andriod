package com.app.fitsmile.response.shop;

import com.app.fitsmile.response.common.CommonResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductListResponse  extends CommonResponse {
    @SerializedName("datas")
    private List<ProductResult> productListResult;

    public List<ProductResult> getProductListResult() {
        return productListResult;
    }
}
