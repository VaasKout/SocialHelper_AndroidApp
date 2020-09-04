package com.example.socialhelper.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothClient implements Closeable {
    public BluetoothSocket btSocket = null;
    public BluetoothAdapter btAdapter = null;
    private OutputStream outStream = null;
    private InputStream inputStream = null;

    // SPP UUID сервиса
    private static final UUID MY_UUID = UUID.fromString("00001112-0000-1000-8000-00805F9B34FB");

    public static final int REQUEST_ENABLE_BT = 1;

    public void findAdapter() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null){
            Log.e("error", "Bluetooth adapter is not available");
        }
    }

    public void createConnection(){
        // MAC-адрес Bluetooth модуля
        String address = "DC:A6:32:85:43:61";
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            closeConnection();
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e("error", "raspberry socket is not available");
        }
            btAdapter.cancelDiscovery();

        if (btSocket != null){
            try{
                btSocket.connect();
                outStream = btSocket.getOutputStream();
                inputStream = btSocket.getInputStream();

            } catch (IOException e){
                closeConnection();
                Log.e("error", "Unable to connect to raspberry");
            }
        }
    }

    public void sendData(int msg){
        if (btSocket.isConnected()){
        try {
            outStream.write(msg);
            } catch (IOException e){
                Log.e("error", "Unable to send message to raspberry");
            }
        }
    }

    public int receiveData(){
        if (btSocket.isConnected()){
            try {
                return inputStream.read();
            } catch (IOException e){
                Log.e("error", "Unable to read message from raspberry");
                return 0;
            }
        } else return 0;
    }

    public void closeConnection(){
        if (btSocket != null && btSocket.isConnected()){
            try{
                btSocket.close();
            }catch (IOException e){
                Log.e("Error", "Failed to close socket");
            } finally {
                btSocket = null;
            }
        }
    }

    @Override
    public void close() throws IOException {
        btSocket.close();
        outStream.close();
        inputStream.close();
    }
}
