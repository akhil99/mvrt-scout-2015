package com.mvrt.scout;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.scout.adapters.TabPagerAdapter;
import com.mvrt.scout.view.NavDrawerFragment;
import com.mvrt.scout.view.SlidingTabLayout;

/**
 * @author Akhil Palla
 */
public class StandFragment extends NavDrawerFragment{

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    private ViewPager viewPager;

    StandScoutStartFragment startFragment;
    StandLocalFragment localFragment;
    StandSettingsFragment settingsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stands, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        slidingTabs = (SlidingTabLayout)v.findViewById(R.id.standscout_slidingtabs);
        viewPager = (ViewPager)v.findViewById(R.id.standscout_viewpager);
        adapter = new TabPagerAdapter(getChildFragmentManager());

        startFragment = new StandScoutStartFragment();
        localFragment = new StandLocalFragment();
        settingsFragment = new StandSettingsFragment();
        adapter.addFragment(startFragment);
        adapter.addFragment(localFragment);
        adapter.addFragment(settingsFragment);

        viewPager.setAdapter(adapter);
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
