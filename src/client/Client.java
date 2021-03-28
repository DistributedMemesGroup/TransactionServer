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
        Integer transactionID = proxy.openTransaction();
        if (transactionID == null) {
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
        logger.logInfo(transactionID, String.format("%d is Source, %d is Dest", srcAcct, dstAcct));

        Integer srcBal = proxy.read(srcAcct, transactionID);
        if (srcBal == null) {
            logger.logError(transactionID,
                    String.format("Account %d's balance was not read correctly, stopping test.", srcAcct));
            return false;
        }

        Integer dstBal = proxy.read(dstAcct, transactionID);
        if (dstBal == null) {
            logger.logError(transactionID,
                    String.format("Account %d's balance was not read correctly, stopping test.", dstAcct));
            return false;
        }

        int withdrawAmt = rand.nextInt(srcBal + 1);
        // Withdraw from the src
        if (proxy.write(srcAcct, srcBal - withdrawAmt, transactionID) == null) {
            logger.logError(transactionID,
                    String.format("Could not write %d to account %d, stopping test.", srcBal - withdrawAmt, dstAcct));
            return false;
        }

        // Deposit to dst
        if (proxy.write(dstAcct, dstBal + withdrawAmt, transactionID) == null) {
            logger.logError(transactionID,
                    String.format("Could not write %d to account %d, stopping test.", dstBal + withdrawAmt, dstAcct));
            return false;
        }

        // Close transaction
        if (proxy.closeTransaction(transactionID) == null) {
            logger.logError(transactionID, "Could not properly close transaction, stopping test.");
            return false;
        }

        return true;
    }

}
