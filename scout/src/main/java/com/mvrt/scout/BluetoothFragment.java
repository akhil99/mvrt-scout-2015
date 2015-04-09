package com.mvrt.scout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
               scanDevices();
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mScanReciever); //unregister the scanning reciever
        } catch (IllegalArgumentException e) {}
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

    BroadcastReceiver mScanReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("MVRT", "BT found");
                nearbyAdapter.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                getActivity().unregisterReceiver(mScanReciever);
                nearbySwipe.setRefreshing(false);
            }
        }
    };

    /**
     * Scans for nearby bluetooth devices
     */
    public void scanDevices() {
        Log.d("MVRT", "scanning");
        nearbySwipe.setRefreshing(true);
        Log.d("MVRT", "swipe is refreshing: " + nearbySwipe.isRefreshing());
        nearbyAdapter.clear();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mScanReciever, filter); // Don't forget to unregister during onDestroy
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mScanReciever, filter);
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
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