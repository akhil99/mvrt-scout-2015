package com.mvrt.superscouter;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.mvrt.superscouter.view.TabFragment;


public class StandScoutFragment extends TabFragment implements View.OnClickListener{

    EditText matchNo;
    EditText team1;
    EditText team2;
    EditText team3;
    Button newMatch;
    TextView allianceView;
    ImageButton syncTeams;

    String alliance;
    String tournamentCode;

    BtService btService;

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
        syncTeams = (ImageButton)v.findViewById(R.id.standscout_button_sync);
        allianceView = (TextView)v.findViewById(R.id.standscout_alliancetextview);
        newMatch.setOnClickListener(this);
        syncTeams.setOnClickListener(this);
        refreshViews();

        Intent service = new Intent(getActivity().getApplicationContext(), BtService.class);
        getActivity().getApplicationContext().bindService(service, btServiceConn, 0);
    }

    @Override
    public void onDestroy(){
        getActivity().getApplicationContext().unbindService(btServiceConn);
        super.onDestroy();
    }

    public void refreshViews(){
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        alliance = prefs.getString(Constants.PREFS_KEY_ALLIANCE, Constants.ALLIANCE_BLUE);
        tournamentCode = prefs.getString(Constants.PREFS_KEY_TOURNAMENT, "CASJ");
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
        if(btService != null)btService.sendAll(uri);
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

    private void syncTeams(){
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        Firebase schedRef = new Firebase("https://scouting115.firebaseio.com/sched");
        String matchText = matchNo.getText().toString();
        if(matchText == null || matchText == ""){
            Toast.makeText(getActivity(), "Invalid Match Number", Toast.LENGTH_SHORT).show();
            return;
        }
        int match = 0;
        try {
            match = Integer.parseInt(matchNo.getText().toString());
        }catch(Exception e){
            Toast.makeText(getActivity(), "Invalid Match Number", Toast.LENGTH_SHORT).show();
            return;
        }
        String matchKey = 2015 + tournamentCode.toLowerCase() + "_qm" + match;
        Log.d("MVRT", "Match key: " + matchKey);
        schedRef.orderByChild("match").equalTo(matchKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                Log.d("MVRT", "Recieved snapshot: " + dataSnapshot.toString());
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    String team = child.child("team").getValue(String.class);
                    int teamNo =  Integer.parseInt(team.substring(3));
                    String alliance = child.child("alliance").getValue(String.class);
                    if(alliance.equals(Constants.ALLIANCE_BLUE)) {
                        setTeamNumber(count++, "" + teamNo);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("MVRT", "cancelled");
            }
        });
    }

    public void setTeamNumber(final int pos, final String teamNo){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (pos){
                    case 0:
                        team1.setText(teamNo);
                        break;
                    case 1:
                        team2.setText(teamNo);
                        break;
                    case 2:
                        team3.setText(teamNo);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.standscout_button_start) {
            startMatch();
        }else if(v.getId() == R.id.standscout_button_sync){
            syncTeams();
        }
    }

    private ServiceConnection btServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BtService.BtServiceBinder binder = (BtService.BtServiceBinder)service;
            btService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MVRT", "Service Disconnected in StandScoutFragment");
            btService = null;
        }
    };
}
