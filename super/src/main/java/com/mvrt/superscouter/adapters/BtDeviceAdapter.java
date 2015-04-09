package com.mvrt.superscouter.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvrt.superscouter.R;

import java.util.ArrayList;

public class BtDeviceAdapter extends RecyclerView.Adapter<BtDeviceAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> deviceList;

    public void clear(){
        deviceList.clear();
        notifyDataSetChanged();
    }

    public void add(BluetoothDevice d) {
        if(d != null) deviceList.add(d);
        notifyDataSetChanged();
    }

    public void remove(BluetoothDevice d){
        deviceList.remove(d);
        notifyDataSetChanged();
    }

    public BtDeviceAdapter( ) {
        deviceList = new ArrayList<BluetoothDevice>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_btdevice, parent, false);
     ViewHolder vh = new ViewHolder(v);
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


    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView deviceName;
        public TextView deviceMAC;
        public BluetoothDevice device;

        public ViewHolder(View v) {
            super(v);
            deviceName = (TextView)v.findViewById(R.id.deviceName_textview);
            deviceMAC = (TextView)v.findViewById(R.id.deviceMAC_textview);
        }

        public void setDevice(BluetoothDevice d){
            deviceName.setText(d.getName());
            deviceMAC.setText(d.getAddress());
            device = d;
        }
    }

}

