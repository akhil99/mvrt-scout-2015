package com.mvrt.scout;


import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by akhil on 11/18/14.
 */
public abstract class DataCollectionFragment extends Fragment {


    DataManager dataManager;

    boolean isRed() {
        return dataManager.getScoutId() <= 3;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dataManager = ((ScoutBase)getActivity().getApplication()).getDataManager();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public abstract void getDataFromUI();

    public DataCollectionFragment(){ }

}
