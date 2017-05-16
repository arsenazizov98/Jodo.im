package diplom.jodoapp;

/**
 * Created by arsen on 28.04.2017.
 */

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import diplom.jodoapp.fragments.ChatFragment;
import diplom.jodoapp.fragments.TaskFragment;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smack.chat.ChatManager;

public class XMPP {

    public static boolean connected = false;
    public static boolean isConnect = false;
    public static boolean isToasted = true;
    public static boolean isCreatedChat = false;
    private String HOST;
    public static XMPPTCPConnection xmpptcpConnection;
    public static String login;
    public static String pass;
    public static String receiver = "bot@bot.jodo.im";
    XMPPServiceConnection context;
    public static XMPP instance = null;
    public static boolean instanceCreated = false;

    private XMPP(final XMPPServiceConnection context, String HOST, String login, String pass) {
        this.HOST = HOST;
        this.login = login;
        this.pass = pass;
        this.context = context;
        init();

    }

    public static XMPP getInstance(XMPPServiceConnection context, String server,
                                   String user, String pass) {

        if (instance == null) {
            instance = new XMPP(context, server, user, pass);
            instanceCreated = true;
        }
        return instance;
    }

    public static org.jivesoftware.smack.chat.Chat Chat;

    ChatManagerListenerImpl ChatManagerListener;
    MMessageListener MessageListener;
    static { //заимствовано
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
        }
    }

    public void init() {
        MessageListener = new MMessageListener(context);
        ChatManagerListener = new ChatManagerListenerImpl();
        initialiseConnection();
    }

    private void initialiseConnection() {

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(HOST);
        config.setHost(HOST);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        xmpptcpConnection = new XMPPTCPConnection(config.build());
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        xmpptcpConnection.addConnectionListener(connectionListener);
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                xmpptcpConnection.disconnect();
            }
        }).start();
    }

    public void connect(){
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (xmpptcpConnection.isConnected()) {
                    return false;
                }
                isConnect = true;
                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"connect...", Toast.LENGTH_LONG).show();
                        }
                    });
                try {
                    xmpptcpConnection.connect();
                    //использование DeliveryReceiptManager заимствовано на форуме StackOverFlow
                    DeliveryReceiptManager deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(xmpptcpConnection);
                    deliveryReceiptManager.setAutoReceiptMode(AutoReceiptMode.always);
                    deliveryReceiptManager.addReceiptReceivedListener(new ReceiptReceivedListener() {
                        @Override
                        public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {

                        }
                    });
                    connected = true;
                } catch (IOException e) {

                } catch (SmackException e) {

                } catch (XMPPException e) {

                }
                return isConnect = false;
            }
        };
        connectionThread.execute();
    }

    public void login() throws IOException, XMPPException, SmackException {
        xmpptcpConnection.login(login,pass);
        context.setIsLogin(true);
    }

    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(org.jivesoftware.smack.chat.Chat chat, boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(MessageListener);
        }
    }

    public void sendMessage(ChatMessage chatMessage) {
        if (!isCreatedChat) {
            Chat = ChatManager.getInstanceFor(xmpptcpConnection).createChat(receiver, MessageListener);
            isCreatedChat = true;
        }
        final Message message = new Message();
        message.setBody(chatMessage.body);
        message.setStanzaId(chatMessage.messageID);
        message.setType(Message.Type.chat);
        message.setFrom(xmpptcpConnection.getUser());
        try {
            if (xmpptcpConnection.isAuthenticated()) {
                Chat.sendMessage(message); //вызывает исключение
            } else if(context.isLogin()) {
                login();
            }
        }catch (Exception e) {

        }
    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
            connected = true;
            try {
                login();
                context.sendIsLoginToActivity();
            } catch (IOException e) {
                failLogin();
                context.setIsLogin(false);
                context.sendIsLoginToActivity();
            } catch (XMPPException e) {
                failLogin();
                context.setIsLogin(false);
                context.sendIsLoginToActivity();
            } catch (SmackException e) {
                failLogin();
                context.setIsLogin(false);
                context.sendIsLoginToActivity();
            }catch (Exception e){
                failLogin();
                context.setIsLogin(false);
                context.sendIsLoginToActivity();
            }
        }

        @Override
        public void connectionClosed() {
            failLogin();
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            failLogin();
        }

        @Override
        public void reconnectingIn(int arg0) {
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            failLogin();
        }

        @Override
        public void reconnectionSuccessful() {
            connected = true;
            isCreatedChat = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            ChatManager.getInstanceFor(xmpptcpConnection).addChatListener(ChatManagerListener);
            isCreatedChat = false;
        }
    }

    private class MMessageListener implements ChatMessageListener {

        public MMessageListener(Context context) {
        }

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat, final Message message) {
            //заимствовано с сайта http://www.tutorialsface.com
            if (message.getType() == Message.Type.chat && message.getBody() != null&&message.getFrom().contains(receiver)) {
                final ChatMessage chatMessage = new ChatMessage(context.USERNAME+context.DOMAIN,receiver,message.getBody(),message.getStanzaId(),false);
                processMessage(chatMessage);
            }else{
                Intent intent = new Intent("insert")
                        .putExtra("body",message.getBody())
                        .putExtra("isMy", "false")
                        .putExtra("isRead", "false")
                        .putExtra("receiver",message.getFrom());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }

        private void processMessage(final ChatMessage chatMessage) {
            chatMessage.isMy = false;
            if (chatMessage.body.contains("Ваше дерево задач:")) {
                context.sendTreeCommand(chatMessage.body);
            }
            else if(chatMessage.body.contains("У вас нет никаких задач.")){}
            else if (chatMessage.body.contains("Сейчас ваша роль: Исполнитель")){
                context.sendHead();
            }
            else if (chatMessage.body.contains("Сейчас ваша роль: Заказчик")){
                context.sendWorker();
            }
            else{
                ChatFragment.chatList.add(chatMessage);
                //заимствовано с сайта http://www.tutorialsface.com
                Intent intent = new Intent("insert")
                        .putExtra("body",chatMessage.body)
                        .putExtra("isMy", "false")
                        .putExtra("isRead", "false")
                        .putExtra("receiver",chatMessage.receiver);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ChatFragment.chatAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }

    private void failLogin(){
        context.setIsLogin(false);
        disconnect();
        instance = null;
        isConnect = false;
        instanceCreated = false;
        connected = false;
        isCreatedChat = false;
    }
}
