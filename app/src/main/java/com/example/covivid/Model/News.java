package com.example.covivid.Model;

import com.google.gson.annotations.SerializedName;

public class News {

    @SerializedName("webUrl")
    private String url;

    @SerializedName("fields")
    private NewsFields fields;

    public String getUrl() {
        return url;
    }

    public News(String url, NewsFields fields) {
        this.url = url;
        this.fields = fields;
    }

    public NewsFields getFields() {
        return fields;
    }
}
