package client;

import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.Socket;

public class ServerProxy implements Runnable {
    final Socket transactionSocket;
    final ObjectOutputStream outputStream;
    final ObjectInputStream inputStream;
    int IP, port;

    public ServerProxy(Socket inputSocket, int ip, int port) throws IOException {
        this.IP = ip;
        this.port = port;
        this.transactionSocket = inputSocket;
        outputStream = new ObjectOutputStream(transactionSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(transactionSocket.getInputStream());
    }

    public void writeBalance() {
        try {
            System.out.println("its just jokes");
            /*
             * writeMessage message = new writeMessage(int accountNumber, int amount) Object
             * toClient = outputStream.writeObject(message);
             */
        } catch (Exception e) {
            System.out.println("Couldn't open transaction socket!");
            e.printStackTrace();
        }

    }

    public void showBalance() {
        try (transactionSocket; outputStream; inputStream) {
            // Notify the user of a new connection
            synchronized (System.out) {
                System.out.println("Transaction node connected!");
            }
            // Get info from connection
            Object fromClient = inputStream.readObject();

            // set variables
        } catch (Exception e) {
            System.out.println("Couldn't open transaction socket!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

}
