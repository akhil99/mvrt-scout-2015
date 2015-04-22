package com.mvrt.scoutview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.scoutview.adapters.TabPagerAdapter;
import com.mvrt.scoutview.view.NavDrawerFragment;
import com.mvrt.scoutview.view.SlidingTabLayout;


public class ReportsFragment extends NavDrawerFragment {

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        viewPager = (ViewPager)v.findViewById(R.id.reportstab_viewpager);
        adapter = new TabPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)v.findViewById(R.id.reportstab_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(viewPager);
    }

    @Override
    public String getTitle() {
        return "Scouting Reports";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_reports_purple;
    }

}
