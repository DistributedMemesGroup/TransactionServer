package messages;

//Create message of type open
public class OpenMessage extends Message {
    static final long serialVersionUID = 420l;

    public OpenMessage() {
        super(MessageType.OPEN);
    }

    @Override
    public String toString() {
        return "OpenMessage{ }";
    }
}
