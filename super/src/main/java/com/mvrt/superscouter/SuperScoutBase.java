package com.mvrt.superscouter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.firebase.client.Firebase;

/**
 * @author Lee Mracek and Akhil Palla
 */
public class SuperScoutBase extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Intent intent = new Intent(getApplicationContext(), BtService.class);
        startService(intent);
    }

}
