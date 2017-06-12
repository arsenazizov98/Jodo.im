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
import android.widget.ListView;

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
    private static String[] itemsContextMenuW = new String[]{"start", "done"};
    private static String[] itemsContextMenuH = new String[]{"no","ok","close"};
    private int numTask = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        random = new Random();
        msg_edittext = (EditText) view.findViewById(R.id.messageEditText);
        msgListView = (ListView) view.findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton)view.findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 sendTextMessage(msg_edittext.getEditableText().toString());
            }
        });
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        dbFriends = ((MenuActivity)getActivity()).getDBFriends();
        chatList = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(getActivity(), chatList);
        registerForContextMenu(msgListView);
        ImageButton addButton = (ImageButton)view.findViewById(R.id.addTaskButton);
        msgListView.setAdapter(chatAdapter);
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
                    activity.getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#tree",""+new Random().nextInt(2100000000),true));
                    msg_edittext.setText("");
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
                try {
                    dbFriends.get(receiver).insert(XMPP.login, null, contentValues);
                }catch (NullPointerException e){
                    try {
                        Intent intent1 = new Intent("createFriendDB").putExtra("dbName", receiver);
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent1);
                        dbFriends.get(receiver).insert(XMPP.login, null, contentValues);
                    }catch (NullPointerException ee){
                        Intent intent1 = new Intent("createTable").putExtra("selectDB", receiver);
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent1);
                        dbFriends.get(receiver).insert(XMPP.login, null, contentValues);
                    }
                }
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
        return view;
    }

    public void sendTextMessage(String body) {
        String message = body;
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
        if (((ChatMessage)chatAdapter.getItem(info.position)).body.toString().contains("Создана задача ") ||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("Начата работа над задачей ") ||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("Проверьте задачу ")||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("Новая задача ")||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("On the task ")||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains(" is created")||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("New task ")||
                ((ChatMessage)chatAdapter.getItem(info.position)).body.toString().toString().contains("Check the task ")){
            String[] parsMas = ((ChatMessage)chatAdapter.getItem(info.position)).body.split(" ");
            for (int i = 0, n = parsMas.length; i < n; i++){
                try {
                    String[] str;
                    if (parsMas[i].contains(".\n")) {
                        str = parsMas[i].split(".\n");
                        numTask = Integer.parseInt(str[0]);
                        break;
                    }
                    if (parsMas[i].contains("\n")){
                        str = parsMas[i].split("\n");
                        numTask = Integer.parseInt(str[0]);
                        break;
                    }
                    if (parsMas[i].contains(".")) {
                        numTask = Integer.parseInt(parsMas[i].replace(".",""));
                        break;
                    }
                    else{
                        numTask = Integer.parseInt(parsMas[i]);
                        break;
                    }
                }catch (Exception e){}
            }
            menu.setHeaderTitle("Задача " + String.valueOf(numTask));
            if (((MenuActivity)getActivity()).whoami){
                    for (int i = 0, n = itemsContextMenuW.length; i < n; i++) {
                        menu.add(Menu.NONE, i, i, itemsContextMenuW[i]);
                    }
            }
            else{
                    for (int i = 0, n = itemsContextMenuH.length; i < n; i++) {
                        menu.add(Menu.NONE, i+2, i, itemsContextMenuH[i]);
                    }
            }
        }
        else
            menu.clear();
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==0) {
            sendTextMessage("#start " + String.valueOf(numTask));
            return true;
        }
        if (item.getItemId()==1) {
            sendTextMessage("#done " + String.valueOf(numTask));
            return true;
        }
        if (item.getItemId()==2) {
            sendTextMessage("#no " + String.valueOf(numTask));
            return true;
        }
        if (item.getItemId()==3) {
            sendTextMessage("#ok " + String.valueOf(numTask));
            return true;
        }
        if (item.getItemId()==4) {
            sendTextMessage("#close " + String.valueOf(numTask));
            return true;
        }
        return true;
    }
}