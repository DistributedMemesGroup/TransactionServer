package account;

import concurrency.locking.Lock;

public class Account {
    private static int idcount = 0;

    private int id;

    private long balance;
    // Lock tied to this account
    public Lock lock;

    public Account(int input) {
        balance = input;
        id = idcount;
        idcount++;
    }

    public long getBalance() {
        return balance;
    }

    public void transferTo(long amount, Account other) throws IllegalArgumentException {
        if (amount > balance) {
            throw new IllegalArgumentException("Cannot transfer more than what is in the account!");
        }
        other.balance += amount;
        this.balance -= amount;
    }

    public boolean equals(Account other) {
        if (this.id == other.id) {
            return true;
        }
        return false;
    }

}
