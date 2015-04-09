package com.mvrt.scout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mvrt.scout.view.TabFragment;

/**
 * @author Akhil Palla
 */
public class StandScoutStartFragment extends TabFragment{

    RadioGroup allianceRadio;
    EditText matchNo;
    EditText teamNo;
    EditText tournament;
    Button startButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stands_start, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        allianceRadio = (RadioGroup)v.findViewById(R.id.standscout_start_radiogroup);
        matchNo = (EditText) v.findViewById(R.id.standscout_start_matchId);
        teamNo = (EditText) v.findViewById(R.id.standscout_start_team);
        tournament = (EditText) v.findViewById(R.id.standscout_start_tournament);
        tournament.setFilters(new InputFilter[] { new InputFilter.AllCaps() });
        startButton = (Button) v.findViewById(R.id.standscout_button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    public void start(){
        matchNo.setError(null);
        teamNo.setError(null);
        tournament.setError(null);

        String alliance = Constants.ALLIANCE_BLUE;
        if(allianceRadio.getCheckedRadioButtonId() == R.id.standscout_radio_redalliance)
            alliance = Constants.ALLIANCE_RED;

        String t = teamNo.getText().toString();
        if(t.length() == 0) {
            teamNo.setError("Please enter a team number");
            return;
        }
        int team = Integer.parseInt(t);

        String m = matchNo.getText().toString();
        if(m.length() == 0){
            matchNo.setError("Please enter a match number");
            return;
        }
        int match = Integer.parseInt(m);

        String tourn = tournament.getText().toString();
        if(tourn.length() == 0){
            matchNo.setError("Please enter a tournament code");
            return;
        }

        Intent i = new Intent(getActivity(), StandScoutActivity.class);
        Uri uri = Uri.parse("scout.mvrt.com/" + tourn + "/" + match + "/" + alliance + "?t=" + team);
        i.setData(uri);
        startActivity(i);
    }

    @Override
    public String getTitle(){
        return "Manually Start Scouting";
    }

}
