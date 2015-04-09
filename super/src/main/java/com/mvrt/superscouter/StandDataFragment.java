package com.mvrt.superscouter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mvrt.superscouter.adapters.MatchRecordListAdapter;
import com.mvrt.superscouter.adapters.RecordListAdapter;
import com.mvrt.superscouter.view.TabFragment;


public class StandDataFragment extends TabFragment implements RecordListAdapter.ItemClickListener{

    private RecordListAdapter dataAdapter;
    private RecyclerView dataRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stands_records, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        dataRecycler = (RecyclerView)v.findViewById(R.id.scout_data_recycler);
        dataRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataAdapter = new RecordListAdapter(this);
        dataRecycler.setAdapter(dataAdapter);
        initFirebase();
    }

    private void initFirebase(){
        Firebase dataRef = new Firebase("https://scouting115.firebaseio.com/data");
        dataRef.addChildEventListener(dataAdapter);
    }

    @Override
    public String getTitle() {
        return "Stand Scout Data";
    }

    @Override
    public void onItemClick(int team, int match, String tourn) {
        String data = "http://scout.mvrt.com/view/" + tourn + "/" + match + "/" + team;
        Uri uri = Uri.parse(data);
        Intent i = new Intent(getActivity(), ViewDataActivity.class);
        i.setData(uri);
        startActivity(i);
    }
}
