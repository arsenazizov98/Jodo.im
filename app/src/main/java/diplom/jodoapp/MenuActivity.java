package diplom.jodoapp;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class MenuActivity extends AppCompatActivity{

    private CoordinatorLayout menu;
    private RadioButton radioButtonWorkers;
    private RadioButton radioButtonBoss;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    PeopleFragment fragmentPeople = new PeopleFragment();
                    return fragmentPeople;
                case 1:
                    PeopleFragment fragmentPeople2 = new PeopleFragment();
                    return fragmentPeople2;
                case 2:
                    PeopleFragment fragmentPeople3 = new PeopleFragment();
                    return fragmentPeople3;
                case 3:
                    PeopleFragment fragmentPeople4 = new PeopleFragment();
                    return fragmentPeople4;
            }
            PeopleFragment fragmentPeople = new PeopleFragment();
            return fragmentPeople;
        }

        @Override
        public int getCount() {
            return 4;
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
}
