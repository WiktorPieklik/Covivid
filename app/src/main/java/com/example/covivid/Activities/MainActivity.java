package com.example.covivid.Activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;

import com.airbnb.lottie.LottieAnimationView;
import com.example.covivid.Model.CovidReport.ComplexCovidReport;
import com.example.covivid.Model.CovidReport.Country;
import com.example.covivid.R;
import com.example.covivid.Retrofit.Exceptions.NoInternetConnectionException;
import com.example.covivid.Retrofit.ICovidAPI;
import com.example.covivid.Utils.Common;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

enum CaseType
{
    ACTIVE_CASES,
    RECOVERED,
    TOTAL_CASES,
    DEATHS,
}

public class MainActivity extends AppCompatActivity
{
    private static final String STAGE_COUNTRY_SELECTION = "country_selection";
    private static final String STAGE_DATE_SELECTION = "date_selection";

    private BottomSheetBehavior<?> covidBottomSheetBehavior;

    private Button dateRangeButton;
    private AutoCompleteTextView countryAutocomplete;
    private TextView activeCasesTxt, recoveredTxt, totalCasesTxt, deathsTxt, caseTypeTxt, yearTxt;
    private MaterialDatePicker<Pair<Long, Long>> dateRangePicker;

    private LottieAnimationView noInternetConnectionAnim, statDetailsAnim;
    private AnimatedPieView pieChart;

