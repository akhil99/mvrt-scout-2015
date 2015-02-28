package com.mvrt.superscouter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.view.TabFragment;

public class MatchDataFragment extends TabFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_data, container, false);
    }

    @Override
    public String getTitle() {
        return "Scouting Data";
    }
}
