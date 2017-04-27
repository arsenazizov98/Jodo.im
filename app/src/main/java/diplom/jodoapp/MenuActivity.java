package diplom.jodoapp;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;

import java.util.ArrayList;
import devlight.io.library.ntb.NavigationTabBar;

public class MenuActivity extends AppCompatActivity{

    ChatManagerListener chatListener;
    ChatMessageListener messageListener;
    public Chat chat;
    ChatFragment chatFragment = new ChatFragment();
    private CoordinatorLayout menu; //Слой с компонентами menu_activity
    private RadioButton radioButtonWorkers;//radioButton включает режим Испольнителя
    private RadioButton radioButtonBoss; //radioButton включает режим Заказчика
    XMPPTCPConnection xmppConnection; //в переменную заносится коннект, созданый в LoginActivity
    private Button sendMessage;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        xmppConnection = XmppConnectionHolder.getInstance().getConnection();
        menu = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        radioButtonWorkers = (RadioButton) findViewById(R.id.radioButtonWorker);
        radioButtonBoss = (RadioButton) findViewById(R.id.radioButtonBoss);
        radioButtonWorkers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //проверка на изменение checked свойства объекта radioButton
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(radioButtonWorkers.isChecked())
                    menu.setBackgroundColor(Color.parseColor("#FBC711"));
                //изменение свойства background, в зависимости от значения свойства checked
            }
        });
        radioButtonBoss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(radioButtonBoss.isChecked())
                    menu.setBackgroundColor(Color.parseColor("#45B735"));
            }
        });
        initUI(); //установка внешненего вида ntb
        initChat();
    }

    private void initChat(){
        try {
            String opt_jidStr = "arsentest@jodo.im";
            messageListener = new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    chatFragment.createTextView(message.getBody());
                }
            };
            chatListener = new ChatManagerListener() {

                @Override
                public void chatCreated(Chat chatCreated, boolean local) {
                    if (chat != null) {
                        if (chat.getParticipant().toString().equals(
                                chatCreated.getParticipant().toString())) {
                            chat.removeMessageListener(messageListener);
                            chat = chatCreated;
                            chat.addMessageListener(messageListener);
                        }
                    } else {
                        chat = chatCreated;
                        chat.addMessageListener(messageListener);
                    }
                }
            };
            ChatManager.getInstanceFor(xmppConnection)
            .addChatListener(chatListener);
            ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(xmppConnection);
            sdm.addFeature("jabber.org/protocol/si");
            sdm.addFeature("http://jabber.org/protocol/si");
            sdm.addFeature("http://jabber.org/protocol/disco#info");
            sdm.addFeature("jabber:iq:privacy");


            try {
                if (chat == null) {
                    chat = ChatManager.getInstanceFor(
                            xmppConnection)
                            .createChat(opt_jidStr, "95 строка кода",messageListener);
                } else {
                    chat.addMessageListener(messageListener);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
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
                    TaskFragment taskFragment = new TaskFragment();
                    return taskFragment;
                case 2:
                    LogFragment logFragment = new LogFragment();
                    return logFragment;
                case 3:
                    return chatFragment;
                case 4:
                    HelpFragment helpFragment = new HelpFragment();
                    return helpFragment;
            }
            return null;
        }

        @Override
        public int getCount() { //определение кол-ва страниц PageViewer
            return 5;
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
                        Color.parseColor("#C4C4C4"))
                        .title("Люди")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.task3),
                        Color.parseColor("#C4C4C4"))
                        .title("Задачи")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.logs3),
                        Color.parseColor("#C4C4C4"))
                        .title("Действия")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.chat3),
                        Color.parseColor("#C4C4C4"))
                        .title("Чат")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.help3),
                        Color.parseColor("#C4C4C4"))
                        .title("Помощь")
                        .build()
        );
        navigationTabBar.setBgColor(Color.parseColor("#FFFFFF")); //установка цвета ntb в белый цвет
        navigationTabBar.setIsTinted(false); //отключение наложение одноцветной маски на иконки
        navigationTabBar.setModels(models); //установка моделей ntb
        navigationTabBar.setViewPager(viewPager,0); //установка viewPager
        // и начального таргет id(фрагмента, который будет отображен при запуске ативности)
    }

    @Override
    protected void onDestroy() { //вызывается при выходе(уничтожении) из данного активит
        super.onDestroy();
        xmppConnection.disconnect();
        XmppConnectionHolder.getInstance().destroyConnection();
        // производится обнуление объекта, который хранит коннект и дисконнект сервера
    }

    public void sendMessage(String body, String toJid) {
        try {
            chat = ChatManager.getInstanceFor(xmppConnection)
                    .createChat(toJid);
            chat.sendMessage(body);
        } catch (Exception e) {
        }
    }
}
