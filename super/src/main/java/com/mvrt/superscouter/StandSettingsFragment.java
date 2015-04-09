package com.mvrt.superscouter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mvrt.superscouter.view.TabFragment;


public class StandSettingsFragment extends TabFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener{

    TextView allianceLabel;
    TextView tournamentCode;
    Button tournamentSave;
    RadioGroup radioButtons;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stands_settings, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

        radioButtons = (RadioGroup)v.findViewById(R.id.standscout_radiogroup);
        radioButtons.setOnCheckedChangeListener(this);

        allianceLabel = (TextView)v.findViewById(R.id.standscout_settings_alliancelabel);
        tournamentCode = (TextView)v.findViewById(R.id.standsettings_tourament_id);
        tournamentCode.setFilters(new InputFilter[] { new InputFilter.AllCaps() });

        tournamentSave = (Button)v.findViewById(R.id.standsettings_tournament_save);
        tournamentSave.setOnClickListener(this);

        refreshViews();
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    public void refreshViews(){
        String alliance = prefs.getString(Constants.PREFS_KEY_ALLIANCE, Constants.ALLIANCE_BLUE);
        String tournament = prefs.getString(Constants.PREFS_KEY_TOURNAMENT, "SVR");
        Log.d("MVRT", "refreshing views, alliance = " + alliance);
        switch(alliance){
            case Constants.ALLIANCE_BLUE:
                radioButtons.check(R.id.standsettings_radio_bluealliance);
                allianceLabel.setTextColor(getResources().getColor(R.color.blue_alliance));
                break;
            case Constants.ALLIANCE_RED:
                radioButtons.check(R.id.standsettings_radio_redalliance);
                allianceLabel.setTextColor(getResources().getColor(R.color.red_alliance));
                break;
        }
        tournamentCode.setText(tournament);
    }

    private void setAlliance(String alliance){
        prefs.edit().putString(Constants.PREFS_KEY_ALLIANCE, alliance).commit();
        ((StandFragment)getParentFragment()).refreshViews();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String alliance = Constants.ALLIANCE_RED;
        if(checkedId == R.id.standsettings_radio_bluealliance)alliance = Constants.ALLIANCE_BLUE;
        setAlliance(alliance);
    }

    private void setTournament(){
        String code = tournamentCode.getText().toString().toUpperCase();
        if(code.length() == 0)tournamentCode.setError("Please enter a tournament code");
        else tournamentCode.setError(null);
        prefs.edit().putString(Constants.PREFS_KEY_TOURNAMENT, code).commit();
        ((StandFragment)getParentFragment()).refreshViews();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.standsettings_tournament_save){
            setTournament();
        }
    }
}
