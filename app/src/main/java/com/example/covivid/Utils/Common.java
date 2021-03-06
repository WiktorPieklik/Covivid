package com.example.covivid.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covivid.Models.CovidReport.BaseCovidReport;
import com.example.covivid.Retrofit.CovidReport.ICovidAPI;
import com.example.covivid.Retrofit.CovidNews.ITheGuardianAPI;
import com.example.covivid.Retrofit.CovidReport.RetrofitCovidClient;
import com.example.covivid.Retrofit.CovidNews.RetrofitNewsClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public class Common
{
    public static final String COVID_BASE_URL = "https://api.covid19api.com";

    public static final String GUARDIAN_BASE_URL = "https://content.guardianapis.com";

    public static final String GUARDIAN_API_KEY = "d2a28e79-441e-4c37-8710-035a8a1dc7c8";


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

    public static ICovidAPI getCovidAPI(Context context)
    {
        return RetrofitCovidClient
                .getClient(context, COVID_BASE_URL)
                .create(ICovidAPI.class);
    }


    public static ITheGuardianAPI getNewsApi(Context context)
    {
        return RetrofitNewsClient
                .getClient(context, GUARDIAN_BASE_URL)
                .create(ITheGuardianAPI.class);
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static int getRandomColor()
    {
        Random random = new Random();

        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)view
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

}
