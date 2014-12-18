package com.mvrt.scout;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lee Mracek on 10/20/14.
 * Serves as Application wrapper
 */
public class ScoutBase extends Application {

    private static Context context;

    public final String PREFERENCES_FILE = "com.mvrt.scout.preferences";

    private DataManager dataMan;

    public static Context getAppContext() {
        return context;
    }

    public void onCreate() {
        context = getApplicationContext();

        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        dataMan = new DataManager(prefs);

    }

    //TODO: Add onDestroy method, in which DataManager data is synced

    public DataManager getDataManager() {
        return dataMan;
    }

}
