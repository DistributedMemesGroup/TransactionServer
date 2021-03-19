package client;

import java.util.Random;
import static utils.Utils.*;

public class Client {
    static ServerProxy proxy;

    public static void main(String[] args) {
        if (isValidIpAddr(args[0]) && isInt(args[1])) {
            proxy = new ServerProxy(args[0], Integer.parseInt(args[1]));
        } else {
            System.err.println("Argument format:\n\t[IPv4 address - required] [port number  - required]");
            System.exit(69);
        }
    }

    // Simplest test, withdraw from one account and deposit to another.
    public static boolean transactionTest1() {
        Random rand = new Random();
        int srcAcct = rand.nextInt(10);

        int dstAcct = rand.nextInt(10);
        // If we roll the same account, make a new id to deposit to.
        while (srcAcct == dstAcct) {
            dstAcct = rand.nextInt(10);
        }
        // Now we have two diff accounts, get their balances.
        int srcBal = proxy.read(srcAcct);
        int dstBal = proxy.read(dstAcct);

        // Check if we can actually read a valid balance
        if (srcBal == -1 || dstBal == -1) {
            System.err.printf(
                    "One of the account balances was not read correctly, stopping test.\nAccount %d balance: %d\nAccount %d balance: %d\n",
                    srcAcct, srcBal, dstAcct, dstBal);
            return false;
        }
        int withdrawAmt = rand.nextInt(srcBal + 1);
        // Withdraw from the src
        proxy.write(srcAcct, srcBal - withdrawAmt);

        // Deposit to dst
        proxy.write(dstAcct, dstBal + withdrawAmt);

        // Return success of the test itself.
        return true;
    }

    public static void transactionTest2() {

    }

}
