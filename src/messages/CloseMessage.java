package messages;

public class CloseMessage extends Message {
    static final long serialVersionUID = 420l;
    int accountNumber;

    public CloseMessage(int accountNumber) {
        super(MessageType.CLOSE);
        this.accountNumber = accountNumber;
    }
}
