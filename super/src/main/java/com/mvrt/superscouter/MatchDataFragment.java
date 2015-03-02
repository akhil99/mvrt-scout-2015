package com.mvrt.superscouter;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.view.TabFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchDataFragment extends TabFragment implements BluetoothService.OnReceivedListener{

    HashMap<Integer, JSONObject> scoutingData;
    BluetoothService btService;

    int[] teams;

    public static MatchDataFragment createInstance(int[] teams){
        MatchDataFragment fragment = new MatchDataFragment();
        fragment.teams = teams;
        return fragment;
    }

    public MatchDataFragment(){
        scoutingData = new HashMap<>();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        btService = ((SuperScoutBase)activity.getApplication()).getBtService();
        btService.addOnReceivedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_data, container, false);
    }

    @Override
    public String getTitle() {
        return "Scouting Data";
    }

    public HashMap<Integer, JSONObject> getScoutingData(){
        HashMap<Integer, JSONObject> data = new HashMap<>();
        for(int team:teams){
            try {
                JSONObject obj = new JSONObject();
                obj.put("team", team);
                obj.put("alliance", "blue");
                obj.put("stacks", 9000);
                data.put(team ,obj);
            }catch(JSONException e){}
        }
        return data;
        //return scoutingData;
    }

    @Override
    public void onReceived(String data) {
        Log.d("MVRT", "received in dataFragment: " + data);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        btService.removeOnReceivedListener();
    }

}
