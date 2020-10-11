package com.example.covivid.Utils;

import com.example.covivid.Retrofit.ICovidAPI;
import com.example.covivid.Retrofit.RetrofitClient;

public class Common
{
    public static final String BASE_URL = "https://api.covid19api.com";

    public static ICovidAPI getApi()
    {
        return RetrofitClient
                .getClient(BASE_URL)
                .create(ICovidAPI.class);
    }
}
