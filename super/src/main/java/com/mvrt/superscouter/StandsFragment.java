package com.mvrt.superscouter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.view.NavDrawerFragment;
import com.mvrt.superscouter.view.SlidingTabLayout;
import com.mvrt.superscouter.view.TabPagerAdapter;

public class StandsFragment extends NavDrawerFragment {

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stands, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        viewPager = (ViewPager)v.findViewById(R.id.standscout_viewpager);
        adapter = new TabPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new StandScoutFragment());
        adapter.addFragment(new StandRecordsFragment());
        viewPager.setAdapter(adapter);
        viewPager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)v.findViewById(R.id.standscout_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(viewPager);

    }

    @Override
    public String getTitle() {
        return "Stand Scouting";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_clipboard;
    }
}
