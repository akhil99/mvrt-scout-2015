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
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;


public class TeamScoutingIntakeFragment extends TabFragment{

    PieChartView intakeChart;
    ColumnChartView intakeBarChart;

    Team team;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teamscoutingintakedata, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        intakeChart = (PieChartView)v.findViewById(R.id.chart_pie_totes);
        intakeChart.setInteractive(false);
        intakeChart.setZoomEnabled(false);
        intakeBarChart = (ColumnChartView)v.findViewById(R.id.chart_col_totes);
        intakeBarChart.setInteractive(true);
        intakeBarChart.setZoomEnabled(false);

        team = ((TeamActivity)getActivity()).getTeam();

        loadPieChart(null);
        loadBarChart();
    }

    public void loadPieChart(MatchData m){
        List<SliceValue> slices = new ArrayList<>();
        if(m == null){
            Log.d("MVRT", "Null");
            SliceValue feed = new SliceValue(0).setLabel("Feeder Station");
            feed.setColor(getResources().getColor(R.color.blue_alliance));
            slices.add(feed);
            SliceValue landfill = new SliceValue(0).setLabel("Landfill");
            landfill.setColor(getResources().getColor(R.color.red_alliance));
            slices.add(landfill);
            for(MatchData match:team.getMatchData()){
                feed.setValue(feed.getValue() + match.feederTotes);
                feed.setLabel("Feeder station: " + feed.getValue());
                landfill.setValue(landfill.getValue() + match.landfillTotes);
                landfill.setLabel("Landfill: " + landfill.getValue());
            }
        }else{
            Log.d("MVRT", "Not Null");
            SliceValue feed = new SliceValue(m.feederTotes).setLabel("Feeder Station: " + m.feederTotes);
            feed.setColor(getResources().getColor(R.color.blue_alliance));
            slices.add(feed);
            SliceValue landfill = new SliceValue(m.landfillTotes).setLabel("Landfill: " + m.landfillTotes);
            landfill.setColor(getResources().getColor(R.color.red_alliance));
            slices.add(landfill);
        }

        PieChartData data = new PieChartData();
        data.setHasLabels(true);
        data.setHasCenterCircle(false);
        data.setValues(slices);
        intakeChart.setPieChartData(data);
    }

    public void loadBarChart(){
        List<Column> cols = new ArrayList<>();

        for(MatchData match:team.getMatchData()){
            List<SubcolumnValue> subCols = new ArrayList<>();
            SubcolumnValue val = new SubcolumnValue(match.feederTotes + match.landfillTotes);
            val.setColor(getResources().getColor(R.color.accent));
            val.setLabel("m" + match.matchNo);
            subCols.add(val);
            cols.add(new Column(subCols).setHasLabels(true));
        }

        ColumnChartData data = new ColumnChartData(cols).setBaseValue(0);
        data.setAxisYLeft(new Axis().setHasLines(true).setName("Total # of Totes"));
        data.setAxisXBottom(new Axis().setName("Match Number"));
        intakeBarChart.setColumnChartData(data);
        intakeBarChart.setValueSelectionEnabled(true);
        intakeBarChart.setOnValueTouchListener(new ValueTouchListener());
    }

    @Override
    public String getTitle() {
        return "Tote Intake";
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener{

        @Override
        public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
            ArrayList<MatchData> matches = new ArrayList<>(team.getMatchData());
            loadPieChart(matches.get(i));
        }

        @Override
        public void onValueDeselected() {
            loadPieChart(null);
        }
    }

}
