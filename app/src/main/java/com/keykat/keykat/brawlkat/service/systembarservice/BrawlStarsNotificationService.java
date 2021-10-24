package com.keykat.keykat.brawlkat.service.systembarservice;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.keykat.keykat.brawlkat.common.IntentKey;
import com.keykat.keykat.brawlkat.common.model.datasource.SharedPreferenceManager;
import com.keykat.keykat.brawlkat.service.model.data.NotificationData;
import com.keykat.keykat.brawlkat.service.util.NotificationContract;
import com.keykat.keykat.brawlkat.service.util.NotificationPresenter;
import com.keykat.keykat.brawlkat.service.util.NotificationUpdater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

public class BrawlStarsNotificationService
        extends LifecycleService
        implements NotificationContract.View {

    @SuppressLint("StaticFieldLeak")
    public NotificationCompat.Builder notification;
    public NotificationManager notificationManager;
    private NotificationChannel channel;

    private NotificationPresenter notificationPresenter;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        notificationPresenter = new NotificationPresenter(this);

        initChannel();
        initNotificationManager();
        initNotification(new NotificationData(null, null, null, null));
        fetchNotification(intent);

        return START_REDELIVER_INTENT;
    }

    public void initChannel() {
        channel = new NotificationChannel(
                "channel",
                "brawl stars play",
                NotificationManager.IMPORTANCE_LOW
        );
    }

    public void initNotificationManager() {
        notificationManager
                = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.createNotificationChannel(channel);
    }

    public void initNotification(NotificationData notificationData) {
        updater = new NotificationUpdater(this, notificationManager, notificationData);
        updater.update();
        notification = updater.getUpdatedNotification();
        startForeground(1, notification.build());
    }

    @Override
    public void updateService(@NonNull NotificationData notificationData) {
        updater = new NotificationUpdater(this, notificationManager, notificationData);
        updater.update();
        notification = updater.getUpdatedNotification();
        notificationManager.notify(1, notification.build());
    }

    public void fetchNotification(Intent intent) {
        try {
            String playerTag = intent.getStringExtra(IntentKey.START_SERVICE_WITH_PLAYER_TAG.getKey());
            if (playerTag == null) {

                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
                playerTag = sharedPreferenceManager.getAccount();
                if (playerTag != null && !playerTag.equals("")) {
                    playerTag = playerTag.substring(1);
                    notificationPresenter.loadData(
                            playerTag,
                            "players",
                            "official"
                    );
                }
            } else {
                notificationPresenter.loadData(
                        playerTag,
                        "players",
                        "official"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
