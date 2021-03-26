package workers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import account.AccountManager;
import concurrency.transaction.TransactionManager;
import messages.*;
import utils.TransactionConnection;
import logger.Logger;

public class TransactionManagerWorker implements Runnable {
    private final TransactionConnection conn;
    public static final TransactionManager transactionManager = TransactionManager.getInstance();
    public final Logger logger = Logger.getInstance();
    AccountManager accountManager = AccountManager.getInstance();
    int currentTransactionId = -1;

    public TransactionManagerWorker(TransactionConnection conn) {
        this.conn = conn;
    }

    public void run() {
        logger.logInfo("Worker started");
        try (conn) {
            do {
                conn.out().flush();
                Object rawMessage = conn.in().readObject();
                if (rawMessage == null) {
                    logger.logError("Received message is null");
                    return;
                }
                var inMessage = (Message) rawMessage;
                switch (inMessage.getType()) {
                case OPEN -> handleOpen((OpenMessage) inMessage, conn.out());
                case READ -> handleRead((ReadMessage) inMessage, conn.out());
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
        logger.logInfo(String.format("Transaction %d: opened", currentTransactionId));
        try {
            logger.logInfo(String.format("Transaction %d: sending ID back to client", currentTransactionId));
            oos.writeInt(currentTransactionId);
            oos.flush();
            logger.logInfo(String.format("Transaction %d: sent back to client", currentTransactionId));
        } catch (IOException e) {
            logger.logError(String.format("Transaction %d: error in opening:\nCould not return ID to client\n",
                    currentTransactionId));
        }
    }

    private void handleRead(ReadMessage message, ObjectOutputStream oos) {
        logger.logInfo(
                String.format("Transaction %d: reading account %d", currentTransactionId, message.accountNumber));
        var transaction = transactionManager.getTransaction(currentTransactionId);
        if (transaction != null) {
            logger.logInfo(String.format("Transaction %d: Trying to read account %d", currentTransactionId,
                    message.accountNumber));
            var accountAmount = accountManager.read(message.accountNumber, transaction);
            logger.logInfo(String.format("Transaction %d: read account %d: %d", currentTransactionId,
                    message.accountNumber, accountAmount));
            try {
                logger.logInfo(String.format("Transaction %d: Trying to send account %d's balance (%d) back to client",
                        currentTransactionId, message.accountNumber, accountAmount));
                oos.writeInt(accountAmount);
                logger.logInfo(String.format("Transaction %d: Sent account %d's balance (%d) back to client",
                        currentTransactionId, message.accountNumber, accountAmount));
            } catch (IOException e) {
                logger.logError(
                        String.format("Transaction %d: error in reading Account %d:\nCould not return %d to client\n",
                                currentTransactionId, message.accountNumber, accountAmount));
            }
        }
    }

    private void handleWrite(WriteMessage message) {
        logger.logInfo(
                String.format("Transaction %d: writing to account %d", currentTransactionId, message.accountNumber));
        var transaction = transactionManager.getTransaction(currentTransactionId);
        if (transaction != null) {
            logger.logInfo(String.format("Transaction %d: Trying to write %d to account %d", currentTransactionId,
                    message.amount, message.accountNumber));
            accountManager.write(message.accountNumber, message.amount, transaction);
            logger.logInfo(String.format("Transaction %d: Wrote %d to account %d", currentTransactionId, message.amount,
                    message.accountNumber));
            try {
                conn.out().writeInt(message.amount);
            } catch (IOException e) {
                logger.logError(String.format(
                        "Transaction %d: error in writing to Account %d:\nCould not return %d to client\n",
                        currentTransactionId, message.accountNumber, message.amount));
            }
        }
    }

    private void handleClose(CloseMessage message) {
        transactionManager.removeTransaction(currentTransactionId);
        logger.logInfo(String.format("Transaction %d: Closed\n", currentTransactionId));
        currentTransactionId = -1; // end the loop
    }
}
