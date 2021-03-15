package account;

import java.util.List;
import concurrency.locking.*;

public class AccountManager {
    List<Account> accounts;

    private static AccountManager instance = null;

    public static AccountManager getInstance() {
        if (instance == null) {
            AccountManager.instance = new AccountManager();
        }
        return AccountManager.instance;

    }

    // constructor for account Manager
    private AccountManager() {
        for (int i = 0; i < 10; i++) {
            accounts.add(new Account(420));
        }
    }

    public int getBalance(int accountID) {
        // call upon lockManager to set a read lock
        // LockManager.setLock(accounts.get(accountID), null, LockType.READ);

        // once lock is set, get the specific account's balance
        // int bal = accounts.get(accountID).balance;

        // release the lock
        // LockManager.unlock(null);
        // return balance;
        return 0;
    }

    public boolean adjustBalance(int accountID, int input) {
        // call upon lockManager to set a write lock
        // LockManager.setLock(accounts.get(accountID), null, LockType.WRITE);

        // once lock is set, get the acccount and adjust the value
        // accounts.get(accountID).balance += input;

        // release the lock
        // LockManager.unlock(null);

        // return true to indicate successful
        return true;
    }

}
