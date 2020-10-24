package com.example.covivid.Activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;

import com.example.covivid.Model.CovidReport.ComplexCovidReport;
import com.example.covivid.Model.CovidReport.Country;
import com.example.covivid.R;
import com.example.covivid.Retrofit.Exceptions.NoInternetConnectionException;
import com.example.covivid.Retrofit.ICovidAPI;
import com.example.covivid.Utils.Common;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
    private static final String STAGE_COUNTRY_SELECTION = "country_selection";
    private static final String STAGE_DATE_SELECTION = "date_selection";

    private BottomSheetBehavior<?> covidBottomSheetBehavior;

    private Button dateRangeButton;
    private AutoCompleteTextView countryAutocomplete;
    private TextView activeCasesTxt, recoveredTxt, totalCasesTxt, deathsTxt;
    private MaterialDatePicker<Pair<Long, Long>> dateRangePicker;

    private Map<String, String> countries; // country_name : country_slug
    private ICovidAPI covidAPI;
    private String selectedCountrySlug;

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
            Date from = new Date(selection.first);
            Date to = new Date(selection.second);
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

        dateRangeButton = findViewById(R.id.date_range_picker);

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
        if(selectedCountrySlug != null) {
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
                        reportNetworkIssue(MainActivity.STAGE_DATE_SELECTION);
                    }
                }
            });
        }
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
        if(onStage.equals(this.STAGE_DATE_SELECTION)) {
            covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        Snackbar snackbar = Snackbar.make(countryAutocomplete, R.string.net_trouble_msg, Snackbar.LENGTH_INDEFINITE);
        snackbar
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAction(R.string.retry, view -> {
                    if(onStage.equals(this.STAGE_COUNTRY_SELECTION)) {
                        loadCountries();
                        countryAutocomplete.setEnabled(true);
                    }
                    else if(onStage.equals(this.STAGE_DATE_SELECTION)) {
                        //add retry action for this stage
                    }
                })
                .setActionTextColor(Color.WHITE)
                .show();
    }
}