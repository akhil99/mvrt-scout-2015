package com.mvrt.scout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mvrt.scout.adapters.BtDeviceListAdapter;
import com.mvrt.scout.view.NavDrawerFragment;

import java.util.List;
import java.util.Set;

public class BluetoothFragment extends NavDrawerFragment implements BtDeviceListAdapter.ConnectListener{

    SwipeRefreshLayout nearbySwipe;
    BtDeviceListAdapter nearbyAdapter;
    ProgressBar btProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bt, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        setupSwipeRefresh();
        setupRecycler();
        setupProgressBar();
        scanDevices();
    }

    private void setupProgressBar(){
        btProgress = (ProgressBar)getView().findViewById(R.id.btProgressBar);
        btProgress.setIndeterminate(true);
        btProgress.setVisibility(View.GONE);
    }

    private void setupSwipeRefresh() {
        nearbySwipe = (SwipeRefreshLayout)getView().findViewById(R.id.nearby_devices_swipe_refresh);
        nearbySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scanDevices();
            }
        });
    }

    public void setupRecycler() {
        nearbyAdapter = new BtDeviceListAdapter(this);
        RecyclerView scanRecycler = (RecyclerView)getView().findViewById(R.id.list_nearby_devices);
        scanRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        scanRecycler.setAdapter(nearbyAdapter);
    }


    /**
     * Scans for nearby bluetooth devices
     */
    public void scanDevices() {
        Log.d("MVRT", "scanning");
        nearbyAdapter.clear();
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for(BluetoothDevice dev: devices){
            nearbyAdapter.add(dev);
        }
    }

    @Override
    public String getTitle() {
        return "Bluetooth Settings";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_bluetooth;
    }

    @Override
    public void connect(BluetoothDevice d) {
        btProgress.setVisibility(View.VISIBLE);
        ((ScoutBase)getActivity().getApplication()).getBtService().connect(d, new BluetoothService.ConnectedListener() {
            @Override
            public void connected(final boolean successful) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btProgress.setVisibility(View.GONE);
                        if(successful) Toast.makeText(getActivity(), "Connected Successfully!", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getActivity(), "Could not connect. Make sure the device is running the app and " +
                                "awaiting connections.", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }
}