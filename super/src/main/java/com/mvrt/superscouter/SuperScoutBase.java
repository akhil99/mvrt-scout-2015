package com.mvrt.superscouter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Lee Mracek and Akhil Palla
 */
public class SuperScoutBase extends Application {

    private static Context context;

    BluetoothService btService;

    public final String PREFERENCE_FILE = "com.mvrt.superscouter.preferences";

    public static Context getAppContext(){return context;}

    public void onCreate() {
        context = getApplicationContext();
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_FILE, MODE_PRIVATE);
    }

    public BluetoothService getBtService(){
        return btService;
    }

    public void initBtService(){
        if(btService == null) {
            btService = new BluetoothService(BluetoothService.MODE_SCOUT_MASTER);
        }
    }

}
