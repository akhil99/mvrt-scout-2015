package com.mvrt.superscouter;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mvrt.superscouter.adapters.BtDeviceAdapter;
import com.mvrt.superscouter.view.NavDrawerFragment;

/**
 * @author Akhil Palla
 */
public class BTFragment extends NavDrawerFragment {

    private SwitchCompat acceptSwitch;
    private TextView btLabel;

    private BtDeviceAdapter connectedDevicesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bt, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){

        RecyclerView connectedDevicesView = (RecyclerView)v.findViewById(R.id.list_connected_devices);
        connectedDevicesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        connectedDevicesAdapter = new BtDeviceAdapter();
        connectedDevicesView.setAdapter(connectedDevicesAdapter);

        acceptSwitch = (SwitchCompat)v.findViewById(R.id.acceptState);
        acceptSwitch.setOnCheckedChangeListener(acceptListener);
        acceptSwitch.setChecked(true);

        btLabel = (TextView)v.findViewById(R.id.bt_settings_deviceid);
        String name = BluetoothAdapter.getDefaultAdapter().getName();
        btLabel.setText("Device ID: " + name);
    }

    @Override
    public String getTitle() {
        return "Bluetooth Settings";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_bluetooth_purple;
    }


    private CompoundButton.OnCheckedChangeListener acceptListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("MVRT", "switchChanged to: " + isChecked);
            if(isChecked)((MainActivity)getActivity()).startBtService();
            else ((MainActivity)getActivity()).stopBtService();
        }
    };

}
