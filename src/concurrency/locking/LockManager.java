package concurrency.locking;

import java.util.List;
import account.Account;
import concurrency.transaction.Transaction;
import java.util.ArrayList;

class LockManager {
    List<Lock> locks;

    public LockManager() {
        // Init our empty list of locks to start
        locks = new ArrayList<Lock>();
    }

    public void setLock(Account inputAccount, Transaction trans, LockType lockType) {
        Lock targetLock = null;
        // Syncrhonize the search through the locks list
        synchronized (this) {
            for (Lock currLock : locks) {
                if (currLock.lockedAccount.equals(inputAccount)) {
                    // If we find the lock in the locks list, set our destLock
                    targetLock = currLock;
                }
            }
            // If we didn't find a lock already, create a new one and add it to the list of
            // locks
            if (targetLock == null) {
                targetLock = new Lock(inputAccount);
                locks.add(targetLock);
            }
            // Since we found or created the lock, acquire it.S
            targetLock.acquire(trans, lockType);
        }
    }

    public synchronized void unlock(Transaction trans) {
        //Release all locks that contain the trans as a holder.
        for (Lock currLock : locks) {
            if (currLock.holders.contains(trans)) {
                currLock.release(trans);
            }
        }
    }

}
