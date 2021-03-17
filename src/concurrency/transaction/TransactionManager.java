package concurrency.transaction;

import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

public class TransactionManager {
    Hashtable<Integer, Transaction> transactions = new Hashtable<>();
    private static TransactionManager instance = null;

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public void handleConnection(Socket conn) {
        // Spawn the transacmanagerworkerthread with conn
        
    }
}
