package com.mvrt.scout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends ActionBarActivity {

    private final int REQUEST_INIT_BT = 1337;

    BluetoothAdapter btAdapter;

    BluetoothService btService;

    SwipeRefreshLayout nearbySwipe;
    SwipeRefreshLayout pairedSwipe;
    BtRecyclerAdapter nearbyAdapter;
    BtRecyclerAdapter pairedAdapter;
    ProgressBar btProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setupSwipeRefresh();
        setupRecycler();
        setupProgressBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        initBt();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_INIT_BT && resultCode == RESULT_OK) {
            initBt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        btService.stop();
        try {
            unregisterReceiver(mScanReciever); //unregister the scanning reciever
        } catch (IllegalArgumentException e) {}
    }

    private void setupProgressBar(){
        btProgress = (ProgressBar)findViewById(R.id.btProgressBar);
        btProgress.setIndeterminate(true);
        btProgress.setVisibility(View.GONE);
    }

    private void setupSwipeRefresh() {
        nearbySwipe = (SwipeRefreshLayout) findViewById(R.id.nearby_devices_swipe_refresh);
        pairedSwipe = (SwipeRefreshLayout) findViewById(R.id.paired_devices_swipe_refresh);
        pairedSwipe.setColorSchemeColors(getResources().getColor(R.color.primary));
        pairedSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPairedDevices();
            }
        });
        nearbySwipe.setColorSchemeColors(getResources().getColor(R.color.primary));
        nearbySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scanDevices();
            }
        });
    }

    public void setupRecycler() {
        nearbyAdapter = new BtRecyclerAdapter();
        pairedAdapter = new BtRecyclerAdapter();
        RecyclerView pairedRecycler = (RecyclerView) findViewById(R.id.list_paired_devices);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        pairedRecycler.setLayoutManager(layoutManager);
        pairedRecycler.setAdapter(pairedAdapter);
        pairedRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        pairedAdapter.connect(position);
                    }
                }));

        RecyclerView scanRecycler = (RecyclerView) findViewById(R.id.list_nearby_devices);
        final LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        scanRecycler.setLayoutManager(layoutManager2);
        scanRecycler.setAdapter(nearbyAdapter);
        scanRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        nearbyAdapter.connect(position);
                    }
                }));
    }

    private void enableBT(){
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, REQUEST_INIT_BT);
    }

    public void initBt() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) return; //device doesn't support bt
        if (!btAdapter.isEnabled()) { //enable the adapter
            enableBT();
        } else {
            scanDevices();
            loadPairedDevices();
            initBtService();
        }
    }

    private void initBtService(){
        if(btService == null) {
            btService = new BluetoothService();
        }
    }

    public void loadPairedDevices() {
        pairedSwipe.setRefreshing(true);
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        pairedAdapter.clear();
        for (BluetoothDevice device : pairedDevices) {
            if (device != null){
                pairedAdapter.add(device);
            }
        }
        pairedSwipe.setRefreshing(false);
    }

    BroadcastReceiver mScanReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nearbyAdapter.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                unregisterReceiver(mScanReciever);
                nearbySwipe.setRefreshing(false);
            }
        }
    };

    /**
     * Scans for nearby bluetooth devices
     */
    public void scanDevices() {
        nearbySwipe.setRefreshing(true);
        nearbyAdapter.clear();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mScanReciever, filter); // Don't forget to unregister during onDestroy
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mScanReciever, filter);
        btAdapter.startDiscovery();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView deviceName;
        public TextView deviceMAC;
        public BluetoothDevice device;

        public ViewHolder(View v) {
            super(v);
            deviceName = (TextView) v.findViewById(R.id.deviceName_textview);
            deviceMAC = (TextView) v.findViewById(R.id.deviceMAC_textview);
        }

        public void setDevice(BluetoothDevice d) {
            deviceName.setText(d.getName());
            deviceMAC.setText(d.getAddress());
            device = d;
        }
    }

    public class BtRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        private ArrayList<BluetoothDevice> deviceList;

        private void connect(int pos) {
            if (pos >= deviceList.size() || pos < 0) return;
            if(btService != null)btService.connect(deviceList.get(pos));
        }

        public void clear() {
            deviceList.clear();
            notifyDataSetChanged();
        }

        public void add(BluetoothDevice d) {
            if (d != null) deviceList.add(d);
            else Log.d("DEBUG", "NULL");
            notifyDataSetChanged();
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public BtRecyclerAdapter() {
            deviceList = new ArrayList<BluetoothDevice>();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bluetooth_device_view, parent, false);
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
    }

    private void sendData(String data) {
        btService.sendData(data.getBytes());
    }

}