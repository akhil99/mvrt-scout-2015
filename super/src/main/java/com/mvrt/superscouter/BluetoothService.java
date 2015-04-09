package com.mvrt.superscouter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

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
public class BluetoothService {

    private AcceptThread mAcceptThread;
    private ConnectedThread connectedThread;

    ArrayList<ConnectedThread> connections;

    private BluetoothAdapter btAdapter;
    private DataManager dataManager;

    private static final UUID mUUID = UUID.fromString("92541f5f-b6f1-4a35-9856-dd8b5ffb852d");
    private String DEVICE_NAME = "MVRT_SCOUT";
    private static final String TAG = "BluetoothService";

    private boolean accepting = false;

    public static final int MODE_SCOUT_MASTER = 1;
    public static final int MODE_SCOUT_SLAVE = 2;

    int mode = 0;

    public BluetoothService(int mode, DataManager manager){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        connections = new ArrayList<>();
        dataManager = manager;
        this.mode = mode;
    }

    public interface ConnectionListener{
        public void onConnected(BluetoothDevice d);
        public void onDisconnected(BluetoothDevice d);
    }

    ConnectionListener connListener;

    public void addConnectionListener(ConnectionListener listener){
        connListener = listener;
    }

    public void removeConnectionListener(ConnectionListener listener){
        connListener = null;
    }


    private class ConnectedThread extends Thread{

        private final OutputStream outputStream;
        private final InputStream inputStream;

        private final BluetoothSocket btSocket;

        public ConnectedThread(BluetoothSocket sock){
            btSocket = sock;
            OutputStream tmpOut = null;
            InputStream tmpIn = null;
            try {
                tmpOut = btSocket.getOutputStream();
                tmpIn = btSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "sockets not created", e);
            }
            outputStream = tmpOut;
            inputStream = tmpIn;
        }

        public void run(){
            while(btSocket.isConnected()){
                byte[] buffer = new byte[10000];
                try {
                    int length = inputStream.read(buffer);
                    String s = new String(buffer, 0, length);
                    Log.d(TAG, "RECIEVED length: " + length + ", data:  " + s);
                    received(s);
                } catch (IOException e) {
                    Log.e(TAG, "IOException in recieving data from bt socket");
                }
            }
            // Reset the ConnectedThread once we're done
            synchronized (BluetoothService.this) {
                connectedThread = null;
            }
        }

        public void write(byte[] buffer) {
            if(!btSocket.isConnected())return;
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel(){
            try {
                if(connListener != null)connListener.onDisconnected(btSocket.getRemoteDevice());
                btSocket.close();
                synchronized (BluetoothService.this){
                    connections.remove(this);
                }
            } catch (IOException e) {
                Log.e(TAG, "closing connected socket failed", e);
            }
        }
    }

    /**
     * Starts accepting incoming connections
     * @param accept: Whether or not to accept connections
     */
    public synchronized void acceptConnections(boolean accept){
        Log.i(TAG, accept?"Accepting connections":"Stopped accepting connectons");
        if(accept){
            if(mAcceptThread == null || !mAcceptThread.isAlive()){
                Log.d("MVRT", "starting new thread");
                mAcceptThread = new AcceptThread();
                mAcceptThread.start();
            }
        }else{
            if(mAcceptThread != null)mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    /**
     * Listens for incoming connections, and calls a thread to manage them
     */
    private class AcceptThread extends Thread{

        final BluetoothServerSocket btServerSock;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try{
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(DEVICE_NAME, mUUID);
            }catch(IOException e){
                Log.e(TAG, "Socket listen failed", e);
            }
            btServerSock = tmp;
        }

        public void run(){
            accepting = true;
            BluetoothSocket socket;
            Log.d(TAG, "Begin mAcceptThread" + this);
            //keep waiting for connections until cancel() is called
            while(accepting){
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = btServerSock.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket accept failed", e);
                    break;
                }

                // If a connection was accepted, start a connected thread with it
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        if(connListener != null)connListener.onConnected(socket.getRemoteDevice());
                        ConnectedThread t = new ConnectedThread(socket);
                        connections.add(t);
                        t.start();
                    }
                }
            }
        }

        public void cancel(){
            accepting = false;
        }
    }

    public void writeToAll(byte[] data){
        for(ConnectedThread conn:connections){
            conn.write(data);
        }
    }

    public void writeToAll(String data){
        writeToAll(data.getBytes());
    }

    public void received(String data){
        try {
            dataManager.addMatchData(new JSONObject(data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stopping all BT threads");
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        for(ConnectedThread thread:connections){
            thread.cancel();
            connections.remove(thread);
        }
    }

}