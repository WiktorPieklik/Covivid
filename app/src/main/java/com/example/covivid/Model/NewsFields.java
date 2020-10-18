package com.example.covivid.Model;

public class NewsFields {

    private String headline;

    private String thumbnail;

    private String trailText;

    public NewsFields(String headline, String thumbnail, String trailText) {
        this.headline = headline;
        this.thumbnail = thumbnail;
        this.trailText = trailText;
    }

    public String getTrailText() {
        return trailText;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
