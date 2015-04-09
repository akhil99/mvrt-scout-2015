package com.mvrt.superscouter;

import android.util.Log;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Akhil Palla
 */
public class DataManager {

    private ArrayList<JSONObject> currentMatchData;
    private Firebase dataRef;

    public DataManager(){
        currentMatchData = new ArrayList<>();
        dataRef = new Firebase("https://scouting115.firebaseio.com/data");
    }

    public interface MatchDataAddedListener {
        public void onDataAdded(JSONObject data);
    }

    MatchDataAddedListener matchDataAddedListener;

    public void setMatchDataAddedListener(MatchDataAddedListener listen){
        matchDataAddedListener = listen;
    }

    public void addMatchData(JSONObject record){
        if(matchDataAddedListener != null)matchDataAddedListener.onDataAdded(record);
        currentMatchData.add(record);
        saveRecordToFirebase(record);
    }

    public void saveRecordToFirebase(JSONObject record){
        try {
            int team = record.getInt("team");
            int match = record.getInt("match");
            String tourn = record.getString("tournament");
            dataRef.child(getPath(team, match, tourn)).setValue(toMap(record));
        } catch (JSONException e) {
            Log.e("MVRT", "JSON exception");
        }
    }

    public void saveCommentToFirebase(int team, int matchNo, String tournament, String comment){
        dataRef.child(getPath(team, matchNo, tournament)).child("team").setValue(team);
        dataRef.child(getPath(team, matchNo, tournament)).child("match").setValue(matchNo);
        dataRef.child(getPath(team, matchNo, tournament)).child("tournament").setValue(tournament);
        dataRef.child(getPath(team, matchNo, tournament)).child("super-comments").setValue(comment);
    }

    private static Map toMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = jsonObject.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = jsonObject.get(key);

            if(value instanceof JSONObject) {
                value = toMap((JSONObject)value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static String getPath(int team, int matchNo, String tourn){
        return team + "-" + tourn + ":" + matchNo;
    }

}
