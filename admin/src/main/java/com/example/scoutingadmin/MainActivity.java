package com.example.scoutingadmin;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    EditText tournamentCode;
    TextView console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(getApplicationContext());

        Button loadTeamList = (Button)findViewById(R.id.button_load_teamlist);
        Button loadSched = (Button)findViewById(R.id.button_load_schedule);
        tournamentCode = (EditText)findViewById(R.id.tournament_code);
        console = (TextView)findViewById(R.id.console);
        console.setMovementMethod(new ScrollingMovementMethod());
        loadTeamList.setOnClickListener(this);
        loadSched.setOnClickListener(this);
        log("Hello there!");
    }

    public void loadTeams(final String event){
        final Firebase teamsRef = new Firebase("http://scouting115.firebaseio.com/teams");
        final Firebase eventTeamsRef = new Firebase("http://scouting115.firebaseio.com/event_teams");
        new TBARequests(this).loadTeams(event, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("MVRT", "Teams: " + response);
                toast("Loaded teams list", Toast.LENGTH_SHORT);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject team = response.getJSONObject(i);
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("nick", team.getString("nickname"));
                        data.put("name", team.getString("name"));
                        data.put("number", team.getInt("team_number"));
                        data.put("website", team.getString("website"));
                        data.put("location", team.getString("location"));
                        log("Loading team " + team.getInt("team_number"));
                        teamsRef.child(team.getString("key")).setValue(data);
                        eventTeamsRef.child(event).child("" + team.getInt("team_number")).child("key").setValue(team.getString("key"));
                        eventTeamsRef.child(event).child("" + team.getInt("team_number")).child("name").setValue(team.getString("nickname"));
                    } catch (JSONException e) {
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MVRT", "Error: " + error.toString());
                toast("Error retrieving team list", Toast.LENGTH_LONG);
            }
        });
    }

    public void loadSchedule(String event){
        final Firebase schedRef = new Firebase("http://scouting115.firebaseio.com/sched");
        new TBARequests(this).loadSched(event, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("MVRT", "Teams: " + response);
                toast("Loaded schedule", Toast.LENGTH_SHORT);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject match = response.getJSONObject(i);
                        String matchKey = match.getString("key");
                        String event = match.getString("event_key");
                        long time = match.getLong("time");
                        log("Loading match " + matchKey);
                        JSONArray blue = match.getJSONObject("alliances").getJSONObject("blue").getJSONArray("teams");
                        JSONArray red = match.getJSONObject("alliances").getJSONObject("red").getJSONArray("teams");
                        for (int t = 0; t < blue.length(); t++) {
                            String team = blue.getString(t);
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("team", team);
                            data.put("alliance", "b");
                            data.put("event", event);
                            data.put("match", matchKey);
                            data.put("time", time);
                            schedRef.child(team + ":" + matchKey).setValue(data);
                        }

                        for (int t = 0; t < red.length(); t++) {
                            String team = red.getString(t);
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("team", team);
                            data.put("alliance", "r");
                            data.put("event", event);
                            data.put("match", matchKey);
                            data.put("time", time);
                            schedRef.child(team + ":" + matchKey).setValue(data);
                        }

                    } catch (JSONException e) {
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MVRT", "Error: " + error.toString());
                toast("Error retrieving schedule", Toast.LENGTH_LONG);
                error("Error retrieving schedule");
            }
        });
    }

    public void loadSched(){
        String event = tournamentCode.getText().toString();
        if(event.equals("")){
            toast("Please enter a tournament code", Toast.LENGTH_SHORT);
            error("Please enter a tournament code");
            return;
        }
        loadSchedule(event.toLowerCase());
    }

    public void loadTeamList(){
        String event = tournamentCode.getText().toString();
        if(event.equals("")){
            toast("Please enter a tournament code", Toast.LENGTH_SHORT);
            error("Please enter a tournament code");
            return;
        }
        loadTeams(event.toLowerCase());
    }

    public void log(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("MVRT", text);
                String msg = "\n-> " + text;
                Spannable spannable = new SpannableString(msg);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4527A0")), 0, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                console.append(spannable);
            }
        });
    }

    public void error(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("MVRT", text);
                String msg = "\nERR -> " + text;
                Spannable spannable = new SpannableString(msg);
                spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                console.append(spannable);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_load_schedule){
            loadSched();
        }else if(view.getId() == R.id.button_load_teamlist){
            loadTeamList();
        }
    }

    public void toast(final String msg, final int length){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, length).show();
            }
        });
    }
}
