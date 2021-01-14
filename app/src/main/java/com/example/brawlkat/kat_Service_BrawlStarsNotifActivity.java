package com.example.brawlkat;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.DisplayMetrics;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.kat_Fragment.kat_SettingFragment;
import com.example.brawlkat.kat_broadcast_receiver.kat_ActionBroadcastReceiver;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class kat_Service_BrawlStarsNotifActivity extends Service {

    public                              static boolean                          alreadyStart = false;

    private                             RequestOptions                          options;
    public                              static int                              height;
    public                              static int                              width;


    kat_Player_MainActivity kat_player_mainActivity;

    private BroadcastReceiver broadcastReceiver;

    private final String BROADCAST_MASSAGE_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private final String BROADCAST_MASSAGE_SCREEN_OFF = "android.intent.action.SCREEN_OFF";

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
        kat_player_mainActivity = kat_Player_MainActivity.kat_player_mainActivity;

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alreadyStart = false;
        // 브로드캐스트 등록
        RegisterBroadcastReceiver();

        kat_NotificationUpdater updater = new kat_NotificationUpdater(kat_player_mainActivity.getApplicationContext());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "brawl stars play",
                    NotificationManager.IMPORTANCE_LOW);

            mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            updater.update();
            startForeground(1, notification.build());
        }

        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        kat_SettingFragment.serviceStarted = false;

        UnregisterBroadcastReceiver();
    }

    private void RegisterBroadcastReceiver(){

        if(broadcastReceiver != null) return;

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_MASSAGE_SCREEN_ON);
        filter.addAction(BROADCAST_MASSAGE_SCREEN_OFF);
        filter.addAction("com.example.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_START");
        filter.addAction("com.example.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_END");

        broadcastReceiver = new kat_ActionBroadcastReceiver(this, kat_player_mainActivity);

        registerReceiver(broadcastReceiver, filter);

        Intent ThreadCheckIntent = new Intent();
        if(kat_LoadBeforeMainActivity.kataSettingBase.getData("AnalyticsService") == 0){
            ThreadCheckIntent.setAction("com.example.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_END");
        }
        else{
            ThreadCheckIntent.setAction("com.example.brawlkat.kat_Service_BrawlStarsNotifActivity.CHECK_START");
        }
        sendBroadcast(ThreadCheckIntent);
    }

    private void UnregisterBroadcastReceiver(){

        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

}
