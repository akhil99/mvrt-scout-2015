package com.mvrt.scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mvrt.scout.adapters.FileListAdapter;
import com.mvrt.scout.view.TabFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Akhil Palla
 */
public class StandLocalFragment extends TabFragment implements FileListAdapter.OptionClickedInterface {

    RecyclerView fileRecycler;
    FileListAdapter fileListAdapter;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_stands_local, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        String[] files = getActivity().fileList();
        fileRecycler = (RecyclerView) v.findViewById(R.id.scout_local_recycler);
        fileRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.local_swipe_refresh);
        fileListAdapter = new FileListAdapter(files, this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFiles();
            }
        });
        fileRecycler.setAdapter(fileListAdapter);
    }


    public void refreshFiles() {
        fileListAdapter.setFiles(getActivity().fileList());
        refreshLayout.setRefreshing(false);
    }

    public String getTitle() {
        return "Local Data";
    }

    @Override
    public void deleteOptionClicked(final String filename) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        AlertDialog dialog = builder.setMessage("You will permanently lose this file and its data")
                .setTitle("Delete scouting record?")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteFile(filename);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        }).create();
        dialog.show();
    }

    @Override
    public void shareOptionClicked(final String filename) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] syncMethods = {"Bluetooth", "Internet Connection"};
        builder.setTitle("Select a way to sync data:")
                .setItems(syncMethods, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            saveBluetooth(filename);
                        }else if(which == 1){
                            saveFirebase(filename);
                        }
                    }
                }).setCancelable(true);
        Dialog d = builder.create();
        d.show();
    }

    public void deleteFile(String filename) {
        File f = getActivity().getFileStreamPath(filename);
        if(f.delete()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Successfully deleted file: " + filename, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity().getApplicationContext(),
                    "Couldn't delete file: " + filename, Toast.LENGTH_SHORT).show();
        }
        refreshFiles();
    }

    public void saveFirebase(String filename) {
        File f = getActivity().getFileStreamPath(filename);
        if(!f.exists())return;
        if(isInternetConnected()){
            try {
                FileInputStream fis = new FileInputStream(f);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                JSONObject record = new JSONObject(new String(buffer));
                Firebase dataRef = new Firebase("https://scouting115.firebaseio.com/data");
                int team = record.getInt("team");
                int match = record.getInt("match");
                String tourn = record.getString("tournament");
                dataRef.child(getPath(team, match, tourn)).setValue(toMap(record));
                f.delete();
                refreshFiles();
            } catch (IOException e) {
                Log.e("MVRT", "IOException");
            } catch (JSONException e) {
                Log.e("MVRT", "JSONException");
            }
        }
        else {
            Toast.makeText(getActivity(), "No data connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveBluetooth(String filename) {
        File f = getActivity().getFileStreamPath(filename);
        if (!f.exists()) return;
        try {
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            //if sending succeeded,
            if (((ScoutBase) getActivity().getApplication()).getBtService().send(buffer)) {
                Toast.makeText(getActivity().getApplicationContext(), "Successfully sent over BT", Toast.LENGTH_SHORT).show();
                f.delete();
                refreshFiles();
            }else{
                Toast.makeText(getActivity().getApplicationContext(), "Couldn't send over BT", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {}
    }

    public boolean isInternetConnected(){
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static Map toMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = jsonObject.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static String getPath(int team, int matchNo, String tourn) {
        return team + "-" + tourn + ":" + matchNo;
    }

}
