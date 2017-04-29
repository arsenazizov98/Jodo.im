package diplom.jodoapp;

/**
 * Created by arsen on 28.04.2017.
 */
import org.jivesoftware.smack.chat.Chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

public class MyService extends Service {
    public static String DOMAIN="jodo.im";
    public static String USERNAME = "arsenazizov";
    public static String PASSWORD = "sparta33";
    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    public static boolean ServerchatCreated = false;
    LoginActivity loginActivity;
    String text = "";

    @Override
    public IBinder onBind(final Intent intent) {
        USERNAME = intent.getStringExtra("login");
        PASSWORD = intent.getStringExtra("pass");
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = MyXMPP.getInstance(MyService.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect("onBind");
        return new LocalBinder<MyService>(this);
    }

    public Chat chat;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setData(String DOMAIN, String USERNAME, String PASSWORD){
        this.DOMAIN = DOMAIN;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId){
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xmpp.connection.disconnect();
    }

    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null;
    }
}