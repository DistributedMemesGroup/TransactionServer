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
        while (!(holders.isEmpty() || isOnlyHolder(newTrans) || bothReadLocks(desiredType))){
            try{
                wait();
            } catch (InterruptedException e){
            }
        }
        //If we are the only holder, set whatever type we want. 
        if(holders.isEmpty()){
            holders.add(newTrans);
            this.lockType = desiredType;
        }
        //If we are the only holder and we want to promote a lock.
        else if(isOnlyHolder(newTrans) && desiredType == LockType.WRITE){
            promote();
        }
        //If we can share the lock with the other holder(s), add us as a holder.
        else if(bothReadLocks(desiredType)){
            if(!holders.contains(newTrans)){
                holders.add(newTrans);
            }
        }

    }

    public synchronized void release(Transaction releasingTrans) {
        // Check this lock type
        if (holders.contains(releasingTrans)) {
            holders.remove(releasingTrans);
            notifyAll();
        }
    }
    //Promoting method for a lock object
    public synchronized void promote(){
        this.lockType = LockType.WRITE;
    }
    //Checks if the querying transaction is the only holder.
    public boolean isOnlyHolder(Transaction trans) {
        return this.holders.size() == 1 && holders.contains(trans);
    }
    //Check for sharable read lock.
    public boolean bothReadLocks(LockType inputLockType) {
        return inputLockType == LockType.READ && this.lockType == LockType.READ;
    }

}
