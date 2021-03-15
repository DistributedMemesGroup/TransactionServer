package messages;

public class OpenMessage extends Message {
    public long accountNumber;

    public OpenMessage(long accountNumber) {
        super(MessageType.WRITE);

    }
}
