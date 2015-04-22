package com.mvrt.superscouter;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Akhil Palla
 */
public class BtService extends Service {

    public static final String START_SERVER = "com.mvrt.scout.btservice.START_FOREGROUND";
    public static final String STOP_SERVER = "com.mvrt.scout.btservice.STOP_FOREGROUND";
    public static final int NOTIF_ID = 11501;
    private static final UUID BT_UUID = UUID.fromString("92541f5f-b6f1-4a35-9856-dd8b5ffb852d");

    private String DEVICE_NAME = "MVRT_SCOUT";
    private String TAG = "com.mvrt.scout.btservice";

    private BluetoothAdapter btAdapter;
    private ServerThread serverThread;
    private ArrayList<ConnectionThread> connThreads;

    private DataManager dataManager;

    private boolean bound;

    Handler handler;

    public BtService(){
        connThreads = new ArrayList<>();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        handler = new Handler();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        dataManager = new DataManager();
        log("Service onCreate");
    }

    public DataManager getDataManager(){
        return dataManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(STOP_SERVER.equals(intent.getAction())){
            if(!bound){
                BluetoothAdapter.getDefaultAdapter().disable();
                stopSelf();
            }else{
                toast("Cannot stop service when super is still running", Toast.LENGTH_SHORT);
            }
        }
        if(START_SERVER.equals(intent.getAction())){
            if(serverThread == null){
                serverThread = new ServerThread();
                serverThread.start();
            }else{
                log("ServerThread already running");
            }

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mvrtsuper);

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

            Intent thisService = new Intent(getApplicationContext(), BtService.class);
            thisService.setAction(STOP_SERVER);
            PendingIntent stop = PendingIntent.getService(getApplicationContext(), 0, thisService, 0);
            NotificationCompat.Action a = new NotificationCompat.Action(R.drawable.ic_bluetooth_purple, "Stop Service", stop);

            Notification notif = new NotificationCompat.Builder(this)
                    .setContentTitle("MVRT Super Scout")
                    .setTicker("MVRT Super Scout")
                    .setContentIntent(pi)
                    .setContentText("Running BT Server")
                    .setSmallIcon(R.drawable.ic_mvrtsuper)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setOngoing(true)
                    .addAction(a)
                    .build();

            startForeground(NOTIF_ID, notif);
        }
        return START_STICKY;
    }

    public void received(String data){
        try {
            dataManager.addMatchData(new JSONObject(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        serverThread.cancel();
        serverThread = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        bound = true;
        log("Bound");
        return new BtServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bound = false;
        log("unbound");
        return true; // ensures onRebind is called
    }

    @Override
    public void onRebind(Intent intent) {
        log("rebound");
        bound = true;
    }

    class ServerThread extends Thread{

        boolean accepting = false;

        public void run(){
            BluetoothServerSocket servSock;
            BluetoothSocket socket;
            log("running serverThread");
            try{
                servSock = btAdapter.listenUsingRfcommWithServiceRecord(DEVICE_NAME, BT_UUID);
                accepting = true;
            }catch(IOException e){
                log("Socket listen failed");
                return;
            }

            while(accepting){
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = servSock.accept();
                    log("Successful accept");
                    // If a connection was accepted, start a connected thread with it
                    if (socket != null) {
                        synchronized (BtService.this) {
                            toast("Device: " + socket.getRemoteDevice().getName() + " connected", Toast.LENGTH_LONG);
                            ConnectionThread thread = new ConnectionThread(socket);
                            connThreads.add(thread);
                            thread.start();
                            log("Added devce: " + socket.getRemoteDevice().getName());
                            log("connThreads size: " + connThreads.size());
                        }
                    }
                } catch (IOException e) {
                    logError("Socket accept failed");
                    break;
                }
            }
        }

        public void cancel(){
            accepting = false;
            synchronized (BtService.this){
                for(ConnectionThread t:connThreads)t.cancel();
                serverThread = null;
            }
        }
    }

    private class ConnectionThread extends Thread {

        private final OutputStream outputStream;
        private final InputStream inputStream;

        private final BluetoothSocket btSocket;

        public ConnectionThread(BluetoothSocket sock) {
            btSocket = sock;
            OutputStream tmpOut = null;
            InputStream tmpIn = null;
            try {
                tmpOut = btSocket.getOutputStream();
                tmpIn = btSocket.getInputStream();
            } catch (IOException e) {
                logError("sockets not created");
            }
            outputStream = tmpOut;
            inputStream = tmpIn;
        }

        public void run() {
            while (btSocket.isConnected()) {
                byte[] buffer = new byte[10000];
                try {
                    int length = inputStream.read(buffer);
                    String s = new String(buffer, 0, length);
                    log("Recieved in BTService Super: " + s);
                    received(s);
                } catch (IOException e) {
                    logError("IOException in recieving data from bt socket");
                }
            }
            log("Connection closed");
            // Reset the ConnectedThread once we're done
            synchronized (BtService.this) {
                connThreads.remove(this);
            }
        }

        public void write(byte[] buffer) {
            if (!btSocket.isConnected()) return;
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                logError("Exception during write");
            }
        }

        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) {
                logError("closing connected socket failed");
            }
            synchronized (BtService.this) {
                connThreads.remove(this);
            }
        }
    }

    public class BtServiceBinder extends Binder{
        BtService getService(){
            return BtService.this;
        }
    }

    public void sendAll(String msg){
        for(ConnectionThread conn:connThreads){
            conn.write(msg.getBytes());
        }
    }

    public void toast(final String msg, final int length){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, length).show();
            }
        });
    }

    public void log(final String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, msg);
            }
        });
    }

    public void logError(final String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, msg);
            }
        });
    }

}
