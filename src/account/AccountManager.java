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

    }

    public void createAccounts(int numOfAccounts, int initialValue) {
        for (int i = 0; i < numOfAccounts; i++) {
            accounts.add(new Account(initialValue));
        }
    }

    public int read(int accountID, Transaction trans) {
        // call upon lockManager to set a read lock
        LockManager.getInstance().setLock(accounts.get(accountID), trans, LockType.READ);

        // once lock is set, get the specific account's balance
        return accounts.get(accountID).getBalance();
    }

    public void write(int accountID, int value, Transaction trans) {
        // call upon lockManager to set a write lock
        LockManager.getInstance().setLock(accounts.get(accountID), trans, LockType.WRITE);

        // once lock is set, get the acccount and adjust the value
        accounts.get(accountID).writeBalance(value);
    }

}
