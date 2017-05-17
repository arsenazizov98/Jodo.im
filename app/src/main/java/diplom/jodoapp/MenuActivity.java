package diplom.jodoapp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import devlight.io.library.ntb.NavigationTabBar;
import diplom.jodoapp.fragments.ChatFragment;
import diplom.jodoapp.fragments.HelpFragment;

import diplom.jodoapp.fragments.PeopleFragment;
import diplom.jodoapp.fragments.TaskFragment;

public class MenuActivity extends AppCompatActivity{

    private CoordinatorLayout menu; //Слой с компонентами menu_activity
    private XMPPServiceConnection mService;
    private static DBHelperContact dbHelperContact;
    private static SQLiteDatabase dbContacts;
    public static HashMap<String, SQLiteDatabase> dbFriends;
    public static boolean isCreateDB = false;
    TextView receiverTextView;
    ImageButton statusButton;
    boolean whoami;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menu = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        receiverTextView = (TextView) findViewById(R.id.receiverTextView);
        statusButton = (ImageButton) findViewById(R.id.statusReceiverImageView);
        XMPP.receiver = "bot@bot.jodo.im";
        receiverTextView.setText(XMPP.receiver);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                addFriend(intent.getStringExtra("selectDB"));
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
                DBHelperMessage dbHelperMessage = new DBHelperMessage(getmService(), dbName , null, 1);
                SQLiteDatabase dbFriend = dbHelperMessage.getWritableDatabase();
                dbFriends.put(dbName, dbFriend);
            }
        },new IntentFilter("addFriend"));
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiverTextView.setText(intent.getStringExtra("setReceiver"));
                if (XMPP.isCreatedChat){
                    XMPP.Chat.close();
                }
                XMPP.isCreatedChat = false;
                getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#whoami",""+new Random().nextInt(2100000000),true));
                statusButton.setClickable(true);
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
                if(intent.getStringExtra("status").equals("worker")) {
                    statusButton.setBackgroundResource(R.drawable.worker_button);
                    whoami = false;
                }
                else {
                    statusButton.setBackgroundResource(R.drawable.head_button);
                    whoami = true;
                }
            }
        },new IntentFilter("status"));
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whoami){
                    getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#head",""+new Random().nextInt(2100000000),true));
                }
                else {
                    getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#worker",""+new Random().nextInt(2100000000),true));
                }
                getmService().xmpp.sendMessage(new ChatMessage(XMPP.login,XMPP.receiver,"#whoami",""+new Random().nextInt(2100000000),true));
            }
        });
        if (!isCreateDB) {
            dbHelperContact = new DBHelperContact(this, XMPP.login+"user", null, 1);
            dbContacts = dbHelperContact.getWritableDatabase();
            Cursor cursor = dbContacts.query("contacts",null,null,null,null,null,null,null);
            if (cursor.moveToFirst()){
                dbFriends = new HashMap<>();
                int indexNameDB = cursor.getColumnIndex("friendJID");
                do {
                    String dbName = cursor.getString(indexNameDB).split("@")[0];
                    dbFriends.put(dbName, new DBHelperMessage(getBaseContext(), dbName, null, 1).getWritableDatabase());
                }while (cursor.moveToNext());
            }
            isCreateDB = true;
        }
        /*ContentValues contentValues = new ContentValues();
        contentValues.put("userJID",XMPP.login);
        contentValues.put("friendJID",XMPP.receiver);
        dbContacts.insert("contacts",null,contentValues);*/
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

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>(); //создание массима моделей ntb
        //далее идет заполнение массива моделей ntb и установка параметров каждой модели
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.workers3),
                        Color.parseColor("#D3D3D3"))
                        .title("Люди")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.chat3),
                        Color.parseColor("#D3D3D3"))
                        .title("Чат")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.task3),
                        Color.parseColor("#D3D3D3"))
                        .title("Задачи")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.help3),
                        Color.parseColor("#D3D3D3"))
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

    public void addFriend(String dbName){
        DBHelperMessage dbHelperMessage = new DBHelperMessage(this, dbName, null, 1);
        SQLiteDatabase dbFriend = dbHelperMessage.getWritableDatabase();
        dbFriends.put(dbName, dbFriend);
        dbFriends.get(dbName);
    }
}
