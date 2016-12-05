import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ukevgen on 06.11.2016.
 */

public class Server {

    private static int uniqueId;
    private final SimpleDateFormat sdf;
    private boolean keepGoing;
    private List<ConnectionThread> connections;
    private String temp;
    private String humidity;
    private ServerSocket server;

    /**
     * Create server
     */
    public Server() {
        connections = new ArrayList<ConnectionThread>();
        sdf = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * Add new connection
     */
    public void start() {
        keepGoing = true;
        try {
            server = new ServerSocket(Const.PORT);
            while (keepGoing) {
                display("Server waiting for Clients on port " + Const.PORT + ".");

                Socket socket = server.accept();
                if (!keepGoing)
                    break;
                ConnectionThread con = new ConnectionThread(socket);
                connections.add(con);
                con.start();

            }
            try {
                System.out.println("Close connection");
                server.close();
                for (int i = 0; i < connections.size(); ++i) {
                    ConnectionThread con = connections.get(i);
                    try {
                        con.dataIn.close();
                        con.dataOut.close();
                        con.socket.close();
                    } catch (IOException ioE) {
                    }
                }
            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    /**
     * Display massage
     */
    private void display(String s) {
        String time = sdf.format(new Date()) + " " + s;
        System.out.println(time);
    }

    /**
     * Remove client
     */
    private synchronized void remove(int id) {
        System.out.println("start remove" + id);
        for (int i = 0; i < connections.size(); i++) {
            ConnectionThread ct = connections.get(i);
            if (ct.id == id) {
                connections.remove(i);
                display(String.valueOf(connections.size()));
                return;
            }
        }
    }

    private synchronized int getClientCount() {
        return connections.size();
    }

    /**
     * Send current weather
     */
    private synchronized void broadcast(String temp, String humidity) {
        String msg = sdf.format(new Date()) + " temp = " + temp + " humidity = " + humidity;

        for (int i = 0; i < connections.size(); i++) {
            ConnectionThread ct = connections.get(i);
            if (!ct.sendMassage(msg))
                connections.remove(ct);
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    /**
     * Client class
     */
    private class ConnectionThread extends Thread {

        DataInputStream dataIn;
        String command;
        DataOutputStream dataOut;
        Socket socket;
        int id;
        String date;
        SendMsg sendMsg;


        public ConnectionThread(Socket socket) {

            id = ++uniqueId;
            this.socket = socket;
            System.out.println("New Thread ");
            sendMsg = new SendMsg();

            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            date = new Date().toString() + "\n";
        }

        /**
         * Listening server
         */

        @Override
        public void run() {
            sendMsg.start();
            boolean keepGoing = true;
            while (keepGoing) {
                try {
                    command = dataIn.readUTF();
                    display(command);
                } catch (IOException e) {
                    display(" Exception reading Streams: " + e);
                }
                switch (command) {
                    case Const.TWO:
                        getCurrentWeather();
                        break;
                    case Const.THREE:
                        sendMassage("Now connected " + getClientCount() + " users");
                        break;
                    case Const.FIVE:
                        display(" disconnected ");
                        keepGoing = false;
                        break;
                }
            }
            sendMsg.interrupt();
            remove(id);
            close();
        }

        /**
         * Get current weather
         */
        private void getCurrentWeather() {
            String msg = sdf.format(new Date()) + " temp = " + temp + " humidity = " + humidity;
            try {
                dataOut.writeUTF(msg);
            } catch (IOException e) {

            }
        }

        /**
         * Close connection
         */
        private void close() {
            try {
                if (dataOut != null) dataOut.close();
            } catch (Exception e) {
            }
            try {
                if (dataIn != null) dataIn.close();
            } catch (Exception e) {
            }
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
            }
        }


        /**
         * Send massage to socket if it possible
         */
        private boolean sendMassage(String msg) {
            if (!socket.isConnected()) {
                remove(id);
                close();
                System.out.println(connections.size());
                return false;
            }
            try {
                dataOut.writeUTF(msg);
            } catch (IOException e) {
                display(e.toString());
            }
            return true;
        }
    }

    /**
     * Class for the regular notification about current weather
     */
    private class SendMsg extends Thread {
        Weather weather = new Weather();

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    temp = weather.getTemp();
                    humidity = weather.getHumidity();
                    Thread.sleep(60000);
                    broadcast(temp, humidity);
                } catch (InterruptedException e) {

                }
            }
        }
    }
}


