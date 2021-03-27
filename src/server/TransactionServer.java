package server;

import concurrency.transaction.TransactionManager;
import logger.Logger;
import concurrency.locking.LockManager;
import account.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import static utils.Utils.*;
import java.util.Map;

public class TransactionServer {
    // init variables
    public static int listenPort = 0;
    public static final TransactionManager transactionManager = TransactionManager.getInstance();
    public static final AccountManager accountManager = AccountManager.getInstance();
    public static final LockManager lockManager = LockManager.getInstance();
    public static final Logger logger = Logger.getInstance();

    public static void main(String[] args) {
        // assign Variables
        Map<String, String> env = System.getenv();
        String listenPortArg = env.get("LISTEN_PORT");
        String accountCntArg = env.get("ACCOUNT_CNT");
        String accountBalArg = env.get("ACCOUNT_BAL");
        int accountCnt = 0;
        int accountInitialBalance = 0;
        // Check for valid inputs
        if (isInt(listenPortArg) && isInt(accountCntArg) && isInt(accountBalArg)) {
            listenPort = Integer.parseInt(listenPortArg);
            accountCnt = Integer.parseInt(accountCntArg);
            accountInitialBalance = Integer.parseInt(accountBalArg);

        } else {
            logger.logError("Required Environment Variables:\n" + "\tLISTEN_PORT : int\n" + "\tACCOUNT_CNT : int\n"
                    + "\tACCOUNT_BAL : int\n");
            System.exit(69);
        }
        // Create designated accounts with designated balance
        accountManager.createAccounts(accountCnt, accountInitialBalance);

        try (var serverSocket = new ServerSocket(listenPort)) {
            // Run the server loop until 10 seconds elapses, then calc branch total.
            serverSocket.setSoTimeout(10_000);
            while (true) {
                try {
                    logger.logInfo("Waiting for clients");
                    transactionManager.handleConnection(serverSocket.accept());
                    logger.logInfo("Client trying to connect");
                } catch (SocketTimeoutException e) {
                    logger.logInfo("Branch Total: " + accountManager.branchTotal());
                    System.exit(0);
                } catch (IOException e) {
                    logger.logError("Error");
                    logger.logError(e);
                    System.exit(69);
                }
            }
        } catch (IOException e) {
            logger.logError("failed to open server socket");
            logger.logError(e);
            System.exit(69);
        }
    }
}
