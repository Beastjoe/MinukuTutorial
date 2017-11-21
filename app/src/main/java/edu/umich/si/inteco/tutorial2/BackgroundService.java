package edu.umich.si.inteco.tutorial2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.umich.si.inteco.minuku.config.Constants;
import edu.umich.si.inteco.minuku.manager.MinukuStreamManager;

public class BackgroundService extends Service {
    MinukuStreamManager streamManager;
    private int count = 0;//used for intermittent sampling


    public BackgroundService() {
        super();
        streamManager = MinukuStreamManager.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        streamManager.updateStreamGenerators();
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 5000,
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );
        return START_STICKY_COMPATIBILITY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
