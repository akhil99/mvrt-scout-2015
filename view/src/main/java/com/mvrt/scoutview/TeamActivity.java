package com.mvrt.scoutview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mvrt.scoutview.adapters.TabPagerAdapter;
import com.mvrt.scoutview.data.MatchData;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.tba.TBARequests;
import com.mvrt.scoutview.view.SlidingTabLayout;


public class TeamActivity extends ActionBarActivity {

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    private ViewPager viewPager;

    ViewBase viewBase;
    TeamStatsFragment statsFragment;

    TBARequests requests;

    Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        viewBase = (ViewBase)getApplication();
        getIntentInfo();

        statsFragment = new TeamStatsFragment();
        statsFragment.reloadViews(team);

        adapter = new TabPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(statsFragment);
        adapter.addFragment(new TeamMatchesFragment());
        adapter.addFragment(new TeamPitScoutingFragment());
        adapter.addFragment(new TeamScoutingStacksFragment());
        adapter.addFragment(new TeamScoutingIntakeFragment());
        adapter.addFragment(new TeamScoutingCanburglarFragment());
        adapter.addFragment(new TeamScoutingAutonFragment());
        adapter.addFragment(new TeamScoutingRatingsFragment());
        viewPager = (ViewPager)findViewById(R.id.teamactivity_viewpager);
        viewPager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)findViewById(R.id.teamactivity_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(viewPager);

        requests = new TBARequests(getApplicationContext());

        Log.d("MVRT", "LoadingTeamInfo from TeamActivity");
        loadTeamInfo();
        loadScoutingInfo();

    }

    public Team getTeam(){
        return team;
    }

    public void getIntentInfo(){
        int teamNo = getIntent().getIntExtra("teamNo", -1);
        if(teamNo == -1){
            finish();
            return;
        }
        team = viewBase.getTeam(teamNo);
        getSupportActionBar().setTitle("Team " + teamNo);
    }

    public void loadTeamInfo(){
        viewBase.getDataLoader().loadTeamFirebaseInfo(team.getTeamNo(), new DataLoader.TeamInfoListener() {
            @Override
            public void onTeamInfoRecieved(int teamNo) {
                reloadViews();
            }

            @Override
            public void onError() {
            }
        });
    }

    public void reloadViews(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statsFragment.reloadViews(team);
            }
        });
    }


    public void loadScoutingInfo(){
        Firebase dataRef = new Firebase("http://scouting115.firebaseio.com/data");
        dataRef.orderByChild("team").equalTo(team.getTeamNo()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot match : dataSnapshot.getChildren()) {
                    int matchNo = match.child("match").getValue(Integer.class);
                    Log.d("MVRT", "Match number: " + matchNo);
                    MatchData d = new MatchData(matchNo, team.getTeamNo());
                    String stacks = match.child("teleop").child("stacks_created").getValue(String.class);
                    String destroyed = match.child("teleop").child("stacks_destroyed").getValue(String.class);
                    d.setStackData(stacks);
                    d.setDestroyedStacks(destroyed);
                    d.setCappedStacks(match.child("teleop").child("stacks_capped").getValue(Integer.class));
                    d.setToteCount(match.child("teleop").child("totes_collected_feeder").getValue(Integer.class),
                            match.child("teleop").child("totes_collected_landfill").getValue(Integer.class));
                    d.setAutonContainersFromStep(match.child("auton").child("bins_step").getValue(Integer.class));
                    d.setContainersFromStep(match.child("teleop").child("containers_from_step").getValue(Integer.class));
                    d.setAutonTotes(match.child("auton").child("yellow_totes").getValue(Integer.class));
                    d.setCappingRating(match.child("postgame").child("capping_rating").getValue(Integer.class));
                    d.setCoopRating(match.child("postgame").child("coop_rating").getValue(Integer.class));
                    d.setStackingRating(match.child("postgame").child("stacking_rating").getValue(Integer.class));
                    d.setIntakeRating(match.child("postgame").child("intake_rating").getValue(Integer.class));
                    d.setLitterRating(match.child("postgame").child("litter_rating").getValue(Integer.class));
                    team.addMatchData(d);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

}
