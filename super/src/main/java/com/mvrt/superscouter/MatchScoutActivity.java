package com.mvrt.superscouter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mvrt.superscouter.view.SlidingTabLayout;
import com.mvrt.superscouter.adapters.TabPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchScoutActivity extends ActionBarActivity implements View.OnClickListener{

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    ViewPager pager;

    Button finishMatch;

    MatchDataFragment dataFragment;
    MatchCommentFragment commentFragment;

    int[] teams;
    int matchNo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchscout);

        teams = new int[3];
        teams[0] = getIntent().getExtras().getInt("team1");
        teams[1] = getIntent().getExtras().getInt("team2");
        teams[2] = getIntent().getExtras().getInt("team3");
        matchNo = getIntent().getExtras().getInt("matchNo");

        Log.d("MVRT", "extraas: " + getIntent().getExtras().toString());

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new TabPagerAdapter(getSupportFragmentManager());
        commentFragment = MatchCommentFragment.createInstance(teams);
        adapter.addFragment(commentFragment);
        dataFragment = MatchDataFragment.createInstance(teams);
        adapter.addFragment(dataFragment);
        pager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)findViewById(R.id.matchscout_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(pager);

        finishMatch = (Button)findViewById(R.id.match_finish);
        finishMatch.setOnClickListener(this);
    }

    public void onDestroy(){
        super.onDestroy();
    }

    public void onClick(View v){
        if(v.getId() == R.id.match_finish){
            finishMatch();
        }
    }

    public void finishMatch(){
        HashMap<Integer, JSONObject> data = dataFragment.getScoutingData();
        HashMap<Integer, String> comments = commentFragment.getComments();

        for(int team:teams){
            if(team > 0) {
                String comment = comments.get(team);
                Log.d("MVRT", "team: " + team + ", comment: " + comment);
                JSONObject record = data.get(team);
                try {
                    record.put("super_comment", comment);
                } catch (JSONException e) { Log.e("MVRT", "error adding comments to JSON"); }
            }
        }

        Log.d("MVRT", "final scouting data: " + data);

        finish();
    }

}
