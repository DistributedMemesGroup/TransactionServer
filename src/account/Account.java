package account;

import concurrency.locking.Lock;

public class Account {
    private static int idcount = 0;

    private int id;

    private int balance;
    // Lock tied to this account
    public Lock lock;

    public Account(int input) {
        balance = input;
        id = idcount;
        idcount++;
    }

    public int getBalance() {
        return balance;
    }

    public void writeBalance(int value) {
        balance = value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Account && ((Account) other).id == this.id;
    }

}
