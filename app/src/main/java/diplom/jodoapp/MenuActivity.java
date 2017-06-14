package diplom.jodoapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import devlight.io.library.ntb.NavigationTabBar;
import diplom.jodoapp.fragments.ChatFragment;
import diplom.jodoapp.fragments.HelpFragment;

import diplom.jodoapp.fragments.PeopleFragment;
import diplom.jodoapp.fragments.TaskFragment;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MenuActivity extends AppCompatActivity{

    private static DBHelperContact dbHelperContact;
    private static SQLiteDatabase dbContacts;
    public static HashMap<String, SQLiteDatabase> dbFriends;
    TextView receiverTextView;
    ImageButton statusButton;
    public static boolean whoami;
    static boolean isCreateDB = false;
    private boolean mBounded;
    private XMPPServiceConnection mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            mService = ((LocalBinder<XMPPServiceConnection>) service).getService();
            mBounded = true;
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        receiverTextView = (TextView) findViewById(R.id.receiverTextView);
        statusButton = (ImageButton) findViewById(R.id.statusReceiverImageView);
        receiverTextView.setText(XMPP.receiver);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dbFriends.get(intent.getStringExtra("selectDB")).execSQL("create table if not exists "+XMPP.login+" (" +
                        "id integer primary key autoincrement," +
                        "body text," +
                        "isMy text," +
                        "isRead text" + ");");
            }
        },new IntentFilter("createTable"));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String dbName = intent.getStringExtra("dbName");
                addFriend(dbName);
            }
        },new IntentFilter("createFriendDB"));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ((FragmentPagerAdapter)viewPager.getAdapter()).getItem(0);
            }
        },new IntentFilter("updatePeople"));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tree = intent.getStringExtra("#tree");
                try {
                    String actuallyTask = tree.split(":\n")[1].split("\n")[0];
                    if(!actuallyTask.equals("")) {
                        ((TextView) findViewById(R.id.textView3)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.actualyTaskTextView)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.actualyTaskTextView)).setText(actuallyTask);
                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams)((LinearLayout) findViewById(R.id.statusTask)).getLayoutParams();
                        layoutParams.height = WRAP_CONTENT;
                        ((LinearLayout) findViewById(R.id.statusTask)).setLayoutParams(layoutParams);

                    }
                    else {
                        ((TextView) findViewById(R.id.textView3)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.actualyTaskTextView)).setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams)((LinearLayout) findViewById(R.id.statusTask)).getLayoutParams();
                        layoutParams.height = 0;
                        ((LinearLayout) findViewById(R.id.statusTask)).setLayoutParams(layoutParams);
                    }
                }catch (Exception e ){
                    ((TextView) findViewById(R.id.textView3)).setVisibility(View.INVISIBLE);
                    ((TextView) findViewById(R.id.actualyTaskTextView)).setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams)((LinearLayout) findViewById(R.id.statusTask)).getLayoutParams();
                    layoutParams.height = 0;
                    ((LinearLayout) findViewById(R.id.statusTask)).setLayoutParams(layoutParams);
                }
            }
        },new IntentFilter("actuallyTask"));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiverTextView.setText(intent.getStringExtra("setReceiver"));

                if (XMPP.isCreatedChat){
                    XMPP.Chat.close();
                }
                XMPP.isCreatedChat = false;
                String tree_command = getResources().getString(R.string.tree_command);
                String whoami_command = getResources().getString(R.string.whoami_command);
                getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,whoami_command,""+new Random().nextInt(2100000000),true));
                statusButton.setClickable(true);
                getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,tree_command,""+new Random().nextInt(2100000000),true));
            }
        },new IntentFilter("setReceiver"));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                 addFriend(intent.getStringExtra("dbName"));
            }
        },new IntentFilter("addFriend"));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tree_command = getResources().getString(R.string.tree_command);
                if(intent.getStringExtra("status").contains("worker")) {
                    statusButton.setBackgroundResource(R.drawable.head_button);
                    whoami = false;
                }
                else{
                    statusButton.setBackgroundResource(R.drawable.worker_button);
                    whoami = true;
                }
                getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,tree_command,""+new Random().nextInt(2100000000),true));
            }
        },new IntentFilter("status"));
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tree_command = getResources().getString(R.string.tree_command);
                String whoami_command = getResources().getString(R.string.whoami_command);
                String head = getResources().getString(R.string.head_command);
                String worker = getResources().getString(R.string.worker_command);
                if(whoami){
                    getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,worker,""+new Random().nextInt(2100000000),true));
                    statusButton.setBackgroundResource(R.drawable.worker_button);
                }
                else {
                    getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,head,""+new Random().nextInt(2100000000),true));
                    statusButton.setBackgroundResource(R.drawable.head_button);
                }
                getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,whoami_command,""+new Random().nextInt(2100000000),true));
                getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,tree_command,""+new Random().nextInt(2100000000),true));
            }
        });
        Cursor cursor = null;
        dbFriends = new HashMap<>();
        if (!isCreateDB) {
            dbHelperContact = new DBHelperContact(this, XMPP.login + "user", null, 1);
            dbContacts = dbHelperContact.getWritableDatabase();
            isCreateDB = true;
        }
        if(isCreateDB){
            cursor = dbContacts.query("contacts", null, "userJID = \"" + XMPP.login + "\"", null, null, null, null);
            if (cursor.moveToFirst()){
                int indexNameDB = cursor.getColumnIndex("friendJID");
                do {
                    String dbName = cursor.getString(indexNameDB).split("@")[0];
                    dbFriends.put(dbName, new DBHelperMessage(getBaseContext(), dbName, null, 1).getWritableDatabase());
                }while (cursor.moveToNext());
            }
        }
        initUI(); //установка внешненего вида ntb
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {  //получение необходимого контента(фрагмента) для определенного id выбранной страницы
            switch(position) {
                case 0:
                    PeopleFragment peopleFragment = new PeopleFragment();
                    return peopleFragment;
                case 1:
                    ChatFragment chatFragment = new ChatFragment();
                    return chatFragment;
                case 2:
                    TaskFragment taskFragment = new TaskFragment();
                    return taskFragment;
                case 3:
                    HelpFragment helpFragment = new HelpFragment();
                    return helpFragment;
            }
            return null;
        }

        @Override
        public int getCount() { //определение кол-ва страниц PageViewer
            return 4;
        }

    }
    ViewPager viewPager;
    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>(); //создание массима моделей ntb
        //далее идет заполнение массива моделей ntb и установка параметров каждой модели
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.workers3), Color.parseColor("#D3D3D3"))
                        .title("Люди")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.chat3), Color.parseColor("#D3D3D3"))
                        .title("Чат")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.task3), Color.parseColor("#D3D3D3"))
                        .title("Задачи")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.help3), Color.parseColor("#D3D3D3"))
                        .title("Помощь")
                        .build()
        );
        navigationTabBar.setBgColor(Color.parseColor("#FFFFFF")); //установка цвета ntb в белый цвет
        navigationTabBar.setIsTinted(false); //отключение наложение одноцветной маски на иконки
        navigationTabBar.setModels(models); //установка моделей ntb
        navigationTabBar.setViewPager(viewPager,0); //установка viewPager
        navigationTabBar.setInactiveColor(Color.parseColor("#A8A8A8"));
        navigationTabBar.setActiveColor(Color.parseColor("#000000"));
        // и начального таргет id(фрагмента, который будет отображен при запуске ативности)

    }

    public HashMap<String, SQLiteDatabase> getDBFriends(){
        return dbFriends;
    }

    public XMPPServiceConnection getmService() {
        return mService;
    }

    public SQLiteDatabase getDataBaseContacts(){
        return dbContacts;
    }

    public void addFriend(String dbName) {
        //DBHelperMessage dbHelperMessage = new DBHelperMessage(this, dbName, null, 1);
        //SQLiteDatabase sqLiteDatabase = dbHelperMessage.getWritableDatabase();
        dbFriends.put(dbName, new DBHelperMessage(this, dbName, null, 1).getWritableDatabase());
    }
}

