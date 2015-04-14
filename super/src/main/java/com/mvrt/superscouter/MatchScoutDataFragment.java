package com.mvrt.superscouter;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.adapters.MatchRecordListAdapter;
import com.mvrt.superscouter.view.TabFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class MatchScoutDataFragment extends TabFragment implements DataManager.MatchDataAddedListener{

    RecyclerView dataRecycler;

    MatchRecordListAdapter dataAdapter;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_data, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        dataRecycler = (RecyclerView)v.findViewById(R.id.matchdata_recycler);
        dataRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataAdapter = new MatchRecordListAdapter();
        dataRecycler.setAdapter(dataAdapter);
    }

    @Override
    public String getTitle() {
        return "Scouting Data";
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onDataAdded(final JSONObject data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataAdapter.onDataAdded(data);
            }
        });
    }
}
