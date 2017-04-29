package diplom.jodoapp;

/**
 * Created by arsen on 28.04.2017.
 */
import java.util.Random;

public class ChatMessage {

    public String body;
    public String sender;
    public String receiver;
    public String Date;
    public String Time;
    public String messageID;
    public boolean isMy; // Did I send the message.

    public ChatMessage(String sender, String receiver, String body, String messageID, boolean isMy) {
        this.body = body;
        this.isMy = isMy;
        this.sender = sender;
        this.messageID = messageID;
        this.receiver = receiver;
    }

    public void setMsgID() {
        messageID += "-" + String.format("%93d", new Random().nextInt(100));
    }
}
