package com.mvrt.superscouter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mvrt.superscouter.view.TabFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class StandScoutFragment extends TabFragment implements View.OnClickListener{

    EditText matchNo;
    EditText team1;
    EditText team2;
    EditText team3;
    Button newMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_standscout_super, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        matchNo = (EditText)v.findViewById(R.id.standscout_matchId);
        team1 = (EditText)v.findViewById(R.id.standscout_team1);
        team2 = (EditText)v.findViewById(R.id.standscout_team2);
        team3 = (EditText)v.findViewById(R.id.standscout_team3);
        newMatch = (Button)v.findViewById(R.id.standscout_button_start);
        newMatch.setOnClickListener(this);
    }

    @Override
    public String getTitle() {
        return "Stand Scouting";
    }

    public void startMatchActivity(String matchName, int team1, int team2, int team3){
        JSONObject obj = new JSONObject();
        try {
            obj.put("matchName", matchName);
            obj.put("1", team1);
            obj.put("2", team2);
            obj.put("3", team3);
            ((SuperScoutBase)getActivity().getApplication()).getBtService().sendToAll(obj);
            matchNo.setText("");
            this.team1.setText("");
            this.team2.setText("");
            this.team3.setText("");
            Intent i = new Intent(getActivity(), MatchScoutActivity.class);
            i.putExtra("matchNo", matchName);
            i.putExtra("team1", team1);
            i.putExtra("team2", team2);
            i.putExtra("team3", team3);
            startActivity(i);
        }catch(JSONException e){
            Log.e("MVRT", "Error forming JSON object");
        }
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
            String match = matchNo.getText().toString();
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
