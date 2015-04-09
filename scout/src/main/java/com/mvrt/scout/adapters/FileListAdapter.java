package com.mvrt.scout.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mvrt.scout.R;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    String[] filenames;
    OptionClickedInterface optionClickedInterface;

    public FileListAdapter(String[] filenames, OptionClickedInterface optInt) {
        this.filenames = filenames;
        optionClickedInterface = optInt;
    }

    public void setFiles(String[] files){
        filenames = files;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_localdata, parent, false);
        ViewHolder vh = new ViewHolder(v, optionClickedInterface);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setFilename(filenames[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filenames.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView fileName;

        OptionClickedInterface optionClickedInterface;

        public ViewHolder(View v, OptionClickedInterface optInt) {
            super(v);
            fileName = (TextView)v.findViewById(R.id.filename_textview);
            optionClickedInterface = optInt;

            ImageButton wifi = (ImageButton)v.findViewById(R.id.fileview_cloud);
            wifi.setOnClickListener(this);
            ImageButton bt = (ImageButton)v.findViewById(R.id.fileview_bt);
            bt.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            if(v.getId() == R.id.fileview_bt){
                optionClickedInterface.btSyncOptionClicked(fileName.getText().toString());
            }else if(v.getId() == R.id.fileview_cloud){
                optionClickedInterface.cloudSyncOptionClicked(fileName.getText().toString());
            }
        }

        public void setFilename(String filename){
            fileName.setText(filename);
        }
    }

    public interface OptionClickedInterface{
        public void btSyncOptionClicked(String filename);
        public void cloudSyncOptionClicked(String filename);
    }

}

