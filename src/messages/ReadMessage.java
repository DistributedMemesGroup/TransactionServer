package messages;

public class ReadMessage extends Message {
    static final long serialVersionUID = 420l;
    public int accountNumber;

    public ReadMessage(int accountNumber) {
        super(MessageType.READ);
        this.accountNumber = accountNumber;
    }
}
