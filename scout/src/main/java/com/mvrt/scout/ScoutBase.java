package com.mvrt.scout;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * @author Akhil Palla
 */
public class ScoutBase extends Application {

    BluetoothService btService;

    public void onCreate() {
        Firebase.setAndroidContext(this);
    }

    public BluetoothService getBtService(){
        return btService;
    }

    public void initBtService(){
        if(btService == null) {
            btService = new BluetoothService(BluetoothService.MODE_SCOUT_SLAVE);
        }
    }

}
