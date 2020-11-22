package com.example.covivid.Models.CovidReport;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BaseCovidReport
{
    @SerializedName("Country")
    protected String country;

    @SerializedName("CountryCode")
    protected String countryCode;

    @SerializedName("Lat")
    protected float latitude;

    @SerializedName("Lon")
    protected float longitude;

    @SerializedName("Cases")
    protected int cases;

    @SerializedName("Status")
    protected String status;

    @SerializedName("Date")
    protected Date date;

    public String getCountry()
    {
        return country;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public float getLatitude()
    {
        return latitude;
    }

    public float getLongitude()
    {
        return longitude;
    }

    public int getCases()
    {
        return cases;
    }

    public String getStatus()
    {
        return status;
    }

    public Date getDate()
    {
        return date;
    }
}
