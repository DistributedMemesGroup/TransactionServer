package concurrency.transaction;

import concurrency.locking.Lock;
import account.AccountManager;
import java.util.ArrayList;

public class Transaction {
    int transactionID;
    ArrayList<Lock> locks;
    AccountManager accountManager;

    public Transaction(int ID) {
        accountManager = AccountManager.getInstance();
        this.transactionID = ID;
        this.locks = new ArrayList<>();
    }

    public int read(int accountNumber) {
        System.out.println("Transaction " + transactionID + "e reading account:  " + accountNumber);
        return accountManager.read(accountNumber, this);
    }

    public void write(int accountNumber, int amount) {
        System.out.println(
                "Transaction " + transactionID + ": writing account: " + accountNumber + " with value: " + amount);
        // Call the accountManager interface to write the amount
        accountManager.write(accountNumber, amount, this);
    }

    public ArrayList<Lock> getLocks() {
        return locks;
    }

    public void addLock(Lock lock) {
        locks.add(lock);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Transaction && ((Transaction) other).transactionID == this.transactionID;
    }

}
