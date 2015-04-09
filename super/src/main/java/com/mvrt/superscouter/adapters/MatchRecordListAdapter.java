package com.mvrt.superscouter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvrt.superscouter.DataManager;
import com.mvrt.superscouter.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MatchRecordListAdapter extends RecyclerView.Adapter<MatchRecordListAdapter.ViewHolder> {

    private ArrayList<JSONObject> data;

    public MatchRecordListAdapter(){
        data = new ArrayList<>();
    }

    public void onDataAdded(JSONObject data) {
        this.data.add(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MatchRecordListAdapter.ViewHolder holder, int position) {
        JSONObject obj = data.get(position);
        String title = "Data Unset (THIS IS A BUG)";
        try {
            title = "Team " + obj.getInt("team") + ", match " + obj.getInt("match");
        } catch (JSONException e) { }
        holder.textView.setText(title);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.rowText);
        }
    }

}