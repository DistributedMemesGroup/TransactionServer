package concurrency.locking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import concurrency.transaction.Transaction;
import account.Account;

/*
Defines an individual lock that is tied to an account 
on the transaction server and database.
*/

public class Lock {
    // This is the account the Lock controls access to.
    Account lockedAccount;
    // Type of this lock object, either write or read.
    LockType lockType;
    List<Transaction> holders;

    public Lock(Account inputAccount) {
        // Tie the account to the lock
        this.lockedAccount = inputAccount;
        this.lockType = null;
        this.holders = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void acquire(Transaction newTrans, LockType desiredType) {
        // Check this lock type
        switch (this.lockType) {
        // This lock is currently held as a write lock
        case WRITE:

        case READ:
            // If set to read, and desired lock is of type read, gucci
            if (desiredType == LockType.READ) {
                holders.add(newTrans);
            }
            // No one acquired the lock, so no type was set
        default:

        }
        // Write lock, only allow one transaction
        // Else read lock, allow multiple transacs to acquire read lock

    }
}
