package com.mvrt.superscouter;

import android.content.SharedPreferences;

/**
 * Created by Lee Mracek on December 10, 2014.
 * Handles the syncing and storage of data
 * @author Lee Mracek
 */
public class DataManager {
    SharedPreferences preferences;
    public final String ALLIANCE_PREFERENCES = "allianceid";
    private int currentMatch = 1;
    private int allianceId = 1;

    public DataManager (int currentMatch, SharedPreferences preferences) {
        setCurrentMatch(currentMatch);
        this.preferences = preferences;

        allianceId = preferences.getInt(ALLIANCE_PREFERENCES, 1);
        setAllianceId(allianceId > 1 || allianceId < 6 ? allianceId : 1);

    }
    public void syncData() {
        //todo perform full data sync
    }

    private void setAllianceId(int allianceId) {
        this.allianceId = allianceId;
    }

    public void setCurrentMatch(int currentMatch) {
        this.currentMatch = currentMatch;
    }
}
