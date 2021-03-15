package messages;

public class ReadMessage extends Message {
    public long accountNumber;

    public ReadMessage(long accountNumber) {
        super(MessageType.READ);
        this.accountNumber = accountNumber;
    }
}
