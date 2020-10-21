package com.example.covivid.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covivid.Adapters.Reports.BaseReportAdapter;
import com.example.covivid.Model.CovidReport.BaseCovidReport;
import com.example.covivid.R;
import com.example.covivid.Retrofit.ICovidAPI;
import com.example.covivid.Utils.Common;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    RecyclerView baseReportRecycler;
    BaseReportAdapter baseReportAdapter;
    ICovidAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Common.requestFullScreenActivity(this);
        setContentView(R.layout.activity_main);
        init();
        loadBaseReports();
    }

    private void init()
    {
        baseReportRecycler = findViewById(R.id.base_report_rv);
        api = Common.getApi();
    }

    private void loadBaseReports()
    {
        //TODO: Remove hardcoded params
        Call<List<BaseCovidReport>> call = api.getByCountry(
                "poland",
                "2020-09-09T00:00:00Z",
                "2020-10-11T00:00:00Z");
        call.enqueue(new Callback<List<BaseCovidReport>>() {
            @Override
            public void onResponse(Call<List<BaseCovidReport>> call, Response<List<BaseCovidReport>> response) {
                if(response.isSuccessful()) {
                    List<BaseCovidReport> reports = response.body();
                    displayBaseReports(reports);
                }
            }

            @Override
            public void onFailure(Call<List<BaseCovidReport>> call, Throwable t) {

            }
        });
    }

    private void displayBaseReports(List<BaseCovidReport> reports)
    {
        baseReportAdapter = new BaseReportAdapter(this, reports);
        baseReportRecycler.setAdapter(baseReportAdapter);
        baseReportRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}