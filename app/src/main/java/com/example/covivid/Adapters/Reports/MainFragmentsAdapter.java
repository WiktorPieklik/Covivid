package com.example.covivid.Adapters.Reports;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.covivid.Activities.CountryFragment;
import com.example.covivid.Activities.MainActivity;
import com.example.covivid.Activities.NewsFragment;

public class MainFragmentsAdapter extends FragmentStateAdapter {
    public MainFragmentsAdapter(@NonNull MainActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
        {
            Fragment fragment = new NewsFragment();
            return fragment;
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
