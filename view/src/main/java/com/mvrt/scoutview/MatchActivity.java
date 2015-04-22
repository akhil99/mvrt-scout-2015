package com.mvrt.scoutview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mvrt.scoutview.adapters.TabPagerAdapter;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.tba.TBARequests;
import com.mvrt.scoutview.view.SlidingTabLayout;


public class MatchActivity extends ActionBarActivity {

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    private ViewPager viewPager;

    ViewBase viewBase;
    String matchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        viewBase = (ViewBase)getApplication();
        getIntentInfo();


        adapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager)findViewById(R.id.teamactivity_viewpager);
        viewPager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)findViewById(R.id.teamactivity_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(viewPager);
    }

    public void getIntentInfo(){
        matchKey  = getIntent().getStringExtra("matchKey");
        if(matchKey == null){
            finish();
            return;
        }
        getSupportActionBar().setTitle("MatchData " + matchKey);
    }

    public void loadTeams(){

    }

}
