package com.mvrt.scoutview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.view.TabFragment;


public class TeamStatsFragment extends TabFragment {

    TextView teamName;
    TextView fullName;
    TextView website;
    TextView location;
    TextView opr;
    TextView avgGamesPlayed;
    TextView auton;
    TextView container;
    TextView coop;
    TextView litter;
    TextView tote;

    Team team;

    boolean created = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teamstats, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        teamName = (TextView)v.findViewById(R.id.team_nick);
        fullName = (TextView)v.findViewById(R.id.team_name);
        website = (TextView)v.findViewById(R.id.team_website);
        location = (TextView)v.findViewById(R.id.team_location);
        opr = (TextView)v.findViewById(R.id.team_opr);
        avgGamesPlayed = (TextView)v.findViewById(R.id.team_avg_gamesplayed);
        auton = (TextView)v.findViewById(R.id.team_autonpts);
        container = (TextView)v.findViewById(R.id.team_containerpts);
        coop = (TextView)v.findViewById(R.id.team_cooppts);
        litter = (TextView)v.findViewById(R.id.team_litterpts);
        tote = (TextView)v.findViewById(R.id.team_totepts);
        created = true;
        reloadViews(((TeamActivity)getActivity()).getTeam());
    }

    public void reloadViews(Team team){
        Log.d("MVRT", "Reload views");
        this.team = team;
        if(!created)return; //onViewCreated has not been called yet, wait
        Log.d("MVRT", "Reloading views");
        teamName.setText(team.getName());
        fullName.setText(team.getFullName());
        website.setText(team.getWebsite());
        location.setText(team.getLocation());
        opr.setText("OPR: " + team.getOPRString());
        double avg = team.getAvg();
        int gamesPlayed = team.getGamesPlayed();
        avgGamesPlayed.setText("Avg: " + avg + " (" + gamesPlayed + " games played)");
        auton.setText("Auton pts (alliance): " + team.getAutoPts() + ", avg " + team.getAutoAvg());
        container.setText("Container pts (alliance): " + team.getContainerPts() + ", avg " + team.getContainerAvg());
        coop.setText("Coop pts (alliance): " + team.getCoopPts() + ", " + team.getCoopTimes() + " times");
        litter.setText("Litter pts (alliance): " + team.getLitterPts() + ", avg " + team.getLitterAvg());
        tote.setText("Tote pts (alliance): " + team.getTotePts() + ", avg " + team.getToteAvg());
    }

    @Override
    public String getTitle() {
        return "Team Info";
    }
}
