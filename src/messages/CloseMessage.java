package messages;

//Message of type close
public class CloseMessage extends Message {
    static final long serialVersionUID = 420l;
    int transactionId;

    public CloseMessage(int transactionId) {
        super(MessageType.CLOSE);
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return String.format("CloseMessage{ transactionId = %d }", transactionId);
    }
}
