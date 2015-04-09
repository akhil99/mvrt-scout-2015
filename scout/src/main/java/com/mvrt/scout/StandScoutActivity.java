package com.mvrt.scout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.mvrt.scout.adapters.TabPagerAdapter;
import com.mvrt.scout.view.SlidingTabLayout;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StandScoutActivity extends ActionBarActivity{

    private ViewPager scoutPager;
    private TabPagerAdapter tabPagerAdapter;

    SlidingTabLayout slidingTabs;
    Toolbar toolbar;

    int matchNo;
    String alliance;
    String tournament;

    int team;
    int scoutID;

    StandScoutTeleopFragment scoutTeleopFragment;
    StandScoutAutonFragment scoutAutonFragment;
    StandScoutPostgameFragment postgamFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout);

        toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        getScoutParams();
        scoutPager = (ViewPager)findViewById(R.id.standscout_viewpager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        scoutTeleopFragment = new StandScoutTeleopFragment();
        scoutAutonFragment = new StandScoutAutonFragment();
        postgamFragment = new StandScoutPostgameFragment();
        tabPagerAdapter.addFragment(scoutAutonFragment);
        tabPagerAdapter.addFragment(scoutTeleopFragment);
        tabPagerAdapter.addFragment(postgamFragment);

        scoutPager.setAdapter(tabPagerAdapter);
        slidingTabs = (SlidingTabLayout)findViewById(R.id.standscout_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(scoutPager);
    }

    public void getScoutParams(){
        Uri uri = getIntent().getData();
        ArrayList<Integer> teams = new ArrayList<>();
        List<String> path = uri.getPathSegments();
        try {
            tournament = path.get(1); //get the second item (1st is "scout")
            matchNo = Integer.parseInt(path.get(2)); //match is 3rd item
            alliance = path.get(3).toLowerCase();

            List<String> qTeams = uri.getQueryParameters("t");
            for(String team:qTeams) teams.add(Integer.parseInt(team));

            boolean badAlliance = !(alliance.equals("r") || alliance.equals("b"));
            if(tournament == null ||    tournament.length() == 0 || teams.size() == 0 || badAlliance) {
                Log.e("MVRT", "Error: alliance: " + alliance);
                throw new Exception("bad");
            }
        }catch(Exception e){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        Log.d("MVRT", "teams: " + teams);

        scoutID = getSharedPreferences(Constants.PREFS_NAME, 0).getInt(Constants.PREFS_KEY_SCOUTID, 1);
        Log.d("MVRT", "Scout ID: " + scoutID);
        if(scoutID <= teams.size())team = teams.get(scoutID - 1);
        else team = teams.get(0);

        String all = (alliance.equals(Constants.ALLIANCE_RED))?"Red":"Blue";
        String title = "T " + team + " (" + all + ") - M " + matchNo + " @ " + tournament;

        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed(){
        return; //prevents going back on accident
    }

    public void endMatch(){
        JSONObject data = new JSONObject();
        JSONObject auton  = scoutAutonFragment.getData();
        if(auton == null){
            scoutPager.setCurrentItem(0);
            scoutAutonFragment.showError();
            return;
        }
        try{
            data.put("auton", auton);
            data.put("teleop", scoutTeleopFragment.getData());
            data.put("postgame", postgamFragment.getData());
            data.put("team", team);
            data.put("match", matchNo);
            data.put("tournament", tournament);
            data.put("alliance", alliance);
        }catch(Exception e){
            Log.e("MVRT", "error w/ json");
        }
        Log.d("MVRT", "sending: " + data.toString());
        boolean success = ((ScoutBase)getApplication()).getBtService() != null &&
                ((ScoutBase)getApplication()).getBtService().send(data.toString().getBytes());
        if(!success){
            Toast.makeText(this, "BT Unsuccessful, saving locally", Toast.LENGTH_LONG).show();
            Log.d("MVRT", "BT unsuccessful, saving locally");
            writeToFile(data);
        }else{
            Toast.makeText(this, "Successfully sent over BT", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void writeToFile(JSONObject data){
        String filename = "scout_" + team + "_" + matchNo + "@" + tournament + ".json";
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
