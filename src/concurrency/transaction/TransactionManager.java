package concurrency.transaction;

import java.net.Socket;
import java.util.List;

public class TransactionManager {
    List<Transaction> transactions;
    private static TransactionManager instance = null;

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public void handleConnection(Socket conn) {

    }
}
