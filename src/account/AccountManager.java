package account;

import java.util.ArrayList;
import java.util.List;
import concurrency.locking.*;
import concurrency.transaction.Transaction;
import logger.Logger;

public class AccountManager {
    List<Account> accounts = new ArrayList<>();
    private static final Logger logger = Logger.getInstance();

    private static AccountManager instance = null;

    public static AccountManager getInstance() {
        if (instance == null) {
            AccountManager.instance = new AccountManager();
        }
        return AccountManager.instance;

    }

    public void createAccounts(int numOfAccounts, int initialValue) {
        for (int i = 0; i < numOfAccounts; i++) {
            accounts.add(new Account(initialValue));
        }
    }

    public int read(int accountID, Transaction trans) {
        logger.logInfo(String.format("Transaction %d: AccountManager reading account %d", trans.getId(), accountID));
        // call upon lockManager to set a read lock
        LockManager.getInstance().setLock(accounts.get(accountID), trans, LockType.READ);

        // once lock is set, get the specific account's balance
        return accounts.get(accountID).getBalance();
    }

    public void write(int accountID, int value, Transaction trans) {
        logger.logInfo(String.format("Transaction %d: AccountManager calling write method for account %d",
                trans.getId(), accountID));
        // call upon lockManager to set a write lock
        LockManager.getInstance().setLock(accounts.get(accountID), trans, LockType.WRITE);

        // once lock is set, get the acccount and adjust the value
        accounts.get(accountID).writeBalance(value);
    }

    /**
     * This function is called at the end of a server run to display the total
     * balance of all accounts after the transactions terminate.
     */
    public int branchTotal() {
        int total = 0;
        for (Account currAccount : accounts) {
            total += currAccount.getBalance();
        }
        return total;
    }

}
