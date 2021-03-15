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
        // while (!(holders.isEmpty()))
        // Write lock, only allow one transaction
        // Else read lock, allow multiple transacs to acquire read lock

    }

    public synchronized void release(Transaction releasingTrans) {
        // Check this lock type
        if (holders.contains(releasingTrans)) {
            holders.remove(releasingTrans);
        }
        // Write lock, only allow one transaction
        // Else read lock, allow multiple transacs to acquire read lock

    }

    public boolean isOnlyHolder(Transaction trans) {
        return this.holders.size() == 1 && holders.contains(trans);
    }

    public boolean bothReadLocks(LockType inputLockType) {
        return inputLockType == LockType.READ && this.lockType == LockType.READ;
    }

}
