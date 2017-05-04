package diplom.jodoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by arsen on 03.05.2017.
 */

public class BotCommandService extends Service{

    @Override
    public IBinder onBind(Intent intent) {

        return new LocalBinder<BotCommandService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
