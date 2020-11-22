package com.example.covivid.Models.CovidReport;

import com.google.gson.annotations.SerializedName;

public class WorldCovidReport
{
    @SerializedName("TotalConfirmed")
    private int totalConfirmed;

    @SerializedName("TotalDeaths")
    private int totalDeaths;

    @SerializedName("TotalRecovered")
    private int totalRecovered;

    public int getTotalConfirmed()
    {
        return totalConfirmed;
    }

    public int getTotalDeaths()
    {
        return totalDeaths;
    }

    public int getTotalRecovered()
    {
        return totalRecovered;
    }
}
