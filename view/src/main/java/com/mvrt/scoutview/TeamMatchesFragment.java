package com.mvrt.scoutview;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mvrt.scoutview.adapters.MatchListAdapter;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.view.TabFragment;

import java.util.ArrayList;


public class TeamMatchesFragment extends TabFragment implements MatchListAdapter.MatchSelectedListener {

    RecyclerView recyclerView;
    MatchListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_matches, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        listAdapter = new MatchListAdapter(this);
        recyclerView = (RecyclerView)v.findViewById(R.id.team_matches_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(listAdapter);
        loadMatches();
    }

    @Override
    public String getTitle() {
        return "Team Matches";
    }

    @Override
    public void matchSelected(String match) {

    }

    public void loadMatches(){
        Team t = ((TeamActivity)getActivity()).getTeam();

        final Firebase schedRef = new Firebase("https://scouting115.firebaseio.com/sched");
        schedRef.orderByChild("team").equalTo("frc" + t.getTeamNo()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot match:dataSnapshot.getChildren()){
                    String matchKey = match.child("match").getValue(String.class);
                    final String matchId = matchKey.substring(matchKey.indexOf("_") + 1);
                    final long time = match.child("time").getValue(Long.class);
                    final ArrayList<Integer> red = new ArrayList<>();
                    final ArrayList<Integer> blue = new ArrayList<>();
                    schedRef.orderByChild("match").equalTo(matchKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot match:dataSnapshot.getChildren()){
                                String team = match.child("team").getValue(String.class);
                                int teamNo = Integer.parseInt(team.substring(3)); //get rid of "frc" in "frc115"
                                if(match.child("alliance").getValue(String.class).equals("b")) blue.add(teamNo);
                                else red.add(teamNo);
                            }
                            addMatch(matchId, time, red, blue);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {}
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public void addMatch(final String key, final long time, final ArrayList<Integer> red, final ArrayList<Integer> blue){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.addMatch(key, time, red, blue);
            }
        });
    }


}
