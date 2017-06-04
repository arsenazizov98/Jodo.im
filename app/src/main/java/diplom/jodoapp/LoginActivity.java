package diplom.jodoapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    public static EditText loginEdit;
    public static EditText passEdit;
    private Button authorisation;
    private Button registration;
    public static String login = "";
    boolean isLogin = true;
    public static String pass = "";
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
        setContentView(R.layout.activity_login);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isLogin = intent.getBooleanExtra("isLogin",false);
                if (isLogin)
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                else
                    doUnbindService();
            }
        },new IntentFilter("isLogin"));

        authorisation = (Button) findViewById(R.id.autorisation);
        registration = (Button) findViewById(R.id.registration);
        loginEdit = (EditText) findViewById(R.id.login);
        passEdit = (EditText) findViewById(R.id.pass);
        authorisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginEdit.getText().toString();
                pass = passEdit.getText().toString();
                doBindService();
            }
        });
    }

    public void doBindService() {
        Intent intent = new Intent(this, XMPPServiceConnection.class).putExtra("pass",pass).putExtra("login",login);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void doUnbindService() {
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }

    public XMPPServiceConnection getService() {
        return mService;
    }

    @Override
    protected void onResume() {
        super.onResume();
        passEdit.setText("");
        loginEdit.setText("");
        if (mBounded)
            doUnbindService();
    }
}
