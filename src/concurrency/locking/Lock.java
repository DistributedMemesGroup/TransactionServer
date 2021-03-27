package concurrency.locking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import concurrency.transaction.Transaction;
import account.Account;
import logger.Logger;

/*
Defines an individual lock that is tied to an account 
on the transaction server and database.
*/
public class Lock {
    // Logger instance
    private static final Logger logger = Logger.getInstance();
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
    /**
     * This method is called when a transaction acquires this lock tied to an
     * account it wants to read or write to, if possible given the existence
     * of conflicting operations.
     * @param newTrans The transaction who seeks to acquire the lock.
     * @param desiredType The type of lock needed by newTrans.
     */
    public synchronized void acquire(Transaction newTrans, LockType desiredType) {
        // If we cannot acquire a lock, wait until notified.
        while (!(holders.isEmpty() || isOnlyHolder(newTrans) || bothReadLocks(desiredType))) {
            try {
                logger.logInfo("Transaction " + newTrans.getId() + ": waiting for lock of type "
                        + desiredType.toString() + " for account " + lockedAccount.getId());
                // Since we can't get the lock right now, wait for another chance.
                wait();
            } catch (InterruptedException e) {
            }
        }
        // If we are the only holder, set whatever type we want.
        if (holders.isEmpty()) {
            logger.logInfo("Transaction " + newTrans.getId() + ": No holders for account " + lockedAccount.getId()
                    + " lock, setting desired lock of type " + desiredType.toString());
            holders.add(newTrans);
            this.lockType = desiredType;
        }
        // If we are the only holder and we want to promote a lock.
        else if (isOnlyHolder(newTrans) && desiredType == LockType.WRITE) {
            logger.logInfo("Transaction " + newTrans.getId() + ": Promoting lock for account " + lockedAccount.getId()
                    + " at the request of Transaction " + newTrans.getId() + ".");
            promote();
        }
        // If we can share the lock with the other holder(s), add us as a holder.
        else if (bothReadLocks(desiredType)) {
            if (!holders.contains(newTrans)) {
                logger.logInfo("Transaction " + newTrans.getId() + ": read lock at the request of Transaction: with "
                        + holders.size() + " other holders.");
                holders.add(newTrans);
            }
        }
    }

    /**
     * Releases all locks held by the input transaction. 
     * @param releasingTrans The transaction that will release locks.
     */
    public synchronized void release(Transaction releasingTrans) {
        // Check this lock type
        if (holders.contains(releasingTrans)) {
            holders.remove(releasingTrans);
            // Notify every transaction thread waiting on this lock.
            notifyAll();
        }
    }

    // Promoting method for a lock object
    public synchronized void promote() {
        this.lockType = LockType.WRITE;
    }

    // Checks if the querying transaction is the only holder.
    public boolean isOnlyHolder(Transaction trans) {
        return this.holders.size() == 1 && holders.contains(trans);
    }

    // Check for sharable read lock.
    public boolean bothReadLocks(LockType inputLockType) {
        return inputLockType == LockType.READ && this.lockType == LockType.READ;
    }

}
