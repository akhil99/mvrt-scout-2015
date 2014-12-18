package com.mvrt.superscouter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lee Mracek on December 10, 2014.
 */
public class SuperScoutBase extends Application {

    private static Context context;

    public final String PREFERENCE_FILE = "com.mvrt.superscouter.preferences";

    public static Context getAppContext(){return context;}

    public void onCreate() {
        context = getApplicationContext();

        SharedPreferences prefs = getSharedPreferences(PREFERENCE_FILE, MODE_PRIVATE);
    }

}
