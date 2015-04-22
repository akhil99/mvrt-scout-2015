package com.mvrt.superscouter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.adapters.TabPagerAdapter;
import com.mvrt.superscouter.view.NavDrawerFragment;
import com.mvrt.superscouter.view.SlidingTabLayout;

public class StandFragment extends NavDrawerFragment {

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    private ViewPager viewPager;

    StandScoutFragment scoutFragment;
    StandDataFragment recordsFragment;
    StandSettingsFragment settingsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stands, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        viewPager = (ViewPager)v.findViewById(R.id.standscout_viewpager);
        adapter = new TabPagerAdapter(getChildFragmentManager());
        recordsFragment = new StandDataFragment();
        scoutFragment = new StandScoutFragment();
        settingsFragment = new StandSettingsFragment();
        adapter.addFragment(scoutFragment);
        adapter.addFragment(recordsFragment);
        adapter.addFragment(settingsFragment);
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
        return R.drawable.ic_clipboard_purple;
    }

    public void refreshViews(){
        scoutFragment.refreshViews();
        settingsFragment.refreshViews();
    }

}
