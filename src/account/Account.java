package account;

import java.math.BigDecimal;
import concurrency.locking.Lock;

public class Account {
    private BigDecimal balance;
    // Lock tied to this account
    public Lock lock;

    public BigDecimal getBalance() {
        return balance;
    }

    public void transferTo(BigDecimal amount, Account other) throws IllegalArgumentException {
        if (amount.compareTo(balance) > 0) {
            throw new IllegalArgumentException("Cannot transfer more than what is in the account!");
        }
        other.balance.add(amount);
        this.balance.subtract(amount);
    }

}
