package com.mvrt.scout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Lee Mracek on 10/22/14.
 */
public class CreateRecordActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    public static final int NUM_PAGES = 2;
    public static final int NUM_PREGAME = 0;
    public static final int NUM_AUTO = 1;

    private DataCollectionFragment[] fragmentList;
    private int currentFragment;

    private DisableablePager disableablePager;

    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disable_pager);

        fragmentList = new DataCollectionFragment[NUM_PAGES];
        fragmentList[NUM_PREGAME] = new PregameFragment();
        fragmentList[NUM_AUTO] = new AutoFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        disableablePager = (DisableablePager) findViewById(R.id.disable_pager);
        disableablePager.setOnPageChangeListener(this);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        disableablePager.setAdapter(pagerAdapter);
        onPageSelected(currentFragment);

        setTitle(getTitleFromPosition(NUM_PREGAME));
    }


    public void startAuto(View view) {
        disableablePager.setCurrentItem(NUM_AUTO, true);
    }

    public void grabInitials(final View view) {
        EditText[] teams = {
            (EditText) findViewById(R.id.team_number_1),
            (EditText) findViewById(R.id.team_number_2),
            (EditText) findViewById(R.id.team_number_3) };

        boolean cont = false;

        for (EditText team : teams) {
            if (team.getText().toString().trim().isEmpty() || !team.getText().toString().matches("^[0-9]+$")) { //just in case
                team.setError("The team number cannot be empty");
                cont = true;
            }
        }

        if (cont) return;

        AlertDialog.Builder grabInitials = new AlertDialog.Builder(this);

        grabInitials.setMessage("Please enter your initials");
        grabInitials.setCancelable(true);

        final EditText input = new EditText(this);
        final LinearLayout layout = new LinearLayout(this);

        input.setTextColor(getResources().getColor(R.color.text_primary_dark));
        input.setHintTextColor(getResources().getColor(R.color.text_secondary_dark));

        int padding = (int)(20 * getResources().getDisplayMetrics().density);

        input.setPadding(padding, padding / 2, padding, padding / 2);

        input.setInputType(InputType.TYPE_CLASS_TEXT);

        final View v = findViewById(android.R.id.content);

        layout.setPaddingRelative(padding, 0, padding, 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(input);

        grabInitials.setView(layout);

        grabInitials.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {

                        if (input.getText().toString().isEmpty() || input.getText().toString() == null) {
                            Toaster.burnToast("You have to enter your initials!", Toaster.TOAST_SHORT);
                        } else if (input.getText().toString().length() >= 2) {
                            ((ScoutBase)getApplication()).getDataManager().setScoutInitials(input.getText().toString());
                            startAuto(view);
                        }

                        ((InputMethodManager) ScoutBase.getAppContext().getSystemService(ScoutBase.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(input.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                });

        grabInitials.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ((InputMethodManager) ScoutBase.getAppContext().getSystemService(ScoutBase.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        grabInitials.show();
    }

    public String getTitleFromPosition(int i) {
        switch (i) {
            case NUM_PREGAME:
                return "Match " + ((ScoutBase)getApplication()).getDataManager().getCurrentMatchNumber();
            case NUM_AUTO:
                return "Autonomous Scouting";
            default:
                return "Scouter 2014";
        }
    }

    @Override
    public void onPageSelected(int i) {

        fragmentList[currentFragment].getDataFromUI(); //sync data
        setTitle(getTitleFromPosition(i));
        currentFragment = i;
        if (currentFragment == NUM_PREGAME) {
            disableablePager.setPagingEnabled(false);
        } else {
            disableablePager.setPagingEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (disableablePager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            disableablePager.setCurrentItem(disableablePager.getCurrentItem() - 1, true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position >= fragmentList.length)position = fragmentList.length - 1;
            currentFragment = position;
            return fragmentList[position];
        }

        @Override
        public int getCount() {
            return fragmentList.length;
        }
    }
}
