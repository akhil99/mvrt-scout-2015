package com.mvrt.superscouter.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.mvrt.superscouter.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> implements ChildEventListener {

    HashMap<String, DataSnapshot> data;
    ItemClickListener listener;

    public RecordListAdapter(ItemClickListener listener){
        data = new HashMap<>();
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        ViewHolder vh = new ViewHolder(v, listener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataSnapshot snap = new ArrayList<DataSnapshot>(data.values()).get(position);
        Log.d("MVRT", snap.child("team").toString());
        int team = ((Long)snap.child("team").getValue()).intValue();
        int match = ((Long)snap.child("match").getValue()).intValue();
        String tourn = (String)snap.child("tournament").getValue();
        holder.setData(team, match, tourn);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String s) {
        data.put(snapshot.getKey(), snapshot);
        notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {
        data.put(snapshot.getKey(), snapshot);
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        data.remove(snapshot.getKey());
        notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String s) {
        data.remove(s);
        data.put(snapshot.getKey(), snapshot);
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {}


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView title;

        int team;
        int match;
        String tourn;
        ItemClickListener listener;

        public ViewHolder(View v, ItemClickListener listener) {
            super(v);
            title = (TextView)v.findViewById(R.id.rowText);
            this.listener = listener;
            v.setOnClickListener(this);
        }

        public void setData(int team, int match, String tourn){
            this.team = team;
            this.match = match;
            this.tourn = tourn;
            title.setText("Team " + team + ": match " + match + "@" + tourn);
        }

        @Override
        public void onClick(View v){
            listener.onItemClick(team, match, tourn);
        }

    }

    public interface ItemClickListener{
        public void onItemClick(int team, int match, String tourn);
    }

}

