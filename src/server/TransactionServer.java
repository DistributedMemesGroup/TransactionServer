package server;

import concurrency.transaction.TransactionManager;
import logger.Logger;
import concurrency.locking.LockManager;
import account.*;
import java.io.IOException;
import java.net.ServerSocket;
import static utils.Utils.*;

public class TransactionServer {
    public static int port;

    public static final TransactionManager transactionManager = TransactionManager.getInstance();
    public static final AccountManager accountManager = AccountManager.getInstance();
    public static final LockManager lockManager = LockManager.getInstance();
    public static final Logger logger = Logger.getInstance();

    public static void main(String[] args) {
        if (args.length == 2 && isInt(args[0]) && isInt(args[1])) {
            port = Integer.parseInt(args[0]);
            AccountManager.numOfAccounts = Integer.parseInt(args[1]);
        } else {
            logger.logError("Argument format:\n\t[port number - required] [number of accounts - required]");
            System.exit(69);
        }

        try (var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    logger.logInfo("Waiting for clients");
                    transactionManager.handleConnection(serverSocket.accept());
                    logger.logInfo("Client trying to connect");
                } catch (IOException e) {
                    logger.logError("Error");
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
