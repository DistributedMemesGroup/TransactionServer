package messages;

import java.io.Serializable;

//Create message class
public class Message implements Serializable {
    static final long serialVersionUID = 420l;
    MessageType type;
   
    //Set message type
    public Message(MessageType type) {
        this.type = type;
    }
    
    //Get message type
    public MessageType getType() {
        return type;
    }
}
