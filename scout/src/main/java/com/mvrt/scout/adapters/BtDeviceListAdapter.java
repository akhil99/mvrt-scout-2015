package com.mvrt.scout.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvrt.scout.R;

import java.util.ArrayList;

public class BtDeviceListAdapter extends RecyclerView.Adapter<BtDeviceListAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> deviceList;

    public interface ConnectListener{
        public void connect(BluetoothDevice d);
    }

    private ConnectListener listener;

    public void clear(){
        deviceList.clear();
        notifyDataSetChanged();
    }

    public void add(BluetoothDevice d) {
        if(d != null) deviceList.add(d);
        notifyDataSetChanged();
    }

    public BtDeviceListAdapter(ConnectListener listener) {
        this.listener = listener;
        deviceList = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_btdevice, parent, false);
     ViewHolder vh = new ViewHolder(v, listener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setDevice(deviceList.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return deviceList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView deviceName;
        public TextView deviceMAC;
        public BluetoothDevice device;
        ConnectListener listener;

        public ViewHolder(View v, ConnectListener listener) {
            super(v);
            this.listener = listener;
            v.setOnClickListener(this);
            deviceName = (TextView)v.findViewById(R.id.deviceName_textview);
            deviceMAC = (TextView)v.findViewById(R.id.deviceMAC_textview);
        }

        @Override
        public void onClick(View v){
            listener.connect(device);
        }

        public void setDevice(BluetoothDevice d){
            deviceName.setText(d.getName());
            deviceMAC.setText(d.getAddress());
            device = d;
        }
    }

}

