package com.mvrt.superscouter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.view.TabFragment;


public class PitScoutFragment extends TabFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pit_records, container, false);
    }

    @Override
    public String getTitle() {
        return "Scout";
    }
}
