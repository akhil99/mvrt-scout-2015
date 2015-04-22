package com.mvrt.scoutview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.mvrt.scoutview.data.MatchData;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.view.TabFragment;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;


public class TeamScoutingRatingsFragment extends TabFragment{

    Team team;

    RatingBar coopRating;
    RatingBar stackRating;
    RatingBar capRating;
    RatingBar intakeRating;
    RatingBar litterRating;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teamratingdata, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        coopRating = (RatingBar)v.findViewById(R.id.standscout_postgame_cooprating);
        stackRating = (RatingBar)v.findViewById(R.id.standscout_postgame_stackrating);
        capRating = (RatingBar)v.findViewById(R.id.standscout_postgame_caprating);
        intakeRating = (RatingBar)v.findViewById(R.id.standscout_postgame_intakerating);
        litterRating = (RatingBar)v.findViewById(R.id.standscout_postgame_litterrating);
        team = ((TeamActivity)getActivity()).getTeam();
        loadRatings();
    }

    public void loadRatings(){
        double coopSum = 0;
        double capSum = 0;
        double stcSum = 0;
        double intSum = 0;
        double litSum = 0;
        double count = 0;
        for(MatchData m:team.getMatchData()){
            coopSum += m.coop_rating;
            capSum += m.capping_rating;
            stcSum += m.stacking_rating;
            intSum += m.intake_rating;
            litSum += m.litter_rating;
            count++;
        }
        double coopAvg = (count == 0)?0:coopSum/count;
        double capAvg = (count == 0)?0:capSum/count;
        double stcAvg = (count == 0)?0:stcSum/count;
        double intAvg = (count == 0)?0:intSum/count;
        double litAvg = (count == 0)?0:litSum/count;

        coopRating.setRating((float)coopAvg);
        capRating.setRating((float)capAvg);
        stackRating.setRating((float)stcAvg);
        litterRating.setRating((float)intAvg);
        intakeRating.setRating((float)litAvg);

    }

    @Override
    public String getTitle() {
        return "Ratings";
    }
}
