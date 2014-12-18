package com.mvrt.scout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Handles the syncing and storage of data
 * @author Akhil Palla
 * @author Lee Mracek
 */
public class DataManager {

    SharedPreferences preferences;
    public final String PREFERENCES_SCOUT_KEY = "scoutid";

    private ArrayList<Match> matchSchedule;

    private ArrayList<ScoutingRecord>records;
    private ScoutingRecord currentRecord;

    private int scoutID = 1;
    private int currentMatch = 1;
    private String scoutInitials = "";

    public DataManager(SharedPreferences prefs) {
        this(1, prefs);
    }

    public DataManager(int currentMatch, SharedPreferences prefs) {

        currentRecord = new ScoutingRecord();
        matchSchedule = new ArrayList<Match>();
        records = new ArrayList<ScoutingRecord>();

        setCurrentMatch(currentMatch);

        preferences = prefs;

        scoutID = preferences.getInt(PREFERENCES_SCOUT_KEY, 1);
        if (scoutID < 1 || scoutID > 6){
            scoutID = 1;
        }
        setScoutId(scoutID);
    }

    //Handle Data Syncing:

    /**
     * Will be called when the app is about to close, critical data must be saved
     */
    public void saveData() {
        //TODO: Add code to sync to file/bluetooth
    }

    public void saveRecord() {
        currentRecord.setScouterInitials(scoutInitials);
        currentRecord.setMatch(getMatch(currentMatch));
        records.add(currentRecord);
        currentRecord = new ScoutingRecord();
    }

    public ArrayList<ScoutingRecord> getRecords() {
        return records;
    }

    public ScoutingRecord getCurrentRecord() {
        return currentRecord;
    }

    //Match Data getting/setting

    /**
     * Gets the match with the specified match number
     * @param matchNumber: The number/id of the match (starting with 1)
     * @return the corresponding Match object to the matchNumber
     */
    public Match getMatch(int matchNumber) {
        matchNumber--; //convert from 1-indexed to 0-indexed
        if(matchSchedule.size() <= matchNumber) {
            return new Match(matchNumber);
        }
        return matchSchedule.get(matchNumber);
    }

    public void setMatch(Match m) {
        matchSchedule.set(m.getMatchNumber() - 1, m);
    }

    public Match getCurrentMatch() {
        return getMatch(currentMatch);
    }

    public int getCurrentMatchNumber() { return currentMatch; }

    public void setCurrentMatch(int match) {
        currentMatch = match;
        currentRecord.setMatch(getCurrentMatch());
        currentRecord.setTeamNumber(getCurrentMatch().getTeamNumber(scoutID));
    }

    //Scout ID getting/setting

    public int getScoutId() { return scoutID; }

    public void setScoutId(int id) {
        if(id > 6 || id < 1) {
            id = scoutID;
        }
        scoutID = id;
        preferences.edit().putInt(PREFERENCES_SCOUT_KEY, scoutID).commit();
        currentRecord.setTeamNumber(getCurrentMatch().getTeamNumber(scoutID));
    }

    //Get,set initials

    public String getScoutInitials() { return scoutInitials; }

    public void setScoutInitials(String initials) {
        scoutInitials = initials;
    }

    //Load match schedule from remotes:

    public void getSchedule(boolean successToast) {
        ConnectivityManager connManager = (ConnectivityManager) ScoutBase.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();

        File schedule = new File(ScoutBase.getAppContext().getFilesDir(), "qualificationSchedule.json");
        Log.d(Constants.Logging.HTTP_LOGCAT.getPath(), "Connection: " + String.valueOf(netInfo != null && netInfo.isConnectedOrConnecting()));

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            downloadSchedule(successToast);
        } else if (!schedule.exists() || schedule.length() < 1)
            Toaster.burnToast("No Schedule File Found\nPlease retrieve via Wifi or Bluetooth", Toaster.TOAST_LONG);
        else //Only load schedule if it exists & network doesn't work (downloadSchedule() also handles loading)
            loadScheduleFromFile(schedule);

    }

    public void downloadSchedule(boolean successToast) {
        File schedule = new File(ScoutBase.getAppContext().getFilesDir(), "qualificationSchedule.json"); //TODO: Make this a string constant
        final HTTPDownloader httpDownloader = new HTTPDownloader(ScoutBase.getAppContext());
        httpDownloader.execute(ScoutBase.getAppContext().getString(R.string.schedule_url), "false", successToast?"true":"false");
        Log.i(Constants.Logging.HTTP_LOGCAT.getPath(), "Attempting to initialize file download");
        loadScheduleFromFile(schedule);
    }

    public void loadScheduleFromFile(File schedule) {
        char[] rawRead = new char[(int) schedule.length()];

        try {
            FileReader reader = new FileReader(schedule);
            reader.read(rawRead);
            reader.close();
        } catch (FileNotFoundException e) { }
        catch (IOException e) {}

        loadScheduleFromJSON(String.valueOf(rawRead));
    }

    public void loadScheduleFromJSON(String JSON) {

        matchSchedule = new ArrayList<Match>();
        try {
            JSONObject jsonFile = new JSONObject(JSON);
            JSONArray matchArray = jsonFile.getJSONArray("qualificationSchedule");
            for (int i = 0; i < matchArray.length(); i++) {
                JSONObject matchObject = matchArray.getJSONObject(i);
                Match match = new Match();
                match.setMatchNumber(matchObject.getInt("matchNumber"));
                Log.d(Constants.Logging.MAIN_LOGCAT.getPath(), "" + matchObject.getJSONArray("redAlliance"));
                match.setRedAllianceJSON(matchObject.getJSONArray("redAlliance"));
                match.setBlueAllianceJSON(matchObject.getJSONArray("blueAlliance"));
                matchSchedule.add(match);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //TODO handle stupid exceptions
        }
    }

    private class HTTPDownloader extends AsyncTask<String, Integer, String> {

        boolean progress;
        boolean successToast;
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public HTTPDownloader(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            progress = Boolean.parseBoolean(sUrl[1]);
            successToast = Boolean.parseBoolean(sUrl[2]);
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(new File(ScoutBase.getAppContext().getFilesDir(), "qualificationSchedule.json"));

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Log.e(Constants.Logging.HTTP_LOGCAT.getPath(), "Download error: " + result);
                if (result.split(":")[0].equals("java.net.MalformedURLException") || result.split(":")[0].equals("java.net.UnknownHostException"))
                    Toaster.burnToast("Error downloading the schedule.\nDo you have a wifi connection?", Toaster.TOAST_LONG);
            } else if (successToast) {
                Toaster.makeToast("File downloaded", Toaster.TOAST_SHORT);
            }
        }
    }

}
