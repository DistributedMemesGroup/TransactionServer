package concurrency.locking;

import account.Account;
import concurrency.transaction.Transaction;
import java.util.Hashtable;

import logger.Logger;

public class LockManager {
    Hashtable<Lock, Boolean> locks;
    private static LockManager instance = null;
    private static final Logger logger = Logger.getInstance();

    private LockManager() {
        // Init our empty list of locks to start
        locks = new Hashtable<>();
    }

    public static LockManager getInstance() {
        if (instance == null) {
            instance = new LockManager();
        }
        return instance;
    }

    public void setLock(Account inputAccount, Transaction trans, LockType lockType) {
        Lock targetLock = null;
        // Syncrhonize the search through the locks list
        logger.logInfo("Transaction " + trans.getId() + ": Is searching for existing lock for account "
                + inputAccount.getId());
        for (Lock currLock : locks.keySet()) {
            if (currLock.lockedAccount.equals(inputAccount)) {
                // If we find the lock in the locks list, set our destLock
                logger.logInfo("Transaction " + trans.getId() + ": found lock for account " + inputAccount.getId());
                targetLock = currLock;
            }
        }
        // If we didn't find a lock already, create a new one and add it to the list of
        // locks
        if (targetLock == null) {
            logger.logInfo("Transaction " + trans.getId() + ": did NOT find lock for account " + inputAccount.getId()
                    + " creating new lock of type " + lockType);
            targetLock = new Lock(inputAccount);
            locks.put(targetLock, true);
        }

        // Since we found or created the lock, acquire it.
        targetLock.acquire(trans, lockType);
    }

    public synchronized void unlock(Transaction trans) {
        // Release all locks that contain the trans as a holder.
        logger.logInfo("Transaction " + trans.getId() + ": is releasing its held locks.");
        for (Lock currLock : locks.keySet()) {
            if (currLock.holders.contains(trans)) {
                currLock.release(trans);
                logger.logInfo(String.format("Transaction %d: released %s lock on %d ", trans.getId(),
                        currLock.lockType.toString(), currLock.lockedAccount.getId()));
            }
        }
    }

}
