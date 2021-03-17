package server;

import concurrency.transaction.TransactionManager;
import concurrency.locking.LockManager;
import account.*;
import java.io.IOException;
import java.net.ServerSocket;

public class TransactionServer {
    public final int port;
    public static final TransactionManager transactionManager = TransactionManager.getInstance();
    public static final AccountManager accountManager = AccountManager.getInstance();
    public static final LockManager lockManager = LockManager.getInstance();

    public TransactionServer() {
        port = 8001;
    }

    public TransactionServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        TransactionServer server;
        if (args.length > 1) {
            server = new TransactionServer(Integer.parseInt(args[1]));
        } else {
            server = new TransactionServer();
        }
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(server.port);
        } catch (IOException e) {
            System.out.println("Failed to open server socket");
            e.printStackTrace();
            System.exit(69);
        }
        
        while (true) {
            try {
                transactionManager.handleConnection(serverSocket.accept());
            } catch (IOException e) {
                System.out.println("Error");
                System.exit(69);
            }
        }
    }

}
