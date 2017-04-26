package diplom.jodoapp;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import java.util.ArrayList;
import devlight.io.library.ntb.NavigationTabBar;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MenuActivity extends AppCompatActivity{

    private CoordinatorLayout menu;
    private RadioButton radioButtonWorkers;
    private RadioButton radioButtonBoss;
    private RelativeLayout chatFragment;
    XMPPTCPConnection xmppConnection;
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
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(radioButtonWorkers.isChecked())
                    menu.setBackgroundColor(Color.parseColor("#FBC711"));
            }
        });
        radioButtonBoss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(radioButtonBoss.isChecked())
                    menu.setBackgroundColor(Color.parseColor("#45B735"));
            }
        });
        initUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        xmppConnection.disconnect();
        XmppConnectionHolder.getInstance().destroyConnection();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
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
            PeopleFragment fragmentPeople = new PeopleFragment();
            return fragmentPeople;
        }

        @Override
        public int getCount() {
            return 5;
        }

    }

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
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
        navigationTabBar.setBgColor(Color.parseColor("#FFFFFF"));
        navigationTabBar.setIsTinted(false);
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
