package diplom.jodoapp;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class MenuActivity extends AppCompatActivity {

    private Switch mySwitch;
    private boolean bSwitch = false;
    private CoordinatorLayout menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mySwitch = (Switch) findViewById(R.id.switch1);
        menu = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bSwitch) {
                    menu.setBackgroundColor(Color.parseColor("#FBC711"));
                    bSwitch=true;
                }
                else{

                    menu.setBackgroundColor(Color.parseColor("#45B735"));
                    bSwitch=false;
                }
            }
        });

    }
}
