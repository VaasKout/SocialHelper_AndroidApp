package com.example.socialhelper.network;

import android.util.Log;

import com.example.socialhelper.viewmodels.LoginViewModel;
import com.example.socialhelper.viewmodels.RegistrationViewModel;
import com.example.socialhelper.viewmodels.RestoreViewModel;
import com.example.socialhelper.viewmodels.WheelChairViewModel;

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

public class NetworkClient implements Closeable {

    /**
     * Network client to write and read data from server
     * it's based on Socket class
     *
     * @see java.net.Socket
     * and two streams with BufferReader and BufferWriter
     * <p>
     * At First, every fragment connects to the server by {@link #connectSocket()} method
     * then, to write data on the server, client have to send String to
     * define which data is going to be transfered, and after it fragment uses specific
     * method to send needed data
     * {@link #writeUserRegData(int, int, String, String, String, String, String)}
     * @see RegistrationViewModel
     * {@link #writeLoginPassword(String, int)}
     * @see LoginViewModel
     * {@link #writeRestoreInfo(String, String)}
     * @see RestoreViewModel
     * {@link #writeWheelchairData(String, String, String, String, String, String, String)}
     * @see WheelChairViewModel
     */

    public static final String IP = "192.168.0.110";
    public static final int PORT = 9000;
    public Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public void connectSocket() {
        int timeout = 1000;
        SocketAddress socketAddress = new InetSocketAddress(IP, PORT);
        try {
            closeConnection();
            socket = new Socket();
            socket.connect(socketAddress, timeout);
            reader = createReader();
            writer = createWriter();

        } catch (SocketTimeoutException exception) {
            Log.e("timeout", "SocketTimeoutException" + IP + ":" + PORT);
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


    //InputStream
    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    //OutputStream
    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            Log.e("error", "Unable to read from server");
            return "";
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


    public void writeUserRegData(int number, int password, String name,
                                 String surname, String login,
                                 String email, String category) {
        write(number);
        write(password);
        writeLine(name);
        writeLine(surname);
        writeLine(login);
        writeLine(email);
        writeLine(category);
    }


    public void writeLoginPassword(String login, int password) {
        writeLine(login);
        write(password);
    }

    public void writeRestoreInfo(String login, String email) {
        writeLine(login);
        writeLine(email);
    }

    public void writeWheelchairData(String login, String name, String surname,
                                    String enter, String exit, String time, String comment) {
        writeLine(login);
        writeLine(name);
        writeLine(surname);
        writeLine(enter);
        writeLine(exit);
        writeLine(time);
        writeLine(comment);
    }

    public void changePassword(int id, int newPassword) {
        write(id);
        write(newPassword);
    }

    public void verify(int key,
                       int ref,
                       String name,
                       String surname, String type) {
        write(key);
        write(ref);
        writeLine(name);
        writeLine(surname);
        writeLine(type);
    }

    /**
     * Methods is used to simplify write messages in OutputSteam
     * and read from InputStream
     *
     * @param message
     */

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

    /**
     * Closeable closes all streams and sockets after the instance of class is destroyed
     *
     * @throws IOException
     */

    @Override //close all streams
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
