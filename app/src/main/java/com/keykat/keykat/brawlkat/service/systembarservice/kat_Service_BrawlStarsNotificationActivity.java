package com.keykat.keykat.brawlkat.service.systembarservice;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.keykat.keykat.brawlkat.service.util.NotificationContract;
import com.keykat.keykat.brawlkat.service.util.NotificationPresenter;
import com.keykat.keykat.brawlkat.service.util.NotificationUpdater;
import com.keykat.keykat.brawlkat.util.parser.kat_brawlersParser;
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser;
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

public class kat_Service_BrawlStarsNotificationActivity
        extends LifecycleService
        implements NotificationContract.View {

    @SuppressLint("StaticFieldLeak")
    public NotificationCompat.Builder notification;
    public NotificationManager notificationManager;
    private NotificationChannel channel;

    private NotificationPresenter notificationPresenter;
    public NotificationUpdater updater;

    private kat_eventsParser eventsParser;
    private kat_brawlersParser brawlersParser;
    private kat_mapsParser mapsParser;


    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        notificationPresenter = new NotificationPresenter(this);
        notificationPresenter.loadData();

        initChannel();
        initNotification();

        return START_STICKY;
    }

    public void initChannel() {
        channel = new NotificationChannel(
                "channel",
                "brawl stars play",
                NotificationManager.IMPORTANCE_LOW
        );
    }

    public void initNotification() {
        notificationManager
                = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.createNotificationChannel(channel);

        updater = new NotificationUpdater(this, notificationManager);
        notification = updater.getUpdatedNotification();
        startForeground(1, notification.build());
    }

    @Override
    public void updateService() {
        updater.update();
        notificationManager.notify(1, notification.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
