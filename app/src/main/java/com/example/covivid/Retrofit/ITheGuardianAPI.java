package com.example.covivid.Retrofit;

import com.example.covivid.Model.NewsResponse;
import com.example.covivid.Utils.Common;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITheGuardianAPI {


    @GET("search")
    Call<NewsResponse> getNews(@Query("q") String keyword,
                               @Query("api-key") String apiKey,
                               @Query("show-fields") String fields);
}
