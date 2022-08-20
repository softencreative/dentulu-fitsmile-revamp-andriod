package com.app.fitsmile.response.shop;

import com.google.gson.annotations.SerializedName;

public class CategoryListResult {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("slug")
    private String slug;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public String getSlug() {
        return slug;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
