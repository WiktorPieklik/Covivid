package com.example.covivid.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.covivid.Model.CovidReport.Country;
import com.example.covivid.R;
import com.example.covivid.Retrofit.ICovidAPI;
import com.example.covivid.Utils.Common;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO: clean this mess
public class MainActivity extends AppCompatActivity
{
    TextInputLayout countriesInput;
    Button dateRangePicker;
    AutoCompleteTextView countryAutocomplete;
    Map<String, String> countries;
    MaterialDatePicker<Pair<Long, Long>> picker;
    ICovidAPI api;

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
        countriesInput = findViewById(R.id.country_dropdown);
        countryAutocomplete = findViewById(R.id.country_autocomplete);
        dateRangePicker = findViewById(R.id.date_range_picker);
        countryAutocomplete.setOnItemClickListener((parent, view, position, id) -> {
            String key = parent.getItemAtPosition(position).toString();
            String slug = countries.get(key);
            hideKeyboard(view);
            dateRangePicker.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, slug, Toast.LENGTH_SHORT).show();
        });
        initDateRangePicker();
        dateRangePicker.setOnClickListener(view -> picker.show(getSupportFragmentManager(), picker.toString()));
        api = Common.getApi();
    }

    private void initDateRangePicker()
    {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(new Pair<>(MaterialDatePicker.todayInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
                .setTitleText("Wybierz daty");
        picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            Date first = new Date(selection.first);
            Date second = new Date(selection.second);
            Toast.makeText(MainActivity.this, String.format("First date: %s, second date: %s", first.toString(), second.toString()), Toast.LENGTH_SHORT).show();
        });
    }

    private void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void loadCountries()
    {
        Call<List<Country>> call = api.getCountries();
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
}