package account;

import concurrency.locking.Lock;
//init Account
public class Account {
    private static int idcount = 0;

    private int id;

    private int balance;
    // Lock tied to this account
    public Lock lock;
    
    //constructor for account
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
    
    //check if account is equal to another account
    @Override
    public boolean equals(Object other) {
        return other instanceof Account && ((Account) other).id == this.id;
    }
    
    //get account ID
    public int getId() {
        return this.id;
    }

}
