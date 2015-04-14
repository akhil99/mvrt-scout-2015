package com.mvrt.scout;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.mvrt.scout.adapters.NavDrawerAdapter;
import com.mvrt.scout.view.NavDrawerFragment;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements NavDrawerAdapter.NavItemClickListener, BluetoothService.OnReceivedListener {

    private static final int REQUEST_INIT_BT = 1234;
    private static final int REQUEST_SCAN_QR = 2345;


    Toolbar toolbar;
    DrawerLayout drawer;
    ArrayList<NavDrawerFragment> fragments;

    BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        fragments = new ArrayList<>();
        fragments.add(new StandFragment());
        fragments.add(new BluetoothFragment());

        checkBt();

        drawer = (DrawerLayout)findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();

        RecyclerView recycler = (RecyclerView)findViewById(R.id.drawer_recycler);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new NavDrawerAdapter(this, fragments));

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
            Log.d("BT", "enabling bt");
            enableBT();
        } else{
            initBtService();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ((ScoutBase)getApplication()).getBtService().stop();
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
        }else if(requestCode == REQUEST_SCAN_QR && resultCode == RESULT_OK){
            String contents = data.getStringExtra("SCAN_RESULT");
            Log.d("MVRT", "QR contents: " + contents);
            onReceived(contents);
        }
    }

    private void initBtService(){
        ((ScoutBase)getApplication()).initBtService();
        ((ScoutBase)getApplication()).getBtService().setOnReceivedListener(this);
    }

    private void enableBT(){
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(i, REQUEST_INIT_BT);
    }

    @Override
    public void onReceived(final String data){
        Log.d("MVRT", "uri: " + data);
        Uri uri = Uri.parse(data);
        if(uri.getHost().equals("scout.mvrt.com") && uri.getPathSegments().size() > 1 && uri.getPathSegments().get(0).equals("scout")){
            Intent i = new Intent(this, StandScoutActivity.class);
            i.setData(uri);
            startActivity(i);
        }
    }

    public void scanQr(){
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, REQUEST_SCAN_QR);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
            startActivity(marketIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_qr_scan:
                scanQr();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
}
