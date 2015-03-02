package com.mvrt.superscouter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mvrt.superscouter.view.TabFragment;


public class StandSettingsFragment extends TabFragment implements RadioGroup.OnCheckedChangeListener {


    TextView allianceLabel;
    RadioGroup radioButtons;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stands_settings, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        int alliance = prefs.getInt(Constants.PREFS_KEY_ALLIANCE, Constants.ALLIANCE_BLUE);

        radioButtons = (RadioGroup)v.findViewById(R.id.standscout_radiogroup);
        radioButtons.setOnCheckedChangeListener(this);

        allianceLabel = (TextView)v.findViewById(R.id.standscout_settings_alliancelabel);
        refreshViews();
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    public void refreshViews(){
        int alliance = prefs.getInt(Constants.PREFS_KEY_ALLIANCE, Constants.ALLIANCE_BLUE);
        Log.d("MVRT", "refreshing views, alliance = " + alliance);
        switch(alliance){
            case Constants.ALLIANCE_BLUE:
                radioButtons.check(R.id.standscout_radio_bluealliance);
                allianceLabel.setTextColor(getResources().getColor(R.color.blue_alliance));
                break;
            case Constants.ALLIANCE_RED:
                radioButtons.check(R.id.standscout_radio_redalliance);
                allianceLabel.setTextColor(getResources().getColor(R.color.red_alliance));
                break;
        }
    }

    private void setAlliance(int alliance){
        prefs.edit().putInt(Constants.PREFS_KEY_ALLIANCE, alliance).commit();
        ((StandsFragment)getParentFragment()).refreshViews();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int alliance = 0;
        switch(checkedId){
            case R.id.standscout_radio_bluealliance:
               alliance = Constants.ALLIANCE_BLUE;
                break;
            case R.id.standscout_radio_redalliance:
                alliance = Constants.ALLIANCE_RED;
                break;
        }
        setAlliance(alliance);
    }
}
