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

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.ViewHolder> {

    ArrayList<Match> matches;
    MatchSelectedListener listener;

    public MatchListAdapter(MatchSelectedListener listener){
        matches = new ArrayList<>();
        this.listener = listener;
    }

    public void addMatch(String match, long time, ArrayList<Integer> red, ArrayList<Integer> blue){
        matches.add(new Match(match, time, red, blue));
        Collections.sort(matches);
        notifyDataSetChanged();
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_list, parent, false);
        ViewHolder vh = new ViewHolder(v, listener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Match m = matches.get(position);
        holder.setData(m.matchKey, m.red, m.blue);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return matches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private TextView red;
        private TextView blue;
        private MatchSelectedListener listener;
        private String match;

        public ViewHolder(View v, MatchSelectedListener listener) {
            super(v);
            title = (TextView)v.findViewById(R.id.matchlistitem_title);
            red = (TextView)v.findViewById(R.id.matchlistitem_red);
            blue = (TextView)v.findViewById(R.id.matchlistitem_blue);
            this.listener = listener;
        }

        public void setData(String match, ArrayList<Integer> red, ArrayList<Integer> blue){
            title.setText(match);
            this.red.setText(red.toString());
            this.blue.setText(blue.toString());
            this.match = match;
        }

        @Override
        public void onClick(View v){
            listener.matchSelected(match);
        }

    }

    public interface MatchSelectedListener {
        public void matchSelected(String match);
    }

    public static class Match implements Comparable<Match>{

        public String matchKey;
        private long time;
        public ArrayList<Integer> red;
        public ArrayList<Integer> blue;

        public Match(String key, long t, ArrayList<Integer> red, ArrayList<Integer> blue){
            matchKey = key;
            time = t;
            this.red = red;
            this.blue = blue;
        }

        @Override
        public int compareTo(Match match) {
            return Long.compare(time, match.time);
        }
    }

}

