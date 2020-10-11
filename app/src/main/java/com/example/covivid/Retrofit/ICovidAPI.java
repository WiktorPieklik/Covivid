package com.example.covivid.Retrofit;

import com.example.covivid.Model.Country;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ICovidAPI
{
    @GET("countries")
    Call<List<Country>> getCountries();
}
