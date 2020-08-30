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

    /**
     *  True bruh moment
     */
    private Socket socket = new Socket();
    private BufferedReader reader;
    private BufferedWriter writer;
    public boolean isAlive;

    /**
     * Now we use default constructor
     */

    //Client constructor(its for my debugging and tests)
//    public ReadWrite() {
//        try {
//            this.reader = createReader();
//            this.writer = createWriter();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    /**
     * Check if connection is available, if not, get logs without throwing IOException
     */
    public void connectSocket(String ip, int port){
        int timeout = 2000;
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        try{
            //make connection
            socket.connect(socketAddress, timeout);
            isAlive = true;
            reader = createReader();
            writer = createWriter();

        }catch (SocketTimeoutException exception){
//            socket.close();
            Log.e("timeout", "SocketTimeoutException"+ ip +":"+ port);
            isAlive = false;
        }catch (IOException e){
//            socket.close();
            Log.e("error", "Unable to connect");
            isAlive = false;
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
