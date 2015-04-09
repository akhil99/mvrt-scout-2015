package com.mvrt.scout.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvrt.scout.R;
import com.mvrt.scout.view.NavDrawerFragment;

import java.util.ArrayList;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private ArrayList<NavDrawerFragment> fragments;
    private NavItemClickListener listener;

    public interface NavItemClickListener{

        public void onClick(int pos);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.rowText);
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(getPosition());
        }
    }

    public NavDrawerAdapter(NavItemClickListener listener, ArrayList<NavDrawerFragment> fragments){
        this.fragments = fragments;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {
            holder.textView.setText(fragments.get(position).getTitle());
            holder.imageView.setImageResource(fragments.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}