    private Map<String, String> countries; // country_name : country_slug
    private ICovidAPI covidAPI;
    private String selectedCountrySlug;
    private Date from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Common.requestFullScreenActivity(this);
        setContentView(R.layout.activity_main);
        init();
        loadCountries();
    }

    private void init()
    {
        initViews();
        covidAPI = Common.getCovidAPI(this);
        countryAutocomplete.setOnItemClickListener((parent, view, position, id) -> {
            String key = parent.getItemAtPosition(position).toString();
            selectedCountrySlug = countries.get(key);
            hideKeyboard(view);
            dateRangeButton.setVisibility(View.VISIBLE);
        });
        countryAutocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                dateRangeButton.setVisibility(View.INVISIBLE);
                covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        dateRangeButton.setOnClickListener(
                view -> dateRangePicker.show(getSupportFragmentManager(), dateRangePicker.toString()));
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            from = new Date(selection.first);
            to = new Date(selection.second);
            covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            dateRangeButton.setVisibility(View.INVISIBLE);
            loadStatisticsForCountry(from, to);
        });
    }

    private void initViews()
    {
        ConstraintLayout bottomSheetCovid = findViewById(R.id.covid_report_layout);
        covidBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetCovid);
        covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        activeCasesTxt = findViewById(R.id.active_cases_no_txt);
        recoveredTxt = findViewById(R.id.recovered_no_txt);
        totalCasesTxt = findViewById(R.id.total_cases_no_txt);
        deathsTxt = findViewById(R.id.deaths_no_txt);
        caseTypeTxt = findViewById(R.id.case_type_txt);
        yearTxt = findViewById(R.id.year_txt);

        dateRangeButton = findViewById(R.id.date_range_picker);

        noInternetConnectionAnim = findViewById(R.id.no_internet_anim);
        statDetailsAnim = findViewById(R.id.chart_anim);
        pieChart = findViewById(R.id.animated_pie_chart);

        countryAutocomplete = findViewById(R.id.country_autocomplete);

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(
                        new Pair<>(MaterialDatePicker.todayInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()))
                .setTitleText(R.string.date_range_picker_hint);
        dateRangePicker = builder.build();
    }

    private void loadCountries()
    {
        Call<List<Country>> call = covidAPI.getCountries();
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if(response.isSuccessful()) {
                    List<Country> countriesList = response.body();
                    countries = new HashMap<>();
                    for(Country country : countriesList) {
                        countries.put(country.getName(), country.getSlug());
                    }
                    displayCountries(countriesList);
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                if(t instanceof NoInternetConnectionException)
                {
                    countryAutocomplete.setEnabled(false);
                    playAnim(noInternetConnectionAnim, true);
                    reportNetworkIssue(MainActivity.STAGE_COUNTRY_SELECTION);
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
                MainActivity.this,
                R.layout.country_dropdown_item,
                countriesNames
        );
        countryAutocomplete.setAdapter(countryAdapter);
    }

    private void loadStatisticsForCountry(Date from, Date to)
    {
        Call<List<ComplexCovidReport>> call = covidAPI.getTotalByCountry(selectedCountrySlug);
        call.enqueue(new Callback<List<ComplexCovidReport>>() {
            @Override
            public void onResponse(Call<List<ComplexCovidReport>> call, Response<List<ComplexCovidReport>> response) {
                if(response.isSuccessful()) {
                    List<ComplexCovidReport> reports = response.body();
                    displayReport(reports, from, to);
                }
            }

            @Override
            public void onFailure(Call<List<ComplexCovidReport>> call, Throwable t) {
                if(t instanceof NoInternetConnectionException)
                {
                    playAnim(statDetailsAnim, true);
                    reportNetworkIssue(MainActivity.STAGE_DATE_SELECTION);
                }
            }
        });
    }

    private void displayReport(List<ComplexCovidReport> reports, Date from, Date to)
    {
        int activeCases = 0, recovered = 0, totalCases = 0, deaths =0;
        List<ComplexCovidReport> matchedReports = reports
                .stream()
                .filter(report -> report.getDate().equals(from) || report.getDate().equals(to))
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
        activeCasesTxt.setText(String.valueOf(activeCases));
        recoveredTxt.setText(String.valueOf(recovered));
        totalCasesTxt.setText(String.valueOf(totalCases));
        deathsTxt.setText(String.valueOf(deaths));
        displayChart(reports, from, CaseType.DEATHS);
    }

    private void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)view
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void reportNetworkIssue(final String onStage)
    {
        Snackbar snackbar = Snackbar.make(countryAutocomplete, R.string.net_trouble_msg, Snackbar.LENGTH_INDEFINITE);
        snackbar
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAction(R.string.retry, view -> {
                    if(onStage.equals(STAGE_COUNTRY_SELECTION)) {
                        loadCountries();
                        countryAutocomplete.setEnabled(true);
                        stopAnim(noInternetConnectionAnim);
                    }
                    else if(onStage.equals(STAGE_DATE_SELECTION)) {
                        //add retry action for this stage
                        statDetailsAnim.setVisibility(View.INVISIBLE);
                        covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        countryAutocomplete.setText("");
                        selectedCountrySlug = "";
                        from = null;
                        to = null;
                        loadCountries();
                    }
                })
                .setActionTextColor(Color.WHITE)
                .show();
    }

    private void displayChart(List<ComplexCovidReport> reports, Date date, CaseType caseType)
    {
        yearTxt.setText(String.valueOf(getYearNumber(date)));
        switch(caseType)
        {
            case ACTIVE_CASES:
                caseTypeTxt.setText(R.string.active_cases);
                break;
            case RECOVERED:
                caseTypeTxt.setText(R.string.recovered);
                break;
            case TOTAL_CASES:
                caseTypeTxt.setText(R.string.total_cases);
                break;
            case DEATHS:
                caseTypeTxt.setText(R.string.deaths);
                break;
        }
        pieChart.refreshDrawableState();
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        addChartData(config, reports, date, caseType);
        config
                .animOnTouch(true)
                .floatExpandAngle(15f)
                .strokeMode(false)
                .floatShadowRadius(18f)
                .floatUpDuration(500)
                .floatDownDuration(500)
                .floatExpandSize(16f)
                .duration(800)
                .startAngle(-90f)
                .drawText(true)
                .textSize(24)
                .autoSize(true)
                .canTouch(true)
                .focusAlpha(150)
                .focusAlphaType(AnimatedPieViewConfig.FOCUS_WITH_ALPHA_REV)
                .interpolator(new DecelerateInterpolator())
                .selectListener(
                        (pieInfo, isFloatUp) -> Toast.makeText(MainActivity.this, String.valueOf((int)pieInfo.getValue()), Toast.LENGTH_SHORT).show());
        pieChart.start(config);
    }

    private void addChartData(
            AnimatedPieViewConfig config,
            List<ComplexCovidReport> reports,
            Date date,
            CaseType caseType)
    {
        List<ComplexCovidReport> reportsWithinYear = reports
                .stream()
                .filter(report -> getYearNumber(date) == getYearNumber(report.getDate()))
                .collect(Collectors.toList());
        for(int i = 0; i < 12; ++i) {
            int monthStats = getStatsCountForMonth(reportsWithinYear, i+1, caseType);
            String monthName = (new DateFormatSymbols()).getMonths()[i];
            config.addData(new SimplePieInfo(monthStats, getRandomColor(), monthName));
        }
    }

    private int getStatsCountForMonth(List<ComplexCovidReport> reports, int month, CaseType caseType)
    {
        int stats = 0;
        List<ComplexCovidReport> reportsWithinMonth = reports
                .stream()
                .filter(report -> month == getMonthNumber(report.getDate()))
                .sorted((report1, report2) -> Integer.compare(getDayNumber(report1.getDate()), getDayNumber(report2.getDate())))
                .collect(Collectors.toList());

        if(reportsWithinMonth.size() > 1) {
            ComplexCovidReport firstReport = reportsWithinMonth.get(0);
            ComplexCovidReport lastReport = reportsWithinMonth.get(reportsWithinMonth.size() - 1);
            switch (caseType) {
                case ACTIVE_CASES:
                    stats = Math.abs(firstReport.getActive() - lastReport.getActive());
                    break;
                case RECOVERED:
                    stats = Math.abs(firstReport.getRecovered() - lastReport.getRecovered());
                    break;
                case TOTAL_CASES:
                    stats = Math.abs(firstReport.getConfirmed() - lastReport.getConfirmed());
                    break;
                case DEATHS:
                    stats = Math.abs(firstReport.getDeaths() - lastReport.getDeaths());
                    break;
            }
        }

        return stats;
    }

    private int getDayNumber(Date date)
    {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate.getDayOfMonth();
    }

    private int getMonthNumber(Date date)
    {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate.getMonthValue();
    }

    private int getYearNumber(Date date)
    {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate.getYear();
    }

    private int getRandomColor()
    {
        Random random = new Random();

        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private void playAnim(LottieAnimationView animation, boolean networkIssue)
    {
        animation.setVisibility(View.VISIBLE);
        if(networkIssue) {
            animation.setAnimation(R.raw.no_internet_connection);
        }
        else {
            animation.setAnimation(R.raw.spin_finity_loader);
        }
        animation.playAnimation();
    }

    private void stopAnim(LottieAnimationView animation)
    {
        animation.pauseAnimation();
        animation.setVisibility(View.INVISIBLE);
    }
}