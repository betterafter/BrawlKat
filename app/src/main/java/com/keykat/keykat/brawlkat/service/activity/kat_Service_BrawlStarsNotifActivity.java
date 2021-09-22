package com.keykat.keykat.brawlkat.service.activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.keykat.keykat.brawlkat.service.util.kat_NotificationUpdater;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class kat_Service_BrawlStarsNotifActivity extends Service {

    public static boolean alreadyStart = false;
    public static BroadcastReceiver broadcastReceiver;

    @SuppressLint("StaticFieldLeak")
    public static NotificationCompat.Builder notification;
    public static NotificationManager mNotificationManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alreadyStart = false;
        kat_NotificationUpdater updater = new kat_NotificationUpdater(this.getApplicationContext());
        NotificationChannel channel = new NotificationChannel("channel", "brawl stars play",
                NotificationManager.IMPORTANCE_LOW);

        mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        mNotificationManager.createNotificationChannel(channel);

        updater.update();

        startForeground(1, notification.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
