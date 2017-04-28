package diplom.jodoapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    public static EditText loginEdit;
    public static EditText passEdit;
    private Button authorisation;
    private Button registration;
    private final String HOST = "jodo.im";
    private final String DOMAIN = "jodo.im";
    private final int port = 5222;
    private String login = "";
    private String pass = "";
    private boolean isDiscon = false;
    private static final String TAG = "MenuActivity";
    private boolean mBounded;
    private MyService mService;
    MyXMPP myXMPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        doBindService();
        loginEdit = (EditText) findViewById(R.id.login);
        passEdit = (EditText) findViewById(R.id.pass);
        authorisation = (Button) findViewById(R.id.autorisation);
        registration = (Button) findViewById(R.id.registration);
        authorisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginEdit.getText().toString();
                pass = passEdit.getText().toString();
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

    }

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

    @Override
    protected void onResume() {
        super.onResume();
        passEdit.setText("");
        loginEdit.setText("");
    }
}
