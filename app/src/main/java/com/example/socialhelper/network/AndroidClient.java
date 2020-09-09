package com.example.socialhelper.network;

import android.util.Log;

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

    public void connectSocket(String ip, int port) {
        int timeout = 1000;
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        try {
            closeConnection();
            socket = new Socket();
            socket.connect(socketAddress, timeout);
            reader = createReader();
            writer = createWriter();

        } catch (SocketTimeoutException exception) {
            Log.e("timeout", "SocketTimeoutException" + ip + ":" + port);
        } catch (IOException e) {
            Log.e("error", "Unable to connect");
        }
    }


    public void closeConnection() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
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

    //    //Reading/writing a String line
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //get int
    public int read() {
        try {
            return reader.read();
        } catch (IOException e) {
            Log.e("Error", "Unable to read from server");
            return -1;
        }
    }


    //Writing userRegData as a consequence of three Strings
    public void writeUserData(String userType, String name,
                              String surname, String login, int password, String email) {
        writeLine(userType);
        writeLine(name);
        writeLine(surname);
        writeLine(login);
        write(password);
        writeLine(email);
    }

    public void writePregnantData(int number, String name,
                                  String surname, String login, int password) {
        write(number);
        writeLine(name);
        writeLine(surname);
        writeLine(login);
        write(password);
    }

    public void writeLoginPassword(String login, int password) {
        writeLine(login);
        write(password);
    }

    public void writeRestoreInfo(String login, String post) {
        writeLine(login);
        writeLine(post);
    }

    public void writeLine(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            Log.e("Error", "Unable to write message to the server");
        }
    }

    public void write(int value) {
        try {
            writer.write(value);
            writer.flush();
        } catch (IOException e) {
            Log.e("Error", "Unable to write id");
        }
    }

    @Override //close all streams
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
