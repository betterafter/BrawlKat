package com.keykat.keykat.brawlkat.service.activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.keykat.keykat.brawlkat.service.util.NotificationUpdater;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

public class kat_Service_BrawlStarsNotificationActivity extends LifecycleService {

    @SuppressLint("StaticFieldLeak")
    public NotificationCompat.Builder notification;
    public static NotificationManager mNotificationManager;

    public NotificationUpdater updater;


    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        updater = new NotificationUpdater(this, notification);
        updater.update();
        notification = updater.updatedNotification();

        Observer<kat_official_playerInfoParser.playerData> observer = playerData -> {
            updater.update();
        };
        KatData.eventsPlayerData.observe(this, observer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        NotificationChannel channel = new NotificationChannel(
                "channel",
                "brawl stars play",
                NotificationManager.IMPORTANCE_LOW
        );

        mNotificationManager
                = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        mNotificationManager.createNotificationChannel(channel);

        if (notification != null) startForeground(1, notification.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
