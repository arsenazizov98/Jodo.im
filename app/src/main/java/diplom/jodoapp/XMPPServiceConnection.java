package diplom.jodoapp;

/**
 * Created by arsen on 28.04.2017.
 */
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

public class XMPPServiceConnection extends Service {
    private static String DOMAIN="jodo.im";
    private static String USERNAME = "";
    private static String PASSWORD = "";
    private boolean isLogin = false;
    public static XMPP xmpp = null;

    @Override
    public IBinder onBind(final Intent intent)  {
        USERNAME = intent.getStringExtra("login");
        PASSWORD = intent.getStringExtra("pass");
        xmpp = XMPP.getInstance(XMPPServiceConnection.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect();
        return new LocalBinder<XMPPServiceConnection>(this); //заимствовано с информационного ресурса http://www.tutorialsface.com
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        xmpp.xmpptcpConnection.disconnect();
    }

    boolean isLogin() {
        return isLogin;
    }

    void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    Intent getIntentIsLogin(){
        return new Intent(XMPPServiceConnection.this,MenuActivity.class).putExtra("isLogin",this.isLogin);
    }

    void sendIsLoginToActivity() {
        Intent intent = new Intent("isLogin").putExtra("isLogin", isLogin);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}