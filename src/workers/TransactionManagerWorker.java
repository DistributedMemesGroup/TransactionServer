package workers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import account.AccountManager;
import concurrency.transaction.TransactionManager;
import messages.*;
import logger.Logger;

public class TransactionManagerWorker implements Runnable {
    Socket conn;
    public static final TransactionManager transactionManager = TransactionManager.getInstance();
    public static final Logger logger = Logger.getInstance();
    AccountManager accountManager = AccountManager.getInstance();
    int currentTransactionId = -1;

    public TransactionManagerWorker(Socket conn) {
        this.conn = conn;
    }

    public void run() {
        try (var ois = new ObjectInputStream(conn.getInputStream());
                var oos = new ObjectOutputStream(conn.getOutputStream())) {
            do {
                Object rawMessage = ois.readObject();
                if (rawMessage == null) {
                    logger.logError("Received message is null");
                    return;
                }
                var inMessage = (Message) rawMessage;
                switch (inMessage.getType()) {
                case OPEN -> handleOpen((OpenMessage) inMessage, oos);
                case READ -> handleRead((ReadMessage) inMessage, oos);
                case WRITE -> handleWrite((WriteMessage) inMessage);
                case CLOSE -> handleClose((CloseMessage) inMessage);
                }
            } while (currentTransactionId != -1);
        } catch (IOException | ClassNotFoundException e) {
            logger.logError(String.format("There was an IO problem:\n\t%s\n", e.getMessage()));
        }

    }

    private void handleOpen(OpenMessage message, ObjectOutputStream oos) {
        // Add transaction to list of transactions in TransacManager
        currentTransactionId = transactionManager.addTransaction();
        try {
            oos.writeInt(currentTransactionId);
        } catch (IOException e) {
            logger.logError(String.format("Error in opening Transaction %d:\nCould not return ID to client\n",
                    currentTransactionId));
        }
    }

    private void handleRead(ReadMessage message, ObjectOutputStream oos) {
        logger.logInfo("Transaction " + currentTransactionId + " reading account:  " + message.accountNumber);
        var transaction = transactionManager.getTransaction(currentTransactionId);
        if (transaction != null) {
            var accountAmount = accountManager.read(message.accountNumber, transaction);
            try {
                oos.writeInt(accountAmount);
            } catch (IOException e) {
                logger.logError(
                        String.format("Error in Transaction %d reading Account %d:\nCould not return %d to client\n",
                                currentTransactionId, message.accountNumber, accountAmount));
            }
        }
    }

    private void handleWrite(WriteMessage message) {
        logger.logInfo("Transaction " + currentTransactionId + " reading account:  " + message.accountNumber);
        var transaction = transactionManager.getTransaction(currentTransactionId);
        if (transaction != null) {
            accountManager.write(message.accountNumber, message.amount, transaction);
        }
    }

    private void handleClose(CloseMessage message) {
        logger.logInfo(String.format("Closed Transaction %d\n", currentTransactionId));
        transactionManager.removeTransaction(currentTransactionId);
        currentTransactionId = -1; // end the loop
    }
}
