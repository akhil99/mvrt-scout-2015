package com.mvrt.scout.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mvrt.scout.view.TabFragment;

import java.util.ArrayList;

public class TabPagerAdapter extends FragmentPagerAdapter {

    ArrayList<TabFragment> fragments;

    public TabPagerAdapter(FragmentManager man, ArrayList<TabFragment> tabs){
        super(man);
        fragments = tabs;
    }

    public TabPagerAdapter(FragmentManager man){
        this(man, new ArrayList<TabFragment>());
    }

    public void addFragment(TabFragment tab){
        fragments.add(tab);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragments.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

}
