package workers;

import java.io.Socket;

public class TransactionManagerWorker implements Runnable {
    Socket conn;
    public TransactionManagerWorker(Socket conn) {
        this.conn = conn;
    }

    public void run() {

    }
}
