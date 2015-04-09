package com.mvrt.scout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mvrt.scout.view.TabFragment;

import org.json.JSONObject;

import java.util.ArrayList;


public class StandScoutTeleopFragment extends TabFragment implements View.OnClickListener{

    Button toteFromFeeder;
    Button toteFromLandfill;
    Button loseTote;
    Button newStack;
    Button stackLost;
    Button stackCapped;
    Button canFromStep;
    Button flipCan;
    Button noodleInBin;
    Button noodleToLandfill;
    CheckBox disabled;
    TextView toteCount;
    TextView stacksLabel;
    TextView cansLabel;
    TextView noodlesLabel;

    int currentTotes = 0;

    int feederTotes = 0;
    int landfillTotes = 0;
    int lostTotes = 0;
    int cappedStacks = 0;
    int cansFromStep = 0;
    int cansFlipped = 0;
    int noodlesInBin = 0;
    int noodlesInLandfil = 0;

    ArrayList<String> stacks;
    ArrayList<Boolean> capped;
    ArrayList<Integer> knockedStacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_standscout_teleop, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        stacks = new ArrayList<>();
        capped = new ArrayList<>();
        knockedStacks = new ArrayList<>();

        toteFromFeeder = (Button)v.findViewById(R.id.scout_button_tote_feeder);
        toteFromFeeder.setOnClickListener(this);
        toteFromLandfill = (Button)v.findViewById(R.id.scout_button_tote_landfill);
        toteFromLandfill.setOnClickListener(this);
        loseTote = (Button)v.findViewById(R.id.scout_button_tote_lost);
        loseTote.setOnClickListener(this);
        newStack = (Button)v.findViewById(R.id.scout_button_stack_new);
        newStack.setOnClickListener(this);
        stackLost = (Button)v.findViewById(R.id.scout_button_stack_attack);
        stackLost.setOnClickListener(this);
        stackCapped = (Button)v.findViewById(R.id.scout_button_stack_cap);
        stackCapped.setOnClickListener(this);
        canFromStep = (Button)v.findViewById(R.id.scout_button_can_step);
        canFromStep.setOnClickListener(this);
        flipCan = (Button)v.findViewById(R.id.scout_button_can_flip);
        flipCan.setOnClickListener(this);
        noodleInBin = (Button)v.findViewById(R.id.scout_button_noodle_bin);
        noodleInBin.setOnClickListener(this);
        noodleToLandfill = (Button)v.findViewById(R.id.scout_button_noodle_landfill);
        noodleToLandfill.setOnClickListener(this);
        disabled = (CheckBox)v.findViewById(R.id.scout_teleop_disabled);
        toteCount = (TextView)v.findViewById(R.id.scout_label_tote_count);
        stacksLabel = (TextView)v.findViewById(R.id.scout_label_stacks);
        cansLabel = (TextView)v.findViewById(R.id.scout_label_cans);
        noodlesLabel = (TextView)v.findViewById(R.id.scout_label_noodles);
        refreshUi();
    }

    @Override
    public String getTitle() {
        return "Teleop";
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.scout_button_tote_landfill:
                currentTotes++;
                landfillTotes++;
                break;
            case R.id.scout_button_tote_feeder:
                currentTotes++;
                feederTotes++;
                break;
            case R.id.scout_button_tote_lost:
                currentTotes--;
                lostTotes++;
                break;
            case R.id.scout_button_noodle_bin:
                noodlesInBin++;
                break;
            case R.id.scout_button_noodle_landfill:
                noodlesInLandfil++;
                break;
            case R.id.scout_button_stack_cap:
                cappedStacks++;
                break;
            case R.id.scout_button_stack_new:
                newStack();
                break;
            case R.id.scout_button_stack_attack:
                destroyStack();
                break;
            case R.id.scout_button_can_step:
                cansFromStep++;
                break;
            case R.id.scout_button_can_flip:
                cansFlipped++;
                break;
        }
        refreshUi();
    }

    public void refreshUi(){
        toteCount.setText("Totes: " + currentTotes);
        stacksLabel.setText("Stacks: " + stacks);
        cansLabel.setText("Containers:\n" + cappedStacks + " capped, " + cansFromStep + " from step, " + cansFlipped + " flipped");
        noodlesLabel.setText("Noodles: " + noodlesInBin + " in containers, " + noodlesInLandfil + " in landfill");
    }

    public void newStack(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.numberdialog, null);
        builder.setView (view);

        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.numberPicker);
        picker.setMinValue(1);
        picker.setMaxValue(6);

        builder.setPositiveButton("Capped", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int height = picker.getValue();
                stacks.add(height + "c");
                capped.add(true);
                currentTotes -= Math.min(currentTotes, height);
                refreshUi();
                Log.d("MVRT", "Stacks: " + stacks);
                dialog.dismiss();
            } });
        builder.setNeutralButton("Not capped", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int height = picker.getValue();
                stacks.add(height + "nc");
                capped.add(false);
                currentTotes -= Math.min(currentTotes, height);
                refreshUi();
                Log.d("MVRT", "Stacks: " + stacks);
                dialog.dismiss();
            } });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            } });
        final AlertDialog dialog = builder.create ();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setTitle("New Stack");
        dialog.show();
    }

    public void destroyStack(){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.numberdialog, null);
        builder.setView (view);

        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.numberPicker);
        picker.setMinValue(1);
        picker.setMaxValue(6);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int height = picker.getValue();
                knockedStacks.add(height);
                refreshUi();
                dialog.dismiss();
            } });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {} });

        final AlertDialog dialog = builder.create ();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setTitle("Destroy Stack");
        dialog.show();
    }

    public JSONObject getData(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("totes_collected_feeder", feederTotes);
            obj.put("totes_collected_landfill", landfillTotes);
            obj.put("totes_collected_total", feederTotes + landfillTotes);
            obj.put("totes_dropped", lostTotes);
            obj.put("stacks_created", stacks);
            obj.put("stacks_destroyed", knockedStacks);
            obj.put("stacks_capped", cappedStacks);
            obj.put("containers_from_step", cansFromStep);
            obj.put("containers_flipped", cansFlipped);
            obj.put("noodles_bin", noodlesInBin);
            obj.put("noodles_landfill", noodlesInLandfil);
            obj.put("disabled", disabled.isChecked());
        }catch(Exception e){
            Log.e("MVRT", "error with json");
        }
        return obj;
    }

}
