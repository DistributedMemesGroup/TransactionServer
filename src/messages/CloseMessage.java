package messages;

public class CloseMessage extends Message {
    long accountNumber;

    public CloseMessage(long accountNumber) {
        super(MessageType.CLOSE);
        this.accountNumber = accountNumber;
    }
}
