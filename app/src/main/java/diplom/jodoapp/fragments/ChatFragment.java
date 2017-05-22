package diplom.jodoapp.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import diplom.jodoapp.ChatAdapter;
import diplom.jodoapp.ChatMessage;
import diplom.jodoapp.CommonMethods;
import diplom.jodoapp.MenuActivity;
import diplom.jodoapp.R;
import diplom.jodoapp.XMPP;

public class ChatFragment extends Fragment{

    private EditText msg_edittext;
    private Random random;
    public static ArrayList<ChatMessage> chatList;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    private static HashMap<String, SQLiteDatabase> dbFriends;
    static String[] itemsContextMenu = new String[]{"start", "done","no","ok","close"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        random = new Random();
        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton)view.findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 sendTextMessage(msg_edittext);
            }
        });
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        dbFriends = ((MenuActivity)getActivity()).getDBFriends();
        chatList = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(getActivity(), chatList);
        registerForContextMenu(msgListView);
        ImageButton addButton = (ImageButton)view.findViewById(R.id.addTaskButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameTask = msg_edittext.getEditableText().toString();
                if (!nameTask.equalsIgnoreCase("")) {
                    String command ="+" + nameTask;
                    final ChatMessage chatMessage = new ChatMessage(XMPP.login+"@jodo.im", XMPP.receiver,
                            command, "" + random.nextInt(2100000000), true);
                    chatMessage.setMsgID();
                    chatMessage.body = command;
                    chatMessage.Date = CommonMethods.getCurrentDate();
                    chatMessage.Time = CommonMethods.getCurrentTime();
                    msg_edittext.setText("");
                    chatAdapter.add(chatMessage);
                    chatAdapter.notifyDataSetChanged();
                    MenuActivity activity = ((MenuActivity) getActivity());
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("body","+"+nameTask);
                    contentValues.put("isMy", "true");
                    contentValues.put("isRead", "true");
                    dbFriends.get(XMPP.receiver.split("@")[0]).insert(XMPP.login,null,contentValues);
                    activity.getmService().xmpp.sendMessage(chatMessage);
                }
                msg_edittext.setText("");
            }
        });
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String body = intent.getStringExtra("body");
                String isMy = intent.getStringExtra("isMy");
                String isRead = intent.getStringExtra("isRead");
                String receiver = intent.getStringExtra("receiver");
                if (receiver.contains("@"))
                    receiver = receiver.split("@")[0];
                ContentValues contentValues = new ContentValues();
                contentValues.put("body",body);
                contentValues.put("isMy", isMy);
                contentValues.put("isRead", isRead);
                dbFriends.get(receiver).insert(XMPP.login,null,contentValues);
            }
        },new IntentFilter("insert"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String dbName = intent.getStringExtra("dbName");
                chatList.clear();
                Cursor cursor = dbFriends.get(dbName).rawQuery("SELECT * FROM (SELECT * FROM "+XMPP.login+" ORDER BY id DESC limit 20) ORDER BY id ASC",null);
                String body = "";
                boolean isMy = true;
                if (cursor.moveToFirst()){
                    int indexBody = cursor.getColumnIndex("body");
                    int indexIsMy = cursor.getColumnIndex("isMy");
                    do {
                        body = cursor.getString(indexBody);
                        isMy = Boolean.parseBoolean(cursor.getString(indexIsMy));
                        if (!body.equals("")){
                            final ChatMessage chatMessage = new ChatMessage(XMPP.login+"@jodo.im", XMPP.receiver,
                                    body, "" + random.nextInt(2100000000), isMy);
                            chatAdapter.add(chatMessage);
                        }
                    }while (cursor.moveToNext());
                }
            }
        },new IntentFilter("canReadDB"));
        msgListView.setAdapter(chatAdapter);
        return view;
    }

    public void sendTextMessage(View v) {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(XMPP.login+"@jodo.im", XMPP.receiver,
                    message, "" + random.nextInt(2100000000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.Time = CommonMethods.getCurrentTime();
            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            MenuActivity activity = ((MenuActivity) getActivity());
            ContentValues contentValues = new ContentValues();
            contentValues.put("body",message);
            contentValues.put("isMy", "true");
            contentValues.put("isRead", "true");
            dbFriends.get(XMPP.receiver.split("@")[0]).insert(XMPP.login,null,contentValues);
            activity.getmService().xmpp.sendMessage(chatMessage);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        /*if (((MenuActivity)getActivity()).whoami){
            if (viewMes.getText().toString().contains("Новая задача ")||
                    viewMes.getText().toString().contains("Начата работа над задачей ")){
                for (int i = 0, n = itemsContextMenuW.length; i < n; i++) {
                    menu.add(Menu.NONE, i, i, itemsContextMenuW[i]);
                }
            }
        }
        if(!((MenuActivity)getActivity()).whoami){
            if (viewMes.getText().toString().contains("Создана задача ") ||
                    viewMes.getText().toString().contains("Начата работа над задачей ") ||
                    viewMes.getText().toString().contains("Проверьте задачу ")){
                for (int i = 0, n = itemsContextMenuH.length; i < n; i++) {
                    menu.add(Menu.NONE, i, i, itemsContextMenuH[i]);
                }
            }
        }*/
        if (((ChatMessage)chatAdapter.getItem(info.position)).body.toString().contains("Создана задача ") ||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("Начата работа над задачей ") ||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("Проверьте задачу ")||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("Новая задача ")){
            for (int i = 0, n = itemsContextMenu.length; i < n; i++) {
                menu.add(Menu.NONE, i, i, itemsContextMenu[i]);
            }
        }
        else
            menu.clear();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;
    }

    public void createTask(String body){

    }
}