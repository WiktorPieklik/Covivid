package com.example.covivid.Retrofit;

import com.example.covivid.Model.BaseCovidReport;
import com.example.covivid.Model.ComplexCovidReport;
import com.example.covivid.Model.Country;
import com.example.covivid.Model.WorldCovidReport;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ICovidAPI
{
    @GET("countries")
    Call<List<Country>> getCountries();

    @GET("total/country/{country}/status/confirmed")
    Call<List<BaseCovidReport>> getByCountry(@Path("country") String countrySlug,
                                             @Query("from") String dateFrom,
                                             @Query("to") String dateTo);

    @GET("total/country/{country}")
    Call<List<ComplexCovidReport>> getTotalByCountry(@Path("country") String countrySlug);

    @GET("world/total")
    Call<List<WorldCovidReport>> getWorldsTotal();

}
