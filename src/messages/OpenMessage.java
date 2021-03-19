package messages;

public class OpenMessage extends Message {
    static final long serialVersionUID = 420l;

    public OpenMessage() {
        super(MessageType.OPEN);
    }
}
