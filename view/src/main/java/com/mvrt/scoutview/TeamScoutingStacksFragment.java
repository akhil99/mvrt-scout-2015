package com.mvrt.scoutview;

import android.graphics.Color;
import android.os.Bundle;
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
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;


public class TeamScoutingStacksFragment extends TabFragment{

    ColumnChartView numStacksChart;
    ColumnChartView stacksChart;

    Team team;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teamscoutingstackdata, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        numStacksChart = (ColumnChartView)v.findViewById(R.id.chart_stack_num);
        numStacksChart.setInteractive(true);
        numStacksChart.setZoomEnabled(false);
        stacksChart = (ColumnChartView)v.findViewById(R.id.chart_stacks);
        stacksChart.setInteractive(false);

        team = ((TeamActivity)getActivity()).getTeam();

        loadNumStacks();
        clearStacks();
    }

    public void loadNumStacks(){
        List<Column> cols = new ArrayList<>();
        for(MatchData m:team.getMatchData()){
            List<SubcolumnValue> subCols = new ArrayList<>();
            int color = getResources().getColor(R.color.accent);
            subCols.add(new SubcolumnValue(m.numStacks).setColor(color).setLabel("m" + m.matchNo));
            cols.add(new Column(subCols).setHasLabels(true));
        }

        ColumnChartData data = new ColumnChartData(cols).setBaseValue(0);
        data.setAxisYLeft(new Axis().setHasLines(true).setName("Number of Stacks"));
        data.setAxisXBottom(new Axis().setName("Match Number"));

        numStacksChart.setColumnChartData(data);
        numStacksChart.setValueSelectionEnabled(true);
        numStacksChart.setOnValueTouchListener(new ValueTouchListener());
    }

    public void clearStacks(){
        List<Column> cols = new ArrayList<>();
        ColumnChartData data = new ColumnChartData(cols).setBaseValue(0);
        stacksChart.setColumnChartData(data);
    }

    public void loadStacks(MatchData match){
        List<Column> cols = new ArrayList<>();

        for(MatchData.Stack stack:match.stacks) {
            List<SubcolumnValue> subCols = new ArrayList<>();
            int color = (stack.capped)? getResources().getColor(R.color.material_green):getResources().getColor(R.color.material_grey);
            subCols.add(new SubcolumnValue(stack.height).setColor(color).setLabel("" + stack.height));
            cols.add(new Column(subCols).setHasLabels(true));
        }

        ColumnChartData data = new ColumnChartData(cols).setBaseValue(0);
        stacksChart.setColumnChartData(data);
    }

    @Override
    public String getTitle() {
        return "Stacks";
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener{

        @Override
        public void onValueSelected(int i, int i1, SubcolumnValue subcolumnValue) {
            ArrayList<MatchData> matches = new ArrayList<>(team.getMatchData());
            loadStacks(matches.get(i));
        }

        @Override
        public void onValueDeselected() {
            clearStacks();
        }
    }

}
