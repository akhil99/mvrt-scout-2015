package com.mvrt.superscouter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mvrt.superscouter.view.TabFragment;


public class StandScoutFragment extends TabFragment implements View.OnClickListener{

    EditText matchNo;
    EditText team1;
    EditText team2;
    EditText team3;
    Button newMatch;
    TextView allianceView;

    String alliance;
    String tournamentCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stands_start, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        matchNo = (EditText)v.findViewById(R.id.standscout_matchId);
        team1 = (EditText)v.findViewById(R.id.standscout_team1);
        team2 = (EditText)v.findViewById(R.id.standscout_team2);
        team3 = (EditText)v.findViewById(R.id.standscout_team3);
        newMatch = (Button)v.findViewById(R.id.standscout_button_start);
        allianceView = (TextView)v.findViewById(R.id.standscout_alliancetextview);
        newMatch.setOnClickListener(this);
        refreshViews();
    }

    public void refreshViews(){
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        alliance = prefs.getString(Constants.PREFS_KEY_ALLIANCE, Constants.ALLIANCE_BLUE);
        tournamentCode = prefs.getString(Constants.PREFS_KEY_TOURNAMENT, "SVR");
        Log.d("MVRT", "refreshing views, alliance = " + alliance);
        switch(alliance){
            case Constants.ALLIANCE_BLUE:
                allianceView.setTextColor(getResources().getColor(R.color.blue_alliance));
                allianceView.setText("Blue Alliance @ " + tournamentCode);
                break;
            case Constants.ALLIANCE_RED:
                allianceView.setTextColor(getResources().getColor(R.color.red_alliance));
                allianceView.setText("Red Alliance @ " + tournamentCode);
                break;
        }
    }

    @Override
    public String getTitle() {
        return "Stand Scouting";
    }

    public void startMatchActivity(int matchNo, int team1, int team2, int team3){
        String uri = "http://scout.mvrt.com/scout/" + tournamentCode
                + "/" + matchNo + "/" + alliance
                + "?t=" + team1
                + "&t=" + team2
                + "&t=" + team3;
        ((SuperScoutBase)getActivity().getApplication()).getBtService().writeToAll(uri);
        this.matchNo.setText("");
        this.team1.setText("");
        this.team2.setText("");
        this.team3.setText("");
        Intent i = new Intent(getActivity(), MatchScoutActivity.class);
        i.putExtra("matchNo", matchNo);
        i.putExtra("team1", team1);
        i.putExtra("team2", team2);
        i.putExtra("team3", team3);
        i.putExtra("alliance", alliance);
        i.putExtra("tournament", tournamentCode);
        i.putExtra("uri", uri);
        startActivity(i);
    }

    private void startMatch(){
        boolean valid = true;
        if(TextUtils.isEmpty(matchNo.getText())){
            matchNo.setError("Please enter a match number");
            valid = false;
        }else{
            matchNo.setError(null);
        }
        if(TextUtils.isEmpty(team1.getText())){
            valid = false;
            team1.setError("Please enter a team number");
        }else{
            team1.setError(null);
        }
        if(TextUtils.isEmpty(team2.getText())){
            team2.setError("Please enter a team number");
            valid = false;
        }else{
            team2.setError(null);
        }
        if(TextUtils.isEmpty(team3.getText())){
            team3.setError("Please enter a team number");
            valid = false;
        }else{
            team3.setError(null);
        }

        if(valid){
            int match = Integer.parseInt(matchNo.getText().toString());
            int t1 = Integer.parseInt(team1.getText().toString());
            int t2 = Integer.parseInt(team2.getText().toString());
            int t3 = Integer.parseInt(team3.getText().toString());
            startMatchActivity(match, t1, t2, t3);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.standscout_button_start) {
            startMatch();
        }
    }
}
