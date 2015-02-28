package com.mvrt.superscouter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mvrt.superscouter.view.SlidingTabLayout;
import com.mvrt.superscouter.view.TabPagerAdapter;

public class MatchScoutActivity extends ActionBarActivity implements View.OnClickListener, BluetoothService.OnReceivedListener{

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    ViewPager pager;

    Button finishMatch;

    BluetoothService btService;

    int team1;
    int team2;
    int team3;
    int matchNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchscout);

        team1 = getIntent().getExtras().getInt("team1");
        team2 = getIntent().getExtras().getInt("team2");
        team3 = getIntent().getExtras().getInt("team3");
        matchNo = getIntent().getExtras().getInt("matchNo");

        Log.d("MVRT", "extraas: " + getIntent().getExtras().toString());

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MatchCommentFragment.createInstance(team1, team2, team3));
        adapter.addFragment(new MatchDataFragment());
        pager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)findViewById(R.id.matchscout_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(pager);

        finishMatch = (Button)findViewById(R.id.match_finish);
        finishMatch.setOnClickListener(this);

        btService = ((SuperScoutBase)getApplication()).getBtService();
        btService.addOnReceivedListener(this);
    }

    public void onDestroy(){
        super.onDestroy();
        btService.removeOnReceivedListener();
    }

    public void onReceived(String data){

    }

    public void onClick(View v){
        if(v.getId() == R.id.match_finish){
            finish();
        }
    }

}
