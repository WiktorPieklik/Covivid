package com.example.covivid.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.example.covivid.Adapters.Reports.ChartFragmentsAdapter;
import com.example.covivid.Model.CovidReport.ComplexCovidReport;
import com.example.covivid.Model.CovidReport.Country;
import com.example.covivid.R;
import com.example.covivid.Retrofit.Exceptions.NoInternetConnectionException;
import com.example.covivid.Retrofit.ICovidAPI;
import com.example.covivid.Utils.Common;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.covivid.Fragments.CaseType.*;

public class CountryFragment extends Fragment {

    private BottomSheetBehavior<?> covidBottomSheetBehavior;

    private ImageButton dateRangeButton;
    private AutoCompleteTextView countryAutocomplete;
    private TextView activeCasesTxt, recoveredTxt, totalCasesTxt, deathsTxt, caseTypeTxt, yearTxt;
    private MaterialDatePicker<Pair<Long, Long>> dateRangePicker;

    private LottieAnimationView noInternetConnectionAnim;

    private Map<String, String> countries; // country_name : country_slug
    private ICovidAPI covidAPI;
    private String selectedCountrySlug;
    private Date from, to;
    private TabLayout chartsTabLayout;
    private ViewPager2 chartsViewPager;
    private ChartFragmentsAdapter chartAdapter;
    private final List<ChartFragment> fragments = new ArrayList<ChartFragment>(Arrays.asList(new ChartFragment(TOTAL_CASES,1), new ChartFragment(ACTIVE_CASES,2), new ChartFragment(RECOVERED,3), new ChartFragment(DEATHS,4)));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_country, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        init(view);
        loadCountries();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        covidAPI = Common.getCovidAPI(context);
    }

    private void init(View rootView)
        {
            initViews(rootView);
            countryAutocomplete.setOnItemClickListener((parent, view, position, id) -> {
                String key = parent.getItemAtPosition(position).toString();
                selectedCountrySlug = countries.get(key);
                hideKeyboard(view);
                loadStatisticsForCountry(from, to);
            });
            countryAutocomplete.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            dateRangeButton.setOnClickListener(
                    view -> dateRangePicker.show(getParentFragmentManager(), dateRangePicker.toString()));
            dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                from = new Date(selection.first);
                to = new Date(selection.second);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(to);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                to = calendar.getTime();
                loadStatisticsForCountry(from, to);
            });
            Calendar calendar = Calendar.getInstance();
            to = calendar.getTime();
            calendar.setTime(to);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            to = calendar.getTime();
            calendar.add(Calendar.MONTH, -3);
            from = calendar.getTime();
            chartAdapter = new ChartFragmentsAdapter(this, fragments);
            chartsViewPager.setAdapter(chartAdapter);
            new TabLayoutMediator(chartsTabLayout, chartsViewPager,
                    (tab, position) -> {
                        switch (fragments.get(position).getCaseType())
                        {
                            case DEATHS:
                                tab.setText(R.string.deaths);
                                break;
                            case RECOVERED:
                                tab.setText(R.string.fight);
                                break;
                            case TOTAL_CASES:
                                tab.setText(R.string.total_cases);
                                break;
                            case ACTIVE_CASES:
                                tab.setText(R.string.active_cases);
                                break;
                        }
            }
            ).attach();

        }

        private void initViews(View rootView)
        {
            ConstraintLayout bottomSheetCovid = rootView.findViewById(R.id.covid_report_layout);
            covidBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetCovid);
            covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            activeCasesTxt = rootView.findViewById(R.id.active_cases_no_txt);
            recoveredTxt = rootView.findViewById(R.id.recovered_no_txt);
            totalCasesTxt = rootView.findViewById(R.id.total_cases_no_txt);
            deathsTxt = rootView.findViewById(R.id.deaths_no_txt);
            caseTypeTxt = rootView.findViewById(R.id.case_type_txt);
            yearTxt = rootView.findViewById(R.id.year_txt);

            dateRangeButton = rootView.findViewById(R.id.date_range_picker);

            noInternetConnectionAnim = rootView.findViewById(R.id.no_internet_anim);

            countryAutocomplete = rootView.findViewById(R.id.country_autocomplete);

            chartsTabLayout = rootView.findViewById(R.id.chart_tabs);
            chartsViewPager = rootView.findViewById(R.id.charts_view_pager);

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
                        reportNetworkIssue();
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
                    getActivity(),
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
                        for (ChartFragment fragment : fragments) {
                            fragment.updateChartData(reports, from);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ComplexCovidReport>> call, Throwable t) {
                    if(t instanceof NoInternetConnectionException)
                    {
                        reportNetworkIssue();
                    }
                }
            });
        }

        private void displayReport(List<ComplexCovidReport> reports, Date from, Date to)
        {
            int activeCases = 0, recovered = 0, totalCases = 0, deaths =0;
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
            activeCasesTxt.setText(String.valueOf(activeCases));
            recoveredTxt.setText(String.valueOf(recovered));
            totalCasesTxt.setText(String.valueOf(totalCases));
            deathsTxt.setText(String.valueOf(deaths));
            covidBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        private void hideKeyboard(View view)
        {
            InputMethodManager inputMethodManager = (InputMethodManager)view
                    .getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }

        private void reportNetworkIssue()
        {
            playAnim(noInternetConnectionAnim);
            countryAutocomplete.setText("");
            countryAutocomplete.setEnabled(false);
            Snackbar snackbar = Snackbar.make(countryAutocomplete, R.string.net_trouble_msg, Snackbar.LENGTH_INDEFINITE);
            snackbar
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAction(R.string.retry, view -> {
                        loadCountries();
                        countryAutocomplete.setEnabled(true);
                        stopAnim(noInternetConnectionAnim);
                    })
                    .setActionTextColor(Color.WHITE)
                    .show();
        }

        private void playAnim(LottieAnimationView animation)
        {
            animation.setVisibility(View.VISIBLE);
            animation.playAnimation();
        }

        private void stopAnim(LottieAnimationView animation)
        {
            animation.pauseAnimation();
            animation.setVisibility(View.INVISIBLE);
        }
    }
