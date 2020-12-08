package com.example.covivid.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covivid.Models.CovidReport.ComplexCovidReport;
import com.example.covivid.Models.CovidReport.Country;
import com.example.covivid.R;
import com.example.covivid.Retrofit.CovidReport.ICovidAPI;
import com.example.covivid.Retrofit.Exceptions.NoInternetConnectionException;
import com.example.covivid.Retrofit.Interceptors.NetworkConnectionInterceptor;
import com.example.covivid.Utils.Common;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompareActivity extends AppCompatActivity
{
    public static final String countrySlug = "slug";
    public static final String fromDate = "fromDate";
    public static final String toDate = "toDate";

    private ICovidAPI covidAPI;
    private BarChart chart_deaths, chart_active, chart_total, chart_recovered;
    private String firstCountrySlug, secondCountrySlug, firstCountryName, secondCountryName;
    private Date from, to;
    private List<Country> countryList;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Map<String, ComplexCovidReport> countriesReports;

    private AutoCompleteTextView countryAutocomplete;
    private ScrollView scrollView;

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
        countryAutocomplete = findViewById(R.id.compare_country_autocomplete);
        scrollView = findViewById(R.id.compare_scroll_view);
        covidAPI = Common.getCovidAPI(this);
        countriesReports = new HashMap<>();

        countryAutocomplete.setOnItemClickListener((parent, view, position, id) -> {
            secondCountryName = parent.getItemAtPosition(position).toString();
            secondCountrySlug = countryList
                    .stream()
                    .filter(country -> country.getName().equals(secondCountryName))
                    .findFirst()
                    .get()
                    .getSlug();
            scrollView.setVisibility(View.VISIBLE);
            displayStatisticsForCountries();
            Common.hideKeyboard(view);
        });

        loadCountries();
    }

    private void loadCountries()
    {
        Call<List<Country>> call = covidAPI.getCountries();
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if(response.isSuccessful()) {
                    countryList = response.body();
                    fetchDataFromCallee();
                    displayCountries(countryList);
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                if(t instanceof NoInternetConnectionException)
                {

                }
            }
        });
    }

    private void displayCountries(List<Country> countries)
    {
        List<String> countriesNames = new ArrayList<>();
        for(Country country : countries) {
            countriesNames.add(country.getName());
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                this,
                R.layout.country_dropdown_item,
                countriesNames
        );
        countryAutocomplete.setAdapter(countryAdapter);
    }

    private void fetchDataFromCallee()
    {
        firstCountrySlug = getIntent().getStringExtra(countrySlug);
        firstCountryName = countryList
                .stream()
                .filter(country -> country.getSlug().equals(firstCountrySlug))
                .findFirst()
                .get()
                .getName();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        from = new Date(getIntent().getLongExtra(fromDate, calendar.toInstant().getEpochSecond()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        to = new Date(getIntent().getLongExtra(toDate, calendar.toInstant().getEpochSecond()));
        loadStatisticsForCountries(from, to, firstCountrySlug);
    }

    private void displayStatisticsForCountries()
    {
        loadStatisticsForCountryAndMakeCharts(from, to, secondCountrySlug);
    }

    private void loadStatisticsForCountries(Date from, Date to, String countrySlug)
    {
        (new CompositeDisposable()).add(
                covidAPI.getTotalByCountry(countrySlug)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(complexCovidReports -> calculateReportsForDates(complexCovidReports, from, to, firstCountryName))
        );
    }

    private void loadStatisticsForCountryAndMakeCharts(Date from, Date to, String countrySlug)
    {
        (new CompositeDisposable()).add(
                covidAPI.getTotalByCountry(countrySlug)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(complexCovidReports -> {
                            calculateReportsForDates(complexCovidReports, from, to, secondCountryName);
                            Pair<String, String> countries = new Pair<>(firstCountryName, secondCountryName);
                            Pair<Integer, Integer> deaths = new Pair<>(countriesReports.get(firstCountryName).getDeaths(), countriesReports.get(secondCountryName).getDeaths());
                            Pair<Integer, Integer> active = new Pair<>(countriesReports.get(firstCountryName).getActive(), countriesReports.get(secondCountryName).getActive());
                            Pair<Integer, Integer> total = new Pair<>(countriesReports.get(firstCountryName).getConfirmed(), countriesReports.get(secondCountryName).getConfirmed());
                            Pair<Integer, Integer> recovered = new Pair<>(countriesReports.get(firstCountryName).getRecovered(), countriesReports.get(secondCountryName).getRecovered());
                            setupChart(chart_deaths, countries, deaths);
                            setupChart(chart_active, countries, active);
                            setupChart(chart_total, countries, total);
                            setupChart(chart_recovered, countries, recovered);
                        })
        );
    }

    private void calculateReportsForDates(List<ComplexCovidReport> reports, Date from, Date to, String countryName)
    {
        int activeCases = 0, recovered = 0, totalCases = 0, deaths = 0;
        List<ComplexCovidReport> matchedReports = reports
                .stream()
                .filter(report -> Common.convertToLocalDate(report.getDate()).equals(Common.convertToLocalDate(from)) || Common.convertToLocalDate(report.getDate()).equals(Common.convertToLocalDate(to)))
                .collect(Collectors.toList());

        if(matchedReports.size() > 1) {
            activeCases = Math.abs(
                    matchedReports.get(0).getActive() - matchedReports.get(1).getActive());
            recovered = Math.abs(
                    matchedReports.get(0).getRecovered() - matchedReports.get(1).getRecovered());
            totalCases = Math.abs(
                    matchedReports.get(0).getConfirmed() - matchedReports.get(1).getConfirmed());
            deaths = Math.abs(
                    matchedReports.get(0).getDeaths() - matchedReports.get(1).getDeaths());
        }
        ComplexCovidReport report = new ComplexCovidReport();
        report.active = activeCases;
        report.recovered = recovered;
        report.confirmed = totalCases;
        report.deaths = deaths;
        countriesReports.put(countryName, report);
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
