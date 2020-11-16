package com.example.covivid.Adapters.Reports;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.covivid.Fragments.CaseType;
import com.example.covivid.Fragments.ChartFragment;

import java.util.List;

public class ChartFragmentsAdapter  extends FragmentStateAdapter {


    List<ChartFragment> charts;
    public ChartFragmentsAdapter(@NonNull Fragment fragment, List<ChartFragment> charts) {
        super(fragment);
        this.charts = charts;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return charts.get(position);
    }

    @Override
    public int getItemCount() {
        return charts.size();
    }
}
