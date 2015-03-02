package com.mvrt.scout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothService {

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private BluetoothAdapter btAdapter;

    private static final UUID mUUID = UUID.fromString("92541f5f-b6f1-4a35-9856-dd8b5ffb852d");
    private static final String TAG = "BluetoothService";

    public BluetoothService(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized void connect(BluetoothDevice device){
        Log.i(TAG, "Connecting to: " + device);
        if(connectedThread != null) {
            alreadyConnected();
        }else if(connectThread != null){
            alreadyConnecting();
        }else{
            connectThread = new ConnectThread(device);
            connectThread.start();
        }
    }

    private class ConnectThread extends Thread{

        private final BluetoothSocket btSocket;

        public ConnectThread(BluetoothDevice dev){
            BluetoothSocket tmp = null;
            try{
                tmp = dev.createRfcommSocketToServiceRecord(mUUID);
            }catch(IOException e){
                Log.e(TAG, "socket creation failed", e);
            }
            btSocket = tmp;
        }

        public void run(){
            Log.i(TAG, "Begin ConnectThread");
            btAdapter.cancelDiscovery();

            try {
                // This is a blocking call and will only return on a successful connection or an exception
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close socket during connection failure", e2);
                    return;
                }
                connectionFailed();
                return;
            }

            //TODO: Tell the activity that we've connected
            connectedThread = new ConnectedThread(btSocket);
            connectedThread.start();

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                connectThread = null;
            }
        }

        public void cancel() {
            try {
                //first close the connectedThread, if it's running
                if(connectedThread != null) connectedThread.cancel();
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "closing connect socket failed", e);
            }
        }
    }

    public synchronized void sendData(byte[] data){
        if(connectedThread != null){
            connectedThread.write(data);
        }
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
                byte[] buffer = new byte[100];
                try {
                    int length = inputStream.read(buffer);
                    String s = new String(buffer, 0, length);
                    Log.d(TAG, "RECIEVED length: " + length + ", data:  " + s);
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
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "closing connected socket failed", e);
            }
        }
    }

    public boolean isConnected(){
        return connectedThread != null;
    }

    private void connectionFailed() {
        //TODO: Send a failure message back to the Activity
    }

    private void alreadyConnected() {
        //TODO: Send a failure message back to the Activity
    }

    private void alreadyConnecting(){
        //TODO: Make a toast saying the device is already trying to connect
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stopping all BT threads");
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

}