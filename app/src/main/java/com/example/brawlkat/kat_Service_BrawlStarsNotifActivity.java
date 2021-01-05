package com.example.brawlkat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.LongSparseArray;

import com.example.brawlkat.kat_Fragment.kat_SettingFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class kat_Service_BrawlStarsNotifActivity extends Service {

    BrawlStarsPlayCheckThread checkThread;
    boolean alreadyStart = false;

    kat_Player_MainActivity kat_player_mainActivity;

    private BroadcastReceiver broadcastReceiver;
    private boolean isBroadcastThreadStop = false;
    private final String BROADCAST_MASSAGE_SCREEN_ON = "Intent.ACTION_SCREEN_ON";
    private final String BROADCAST_MASSAGE_SCREEN_OFF = "Intent.ACTION_SCREEN_OFF";

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

        checkThread = new BrawlStarsPlayCheckThread();
        checkThread.start();

        makeBroadcastIntent();
        RegisterBroadcastReceiver();

        Intent clsIntent = new Intent(getApplicationContext(), kat_LoadBeforeMainActivity.class);
        //clsIntent.setAction(Intent.ACTION_MAIN);
        //clsIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                clsIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "brawl stars play",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder notification
                    = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(R.drawable.player_level_icon)
                    .setContentTitle("test")
                    .setContentIntent(pendingIntent)
                    .setContentText("test");

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager.notify(1, notification.build());
            startForeground(1, notification.build());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(checkThread.isAlive()) checkThread.interrupt();
        kat_SettingFragment.serviceStarted = false;

        UnregisterBroadcastReceiver();
    }



    private class BrawlStarsPlayCheckThread extends Thread{

        public void run(){
            while(true){

                if(isBroadcastThreadStop) break;
                if(!kat_SettingFragment.analyticsServiceStarted) continue;
                System.out.println(getTopPackageName(getApplicationContext()));
                // 브롤스타즈가 실행되고 서비스가 아직 실행되지 않았다면
                if(getTopPackageName(getApplicationContext()).toLowerCase().contains("brawlstar") && !alreadyStart){

                    if (!kat_player_mainActivity.isServiceStart) {
                        kat_player_mainActivity.getPermission();
                        kat_player_mainActivity.isServiceStart = true;
                    }
                    alreadyStart = true;
                }
                // 브롤스타즈가 실행됐는데 서비스가 실행중이 아니라면 = 유저가 강제로 꺼버린 경우
                if(getTopPackageName(getApplicationContext()).toLowerCase().contains("brawlstar") &&
                !kat_player_mainActivity.isServiceStart){
                    continue;
                }
                // 브롤스타즈 실행 중이 아니면서 서비스가 꺼진 경우 -> 비로소 서비스 꺼진 것을 알려줌
                if(!kat_player_mainActivity.isServiceStart) alreadyStart = false;

                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getTopPackageName(@NonNull Context context) {

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long lastRunAppTimeStamp = 0L;

        final long INTERVAL = 10000;
        final long end = System.currentTimeMillis();
        // 1 minute ago
        final long begin = end - INTERVAL;

        LongSparseArray packageNameMap = new LongSparseArray<>();
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            if(isForeGroundEvent(event)) {
                packageNameMap.put(event.getTimeStamp(), event.getPackageName());
                if(event.getTimeStamp() > lastRunAppTimeStamp) {
                    lastRunAppTimeStamp = event.getTimeStamp();
                }
            }
        }

        return packageNameMap.get(lastRunAppTimeStamp, "").toString();
    }

    private static boolean isForeGroundEvent(UsageEvents.Event event) {

        if(event == null) {
            return false;
        }

        if(BuildConfig.VERSION_CODE >= 29) {
            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;
        }

        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }

    private void makeBroadcastIntent(){

        Intent intent = new Intent(BROADCAST_MASSAGE_SCREEN_ON);
        sendBroadcast(intent);
    }

    private void RegisterBroadcastReceiver(){

        if(broadcastReceiver != null) return;

        final IntentFilter ScreenOnIntentFilter = new IntentFilter();
        final IntentFilter ScreenOffIntentFilter = new IntentFilter();

        ScreenOnIntentFilter.addAction(BROADCAST_MASSAGE_SCREEN_ON);
        ScreenOffIntentFilter.addAction(BROADCAST_MASSAGE_SCREEN_OFF);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(BROADCAST_MASSAGE_SCREEN_ON.equals(intent.getAction())){
                    isBroadcastThreadStop = false;
                    checkThread.run();
                }
                else if(BROADCAST_MASSAGE_SCREEN_OFF.equals(intent.getAction())){
                    isBroadcastThreadStop = true;
                }
            }
        };

        registerReceiver(broadcastReceiver, ScreenOnIntentFilter);
    }

    private void UnregisterBroadcastReceiver(){

        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }
}
