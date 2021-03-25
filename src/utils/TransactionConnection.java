package utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TransactionConnection implements Closeable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public TransactionConnection(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public ObjectOutputStream out() {
        return out;
    }

    public ObjectInputStream in() {
        return in;
    }

    public void close() throws java.io.IOException {
        out.close();
        in.close();
        socket.close();
    }

}
