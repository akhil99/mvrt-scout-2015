package com.mvrt.scoutview;

import android.app.Application;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.client.Firebase;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.tba.TBARequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Akhil Palla
 */
public class ViewBase extends Application {

    HashMap<Integer, Team> teamList;
    DataLoader loader;

    public void onCreate() {
        teamList = new HashMap<>();
        Firebase.setAndroidContext(this);
        loader = new DataLoader(this);
    }

    public HashMap<Integer, Team> getTeamList(){
        return teamList;
    }

    public Team getTeam(int teamNo){
        if(teamList.get(teamNo) == null) teamList.put(teamNo, new Team(teamNo));
        return getTeamList().get(teamNo);
    }

    public DataLoader getDataLoader(){ return loader; }

}
