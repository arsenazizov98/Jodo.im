package diplom.jodoapp;

/**
 * Created by arsen on 28.04.2017.
 */
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class XMPPServiceConnection extends Service {
    static String DOMAIN="jodo.im";
    static String USERNAME = "";
    private static String PASSWORD = "";
    private boolean isLogin = false;
    public static XMPP xmpp = null;

    @Override
    public IBinder onBind(final Intent intent)  {
        USERNAME = intent.getStringExtra("login");
        PASSWORD = intent.getStringExtra("pass");
        xmpp = XMPP.getInstance(XMPPServiceConnection.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("login").putExtra("login", USERNAME));
        //заимствовано с информационного ресурса http://www.tutorialsface.com
        return new LocalBinder<XMPPServiceConnection>(this);
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId){

        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        xmpp=null;
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

    void sendIsLoginToActivity() {
        Intent intent = new Intent("isLogin").putExtra("isLogin", isLogin);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void sendTreeCommand(String command){
        Intent intentTask = new Intent("#tree").putExtra("#tree", command);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentTask);
        Intent intentActuallyTask = new Intent("actuallyTask").putExtra("#tree",command);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentActuallyTask);
    }

    void sendTreeAllCommand(String command){
        Intent intentTask = new Intent("#tree").putExtra("#tree", command);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentTask);
    }

    void sendWorker(){
        Intent intent = new Intent("status").putExtra("status","worker");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void sendHead(){
        Intent intent = new Intent("status").putExtra("status","head");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}