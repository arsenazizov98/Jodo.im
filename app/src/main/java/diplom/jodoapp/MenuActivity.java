package diplom.jodoapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
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
import diplom.jodoapp.fragments.ChatFragment;
import diplom.jodoapp.fragments.HelpFragment;
import diplom.jodoapp.fragments.LogFragment;
import diplom.jodoapp.fragments.PeopleFragment;
import diplom.jodoapp.fragments.TaskFragment;

public class MenuActivity extends AppCompatActivity{

    private static final String TAG = "MenuActivity";
    private boolean mBounded;
    private MyService mService;
    ChatManagerListener chatListener;
    ChatMessageListener messageListener;
    public Chat chat;
    private CoordinatorLayout menu; //Слой с компонентами menu_activity
    private RadioButton radioButtonWorkers;//radioButton включает режим Испольнителя
    private RadioButton radioButtonBoss; //radioButton включает режим Заказчика
    private Button sendMessage;

    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            mService = ((LocalBinder<MyService>) service).getService();
            mBounded = true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        doBindService();
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
                    ChatFragment chatFragment = new ChatFragment();
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
        navigationTabBar.setViewPager(viewPager,3); //установка viewPager
        // и начального таргет id(фрагмента, который будет отображен при запуске ативности)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    void doBindService() {
        bindService(new Intent(this, MyService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }

    public MyService getmService() {
        return mService;
    }
}
