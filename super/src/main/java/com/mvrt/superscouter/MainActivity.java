package com.mvrt.superscouter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.mvrt.superscouter.adapters.NavDrawerAdapter;
import com.mvrt.superscouter.view.NavDrawerFragment;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements NavDrawerAdapter.NavItemClickListener, BluetoothService.ConnectionListener {

    private static final int REQUEST_INIT_BT = 1234;
    private static final int REQUEST_DISCOVERABLE = 3421;
    private static final int REQUEST_UNDISCOVERABLE = 2341;

    Toolbar toolbar;
    DrawerLayout drawer;
    ArrayList<NavDrawerFragment> fragments;

    BluetoothAdapter btAdapter;

    BTFragment btFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        fragments = new ArrayList<>();
        fragments.add(new StandFragment());
        btFrag = new BTFragment();
        fragments.add(btFrag);

        checkBt();

        drawer = (DrawerLayout)findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();

        RecyclerView recycler = (RecyclerView)findViewById(R.id.drawer_recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new NavDrawerAdapter(this, fragments));

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ((SuperScoutBase)getApplication()).getBtService().stop();
    }

    @Override
    public void onClick(int pos) {
        setFragment(pos);
    }

    public void setFragment(int pos){
        drawer.closeDrawers();
        NavDrawerFragment frag = fragments.get(pos);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content_frame, frag).commit();
        toolbar.setTitle(frag.getTitle());
    }

    @Override
    public void onStart() {
        super.onStart();
        setFragment(0);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Log.d("btsuper", "enabling bt");
            enableBT();
        } else{
            initBtService();
        }
    }

    public void checkBt(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_INIT_BT && resultCode == RESULT_OK) {
            initBtService();
        }else if(requestCode == REQUEST_DISCOVERABLE){
            btFrag.isDiscoverable(resultCode == 1);
        }else if(requestCode == REQUEST_UNDISCOVERABLE){
            btFrag.isDiscoverable(resultCode != 1);
        }
    }

    private void initBtService(){
        ((SuperScoutBase)getApplication()).initBtService();
        ((SuperScoutBase)getApplication()).getBtService().addConnectionListener(this);
    }

    private void enableBT(){
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, REQUEST_INIT_BT);
    }

    private boolean isDiscoverable(){
        return BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }

    public void discoverable(boolean discover){
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        if(discover == isDiscoverable()){
            Log.d("MVRT", discover?"Already discoverable":"Already not discoverable");
            return;
        }
        enableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discover?0:1);
        startActivityForResult(enableIntent, discover?REQUEST_DISCOVERABLE:REQUEST_UNDISCOVERABLE);
    }

    @Override
    public void onConnected(final BluetoothDevice d) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Device: " + d.getName() + " connected!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDisconnected(final BluetoothDevice d) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Device: " + d.getName() + " disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
