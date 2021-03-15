package messages;

public class WriteMessage extends Message {
    public long accountNumber;
    public long amount;

    public WriteMessage(long accountNumber, long amount) {
        super(MessageType.WRITE);
        this.accountNumber = accountNumber;
        this.amount = amount;

    }
}
