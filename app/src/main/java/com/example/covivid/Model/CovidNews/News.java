package com.example.covivid.Model.CovidNews;

import com.google.gson.annotations.SerializedName;

public class News
{
    //TODO: choose which fields you'd like to retrieve. Down here are all available from guardian API
    private String id;
    private String type;
    private String sectionId;
    private String sectionName;
    private String webPublicationDate; //TODO: change it to java.util.Date or some other date class
    private String webTitle;
    @SerializedName("webUrl")
    private String url;
    private String apiUrl;
    private NewsFields fields;
    private boolean isHosted;
    private String pillarId;
    private String pillarName;

    public String getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public String getSectionId()
    {
        return sectionId;
    }

    public String getSectionName()
    {
        return sectionName;
    }

    public String getWebPublicationDate()
    {
        return webPublicationDate;
    }

    public String getWebTitle()
    {
        return webTitle;
    }

    public String getUrl()
    {
        return url;
    }

    public String getApiUrl()
    {
        return apiUrl;
    }

    public NewsFields getFields()
    {
        return fields;
    }

    public boolean isHosted()
    {
        return isHosted;
    }

    public String getPillarId()
    {
        return pillarId;
    }

    public String getPillarName()
    {
        return pillarName;
    }
}
