package com.mvrt.scout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Lee Mracek on 10/21/14.
 */
public class Match {
    private int matchNumber;

    //TODO: Switch Team objects to integers
    private List<Team> redAlliance = Arrays.asList(new Team(), new Team(), new Team());
    private List<Team> blueAlliance = Arrays.asList(new Team(), new Team(), new Team());

    public Match() {}

    public Match(int matchNo) {
        matchNumber = matchNo;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public int getTeamNumber(int scoutingId){
        if(scoutingId < 1 || scoutingId > 6)return 0;
        if(scoutingId < 4)return redAlliance.get(scoutingId - 1).getTeamNumber();
        return blueAlliance.get(scoutingId - 4).getTeamNumber();
    }

    public void setRedAllianceJSON(JSONArray redAlliance) {
        for (int i = 0; i < redAlliance.length(); i++) {
            try {
                this.redAlliance.set(i, new Team(redAlliance.getInt(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBlueAllianceJSON(JSONArray blueAlliance) {
        for (int i = 0; i < blueAlliance.length(); i++) {
            try {
                this.blueAlliance.set(i, new Team(blueAlliance.getInt(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRedAlliance(int team1, int team2, int team3) {
        redAlliance.set(0, new Team(team1));
        redAlliance.set(1, new Team(team2));
        redAlliance.set(2, new Team(team3));
    }

    public void setBlueAlliance(int team1, int team2, int team3) {
        blueAlliance.set(0, new Team(team1));
        blueAlliance.set(1, new Team(team2));
        blueAlliance.set(2, new Team(team3));
    }

    public List<Team> getRedAlliance() {
        return redAlliance;
    }

    public void setRedAlliance(List<Team> redAlliance) {
        this.redAlliance = redAlliance;
    }

    public List<Team> getBlueAlliance() {
        return blueAlliance;
    }

    public void setBlueAlliance(List<Team> blueAlliance) {
        this.blueAlliance = blueAlliance;
    }
}
