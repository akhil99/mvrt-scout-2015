package com.mvrt.superscouter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mvrt.superscouter.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoutingCommentsAdapter extends RecyclerView.Adapter<ScoutingCommentsAdapter.ViewHolder> {


    ArrayList<String> comments;
    ArrayList<Integer> teams;

    ArrayList<EditText> commentFields;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        EditText comments;

        public ViewHolder(View itemView, ArrayList<EditText> fields) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.item_comment_teamnumber);
            comments = (EditText) itemView.findViewById(R.id.item_comment_edittext);
            fields.add(comments);
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

    public ScoutingCommentsAdapter(int[] tems){
        comments = new ArrayList<>();
        teams = new ArrayList<>();
        commentFields = new ArrayList<>();
        for(int i:tems){
            if(i > 0){
                comments.add("");
                teams.add(i);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments,parent,false);
        return new ViewHolder(v, commentFields);
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

    public HashMap<Integer, String> getComments(){
        HashMap <Integer, String> comments = new HashMap<>();
        for(int i = 0; i < teams.size(); i++){
                comments.put(teams.get(i), commentFields.get(i).getText().toString());
        }
        return comments;
    }

}