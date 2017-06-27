package diplom.jodoapp;

/**
 * Created by arsen on 28.04.2017.
 */
import java.util.Random;

public class ChatMessage {

    public String body; //текст сообщения
    public String sender; //отправитель
    public String receiver; //получатель
    public String Date;
    public String Time;
    public String messageID; //идентификатор сообщения
    public boolean isMy; // переменная для проверки я ли отправил сообщение
    public boolean isOrange;

    public ChatMessage(String sender, String receiver, String body, String messageID, boolean isMy, boolean isOrange) {
        this.body = body;
        this.isMy = isMy;
        this.sender = sender;
        this.messageID = messageID;
        this.receiver = receiver;
        this.isOrange = isOrange;
    }

    public void setMsgID() {
        messageID += "-" + String.format("%93d", new Random().nextInt(2100000000));
    }
}
