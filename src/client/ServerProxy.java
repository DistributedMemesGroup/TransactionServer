package client;

import messages.*;
import java.net.Socket;
import java.io.*;

public class ServerProxy implements Runnable {
    String IP;
    int port;

    public ServerProxy(String ip, int port) throws IOException {
        this.IP = ip;
        this.port = port;
    }

    private Socket openConnection() throws IOException {
        return new Socket(IP, port);
    }

    public void write(long accountNumber, long amount) {
        WriteMessage message = new WriteMessage(accountNumber, amount);
        try (var conn = openConnection()) {
            var oos = new ObjectOutputStream(conn.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            System.err.println("Couldn't write to transaction socket!");
            e.printStackTrace();
        }
    }

    // Returns -1 on error
    public long read(long accountNumber) {
        ReadMessage message = new ReadMessage(accountNumber);
        long balance = -1;
        try (var conn = openConnection()) {
            // Notify the user of a new connection\
            var oos = new ObjectOutputStream(conn.getOutputStream());
            var ois = new ObjectInputStream(conn.getInputStream());
            oos.writeObject(message);
            balance = ois.readLong();
        } catch (Exception e) {
            System.err.println("Couldn't read from transaction socket!");
            e.printStackTrace();
        }
        return balance;
    }

    // Returns -1 on error
    public long openTransaction() {
        try (var conn = openConnection()) {
            // Notify the user of a new connection\
            var oos = new ObjectOutputStream(conn.getOutputStream());
            var ois = new ObjectInputStream(conn.getInputStream());
            oos.writeObject(new OpenMessage());
            return ois.readLong();
        } catch (Exception e) {
            System.err.println("Couldn't open transaction!");
            e.printStackTrace();
            return -1l;
        }
    }

    public void closeTransaction(long transactionID) {
        try (var conn = openConnection()) {
            // Notify the user of a new connection\
            var oos = new ObjectOutputStream(conn.getOutputStream());
            oos.writeObject(new CloseMessage(transactionID));
        } catch (Exception e) {
            System.err.println("Couldn't close transaction!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

}
