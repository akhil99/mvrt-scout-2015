package com.mvrt.scoutview.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvrt.scoutview.R;
import com.mvrt.scoutview.data.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {

    TeamSelectedListener listener;
    HashMap<Integer, Team> data;
    ArrayList<Team> visibleData;

    Team.SortingType sortingType;

    public TeamListAdapter(TeamSelectedListener listener){
        this.listener = listener;
        data = new HashMap<>();
        visibleData = new ArrayList<>();
        sortingType = Team.SortingType.SORT_TEAMNO;
    }

    public void setData(HashMap<Integer, Team> data){
        this.data = data;
        updateView();
    }

    public void updateView(){
        sort(sortingType); //re-sorts the data
    }

    public void sort(Team.SortingType type){
        sortingType = type;
        visibleData.clear();
        for(Team team:data.values()){
            team.setSortingType(sortingType);
            if(team.hasData())visibleData.add(team);
        }
        Collections.sort(visibleData);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_list, parent, false);
        ViewHolder vh = new ViewHolder(v, listener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Team t = visibleData.get(position);
        holder.setData(position, t);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return visibleData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        private TextView rankView;
        private TextView scoreView;
        private TextView teamView;
        private TextView statsView;
        private Team team;
        TeamSelectedListener listener;

        public ViewHolder(View v, TeamSelectedListener listener) {
            super(v);
            rankView = (TextView)v.findViewById(R.id.teamlistitem_rank);
            scoreView = (TextView)v.findViewById(R.id.teamlistitem_score);
            teamView = (TextView)v.findViewById(R.id.teamlistitem_teamname);
            statsView = (TextView)v.findViewById(R.id.teamlistitem_teamstats);
            this.listener = listener;
            v.setOnClickListener(this);
        }

        public void setData(int position, Team t){
            team = t;
            teamView.setText(t.getTitle());
            statsView.setText(t.getStatsString());
            int rank = (t.getSortingType() == Team.SortingType.SORT_TEAMNO)?t.getRank():position + 1;
            rankView.setText("" + rank);
            scoreView.setText(t.getScoreString());
        }

        @Override
        public void onClick(View v){
            if(team != null)listener.teamSelected(team);
        }

    }

    public interface TeamSelectedListener {
        public void teamSelected(Team team);
    }

}

