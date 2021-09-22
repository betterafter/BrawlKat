package com.keykat.keykat.brawlkat.service.util;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.LongSparseArray;

import com.keykat.keykat.brawlkat.BuildConfig;
import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;
import com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity;
import com.keykat.keykat.brawlkat.util.kat_Data;

import androidx.annotation.NonNull;

public class kat_ActionBroadcastReceiver extends BroadcastReceiver {

    private final String BROADCAST_MASSAGE_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private final String BROADCAST_MASSAGE_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    private final String BROADCAST_MASSAGE_ACTION = "";

    private boolean isThreaStop = false;

    kat_Service_BrawlStarsNotifActivity kat_service_brawlStarsNotifActivity;
    kat_Player_MainActivity kat_player_mainActivity;

    BrawlStarsPlayCheckThread checkThread;

    public kat_ActionBroadcastReceiver(){};

    public kat_ActionBroadcastReceiver(kat_Player_MainActivity kat_player_mainActivity){
        this.kat_player_mainActivity = kat_player_mainActivity;

        isThreaStop = false;
    }

    public kat_ActionBroadcastReceiver(kat_Service_BrawlStarsNotifActivity kat_service_brawlStarsNotifActivity,
                                       kat_Player_MainActivity kat_player_mainActivity){
        this.kat_service_brawlStarsNotifActivity = kat_service_brawlStarsNotifActivity;
        this.kat_player_mainActivity = kat_player_mainActivity;

        isThreaStop = false;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(BROADCAST_MASSAGE_SCREEN_ON)){
            if(kat_Data.kataSettingBase.getData("AnalyticsService") == 1) {
                isThreaStop = false;
                if (checkThread != null) checkThread = null;
                checkThread = new BrawlStarsPlayCheckThread(context);
                checkThread.start();
            }
        }
        else if(intent.getAction().equals(BROADCAST_MASSAGE_SCREEN_OFF)){
            isThreaStop = true;
        }
        else if(intent.getAction().equals("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_START")){
            isThreaStop = false;
            if(checkThread != null) checkThread = null;
            checkThread = new BrawlStarsPlayCheckThread(context);
            checkThread.start();
        }
        else if(intent.getAction().equals("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_END")){
            isThreaStop = true;
        }
    }





    private class BrawlStarsPlayCheckThread extends Thread{

        Context context;

        public BrawlStarsPlayCheckThread(Context context){
            this.context = context;
        }

        public void run(){
            while(!isThreaStop){

                try {
                    // 브롤스타즈가 실행되고 서비스가 아직 실행되지 않았다면
                    if(getTopPackageName(context).toLowerCase().contains("brawlstar")
                            && !kat_Service_BrawlStarsNotifActivity.alreadyStart){

                        if (!kat_player_mainActivity.isServiceStart) {
                            kat_player_mainActivity.getPermission();
                            kat_player_mainActivity.isServiceStart = true;
                        }
                        kat_Service_BrawlStarsNotifActivity.alreadyStart = true;
                    }
                    // 브롤스타즈가 실행됐는데 서비스가 실행중이 아니라면 = 유저가 강제로 꺼버린 경우
                    if(getTopPackageName(context).toLowerCase().contains("brawlstar") &&
                            !kat_player_mainActivity.isServiceStart){
                        continue;
                    }
                    // 브롤스타즈 실행 중이 아니면서 서비스가 꺼진 경우 -> 비로소 서비스 꺼진 것을 알려줌
                    if(!kat_player_mainActivity.isServiceStart) kat_Service_BrawlStarsNotifActivity.alreadyStart = false;

                    sleep(3000);
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

    public static boolean isForeGroundEvent(UsageEvents.Event event) {

        if(event == null) {
            return false;
        }

        if(BuildConfig.VERSION_CODE >= 29) {
            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;
        }

        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }
}
