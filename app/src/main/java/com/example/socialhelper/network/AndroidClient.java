package com.example.socialhelper.network;

import android.util.Log;

import com.example.socialhelper.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class AndroidClient implements Closeable {

    public Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * Check if connection is available, if not, get logs without throwing IOException
     */

        public void connectSocket(String ip, int port){
            int timeout = 2000;
            SocketAddress socketAddress = new InetSocketAddress(ip, port);
            try{
                closeConnection();
                socket = new Socket();
                socket.connect(socketAddress, timeout);
                reader = createReader();
                writer = createWriter();

            }catch (SocketTimeoutException exception){
                Log.e("timeout", "SocketTimeoutException"+ ip +":"+ port);
            }catch (IOException e){
                Log.e("error", "Unable to connect");
            }
        }


    public void closeConnection(){
        if (socket != null && !socket.isClosed()){
            try{
                socket.close();
            }catch (IOException e){
                Log.e("Error", "Failed to close socket");
            } finally {
                socket = null;
            }
        }
    }



    //Here i create BReader(InputStream)
    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    //OutputStream
    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    //Reading/writing a String line
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Write message to server to recognise, how has connected
    public void writeLine(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Log.e("Error", "Unable to connect to the server");
        }
    }

    //Reading/Writing an int value

    //get int
    public int read() {
        try {
            return reader.read();
        } catch (IOException e) {
            Log.e("Error", "Server is not respond");
            return -1;
        }
    }
    public void write(int value) {
        try {
            writer.write(value);
            writer.flush();
        } catch (IOException e) {
            Log.e("Error", "Unable to connect to the server");
        }
    }

    //Writing userRegData as a consequence of three Strings
    public void writeUserData(String userType, String FIO, int password) {
        try {
            writer.write(userType);
            writer.newLine();
            writer.flush();
            writer.write(FIO);
            writer.newLine();
            writer.flush();
            writer.write(password);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Log.e("Error", "Unable to connect to the server");
        }
    }
    //Reading userRegData as a String[]
    public String[] readUserData()  {
        try {
            String[] userData = new String[]{reader.readLine(), reader.readLine(), reader.readLine()};
            return userData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Reading Login, MetroStation and wheelChair invalid's commentary as a String[].
    public String[] readHelp() {
        try {
            String[] userData = new String[]{reader.readLine(), reader.readLine(), reader.readLine()};
            return userData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override //close all streams
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
