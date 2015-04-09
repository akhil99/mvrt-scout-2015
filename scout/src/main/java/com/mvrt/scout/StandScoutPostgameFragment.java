package com.mvrt.scout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;

import com.mvrt.scout.view.TabFragment;

import org.json.JSONObject;

/**
 * @author Akhil Palla
 */
public class StandScoutPostgameFragment extends TabFragment {

    RatingBar coop;
    RatingBar stacking;
    RatingBar capping;
    RatingBar intake;
    RatingBar litter;
    CheckBox tippy;
    CheckBox interfere;
    EditText comments;
    Button finish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_standscout_postgame, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        coop = (RatingBar)v.findViewById(R.id.standscout_postgame_cooprating);
        stacking = (RatingBar)v.findViewById(R.id.standscout_postgame_stackrating);
        capping = (RatingBar)v.findViewById(R.id.standscout_postgame_caprating);
        intake = (RatingBar)v.findViewById(R.id.standscout_postgame_intakerating);
        litter = (RatingBar)v.findViewById(R.id.standscout_postgame_litterrating);
        tippy = (CheckBox)v.findViewById(R.id.standscout_postgame_tippy);
        interfere = (CheckBox)v.findViewById(R.id.standscout_postgame_interfere);
        comments = (EditText)v.findViewById(R.id.standscout_postgame_comments);
        finish = (Button)v.findViewById(R.id.standscout_postgame_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StandScoutActivity)getActivity()).endMatch();
            }
        });
    }

    @Override
    public String getTitle(){
        return "Postgame";
    }

    public JSONObject getData(){
        JSONObject o = new JSONObject();
        try{
            o.put("coop_rating", coop.getRating());
            o.put("stacking_rating", stacking.getRating());
            o.put("capping_rating", capping.getRating());
            o.put("intake_rating", intake.getRating());
            o.put("litter_rating", litter.getRating());
            o.put("tippy", tippy.isChecked());
            o.put("interferes", interfere.isChecked());
            o.put("scout_comments", comments.getText().toString());
        }catch(Exception e){}
        return o;
    }
}
