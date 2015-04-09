package com.mvrt.scout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mvrt.scout.view.TabFragment;

public class StandSettingsFragment extends TabFragment implements View.OnClickListener{

    EditText scoutId;
    Button saveId;
    TextView idLabel;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stands_settings, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

        scoutId = (EditText)v.findViewById(R.id.standsettings_scout_id);
        idLabel = (TextView)v.findViewById(R.id.standsettings_scoutid_label);
        saveId = (Button)v.findViewById(R.id.standsettings_scoutid_save);
        saveId.setOnClickListener(this);

        refreshViews();
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    public void refreshViews(){
        int scoutid = prefs.getInt(Constants.PREFS_KEY_SCOUTID, 1);
        scoutId.setText("" + scoutid);
        idLabel.setText("Scout ID: " + scoutid);
    }

    public void saveScoutId(){
        String text = scoutId.getText().toString();
        if(text.length() == 0) {
            scoutId.setError("Please enter an ID");
            return;
        }
        int id = Integer.parseInt(text);
        if(id < 1 || id > 3){
            scoutId.setError("Pleas enter an ID between 1 and 3");
            return;
        }
        scoutId.setError(null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        prefs.edit().putInt(Constants.PREFS_KEY_SCOUTID, id).commit();
        refreshViews();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.standsettings_scoutid_save){
            saveScoutId();
        }
    }
}
