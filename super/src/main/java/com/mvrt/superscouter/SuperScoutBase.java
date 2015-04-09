package com.mvrt.superscouter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.firebase.client.Firebase;

/**
 * @author Lee Mracek and Akhil Palla
 */
public class SuperScoutBase extends Application {

    private BluetoothService btService;
    private DataManager dataManager;


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        dataManager = new DataManager();
    }

    public DataManager getDataManager(){
        return dataManager;
    }

    public BluetoothService getBtService(){
        return btService;
    }

    public void initBtService(){
        if(btService == null) {
            btService = new BluetoothService(BluetoothService.MODE_SCOUT_MASTER, dataManager);
        }
    }

}
