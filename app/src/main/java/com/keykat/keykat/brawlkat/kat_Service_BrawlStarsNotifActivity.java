package com.keykat.keykat.brawlkat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.keykat.keykat.brawlkat.kat_Fragment.kat_SettingFragment;
import com.keykat.keykat.brawlkat.kat_broadcast_receiver.kat_ActionBroadcastReceiver;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class kat_Service_BrawlStarsNotifActivity extends Service {

    public                              static boolean                          alreadyStart = false;



    private kat_Player_MainActivity kat_player_mainActivity;

    public static BroadcastReceiver broadcastReceiver;

    private static final String BROADCAST_MASSAGE_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private static final String BROADCAST_MASSAGE_SCREEN_OFF = "android.intent.action.SCREEN_OFF";

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

        // 브로드캐스트 등록
        try {
            RegisterBroadcastReceiver();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        kat_player_mainActivity = kat_Player_MainActivity.kat_player_mainActivity;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alreadyStart = false;
        kat_NotificationUpdater updater = new kat_NotificationUpdater(this.getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "brawl stars play",
                    NotificationManager.IMPORTANCE_LOW);

            mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);


            updater.update();

            // 의심 1. notification = null 인건가?
            startForeground(1, notification.build());
        }

        return START_STICKY;
    }






    @Override
    public void onDestroy() {
        super.onDestroy();

        kat_SettingFragment.serviceStarted = false;
        try {
            UnregisterBroadcastReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void RegisterBroadcastReceiver() {

        if(kat_LoadBeforeMainActivity.kataSettingBase == null) return;
        if(kat_LoadBeforeMainActivity.kataSettingBase.getData("AnalyticsService") == 0) return;
        if(broadcastReceiver != null) return;

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_MASSAGE_SCREEN_ON);
        filter.addAction(BROADCAST_MASSAGE_SCREEN_OFF);
        filter.addAction("com.keykat.keykat.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_START");
        filter.addAction("com.keykat.keykat.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_END");

        broadcastReceiver = new kat_ActionBroadcastReceiver(this, kat_player_mainActivity);

        registerReceiver(broadcastReceiver, filter);

        Intent ThreadCheckIntent = new Intent();
        if(kat_LoadBeforeMainActivity.kataSettingBase.getData("AnalyticsService") == 0){
            ThreadCheckIntent.setAction("com.keykat.keykat.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_END");
        }
        else{
            ThreadCheckIntent.setAction("com.keykat.keykat.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_START");
        }
        sendBroadcast(ThreadCheckIntent);
    }

    private void UnregisterBroadcastReceiver() {

        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

}
