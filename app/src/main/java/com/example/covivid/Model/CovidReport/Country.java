package com.example.covivid.Model.CovidReport;

import com.google.gson.annotations.SerializedName;

public class Country
{
    @SerializedName("Country")
    private String name;

    @SerializedName("Slug")
    private String slug;

    @SerializedName("ISO2")
    private String iso2;

    public String getName()
    {
        return name;
    }

    public String getSlug()
    {
        return slug;
    }

    public String getIso2()
    {
        return iso2;
    }
}
