package server;

import concurrency.transaction.*;
import java.io.IOException;
import java.net.ServerSocket;

import concurrency.transaction.TransactionManager;

class TransactionServer {
    public final int port;

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

        while (true) {
            try {
                var serverSocket = new ServerSocket(server.port);
                var clientConn = serverSocket.accept();
                var manager = TransactionManager.getInstance();
                manager.handleConnection(clientConn);

            } catch (IOException e) {
                System.out.println("Error");
            }
        }
    }

}
