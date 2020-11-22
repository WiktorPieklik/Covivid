package com.example.covivid.Adapters.CovidReport;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.covivid.Fragments.CovidReport.CountryFragment;
import com.example.covivid.Fragments.CovidNews.NewsFragment;

public class MainFragmentsAdapter extends FragmentStateAdapter {

    public MainFragmentsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
        {
            return new NewsFragment();
        } else
        {
            return new CountryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
