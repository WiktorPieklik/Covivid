package com.example.covivid.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covivid.R;
import com.example.covivid.Utils.Common;

public class BaseReportActivity extends AppCompatActivity
{
    TextView countryTxt, latTxt, lonTxt, statusTxt, casesTxt, dateTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Common.requestFullScreenActivity(this);
        setContentView(R.layout.activity_base_report);
        initViews();
    }

    @SuppressLint("DefaultLocale")
    private void initViews()
    {
        countryTxt = findViewById(R.id.base_report_country);
        latTxt = findViewById(R.id.base_report_lat);
        lonTxt = findViewById(R.id.base_report_lon);
        statusTxt = findViewById(R.id.base_report_status);
        casesTxt = findViewById(R.id.base_report_cases);
        dateTxt = findViewById(R.id.base_report_date);

        if(Common.baseReport != null)
        {
            countryTxt.setText(String.format(
                    "%s: %s",
                    getResources().getString(R.string.country),
                    Common.baseReport.getCountry()));
            latTxt.setText(String.format(
                    "%s: %f",
                    getResources().getString(R.string.latitude),
                    Common.baseReport.getLatitude()));
            lonTxt.setText(String.format(
                    "%s: %f",
                    getResources().getString(R.string.longitude),
                    Common.baseReport.getLongitude()));
            statusTxt.setText(String.format(
                    "%s: %s",
                    getResources().getString(R.string.status),
                    Common.baseReport.getStatus()));
            casesTxt.setText(String.format(
                    "%s: %s",
                    getResources().getString(R.string.cases),
                    Common.baseReport.getCases()));
            dateTxt.setText(String.format(
                    "%s: %s",
                    getResources().getString(R.string.date),
                    Common.baseReport.getDate()));
        }
    }
}