package com.mvrt.superscouter.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvrt.superscouter.R;

import java.util.ArrayList;

public class ScoutingCommentsAdapter extends RecyclerView.Adapter<ScoutingCommentsAdapter.ViewHolder> {


    ArrayList<String> comments;
    ArrayList<Integer> teams;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        EditText comments;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.item_comment_teamnumber);
            comments = (EditText) itemView.findViewById(R.id.item_comment_edittext);
        }

        @Override
        public void onClick(View v) {
            if(comments.getVisibility() == View.GONE){
                comments.setVisibility(View.VISIBLE);
            }else{
                comments.setVisibility(View.GONE);
            }
        }

    }

    public ScoutingCommentsAdapter(int team1, int team2, int team3){
        comments = new ArrayList<>();
        comments.add("");
        comments.add("");
        comments.add("");
        teams = new ArrayList<>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ScoutingCommentsAdapter.ViewHolder holder, int position) {
            holder.textView.setText("Team " + teams.get(position).toString());
            holder.comments.setText(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

}