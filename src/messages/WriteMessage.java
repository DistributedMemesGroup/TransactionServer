package messages;

//Create message of type Write
public class WriteMessage extends Message {
    static final long serialVersionUID = 420l;
    public int accountNumber;
    public int amount;

    public WriteMessage(int accountNumber, int amount) {
        super(MessageType.WRITE);
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("WriteMessage{ accountNumber = %d, amount = %d }", accountNumber, amount);
    }
}
