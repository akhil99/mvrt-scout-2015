package com.mvrt.scoutview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.scoutview.data.MatchData;
import com.mvrt.scoutview.data.Team;
import com.mvrt.scoutview.view.TabFragment;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;


public class TeamScoutingCanburglarFragment extends TabFragment{

    ColumnChartView cansStolen;

    Team team;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teamcanburglardata, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        cansStolen = (ColumnChartView)v.findViewById(R.id.chart_canburglar);
        cansStolen.setScrollEnabled(true);
        cansStolen.setZoomType(ZoomType.HORIZONTAL);
        team = ((TeamActivity)getActivity()).getTeam();

        loadNumStacks();
    }

    public void loadNumStacks(){
        List<Column> cols = new ArrayList<>();
        for(MatchData m:team.getMatchData()){
            List<SubcolumnValue> subCols = new ArrayList<>();
            SubcolumnValue auton = new SubcolumnValue(m.autonContainersFromStep);
            auton.setColor(getResources().getColor(R.color.accent));
            SubcolumnValue teleop = new SubcolumnValue(m.containersFromStep).setLabel("m" + m.matchNo);
            teleop.setColor(getResources().getColor(R.color.primary));
            subCols.add(auton);
            subCols.add(teleop);
            cols.add(new Column(subCols).setHasLabels(true));
        }

        ColumnChartData data = new ColumnChartData(cols).setBaseValue(0).setStacked(true);
        data.setAxisYLeft(new Axis().setHasLines(true).setName("Bins Stolen"));
        data.setAxisXBottom(new Axis().setName("Match"));

        cansStolen.setColumnChartData(data);
    }

    @Override
    public String getTitle() {
        return "Canburglar";
    }
}
