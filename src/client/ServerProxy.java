package client;

import messages.*;
import java.io.*;
import java.net.Socket;
import java.util.Hashtable;
import utils.TransactionConnection;
import logger.Logger;

public class ServerProxy {
    String IP;
    int port;
    public final Logger logger = Logger.getInstance();

    // TransactionID -> Open Socket for the transaction
    public final Hashtable<Integer, TransactionConnection> transactionConnections = new Hashtable<>();

    public ServerProxy(String ip, int port) {
        this.IP = ip;
        this.port = port;
    }

    private Socket openConnection() throws IOException {
        return new Socket(IP, port);
    }

    // Returns -1 on error
    public int openTransaction() {
        logger.logInfo("Trying to open transaction");

        try {
            // init conn
            var conn = new TransactionConnection(openConnection());
            // Notify the user of a new connection\

            conn.out().flush();
            var message = new OpenMessage();
            logger.logInfo(String.format("Trying to send %s", message.toString()));
            conn.out().writeObject(message);
            conn.out().flush();
            logger.logInfo(String.format("Sent %s", message.toString()));
            logger.logInfo("Waiting for transaction ID");
            var transactionId = conn.in().readInt();
            transactionConnections.put(transactionId, conn);
            logger.logInfo(String.format("Received transaction ID %d", transactionId));
            return transactionId;
        } catch (Exception e) {
            logger.logError("Couldn't open transaction!");
            logger.logError(e);
            return -1;
        }
    }

    // Returns -1 on error
    public int read(int accountNumber, int transactionId) {
        logger.logInfo(String.format("Trying to read account %d's balance", accountNumber));
        var message = new ReadMessage(accountNumber);
        int balance = -1;
        try {
            var conn = transactionConnections.get(transactionId);
            logger.logInfo(String.format("Trying to send %s", message.toString()));
            conn.out().writeObject(message);
            conn.out().flush();
            logger.logInfo(String.format("Sent %s", message.toString()));
            logger.logInfo(String.format("Waiting for account %d's balance", accountNumber));
            balance = conn.in().readInt();
            logger.logInfo(String.format("Recieved account %d's balance: %d", accountNumber, balance));
        } catch (Exception e) {
            logger.logError("Couldn't read from transaction socket!");
            logger.logError(e);
        }
        return balance;
    }

    public void write(int accountNumber, int amount, int transactionID) {
        logger.logInfo(String.format("Trying to write %d to account %d's balance", amount, accountNumber));
        var message = new WriteMessage(accountNumber, amount);
        try {
            var conn = transactionConnections.get(transactionID);
            logger.logInfo(String.format("Trying to send %s", message.toString()));
            conn.out().writeObject(message);
            logger.logInfo(String.format("Sent %s", message.toString()));
        } catch (IOException e) {
            logger.logError("Couldn't write to transaction socket!");
            logger.logError(e);
        }
    }

    public void closeTransaction(int transactionID) {
        logger.logInfo(String.format("Trying to close transaction %d", transactionID));
        var message = new CloseMessage(transactionID);
        try (var conn = transactionConnections.get(transactionID)) {
            // Notify the user of a new connection\
            logger.logInfo(String.format("Trying to send %s", message.toString()));
            conn.out().writeObject(message);
            logger.logInfo(String.format("Sent %s", message.toString()));
        } catch (Exception e) {
            logger.logError("Couldn't close transaction!");
            logger.logError(e);
        } finally {
            transactionConnections.remove(transactionID);
        }
    }
}
