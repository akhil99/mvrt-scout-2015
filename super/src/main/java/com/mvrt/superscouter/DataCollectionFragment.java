package com.mvrt.superscouter;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Lee Mracek on December 10, 2014.
 */
public abstract class DataCollectionFragment extends Fragment {
    DataManager dataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onPause() {
        super.onPause();
        getDataFromUI();
    }

    public abstract void getDataFromUI();

    public DataCollectionFragment() { }
}
