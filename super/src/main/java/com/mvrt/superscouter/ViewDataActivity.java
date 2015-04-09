package com.mvrt.superscouter;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.mvrt.superscouter.adapters.RecordDetailListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewDataActivity extends ActionBarActivity {

    int team = 123;
    String tournament = "SAMP";
    int match = 1;

    RecordDetailListAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        adapter = new RecordDetailListAdapter();
        recyclerView = (RecyclerView)findViewById(R.id.activity_dataview_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getParams();
        loadData();
    }

    public void getParams(){
        Uri uri = getIntent().getData();
        List<String> path = uri.getPathSegments();
        try {
            tournament = path.get(1).toUpperCase(); //get 2nd item (1st is "view")
            match = Integer.parseInt(path.get(2)); //3rd item
            team = Integer.parseInt(path.get(3)); //4th item
        }catch (Exception e){
            invalidParams();
        }
        getSupportActionBar().setTitle("Team " + team + ", match " + match + "@" + tournament);
    }

    public void loadData(){
        Firebase dataRef = new Firebase("https://scouting115.firebaseio.com/data/");
        final String key = team + "-" + tournament + ":" + match;
        Query q = dataRef.orderByKey().equalTo(key);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    invalidParams();
                    return;
                }
                Log.d("MVRT", "val: " + dataSnapshot.child(key).toString());
                loadData(dataSnapshot.child(key));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("MVRT", "error: " + firebaseError.getMessage());
            }
        });

    }

    public void loadData(DataSnapshot snapshot){
        Iterable<DataSnapshot> auton = snapshot.child("auton").getChildren();
        Iterable<DataSnapshot> teleop = snapshot.child("teleop").getChildren();
        Iterable<DataSnapshot> postgame = snapshot.child("postgame").getChildren();
        for(DataSnapshot shot:auton){
            Log.d("MVRT", "auton snapshot key: " + shot.getKey() + ", value: " + shot.getValue().toString());
            adapter.add(new RecordDetailListAdapter.Entry(shot.getKey(), getLabel(shot.getKey()), shot.getValue().toString()));
        }
        for(DataSnapshot shot:teleop){
            Log.d("MVRT", "auton snapshot key: " + shot.getKey() + ", value: " + shot.getValue().toString());
            adapter.add(new RecordDetailListAdapter.Entry(shot.getKey(), getLabel(shot.getKey()), shot.getValue().toString()));
        }
        for(DataSnapshot shot:postgame){
            Log.d("MVRT", "auton snapshot key: " + shot.getKey() + ", value: " + shot.getValue().toString());
            adapter.add(new RecordDetailListAdapter.Entry(shot.getKey(), getLabel(shot.getKey()), shot.getValue().toString()));
        }
    }

    private String getLabel(String key){
        if(keyLabels.get(key) != null)return keyLabels.get(key);
        return key;
    }

    HashMap<String, String> keyLabels = new HashMap<String, String>(){{
        put("bins_step", "# Bins retrieved from step");
        put("grey_totes", "Interact with grey totes?");
        put("interferes", "Interferes with other robots?");
        put("mobility", "Mobility?");
        put("starting_pos", "Starting Position");
        put("yellow_totes", "# Yellow Totes interacted with");
        put("disabled", "Disabled during the game?");
        put("noodles_bin", "# Noodles put in bins");
        put("noodles_landfill", "# Noodles moved to landfill");
        put("stacks_capped", "# of Capped Stacks");
        put("containers_from_step", "# Containers from step");
        put("containers_flipped", "Flipped Containers");
        put("stacks_created", "Stacks");
        put("stacks_destroyed", "Destroyed Stacks");
        put("totes_collected_feeder", "Totes from feeder");
        put("totes_collected_landfill", "Totes from landfill");
        put("totes_collected_total", "Total # of totes collected");
        put("totes_dropped", "Dropped Totes");
        put("capping_rating", "Stack Cappping Ability (0-5)");
        put("coop_rating", "Coop Rating (0-5)");
        put("intake_rating", "Intake Rating (0-5)");
        put("litter rating", "Dealt with Litter (Rating, 0-5)");
        put("scout_comments", "scout comments");
        put("interferes", "Interferes?");
        put("stacking_rating", "Stacking ability (0-5)");
        put("tippy", "Tippy?");
        put("noshow", "No Show?");
    }};

    private void invalidParams(){
        Toast.makeText(this, "Invalid params", Toast.LENGTH_LONG).show();
        finish();
    }

}
