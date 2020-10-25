package com.example.covivid.Retrofit.Interceptors;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.covivid.Retrofit.Exceptions.NoInternetConnectionException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor
{
    private Context context;

    public NetworkConnectionInterceptor(Context context)
    {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        if(!isConnectionEstablished())
        {
            throw new NoInternetConnectionException("Internet connection is not established!");
        }

        Request.Builder requestBuilder = chain.request().newBuilder();

        return chain.proceed(requestBuilder.build());
    }

    private boolean isConnectionEstablished()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
}
