package com.example.covivid.Fragments.CovidReport;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.covivid.Models.CovidReport.ComplexCovidReport;
import com.example.covivid.R;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ChartFragment extends Fragment {

    private final CaseType caseType;
    private final int id;
    private List<ComplexCovidReport> reports;
    private Date date;
    private boolean isUpdated = true;

    public ChartFragment(CaseType type, int id){
        super();
        this.caseType = type;
        this.id = id;
        Log.d("INFO", "Creating fragment with id: " + id);
    }

    public CaseType getCaseType() {
        return caseType;
    }

    private AnimatedPieView pieChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        pieChart = rootView.findViewById(R.id.animated_pie_chart);
        if(!isUpdated)
            displayChart(reports, date, caseType);
    }

    public void updateChartData(List<ComplexCovidReport> reports, Date date)
    {
        this.reports = reports;
        this.date = date;
        Log.d("INFO", "Updating chart with id: " + id);
        isUpdated = false;
        if(pieChart != null)
            displayChart(reports, date, caseType);
    }

    private void displayChart(List<ComplexCovidReport> reports, Date date, CaseType caseType)
    {
        isUpdated = true;
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
                        (pieInfo, isFloatUp) -> Toast.makeText(getActivity(), String.valueOf((int)pieInfo.getValue()), Toast.LENGTH_SHORT).show());
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

    private int getStatsCountForMonth(List<ComplexCovidReport> reportsWithinYear, int month, CaseType caseType)
    {
        int stats = 0;
        List<ComplexCovidReport> reportsWithinMonth = reportsWithinYear
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
}
