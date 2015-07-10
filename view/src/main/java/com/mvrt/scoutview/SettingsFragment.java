package com.mvrt.scoutview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mvrt.scoutview.adapters.TabPagerAdapter;
import com.mvrt.scoutview.view.NavDrawerFragment;
import com.mvrt.scoutview.view.SlidingTabLayout;


public class SettingsFragment extends NavDrawerFragment implements View.OnClickListener{

    EditText tournamentCode;
    Button tournamentSave;
    TextView tournamentLabel;

    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

        tournamentCode = (EditText)v.findViewById(R.id.settings_tournament_id);
        tournamentCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        tournamentSave = (Button)v.findViewById(R.id.settings_tournament_save);
        tournamentSave.setOnClickListener(this);

        String event =  prefs.getString(Constants.PREFS_KEY_TOURNAMENT, "casj");

        tournamentLabel = (TextView)v.findViewById(R.id.settings_label_tournament);
        tournamentLabel.setText("Tournament code: " + event.toUpperCase());
        Log.d("MVRT", "view created");
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_settings_purple;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.settings_tournament_save){
            setTournament();
        }
    }

    private void setTournament(){
        String code = tournamentCode.getText().toString().toLowerCase();
        if(code.length() == 0)tournamentCode.setError("Please enter a tournament code");
        else tournamentCode.setError(null);
        prefs.edit().putString(Constants.PREFS_KEY_TOURNAMENT, code).commit();
        tournamentLabel.setText("Tournament code: " + code.toUpperCase());
    }

}
