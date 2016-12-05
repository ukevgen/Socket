/**
 * Created by ukevgen on 07.11.2016.
 */

import java.net.*;
import java.io.*;

public class Client {

    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Socket socket;
    private String server;
    private int port;


    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public boolean start() {
        try {
            socket = new Socket(server, port);
        } catch (Exception e) {
            display("Error connecting to server:" + e);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        try {
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            display("creat out/in");
        } catch (Exception e) {
            display("Exception creating new Input/output Streams: " + e);
            return false;
        }
        new ListenFromServer().start();
        return true;
    }


    private void display(String s) {
        System.out.println(s);
    }

    /**
     * To send a message to the server
     */
    void sendMessage(String msg) {
        try {
            dataOut.writeUTF(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /**
     * Close the Input/Output streams
     */
    public void disconnect() {
        try {
            if (dataIn != null) dataIn.close();
        } catch (Exception e) {
        }
        try {
            if (dataOut != null) dataOut.close();
        } catch (Exception e) {
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
        }
        display("Logout");
    }

    /**
     * Send ping to server
     */
    public void alive() {

        try {
            if (socket == null)
                return;
            InetAddress inet = InetAddress.getByName(server);
            if (inet.isReachable(5000)) {
                String msg = String.valueOf(socket.getLocalSocketAddress());
                display("Socket stile alive " + msg);
            }
        } catch (SocketException e) {
            display("Socket " + e);
        } catch (UnknownHostException e) {
            display("Host " + e);
        } catch (IOException e) {
            display("IO " + e);
        }
    }

    /**
     * a class that waits for the message from the server
     */
    class ListenFromServer extends Thread {
        public void run() {
            while (true) {
                try {
                    String msg = dataIn.readUTF();
                    display(msg);
                } catch (IOException e) {
                    display("Server has close the connection: ");
                    break;
                } finally {
                    display("Select a command..");
                }
            }
        }
    }
}

