package com.example.covivid.Model.CovidNews;

import com.google.gson.annotations.SerializedName;

public class NewsResponse
{
    @SerializedName("response")
    private NewsPack newsPack;

    public NewsPack getNewsPack()
    {
        return newsPack;
    }
}
