package com.mvrt.scout;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.mvrt.scout.adapters.FileListAdapter;
import com.mvrt.scout.view.TabFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Akhil Palla
 */
public class StandLocalFragment extends TabFragment implements FileListAdapter.OptionClickedInterface{

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
        fileRecycler = (RecyclerView)v.findViewById(R.id.scout_local_recycler);
        fileRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.local_swipe_refresh);
        fileListAdapter = new FileListAdapter(files, this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFiles();
            }
        });
        fileRecycler.setAdapter(fileListAdapter);
    }

    public String getTitle(){
        return "Local Data";
    }

    @Override
    public void btSyncOptionClicked(String filename) {
        File f = getActivity().getFileStreamPath(filename);
        if(!f.exists())return;
        try {
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            //if sending succeeded,
            if(((ScoutBase)getActivity().getApplication()).getBtService().send(buffer)){
                f.delete();
                refreshFiles();
            }

        }catch(IOException e){}
    }

    @Override
    public void cloudSyncOptionClicked(String filename){
        File f = getActivity().getFileStreamPath(filename);
        if(!f.exists())return;
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
        }catch(IOException e){
            Log.e("MVRT", "IOException");
        }catch (JSONException e){
            Log.e("MVRT", "JSONException");
        }
    }

    private static Map toMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = jsonObject.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = jsonObject.get(key);

            if(value instanceof JSONObject) {
                value = toMap((JSONObject)value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static String getPath(int team, int matchNo, String tourn){
        return team + "-" + tourn + ":" + matchNo;
    }

    public void refreshFiles(){
        fileListAdapter.setFiles(getActivity().fileList());
        refreshLayout.setRefreshing(false);
    }

}
