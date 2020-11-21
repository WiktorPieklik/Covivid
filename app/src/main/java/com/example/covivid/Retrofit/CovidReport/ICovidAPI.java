package com.example.covivid.Retrofit.CovidReport;

import com.example.covivid.Models.CovidReport.BaseCovidReport;
import com.example.covivid.Models.CovidReport.ComplexCovidReport;
import com.example.covivid.Models.CovidReport.Country;
import com.example.covivid.Models.CovidReport.WorldCovidReport;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ICovidAPI
{
    @GET("countries")
    Call<List<Country>> getCountries();

    @GET("total/country/{country}/status/confirmed")
    Observable<List<BaseCovidReport>> getByCountry(@Path("country") String countrySlug,
                                             @Query("from") String dateFrom,
                                             @Query("to") String dateTo);

    @GET("total/country/{country}")
    Observable<List<ComplexCovidReport>> getTotalByCountry(@Path("country") String countrySlug);

    @GET("world/total")
    Observable<List<WorldCovidReport>> getWorldsTotal();

}
