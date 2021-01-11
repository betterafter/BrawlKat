package com.example.brawlkat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.example.brawlkat.kat_Fragment.kat_SettingFragment;
import com.example.brawlkat.kat_broadcast_receiver.kat_ActionBroadcastReceiver;
import com.example.brawlkat.kat_broadcast_receiver.kat_ButtonBroadcastReceiver;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class kat_Service_BrawlStarsNotifActivity extends Service {

    public static boolean alreadyStart = false;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alreadyStart = false;

        RegisterBroadcastReceiver();

        kat_official_playerInfoParser.playerData playerData = kat_LoadBeforeMainActivity.eventsPlayerData;
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.main_notification);
        int seasonRewards;
        int seasonReset;

        if(playerData != null) {
            kat_SeasonRewardsCalculator seasonRewardsCalculator = new kat_SeasonRewardsCalculator(playerData);

            seasonRewards = seasonRewardsCalculator.SeasonsRewardsCalculator();
            seasonReset = seasonRewardsCalculator.SeasonsResetTrophiesCalculator();

            contentView.setTextViewText(R.id.title, playerData.getName());
            contentView.setTextViewText(R.id.explain_text, " after season end");
            contentView.setTextViewText(R.id.text, seasonRewards + " points");
        }


        Intent homeIntent = new Intent(this, kat_ButtonBroadcastReceiver.class);
        homeIntent.setAction("main.HOME");

        Intent analyticsIntent = new Intent(this, kat_ButtonBroadcastReceiver.class);
        analyticsIntent.setAction("main.ANALYTICS");


        PendingIntent HomePendingIntent = PendingIntent.getBroadcast(this, 0, homeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 종료버튼과 펜딩 인텐트 연결
        PendingIntent AnalyticsPendingIntent = PendingIntent.getBroadcast(this, 0, analyticsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        contentView.setOnClickPendingIntent(R.id.main_home, HomePendingIntent);
        contentView.setOnClickPendingIntent(R.id.main_analytics, AnalyticsPendingIntent);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "brawl stars play",
                    NotificationManager.IMPORTANCE_LOW);

            mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            notification
                    = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(R.drawable.kat_notification_icon)
                    .setColor(getResources().getColor(R.color.semiBlack))
                    .setColorized(true)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(contentView)
                    //.setCustomBigContentView(contentView)
                    .setShowWhen(false);

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager.notify(1, notification.build());
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
