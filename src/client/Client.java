package client;

public class Client {
    int initBalance, port;

    public Client(int initBalance, int port) {
        this.initBalance = initBalance;
        this.port = port;
    }

    public void run() {
        int amount = 0;
        int balancetoSend = 0;
        int balanceReceiver = 0;
        int balanceSender = 0;
        int intTransactionID = 0;
        // read the current balance of the transferring account
        System.out.println("Current balance of Sender Account: " + balanceSender);
        // write the new balance after transfer
        balanceSender -= balancetoSend;
        System.out.println("New balance of Sender Account: " + balanceSender);
        // read balance of receiving acount
        System.out.println("Current balance of Receiving Account: " + balanceReceiver);
        // write balance to the receiving account
        balanceReceiver += balancetoSend;
        System.out.println("New balance of Receiving Account: " + balanceReceiver);

    }

    public static void main(String[] args) {
        // write your code here
        System.out.println("Meme");
    }

    public static void transaction() {
        // Needs a lock to prevent multiple read/writes
    }
}
