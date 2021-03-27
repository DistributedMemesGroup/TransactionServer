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

    // opens new connection
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
            // create new open message to send
            var message = new OpenMessage();
            logger.logInfo(String.format("Trying to send %s", message.toString()));
            conn.out().writeObject(message);
            conn.out().flush();
            logger.logInfo(String.format("Sent %s", message.toString()));
            logger.logInfo("Waiting for transaction ID");
            // Add current transaction to our active connections
            var transactionId = conn.in().readInt();
            transactionConnections.put(transactionId, conn);
            logger.logInfo(transactionId, "opened");
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
        // create read message
        var message = new ReadMessage(accountNumber);
        int balance = -1;
        // check if we can read from given account number
        try {
            var conn = transactionConnections.get(transactionId);
            logger.logInfo(transactionId, String.format("Trying to send %s", message.toString()));
            // send message
            conn.out().writeObject(message);
            conn.out().flush();
            logger.logInfo(transactionId, String.format("Sent %s", message.toString()));
            logger.logInfo(transactionId, String.format("Waiting for account %d's balance", accountNumber));
            // set balance to return of our conn
            balance = conn.in().readInt();
            logger.logInfo(transactionId, String.format("Recieved account %d's balance: %d", accountNumber, balance));
        } catch (Exception e) {
            logger.logError(transactionId, "Couldn't read from transaction socket!");
            logger.logError(transactionId, e);
        }
        return balance;
    }

    // write amount to desired account
    public void write(int accountNumber, int amount, int transactionID) {
        logger.logInfo(transactionID,
                String.format("Trying to write %d to account %d's balance", amount, accountNumber));
        // Create write message
        var message = new WriteMessage(accountNumber, amount);
        try {
            // Get connection for given transaction ID
            var conn = transactionConnections.get(transactionID);
            logger.logInfo(transactionID, String.format("Trying to send %s", message.toString()));
            // write our amount to the given account
            conn.out().writeObject(message);
            logger.logInfo(transactionID, String.format("Sent %s", message.toString()));
            conn.in().readInt();
        } catch (IOException e) {
            logger.logError(transactionID, "Couldn't write to transaction socket!");
            logger.logError(transactionID, e);
        }
    }

    // Close out transaction
    public void closeTransaction(int transactionID) {
        logger.logInfo(transactionID, "Trying to close");
        // create our close message
        var message = new CloseMessage(transactionID);
        try (var conn = transactionConnections.get(transactionID)) {
            // Notify the user of a new connection\
            logger.logInfo(transactionID, String.format("Trying to send %s", message.toString()));
            conn.out().writeObject(message);
            logger.logInfo(transactionID, String.format("Sent %s", message.toString()));
        } catch (Exception e) {
            logger.logError(transactionID, "Couldn't close transaction!");
            logger.logError(transactionID, e);
        } finally {
            transactionConnections.remove(transactionID);
        }
    }
}
