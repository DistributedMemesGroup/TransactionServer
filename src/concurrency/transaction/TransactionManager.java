package concurrency.transaction;

import java.net.Socket;
import workers.TransactionManagerWorker;
import java.util.Hashtable;

public class TransactionManager {
    private Hashtable<Integer, Transaction> transactions = new Hashtable<>();
    private static TransactionManager instance = null;
    private static int currentId = 0;

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public void handleConnection(Socket conn) {
        // Spawn the transacmanagerworkerthread with conn
        Thread transacWorkerThread = new Thread(new TransactionManagerWorker(conn));
        transacWorkerThread.start();

    }

    public synchronized int addTransaction() {
        Transaction newTrans = new Transaction(currentId);
        // Put the transaction in the hashtable with its id.
        transactions.put(currentId, newTrans);
        // Increment the id counter for the next transaction.
        currentId++;
        return newTrans.transactionID;
    }

    public synchronized Transaction getTransaction(int transactionId) {
        if (transactions.containsKey(transactionId)) {
            return transactions.get(transactionId);
        } else {
            return null;
        }
    }

    public synchronized void removeTransaction(int transactionId) {
        transactions.remove(transactionId);
    }
}
