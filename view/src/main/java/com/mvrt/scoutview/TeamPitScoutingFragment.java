package com.mvrt.scoutview;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mvrt.scoutview.adapters.PitImageAdapter;
import com.mvrt.scoutview.data.MatchData;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.view.TabFragment;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;


public class TeamPitScoutingFragment extends TabFragment{

    PitImageAdapter imageAdapter;
    Team team;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teampitscouting, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        team = ((TeamActivity)getActivity()).getTeam();

        RecyclerView recyclerView;
        recyclerView = (RecyclerView)v.findViewById(R.id.pit_img_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        imageAdapter = new PitImageAdapter();
        recyclerView.setAdapter(imageAdapter);
        loadImages();
    }

    public void loadImages(){
        Firebase pitRef = new Firebase("https://scouting115.firebaseio.com/pitscout/images");
        pitRef.orderByChild("team").equalTo(team.getTeamNo()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot record:dataSnapshot.getChildren()){
                    String url = record.child("cloudinary_url").getValue(String.class);
                    if(url == null)url = record.child("url").getValue(String.class);
                    String msg = record.child("msg").getValue(String.class);
                    final PitImageAdapter.Image img = new PitImageAdapter.Image(url, msg);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageAdapter.addImage(img);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }


    @Override
    public String getTitle() {
        return "Pit Scouting";
    }
}
