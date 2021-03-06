package com.example.socialhelper.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.socialhelper.ui.PregnantFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class BluetoothClient implements Closeable {
    public BluetoothSocket btSocket = null;
    public BluetoothAdapter btAdapter = null;
    private BufferedWriter writer = null;
    private BufferedReader reader = null;

    /**
     * @see PregnantFragment
     */

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int REQUEST_ENABLE_BT = 1;

    //InputStream
    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
    }

    //OutputStream
    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(btSocket.getOutputStream()));
    }

    /**
     * Get bluetooth adapter on android
     */

    public void findAdapter() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Log.e("error", "Bluetooth adapter is not available");
        }
    }

    /**
     * Connection to specific remote device (arduino with bluetooth)
     */

    public void createConnection() {
        if (btAdapter != null) {
            // MAC-address Bluetooth module
            String address = "98:D3:11:F8:72:6B";
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            try {
//                closeConnection();
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e("error", "raspberry socket is not available");
            }
            btAdapter.cancelDiscovery();

            if (btSocket != null) {
                try {
                    btSocket.connect();
                    writer = createWriter();
                    reader = createReader();
                } catch (IOException e) {
                    closeConnection();
                    Log.e("error", "Unable to connect to arduino");
                }
            }
        }
    }

    /**
     * Send data by bluetooth
     * @param msg
     */

    public void sendData(String msg) {
        if (btSocket.isConnected()) {
            writeLine(msg);
        }
    }

    /**
     * Close connection to arduino
     */

    public void closeConnection() {
        if (btSocket != null && btSocket.isConnected()) {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e("Error", "Failed to close socket");
            } finally {
                btSocket = null;
            }
        }
    }

    /**
     * Method is used to simplify to write message in OutputStream
     * @param message
     */

    public void writeLine(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Log.e("Error", "Unable to write message to the arduino");
        }
    }

    /**
     * Closeable closes all streams and sockets after the instance of class is destroyed
     * @throws IOException
     */

    @Override
    public void close() throws IOException {
        btSocket.close();
        writer.close();
        reader.close();
    }
}
