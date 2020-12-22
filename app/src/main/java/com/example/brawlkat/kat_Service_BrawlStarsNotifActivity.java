package com.example.brawlkat;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.LongSparseArray;
import android.widget.Toast;

import com.example.brawlkat.kat_Fragment.kat_SettingFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class kat_Service_BrawlStarsNotifActivity extends Service {

    BrawlStarsPlayCheckThread checkThread;
    boolean alreadyStart = false;

    kat_Player_MainActivity kat_player_mainActivity;

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

        Toast myToast = Toast.makeText(getApplicationContext(), "service start", Toast.LENGTH_SHORT);
        myToast.show();
        checkThread = new BrawlStarsPlayCheckThread();
        checkThread.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(checkThread.isAlive()) checkThread.interrupt();
        kat_SettingFragment.serviceStarted = false;
    }

    private class BrawlStarsPlayCheckThread extends Thread{
        public void run(){
            while(true){
                // 브롤스타즈가 실행되고 서비스가 아직 실행되지 않았다면
                if(getTopPackageName(getApplicationContext()).toLowerCase().contains("brawlstar") && !alreadyStart){

                    if (kat_player_mainActivity.isServiceStart) {
                        if (kat_player_mainActivity.katService != null && !kat_player_mainActivity.endClickToUnbind) {
                            kat_player_mainActivity.endClickToUnbind = true;
                            unbindService(kat_player_mainActivity.katConnection);
                            kat_player_mainActivity.isServiceStart = false;
                        }
                    } else {
                        kat_player_mainActivity.endClickToUnbind = false;
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
                    sleep(1000);
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

        System.out.println(end);
        System.out.println(begin);

        LongSparseArray packageNameMap = new LongSparseArray<>();
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);
        System.out.println(usageEvents.hasNextEvent());
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            System.out.println("before isForeGroundEvent");
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

        System.out.println("isForeGroundEvent");

        if(event == null) {
            return false;
        }

        if(BuildConfig.VERSION_CODE >= 29) {
            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;
        }

        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }
}
