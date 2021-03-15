package concurrency.transaction;

import java.util.List;
import concurrency.locking.Lock;

public class Transaction {
    long transactionID;
    List<Lock> locks;

    public Transaction(long ID) {
        this.transactionID = ID;
    }

    public long read(long accountNumber) {
        return 0l;
    }

    public void write(long accountNumber, long amount) {
    }

}
