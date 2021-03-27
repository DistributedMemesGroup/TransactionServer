package concurrency.transaction;

import java.io.IOException;
import java.net.Socket;
import workers.TransactionManagerWorker;
import java.util.Hashtable;

import concurrency.locking.LockManager;
import logger.Logger;
import utils.TransactionConnection;

public class TransactionManager {
    //init variables
    private Hashtable<Integer, Transaction> transactions = new Hashtable<>();
    private static TransactionManager instance = null;
    private static final LockManager lockManager = LockManager.getInstance();
    private static int currentId = 0;
    private final Logger logger = Logger.getInstance();
    
    //Get current instance of transaction
    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }
    
     // Spawn the transacmanagerworkerthread with conn
    public void handleConnection(Socket socket) {
        try {
            var conn = new TransactionConnection(socket);
            Thread transacWorkerThread = new Thread(new TransactionManagerWorker(conn));
            transacWorkerThread.start();
        } catch (IOException e) {
            logger.logError("Could not open connection with client");
            logger.logError(e);
        }

    }
    
    //Add a transaction to out hash map
    public synchronized int addTransaction() {
        Transaction newTrans = new Transaction(currentId);
        // Put the transaction in the hashtable with its id.
        transactions.put(currentId, newTrans);
        // Increment the id counter for the next transaction.
        currentId++;
        return newTrans.transactionID;
    }
    
    //Get information of the desired transaction
    public synchronized Transaction getTransaction(int transactionId) {
        if (transactions.containsKey(transactionId)) {
            return transactions.get(transactionId);
        } else {
            return null;
        }
    }
    
    //Remove transaction from the map
    public synchronized void removeTransaction(int transactionId) {
        var transaction = transactions.remove(transactionId);
        lockManager.unlock(transaction);
    }
}
