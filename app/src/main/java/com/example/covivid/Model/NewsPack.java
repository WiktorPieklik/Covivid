package com.example.covivid.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsPack {
    @SerializedName("results")
    List<News> results;

    public List<News> getResults() {
        return results;
    }
}
