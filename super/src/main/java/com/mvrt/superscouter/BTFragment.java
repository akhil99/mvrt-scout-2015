package com.mvrt.superscouter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
    private SwitchCompat discoverSwitch;
    private TextView btLabel;

    private BluetoothService btService;

    private BtDeviceAdapter connectedDevicesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bt, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        btService = ((SuperScoutBase)getActivity().getApplication()).getBtService();

        RecyclerView connectedDevicesView = (RecyclerView)v.findViewById(R.id.list_connected_devices);
        connectedDevicesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        connectedDevicesAdapter = new BtDeviceAdapter();
        connectedDevicesView.setAdapter(connectedDevicesAdapter);

        acceptSwitch = (SwitchCompat)v.findViewById(R.id.acceptState);
        acceptSwitch.setOnCheckedChangeListener(acceptListener);
        acceptSwitch.setChecked(false);

        discoverSwitch = (SwitchCompat)v.findViewById(R.id.discoverState);
        discoverSwitch.setOnCheckedChangeListener(discoverListener);
        discoverSwitch.setChecked(false);

        btLabel = (TextView)v.findViewById(R.id.bt_settings_deviceid);
        String name = BluetoothAdapter.getDefaultAdapter().getName();
        btLabel.setText("Device ID: " + name);
    }

    @Override
    public String getTitle() {
        return "Bluetooth";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_bluetooth;
    }

    /**
     * Callback (called from MainActivity)
     */
    public void isDiscoverable(boolean state){
        Log.d("MVRT", "setChecked: " + state);
        discoverSwitch.setChecked(state);
    }

    private void discoverable(boolean discover){
        ((MainActivity)getActivity()).discoverable(discover);
    }

    private void acceptConnections(boolean accept){
        if(btService != null)btService.acceptConnections(accept);
        if(!accept)connectedDevicesAdapter.clear();
    }

    private CompoundButton.OnCheckedChangeListener acceptListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("MVRT", "switchChanged to: " + isChecked);
            acceptConnections(isChecked);
        }
    };

    private CompoundButton.OnCheckedChangeListener discoverListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("MVRT", "switchChanged to: " + isChecked);
            discoverable(isChecked);
        }
    };

}
