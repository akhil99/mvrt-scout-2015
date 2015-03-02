package com.mvrt.superscouter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.view.NavDrawerFragment;
import com.mvrt.superscouter.view.SlidingTabLayout;
import com.mvrt.superscouter.adapters.TabPagerAdapter;

public class PitFragment extends NavDrawerFragment {

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pit, container, false);
    }

    @Override
     public void onViewCreated(View v, Bundle savedInstanceState){
        viewPager = (ViewPager)v.findViewById(R.id.pitscout_viewpager);
        adapter = new TabPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PitScoutFragment());
        adapter.addFragment(new PitRecordsFragment());
        viewPager.setAdapter(adapter);
        viewPager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)v.findViewById(R.id.pitscout_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(viewPager);
    }

    @Override
    public String getTitle() {
        return "Pit Scouting";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_pit;
    }
}
