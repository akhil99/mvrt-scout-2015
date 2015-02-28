package com.mvrt.superscouter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothService {

    private AcceptThread mAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    ArrayList<ConnectedThread> connections;

    private BluetoothAdapter btAdapter;

    private static final UUID mUUID = UUID.fromString("92541f5f-b6f1-4a35-9856-dd8b5ffb852d");
    private String DEVICE_NAME = "MVRT_SCOUT";
    private static final String TAG = "BluetoothService";

    private boolean accepting = false;

    public static final int MODE_SCOUT_MASTER = 1;
    public static final int MODE_SCOUT_SLAVE = 2;

    int mode = 0;

    public BluetoothService(int mode){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        connections = new ArrayList<>();
        this.mode = mode;
    }

    public interface OnReceivedListener{
        public void onReceived(String data);
    }

    OnReceivedListener listener;

    public void addOnReceivedListener(OnReceivedListener listener){
        this.listener = listener;
    }

    public void removeOnReceivedListener(){
        listener = null;
    }

    /**
     * Connects to a device which is awaiting connections
     * @param device: The device to connect to
     */
    public synchronized void connect(BluetoothDevice device){
        Log.i(TAG, "Connecting to: " + device);
        if(mode == MODE_SCOUT_MASTER){
            Log.e(TAG, "ERROR: cannot connect while master");
        }else if(connectedThread != null) {
            alreadyConnected();
        }else if(connectThread != null){
            alreadyConnecting();
        }else{
            connectThread = new ConnectThread(device);
            connectThread.start();
        }
    }

    /**
     * Connects to a device which is awaiting connections
     */
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
                    if(listener != null)listener.onReceived(s);
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
            if(mAcceptThread == null)mAcceptThread = new AcceptThread();
            mAcceptThread.start();
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

    public void sendToAll(JSONObject obj){
        writeToAll(obj.toString().getBytes());
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