package com.example.covivid.Models.CovidReport;

import com.google.gson.annotations.SerializedName;

public class ComplexCovidReport extends BaseCovidReport
{
    @SerializedName("Confirmed")
    public int confirmed;

    @SerializedName("Deaths")
    public int deaths;

    @SerializedName("Recovered")
    public int recovered;

    @SerializedName("Active")
    public int active;

    public int getConfirmed()
    {
        return confirmed;
    }

    public int getDeaths()
    {
        return deaths;
    }

    public int getRecovered()
    {
        return recovered;
    }

    public int getActive()
    {
        return active;
    }
}
