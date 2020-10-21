package com.example.covivid.Model.CovidReport;

import com.google.gson.annotations.SerializedName;

public class ComplexCovidReport extends BaseCovidReport
{
    @SerializedName("Confirmed")
    protected int confirmed;

    @SerializedName("Deaths")
    protected int deaths;

    @SerializedName("Recovered")
    protected int recovered;

    @SerializedName("Active")
    protected int active;

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
