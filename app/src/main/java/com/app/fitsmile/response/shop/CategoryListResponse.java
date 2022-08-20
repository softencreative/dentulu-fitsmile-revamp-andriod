package com.app.fitsmile.response.shop;

import com.app.fitsmile.response.common.CommonResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryListResponse extends CommonResponse {
    @SerializedName("datas")
    private List<CategoryListResult> categoryListResult;

    public List<CategoryListResult> getCategoryListResult() {
        return categoryListResult;
    }

}
