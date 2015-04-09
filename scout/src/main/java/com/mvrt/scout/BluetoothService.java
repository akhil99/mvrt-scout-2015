package com.mvrt.scout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Akhil Palla
 */
public class BluetoothService {

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    ArrayList<ConnectedThread> connections;

    private BluetoothAdapter btAdapter;

    OnReceivedListener receivedListener;

    private static final UUID mUUID = UUID.fromString("92541f5f-b6f1-4a35-9856-dd8b5ffb852d");
    private static final String TAG = "BluetoothService";

    public static final int MODE_SCOUT_MASTER = 1;
    public static final int MODE_SCOUT_SLAVE = 2;

    int mode = 0;

    public BluetoothService(int mode){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        connections = new ArrayList<>();
        this.mode = mode;
    }

    /**
     * Connects to a device which is awaiting connections
     * @param device: The device to connect to
     */
    public synchronized void connect(BluetoothDevice device, ConnectedListener listener){
        Log.i(TAG, "Connecting to: " + device);
        if(mode == MODE_SCOUT_MASTER){
            Log.e(TAG, "ERROR: cannot connect while master");
        }else if(connectedThread != null) {
            alreadyConnected();
        }else if(connectThread != null){
            alreadyConnecting();
        }else{
            connectThread = new ConnectThread(device, listener);
            connectThread.start();
        }
    }

    public interface ConnectedListener{
        public void connected(boolean successful);
    }

    /**
     * Connects to a device which is awaiting connections
     */
    private class ConnectThread extends Thread{

        private final BluetoothSocket btSocket;
        private ConnectedListener listener;

        public ConnectThread(BluetoothDevice dev, ConnectedListener listener){
            this.listener = listener;
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
                listener.connected(false);
                return;
            }

            connectedThread = new ConnectedThread(btSocket);
            connectedThread.start();
            listener.connected(true);
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
                    if(receivedListener != null)receivedListener.onReceived(s);
                } catch (IOException e) {
                    Log.e(TAG, "IOException in recieving data from bt socket");
                }
            }
            // Reset the ConnectedThread once we're done
            synchronized (BluetoothService.this) {
                connectedThread = null;
            }
        }

        public boolean write(byte[] buffer) {
            if(!btSocket.isConnected())return false;
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
                return false;
            }
            return true;
        }

        public void cancel(){
            try {
                btSocket.close();
                synchronized (BluetoothService.this){
                    connections.remove(this);
                }
            } catch (IOException e) {
                Log.e(TAG, "closing connected socket failed", e);
            }
        }
    }

    public boolean send(byte[] data){
        if(connectedThread != null && connectedThread.btSocket.isConnected()){
            return connectedThread.write(data);
        }
        return false;
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
        for(ConnectedThread thread:connections){
            thread.cancel();
            connections.remove(thread);
        }
    }

    public interface OnReceivedListener{
        public void onReceived(String s);
    }

    public void setOnReceivedListener(OnReceivedListener listener){
        receivedListener = listener;
    }

}