package client;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import logger.Logger;

import static utils.Utils.*;

public class Client {
    static ServerProxy proxy;
    public static final Logger logger = Logger.getInstance();

    public static void main(String[] args) {
        // Create a map based on our env variables
        // Init Variables
        Map<String, String> env = System.getenv();
        String ipAddr = env.get("SERVER_ADDR");
        String portArg = env.get("SERVER_PORT");
        String transactionCntArg = env.get("TRANSACTION_CNT");
        String accountCntArg = env.get("ACCOUNT_CNT");
        // Check if environment variables are valid
        if (isValidIpAddr(ipAddr) && isInt(portArg) && isInt(transactionCntArg) && isInt(accountCntArg)) {
            proxy = new ServerProxy(ipAddr, Integer.parseInt(portArg));
        } else {
            logger.logError("Required Environment Variables:\n" + "\tSERVER_ADDR : IPv4 address\n"
                    + "\tSERVER_PORT : int\n" + "\tTRANSACTION_CNT : int\n");
            System.exit(69);
        }

        // Get number of accounts and transactions
        var transactionCnt = Integer.parseInt(transactionCntArg);
        var accountCnt = Integer.parseInt(accountCntArg);
        for (int i = 0; i < transactionCnt; i++) {
            var test = new Thread(() -> {
                runTransactionTest(accountCnt);
            });
            test.start();
        }
    }

    // Simplest test, withdraw from one account and deposit to another.
    public static boolean runTransactionTest(int accountCnt) {
        int transactionID = proxy.openTransaction();
        if (transactionID == -1) {
            logger.logError("Could not open transaction");
            return false;
        }
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int srcAcct = rand.nextInt(accountCnt);

        int dstAcct = rand.nextInt(accountCnt);
        // If we roll the same account, make a new id to deposit to.
        while (srcAcct == dstAcct) {
            dstAcct = rand.nextInt(accountCnt);
        }
        // Now we have two diff accounts, get their balances.
        int srcBal = proxy.read(srcAcct, transactionID);
        int dstBal = proxy.read(dstAcct, transactionID);
        logger.logInfo(transactionID, String.format("%d is Source, %d is Dest", srcAcct, dstAcct));

        // Check if we can actually read a valid balance
        if (srcBal == -1 || dstBal == -1) {
            logger.logError(transactionID, String.format(
                    "One of the account balances was not read correctly, stopping test.\nAccount %d balance: %d\nAccount %d balance: %d\n",
                    srcAcct, srcBal, dstAcct, dstBal));
            return false;
        }
        int withdrawAmt = rand.nextInt(srcBal + 1);
        // Withdraw from the src
        proxy.write(srcAcct, srcBal - withdrawAmt, transactionID);

        // Deposit to dst
        proxy.write(dstAcct, dstBal + withdrawAmt, transactionID);
        proxy.closeTransaction(transactionID);

        // Return success of the test itself.
        return true;
    }

}
