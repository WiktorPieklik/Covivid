package com.example.covivid.Retrofit;

import com.example.covivid.Model.News;
import com.example.covivid.Model.NewsPack;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITheGuardianAPI {


    @GET("/search")
    Call<NewsPack> getNews(@Query("q") String keyword, @Query("api-key") String apiKey, @Query("show-fields") String[] fields);

    @GET("/search?show-fields=headline%2Cthumbnail%2CtrailText&q=covid&api-key=d2a28e79-441e-4c37-8710-035a8a1dc7c8")
    Call<NewsPack> testCall();
}
