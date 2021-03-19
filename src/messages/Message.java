package messages;

import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 420l;
    MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }
}
