package com.example.covivid.Model.CovidNews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsPack
{
    //TODO: choose which fields you'd like to retrieve. Down here are all available from guardian API
    private String status;
    private String userTier;
    private int total;
    private int startIndex;
    private int pageSize;
    private int currentPage;
    private int pages;
    private String orderBy;
    @SerializedName("results")
    private List<News> news;

    public String getStatus()
    {
        return status;
    }

    public String getUserTier()
    {
        return userTier;
    }

    public int getTotal()
    {
        return total;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public int getPages()
    {
        return pages;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public List<News> getNews()
    {
        return news;
    }
}
