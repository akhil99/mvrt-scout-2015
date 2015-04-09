package com.mvrt.superscouter;

import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.mvrt.superscouter.adapters.TabPagerAdapter;
import com.mvrt.superscouter.view.SlidingTabLayout;

import java.util.HashMap;

public class MatchScoutActivity extends ActionBarActivity implements View.OnClickListener {

    private SlidingTabLayout slidingTabs;
    TabPagerAdapter adapter;
    ViewPager pager;

    Button finishMatch;

    MatchScoutDataFragment dataFragment;
    MatchScoutCommentFragment commentFragment;

    DataManager dataManager;

    int[] teams;
    int matchNo;
    String tournament;
    String alliance;
    String uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchscout);

        teams = new int[3];
        teams[0] = getIntent().getExtras().getInt("team1");
        teams[1] = getIntent().getExtras().getInt("team2");
        teams[2] = getIntent().getExtras().getInt("team3");
        matchNo = getIntent().getExtras().getInt("matchNo");
        uri = getIntent().getExtras().getString("uri");

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new TabPagerAdapter(getSupportFragmentManager());
        commentFragment = MatchScoutCommentFragment.createInstance(teams);
        adapter.addFragment(commentFragment);
        dataFragment = new MatchScoutDataFragment();
        adapter.addFragment(dataFragment);
        pager.setAdapter(adapter);
        slidingTabs = (SlidingTabLayout)findViewById(R.id.matchscout_slidingtabs);
        slidingTabs.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabs.setViewPager(pager);

        finishMatch = (Button)findViewById(R.id.match_finish);
        finishMatch.setOnClickListener(this);

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, 0);
        alliance = prefs.getString(Constants.PREFS_KEY_ALLIANCE, Constants.ALLIANCE_BLUE);
        tournament = prefs.getString(Constants.PREFS_KEY_TOURNAMENT, "SVR");

        dataManager = ((SuperScoutBase)getApplication()).getDataManager();
    }

    public void onDestroy(){
        super.onDestroy();
        dataManager.setMatchDataAddedListener(null);
    }

    public void onClick(View v){
        if(v.getId() == R.id.match_finish){
            finishMatch();
        }
    }

    private void finishMatch(){
        HashMap<Integer, String> comments = commentFragment.getComments();
        for(int team:comments.keySet()) {
            dataManager.saveCommentToFirebase(team, matchNo, tournament, comments.get(team));
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_matchscout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_matchscout_share_qr:
                showQR();
                return true;
            case R.id.menu_matchscout_share_NFC:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showQR(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.shareText(uri);
    }

}
