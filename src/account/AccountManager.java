package account;

import java.util.ArrayList;
import java.util.List;
import concurrency.locking.*;
import concurrency.transaction.Transaction;

public class AccountManager {
    List<Account> accounts = new ArrayList<>();

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

    public int read(int accountID, Transaction trans) {
        // call upon lockManager to set a read lock
        LockManager.getInstance().setLock(accounts.get(accountID), trans, LockType.READ);

        // once lock is set, get the specific account's balance
        return accounts.get(accountID).getBalance();
    }

    public boolean write(int accountID, int value, Transaction trans) {
        // call upon lockManager to set a write lock
        LockManager.getInstance().setLock(accounts.get(accountID), trans, LockType.WRITE);

        // once lock is set, get the acccount and adjust the value
        accounts.get(accountID).adjustBalance(value);

        // return true to indicate successful
        return true;
    }

}
