package com.mvrt.superscouter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvrt.superscouter.R;

import java.util.ArrayList;

public class RecordDetailListAdapter extends RecyclerView.Adapter<RecordDetailListAdapter.ViewHolder> {

    ArrayList<Entry> entries;

    public RecordDetailListAdapter(){
        entries = new ArrayList<>();
    }

    public void add(Entry e){
        entries.add(e);
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_entry, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(entries.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return entries.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView key;
        public TextView value;

        public ViewHolder(View v) {
            super(v);
            key = (TextView)v.findViewById(R.id.scoutitem_left);
            value = (TextView)v.findViewById(R.id.scoutitem_right);
        }

        public void setData(Entry e){
            key.setText(e.label);
            value.setText(e.value);
        }


    }

    public static class Entry{

        public String key;
        public String label;
        public String value;

        public Entry(String key, String label, String value){
            this.key = key;
            this.label = label;
            this.value = value;
        }
    }

}

