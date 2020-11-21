package com.example.covivid.Retrofit.CovidNews;

import com.example.covivid.Models.CovidNews.NewsResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITheGuardianAPI {


    @GET("search")
    Observable<NewsResponse> getNews(@Query("q") String keyword,
                                     @Query("api-key") String apiKey,
                                     @Query("show-fields") String fields);
}
