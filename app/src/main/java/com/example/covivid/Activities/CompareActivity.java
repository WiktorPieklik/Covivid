package com.example.covivid.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covivid.R;
import com.example.covivid.Retrofit.CovidReport.ICovidAPI;
import com.example.covivid.Utils.Common;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import java.util.ArrayList;
import java.util.List;

public class CompareActivity extends AppCompatActivity {

    private ICovidAPI covidAPI;
    private BarChart chart_deaths, chart_active, chart_total, chart_recovered;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Common.requestFullScreenActivity(this);
        setContentView(R.layout.activity_compare);
        chart_deaths = findViewById(R.id.chart_compare_deaths);
        chart_active = findViewById(R.id.chart_compare_active);
        chart_total = findViewById(R.id.chart_compare_total);
        chart_recovered = findViewById(R.id.chart_compare_recovered);
        covidAPI = Common.getCovidAPI(this);
        initData();
    }

    private void initData()
    {
        Pair<String, String> countries = new Pair<>("Germany", "Poland");
        Pair<Integer, Integer> values = new Pair<>(400, 200);
        setupChart(chart_deaths, countries, values);
        setupChart(chart_active, countries, values);
        setupChart(chart_total, countries, values);
        setupChart(chart_recovered, countries, values);

    }
    void setupChart(BarChart chart, Pair<String, String> countries, Pair<Integer, Integer> data)
    {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, data.first));
        entries.add(new BarEntry(2, data.second));

        BarDataSet dataSet = new BarDataSet(entries, "Data set");
        dataSet.setValueFormatter(new LargeValueFormatter());
        dataSet.setValueTextSize(16f);
        dataSet.setColors(Common.getRandomColor(), Common.getRandomColor());
        dataSet.setBarShadowColor(Color.GRAY);

        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(getResources().getColor(R.color.chart_background, getTheme()));
        chart.setDrawBorders(false);
        chart.setTouchEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);

        chart.getLegend().setEnabled(false);

        chart.getDescription().setEnabled(false);

        chart.getAxisLeft().setDrawTopYLabelEntry(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setAxisMinimum(0);

        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setAxisMinimum(0);

        chart.getXAxis().setTextSize(16f);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setDrawAxisLine(false);



        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.64f);
        chart.setData(barData);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"", countries.first, countries.second}));//setting String values in Xaxis


        chart.setExtraOffsets(30, 30, 30, 30);
        chart.animateY(1000);
        chart.invalidate();
    }

}
