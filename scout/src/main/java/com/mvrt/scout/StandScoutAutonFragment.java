package com.mvrt.scout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mvrt.scout.view.TabFragment;

import org.json.JSONObject;


public class StandScoutAutonFragment extends TabFragment implements CompoundButton.OnCheckedChangeListener{

    RadioGroup startingPos;
    CheckBox binsFromStep;
    EditText numberBinsFromStep;
    CheckBox yellowTotes;
    EditText numberYellowTotes;
    CheckBox greyTotes;
    CheckBox mobility;
    CheckBox interference;
    CheckBox noshow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_standscout_auton, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        startingPos = (RadioGroup)v.findViewById(R.id.scout_auton_start_radio);
        binsFromStep = (CheckBox)v.findViewById(R.id.scout_auton_bins_checkbox);
        binsFromStep.setOnCheckedChangeListener(this);
        numberBinsFromStep = (EditText)v.findViewById(R.id.scout_auton_bins_number);
        yellowTotes = (CheckBox)v.findViewById(R.id.scout_auton_totes_yellow);
        yellowTotes.setOnCheckedChangeListener(this);
        numberYellowTotes = (EditText)v.findViewById(R.id.scout_auton_yellow_number);
        greyTotes = (CheckBox)v.findViewById(R.id.scout_auton_totes_interact);
        mobility = (CheckBox)v.findViewById(R.id.scout_auton_mobility);
        interference = (CheckBox)v.findViewById(R.id.scout_auton_interfere);
        noshow = (CheckBox)v.findViewById(R.id.scout_auton_noshow);
    }

    @Override
    public String getTitle() {
        return "Autonomous";
    }

    public void clearErrors(){
        numberBinsFromStep.setError(null);
    }

    public JSONObject getData(){
        clearErrors();

        String starting_pos = "";
        switch(startingPos.getCheckedRadioButtonId()){
            case R.id.scout_auton_start_staging:
                starting_pos = "staging area";
                break;
            case R.id.scout_auton_start_landfill:
                starting_pos = "landfill";
                break;
            default:
                Toast.makeText(getActivity(), "Select a starting position", Toast.LENGTH_LONG).show();
                return null;
        }

        int bins_from_step = 0;
        if(binsFromStep.isChecked()){
            String number = numberBinsFromStep.getText().toString();
            if(number.length() == 0){
                numberBinsFromStep.setError("Enter a valid number");
                return null;
            }
            bins_from_step = Integer.parseInt(number);
        }

        int yellow_totes = 0;
        if(yellowTotes.isChecked()){
            String number = numberYellowTotes.getText().toString();
            if(number.length() == 0){
                numberYellowTotes.setError("Enter a valid number");
                return null;
            }
            yellow_totes = Integer.parseInt(number);
        }

        JSONObject data = new JSONObject();
        try{
            data.put("starting_pos", starting_pos);
            data.put("bins_step", bins_from_step);
            data.put("yellow_totes", yellow_totes);
            data.put("grey_totes", greyTotes.isChecked());
            data.put("mobility", mobility.isChecked());
            data.put("interferes", interference.isChecked());
            data.put("noshow", noshow.isChecked());
        }catch(Exception e){
            Log.e("MVRT", "error with json");
        }

        return data;
    }

    public void showError(){
        if(binsFromStep.isChecked()){
            String number = numberBinsFromStep.getText().toString();
            if(number.length() == 0){
                numberBinsFromStep.setError("Enter a valid number");
            }
        }

        if(yellowTotes.isChecked()){
            String number = numberYellowTotes.getText().toString();
            if(number.length() == 0){
                numberYellowTotes.setError("Enter a valid number");
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if(view.getId() == R.id.scout_auton_bins_checkbox){
            numberBinsFromStep.setVisibility(isChecked?View.VISIBLE:View.GONE);
        }else if(view.getId() == R.id.scout_auton_totes_yellow){
            numberYellowTotes.setVisibility(isChecked?View.VISIBLE:View.GONE);
        }
    }
}
