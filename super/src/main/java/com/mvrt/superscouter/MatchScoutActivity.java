package com.mvrt.superscouter;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mvrt.superscouter.adapters.TabPagerAdapter;
import com.mvrt.superscouter.view.SlidingTabLayout;

import java.util.HashMap;

public class MatchScoutActivity extends ActionBarActivity implements View.OnClickListener {

    private BtService btService;
    private DataManager localDataMan;

    MatchScoutDataFragment dataFragment;
    MatchScoutCommentFragment commentFragment;

    int[] teams;
    int matchNo;
    String tournament;
    String alliance;
    String uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchscout);

        localDataMan = new DataManager();

        teams = new int[3];
        teams[0] = getIntent().getExtras().getInt("team1");
        teams[1] = getIntent().getExtras().getInt("team2");
        teams[2] = getIntent().getExtras().getInt("team3");
        matchNo = getIntent().getExtras().getInt("matchNo");
        uri = getIntent().getExtras().getString("uri");

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MatchQRFragment.createInstance(uri));
        commentFragment = MatchScoutCommentFragment.createInstance(teams);
        adapter.addFragment(commentFragment);
        dataFragment = new MatchScoutDataFragment();
        adapter.addFragment(dataFragment);
        pager.setAdapter(adapter);
        SlidingTabLayout slidingTabs = (SlidingTabLayout)findViewById(R.id.matchscout_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(pager);

        Button finishMatch = (Button)findViewById(R.id.match_finish);
        finishMatch.setOnClickListener(this);

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, 0);
        alliance = prefs.getString(Constants.PREFS_KEY_ALLIANCE, Constants.ALLIANCE_BLUE);
        tournament = prefs.getString(Constants.PREFS_KEY_TOURNAMENT, "SVR");

        Intent service = new Intent(getApplicationContext(), BtService.class);
        getApplicationContext().bindService(service, btServiceConn, 0);
    }

    public void onDestroy(){
        getApplicationContext().unbindService(btServiceConn);
        super.onDestroy();
    }

    public void onClick(View v){
        if(v.getId() == R.id.match_finish){
            finishMatch();
        }
    }

    private void finishMatch(){
        HashMap<Integer, String> comments = commentFragment.getComments();
        for(int team:comments.keySet()) {
            localDataMan.saveCommentToFirebase(team, matchNo, tournament, comments.get(team));
        }
        finish();
    }

    private ServiceConnection btServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BtService.BtServiceBinder binder = (BtService.BtServiceBinder)service;
            btService = binder.getService();
            btService.getDataManager().setMatchDataAddedListener(dataFragment);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            btService.getDataManager().setMatchDataAddedListener(null);
            Log.d("MVRT", "BTService disconnected in MatchScoutActivity");
            btService = null;
        }
    };

}
