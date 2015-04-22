package com.mvrt.scoutview;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.tba.TBARequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Akhil Palla
 */
public class DataLoader{

    ViewBase viewBase;
    TBARequests requests;

    public DataLoader(ViewBase v){
        viewBase = v;
        requests = new TBARequests(v.getApplicationContext());
    }


    public interface TeamsLoadedListener{
        public void onTeamsLoaded();
        public void onError();
    }

    public void loadTeams(final TeamsLoadedListener loadedListener){
        String event = "2015casj";
        Log.d("MVRT", "Loading teams");
        Firebase eventTeamsRef = new Firebase("http://scouting115.firebaseio.com/event_teams");
        eventTeamsRef.child(event).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MVRT", "Data Change");
                for (DataSnapshot team : dataSnapshot.getChildren()) {
                    int teamNo = Integer.parseInt(team.getKey());
                    String teamNick = team.child("name").getValue(String.class);
                    viewBase.getTeam(teamNo).setName(teamNick);
                }
                Log.d("MVRT", "Teams loaded");
                loadedListener.onTeamsLoaded();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("MVRT", "Load teams firebase error");
                loadedListener.onError();
            }
        });
    }


    public interface TeamInfoListener{
        public void onTeamInfoRecieved(int teamNo);
        public void onError();
    }

    public void loadTeamFirebaseInfo(final int teamNo, final TeamInfoListener teamInfoListener){
        Firebase teamsRef = new Firebase("https://scouting115.firebaseio.com/teams/frc" + teamNo);
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Team t = viewBase.getTeam(teamNo);
                String loc = dataSnapshot.child("location").getValue(String.class);
                String fullName = dataSnapshot.child("name").getValue(String.class);
                String website = dataSnapshot.child("website").getValue(String.class);
                String name = dataSnapshot.child("nick").getValue(String.class);
                t.setLocation(loc);
                t.setFullName(fullName);
                t.setWebsite(website);
                t.setName(name);
                teamInfoListener.onTeamInfoRecieved(teamNo);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                teamInfoListener.onError();
            }
        });
    }

    public interface OPRLoadedListener{
        public void onOPRLoaded();
        public void onError();
    }

    public void loadOprs(final OPRLoadedListener loadedListener){
        String event = "2015casj";
        Log.d("MVRT", "Loading OPRs");
        requests.loadStats(event, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("MVRT", "OPR Response");
                try {
                    JSONObject oprs = response.getJSONObject("oprs");
                    Iterator<String> teamIterator = oprs.keys();
                    while (teamIterator.hasNext()) {
                        String team = teamIterator.next();
                        int teamNo = Integer.parseInt(team);
                        viewBase.getTeam(teamNo).setOPR(oprs.getDouble(team));
                    }
                } catch (JSONException e) {
                    loadedListener.onError();
                }
                loadedListener.onOPRLoaded();
                Log.d("MVRT", "OPRs loaded");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MVRT", "OPR load response error");
                loadedListener.onError();
            }
        });
    }

    public interface RankDataLoadedListener{
        public void onRankDataLoaded();
        public void onError();
    }

    public void loadRankData(final RankDataLoadedListener loadedListener){
        String event = "2015casj";
        Log.d("MVRT", "Loading Rank Data");
        requests.loadRanks(event, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("MVRT", "Rank Data response");
                for (int rank = 1; rank < response.length(); rank++) {
                    try {
                        JSONArray data = response.getJSONArray(rank);
                        int teamNo = data.getInt(1);
                        viewBase.getTeam(teamNo).setAdvancedRankInfo(rank, data.getDouble(2), data.getInt(3), data.getInt(4),
                                data.getInt(5), data.getInt(6), data.getInt(7), data.getInt(8));
                        loadedListener.onRankDataLoaded();
                    } catch (JSONException e) {
                        loadedListener.onError();
                    }
                }
                Log.d("MVRT", "Rank Data loaded");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MVRT", "Rank loading response error");
                loadedListener.onError();
            }
        });
    }

}
