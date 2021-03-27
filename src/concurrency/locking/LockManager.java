package concurrency.locking;

import java.util.HashSet;

import account.Account;
import concurrency.transaction.Transaction;
import logger.Logger;

/**
 * Controls the assignment and creation of new and existing locks. Serves as an
 * interface for all lock based operations.
 */
public class LockManager {
    // Init our empty set of locks to start
    HashSet<Lock> locks = new HashSet<Lock>();
    private static LockManager instance = null;
    private static final Logger logger = Logger.getInstance();

    public static LockManager getInstance() {
        if (instance == null) {
            instance = new LockManager();
        }
        return instance;
    }

    /**
     * Searches for the lock tied to a specific account
     * 
     * @param inputAccount The account that needs a lock set or used
     * @param trans        The transaction requesting to lock
     * @param lockType     The type of lock requested.
     */
    public void setLock(Account inputAccount, Transaction trans, LockType lockType) {
        Lock targetLock = null;
        // Syncrhonize the search through the locks list
        logger.logInfo("Transaction " + trans.getId() + ": Is searching for existing lock for account "
                + inputAccount.getId());
        synchronized (this) {
            for (Lock currLock : locks) {
                if (currLock.lockedAccount.equals(inputAccount)) {
                    // If we find the lock in the locks list, set our destLock
                    logger.logInfo("Transaction " + trans.getId() + ": found lock for account " + inputAccount.getId());
                    targetLock = currLock;
                }
            }
            // If we didn't find a lock already, create a new one and add it to the list of
            // locks
            if (targetLock == null) {
                logger.logInfo("Transaction " + trans.getId() + ": did NOT find lock for account "
                        + inputAccount.getId() + " creating new lock of type " + lockType);
                targetLock = new Lock(inputAccount);
                locks.add(targetLock);
            }
        }

        // Since we found or created the lock, acquire it.
        targetLock.acquire(trans, lockType);
    }

    /**
     * This will unlock all locks associated with the passed transaction
     * 
     * @param trans The transaction that is releasing its locks.
     */
    public synchronized void unlock(Transaction trans) {
        // Release all locks that contain the trans as a holder.
        logger.logInfo("Transaction " + trans.getId() + ": is releasing its held locks.");
        for (Lock currLock : locks) {
            if (currLock.holders.contains(trans)) {
                currLock.release(trans);
                logger.logInfo(String.format("Transaction %d: released %s lock on %d ", trans.getId(),
                        currLock.lockType.toString(), currLock.lockedAccount.getId()));
            }
        }
    }

}
