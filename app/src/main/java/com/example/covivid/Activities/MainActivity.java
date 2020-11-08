package com.example.covivid.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.covivid.Adapters.Reports.MainFragmentsAdapter;
import com.example.covivid.R;
import com.example.covivid.Utils.Common;



public class MainActivity extends AppCompatActivity
{
    MainFragmentsAdapter fragmentsAdapter;
    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Common.requestFullScreenActivity(this);
        setContentView(R.layout.activity_main);
        fragmentsAdapter = new MainFragmentsAdapter(this);
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(fragmentsAdapter);

    }

}