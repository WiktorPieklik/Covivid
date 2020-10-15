package com.example.covivid.Utils;

import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covivid.Model.BaseCovidReport;
import com.example.covivid.Retrofit.ICovidAPI;
import com.example.covivid.Retrofit.RetrofitClient;

public class Common
{
    public static final String BASE_URL = "https://api.covid19api.com";

    public static BaseCovidReport baseReport = null; //there can be only one base report at the time

    /**
     * Must be called before setContentView()
     */
    public static void requestFullScreenActivity(AppCompatActivity activity)
    {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity
                .getWindow()
                .setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getSupportActionBar().hide();
    }

    public static ICovidAPI getApi()
    {
        return RetrofitClient
                .getClient(BASE_URL)
                .create(ICovidAPI.class);
    }
}
