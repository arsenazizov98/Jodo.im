package diplom.jodoapp.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
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
    private static String[] itemsContextMenuW = new String[]{"start", "done","копироать"};
    private static String[] itemsContextMenuH = new String[]{"no","ok","close","копировать"};
    private int numTask = 0;
    private double numTaskDouble = 0;
    private boolean isInt;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_chat, container, false);
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
                    boolean isOrange = false;
                    final ChatMessage chatMessage = new ChatMessage(XMPP.login+XMPP.HOST, XMPP.receiver,
                            nameTask, "" + random.nextInt(2100000000), true,isOrange);
                    chatMessage.setMsgID();
                    chatMessage.body = nameTask;
                    chatMessage.Date = CommonMethods.getCurrentDate();
                    chatMessage.Time = CommonMethods.getCurrentTime();
                    msg_edittext.setText("");
                    chatList.add(chatMessage);
                    chatAdapter.notifyDataSetChanged();
                    MenuActivity activity = ((MenuActivity) getActivity());
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("body", nameTask);
                    contentValues.put("isMy", "true");
                    contentValues.put("isOrange", String.valueOf(isOrange));
                    contentValues.put("isRead", "true");
                    dbFriends.get(XMPP.receiver.split("@")[0]).insert(XMPP.login,null,contentValues);
                    activity.getmService().xmpp.sendMessage(new ChatMessage(XMPP.login+XMPP.HOST, XMPP.receiver,command, "" + random.nextInt(2100000000), true,isOrange));
                    activity.getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#tree",""+new Random().nextInt(2100000000),true,false));
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
                String isOrange = intent.getStringExtra("isOrange");
                String isRead = intent.getStringExtra("isRead");
                String receiver = intent.getStringExtra("receiver");
                if (receiver.contains("@"))
                    receiver = receiver.split("@")[0];
                ContentValues contentValues = new ContentValues();
                contentValues.put("body",body);
                contentValues.put("isMy", isMy);
                contentValues.put("isOrange", isOrange);
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
                boolean isOrange;
                if (cursor.moveToFirst()){
                    int indexBody = cursor.getColumnIndex("body");
                    int indexIsMy = cursor.getColumnIndex("isMy");
                    int indexIsOrange = cursor.getColumnIndex("isOrange");
                    do {
                        body = cursor.getString(indexBody);
                        isMy = Boolean.parseBoolean(cursor.getString(indexIsMy));
                        isOrange = Boolean.parseBoolean(cursor.getString(indexIsOrange));
                        if (!body.equals("")){
                            final ChatMessage chatMessage = new ChatMessage(XMPP.login+"@jodo.im", XMPP.receiver,
                                    body, "" + random.nextInt(2100000000), isMy,isOrange);
                            chatList.add(chatMessage);
                            chatAdapter.notifyDataSetChanged();
                        }
                    }while (cursor.moveToNext());
                }
            }
        },new IntentFilter("canReadDB"));
        return view;
    }

    public void sendTextMessage(String body) {
        String message = body;
        boolean isOrange;
        if (message.contains(view.getResources().getString(R.string.create_task_ru)) ||
                message.contains(view.getResources().getString(R.string.create_task_en))||
                message.contains(view.getResources().getString(R.string.new_task_en))||
                message.contains(view.getResources().getString(R.string.new_task_ru))){
            isOrange = true;
        }
        else
            isOrange =false;
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(XMPP.login+"@jodo.im", XMPP.receiver,
                    message, "" + random.nextInt(2100000000), true,isOrange);
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
            contentValues.put("isOrange", String.valueOf(isOrange));
            contentValues.put("isRead", "true");
            dbFriends.get(XMPP.receiver.split("@")[0]).insert(XMPP.login,null,contentValues);
            activity.getmService().xmpp.sendMessage(chatMessage);
        }
    }

    String copy ="";
    LinearLayout changeView;
    boolean orangeReceive = false;
    boolean orangeSend = false;
    int infoPosition;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (((ChatMessage)chatList.get(info.position)).isOrange){
                orangeSend = false;
                orangeReceive = true;
        }
        else {
            orangeReceive = false;
            orangeSend = false;
        }
        infoPosition = info.position;
        copy = ((ChatMessage)chatAdapter.getItem(info.position)).body;
        String checkStr = ((ChatMessage)chatAdapter.getItem(info.position)).body.toString();
        if (checkStr.contains(getResources().getString(R.string.create_task_ru)) ||
                checkStr.contains(getResources().getString(R.string.start_task_ru)) ||
                checkStr.contains(getResources().getString(R.string.check_task_ru))||
                checkStr.contains(getResources().getString(R.string.new_task_ru))||
                checkStr.contains(getResources().getString(R.string.create_task_en))||
                checkStr.contains(getResources().getString(R.string.start_task_en))||
                checkStr.contains(getResources().getString(R.string.new_task_en))||
                checkStr.contains(getResources().getString(R.string.check_task_en))||
                checkStr.contains(getResources().getString(R.string.reopen_task_en))||
                checkStr.contains(getResources().getString(R.string.reopen_task_ru))||
                checkStr.contains(getResources().getString(R.string.top_task_en))||
                checkStr.contains(getResources().getString(R.string.top_task_ru))||
                checkStr.contains(getResources().getString(R.string.bottom_task_ru))||
                checkStr.contains(getResources().getString(R.string.up_task_en))||
                checkStr.contains(getResources().getString(R.string.up_task_ru))||
                checkStr.contains(getResources().getString(R.string.down_task_ru))){
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
                        String strr = parsMas[i].substring(0,parsMas[i].length()-1);
                        if (strr.matches("\\d+\\.\\d")) {
                            numTaskDouble = Double.parseDouble(strr);
                            isInt = false;
                        }
                        else {
                            numTask = Integer.parseInt(strr);
                            isInt = true;
                        }
                        break;
                    }
                    else{
                        numTask = Integer.parseInt(parsMas[i]);
                        isInt = true;
                        break;
                    }
                }catch (Exception e){

                }
            }
            if (isInt)
                menu.setHeaderTitle("Задача " + String.valueOf(numTask));
            else
                menu.setHeaderTitle("Задача " + String.valueOf(numTaskDouble));
            if (!((MenuActivity)getActivity()).whoami){
                    for (int i = 0, n = itemsContextMenuW.length; i < n; i++) {
                        menu.add(Menu.NONE, i, i, itemsContextMenuW[i]);
                    }
            }
            else{
                    for (int i = 0, n = itemsContextMenuH.length; i < n; i++) {
                        menu.add(Menu.NONE, i+3, i, itemsContextMenuH[i]);
                    }
            }
        }
        else
            menu.add(Menu.NONE, 7, 7, "копировать");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==0) {
            if (isInt)
                sendTextMessage(getResources().getString(R.string.start_command)+" " + String.valueOf(numTask));
            else
                sendTextMessage(getResources().getString(R.string.start_command)+" " + String.valueOf(numTaskDouble));
            ((MenuActivity) getActivity()).getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#tree",""+new Random().nextInt(2100000000),true,false));
            return true;
        }
        if (item.getItemId()==1) {
            if (isInt)
                sendTextMessage(getResources().getString(R.string.done_command)+" " + String.valueOf(numTask));
            else
                sendTextMessage(getResources().getString(R.string.done_command)+" " + String.valueOf(numTaskDouble));
            ((MenuActivity) getActivity()).getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#tree",""+new Random().nextInt(2100000000),true,false));
            return true;
        }
        if (item.getItemId()==2) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", copy);
            clipboard.setPrimaryClip(clip);
            return true;
        }
        if (item.getItemId()==3) {
            if (isInt)
                sendTextMessage(getResources().getString(R.string.no_command)+" " + String.valueOf(numTask));
            else
                sendTextMessage(getResources().getString(R.string.no_command)+" " + String.valueOf(numTaskDouble));
            ((MenuActivity) getActivity()).getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#tree",""+new Random().nextInt(2100000000),true,false));
            return true;
        }
        if (item.getItemId()==4) {
            if (isInt)
                sendTextMessage(getResources().getString(R.string.ok_command)+" " + String.valueOf(numTask));
            else
                sendTextMessage(getResources().getString(R.string.ok_command)+" " + String.valueOf(numTaskDouble));
            ((MenuActivity) getActivity()).getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#tree",""+new Random().nextInt(2100000000),true,false));
            return true;
        }
        if (item.getItemId()==5) {
            if (isInt)
                sendTextMessage(getResources().getString(R.string.close_command)+" " + String.valueOf(numTask));
            else
                sendTextMessage(getResources().getString(R.string.close_command)+" " + String.valueOf(numTaskDouble));
            ((MenuActivity) getActivity()).getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#tree",""+new Random().nextInt(2100000000),true,false));
            return true;
        }
        if (item.getItemId()==6) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", copy);
            clipboard.setPrimaryClip(clip);
            return true;
        }
        if (item.getItemId()==7) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", copy);
            clipboard.setPrimaryClip(clip);
            return true;
        }
        return true;
    }
}