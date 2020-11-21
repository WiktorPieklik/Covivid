package com.example.covivid.Retrofit;

import android.content.Context;

import com.example.covivid.Retrofit.Interceptors.NetworkConnectionInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNewsClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context, String baseUrl)
    {
        if(retrofit == null) {
            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NetworkConnectionInterceptor(context));
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}