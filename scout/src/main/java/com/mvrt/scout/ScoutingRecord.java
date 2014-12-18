package com.mvrt.scout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by akhil_000 on 11/22/2014.
 */
public class ScoutingRecord {

    public boolean synced = false;
    private String scouterInitials = "";
    private JSONObject data;
    private int teamNumber = 0;
    private Match match;

    public ScoutingRecord(){
        data = new JSONObject();
        match = new Match();
    }

    public boolean hasSynced() { return  synced; }
    public void setSynced(boolean b) { synced = b; }

    public Match getMatch() { return match; }
    public void setMatch(Match m) { match = m; }

    public int getTeamNumber() {
        return teamNumber;
    }
    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public String getScouterInitials(){
        return scouterInitials;
    }
    public void setScouterInitials(String s){
        scouterInitials = s;
    }

    public JSONObject getData(){
        return data;
    }

    //Handle conversions to JSON, Strings

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("scouter_initials", scouterInitials);
            obj.put("team_number", teamNumber);
            obj.put("match_number", match.getMatchNumber());
            obj.put("data", data);
        }catch(JSONException e){}
        return obj;
    }

    public String toString(){
        return toJSONObject().toString();
    }

}
