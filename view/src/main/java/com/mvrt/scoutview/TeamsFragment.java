package com.mvrt.scoutview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mvrt.scoutview.adapters.TeamListAdapter;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.tba.TBARequests;
import com.mvrt.scoutview.view.NavDrawerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class TeamsFragment extends NavDrawerFragment implements TeamListAdapter.TeamSelectedListener, AdapterView.OnItemSelectedListener {

    TeamListAdapter listAdapter;
    TBARequests requests;

    Spinner navSpinner;

    ViewBase viewBase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        viewBase =  (ViewBase)(getActivity().getApplication());
        requests = new TBARequests(getActivity().getApplicationContext());

        ArrayList<String> items = new ArrayList<>();
        items.add("By Team Number");
        items.add("By Average Score");
        items.add("By OPR");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, items);

        navSpinner = new Spinner(((MainActivity)getActivity()).getSupportActionBar().getThemedContext());
        navSpinner.setPopupBackgroundResource(R.color.primary);
        navSpinner.setAdapter(adapter);
        navSpinner.setOnItemSelectedListener(this);

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.app_toolbar);

        RecyclerView teamsList = (RecyclerView)v.findViewById(R.id.teams_recycler);
        teamsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        listAdapter = new TeamListAdapter(this);
        teamsList.setAdapter(listAdapter);

        loadData();
    }

    @Override
    public void onStart(){
        super.onResume();
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.app_toolbar);
        toolbar.removeView(navSpinner);
        toolbar.addView(navSpinner);
    }

    @Override
    public void onStop(){
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.app_toolbar);
        toolbar.removeView(navSpinner);
        super.onStop();
    }

    @Override
    public String getTitle() {
        return "Teams";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_list_purple;
    }

    public void loadData(){
        viewBase.getDataLoader().loadTeams(new DataLoader.TeamsLoadedListener() {
            @Override
            public void onTeamsLoaded() {
                dataLoaded();
            }

            @Override
            public void onError() {}
        });
        viewBase.getDataLoader().loadOprs(new DataLoader.OPRLoadedListener() {
            @Override
            public void onOPRLoaded() {
                dataLoaded();
            }

            @Override
            public void onError() {
            }
        });
        viewBase.getDataLoader().loadRankData(new DataLoader.RankDataLoadedListener() {
            @Override
            public void onRankDataLoaded() {
                dataLoaded();
            }

            @Override
            public void onError() {
            }
        });
    }

    public void dataLoaded(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.setData(viewBase.getTeamList());
            }
        });
    }

    @Override
    public void teamSelected(Team team) {
        Intent i = new Intent(getActivity(), TeamActivity.class);
        i.putExtra("teamNo", team.getTeamNo());
        startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i){
            case 0:
                listAdapter.sort(Team.SortingType.SORT_TEAMNO);
                break;
            case 1:
                listAdapter.sort(Team.SortingType.SORT_AVG);
                break;
            case 2:
                listAdapter.sort(Team.SortingType.SORT_OPR);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        listAdapter.sort(Team.SortingType.SORT_TEAMNO);
    }
}
