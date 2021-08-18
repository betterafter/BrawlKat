package com.keykat.keykat.brawlkat.service.util;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.keykat.keykat.brawlkat.load.activity.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;
import com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity;
import com.keykat.keykat.brawlkat.service.activity.kat_Service_OverdrawActivity;

import java.util.List;

public class kat_ButtonBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("main.HOME")){
            Intent homeIntent = new Intent(context, kat_LoadBeforeMainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(homeIntent);
        }
        else if(intent.getAction().equals("main.ANALYTICS")){

            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(1000);

            boolean isMainServiceExist = false;
            for(ActivityManager.RunningServiceInfo info : rs) {
                String className = info.service.getClassName();
                String packageName = info.service.getPackageName();
                if(className.equals("com.keykat.keykat.brawlkat.service.activity.kat_Service_OverdrawActivity")){
                    isMainServiceExist = true;
                    break;
                }
            }
            if(!isMainServiceExist){

                if (!kat_Player_MainActivity.kat_player_mainActivity.isServiceStart) {
                    kat_Player_MainActivity.kat_player_mainActivity.getPermission();
                    kat_Player_MainActivity.kat_player_mainActivity.isServiceStart = true;
                }
                kat_Service_BrawlStarsNotifActivity.alreadyStart = true;
            }
        }
        else if(intent.getAction().equals("overdraw.STOP")){
            Intent serviceIntent = new Intent(context, kat_Service_OverdrawActivity.class);
            context.stopService(serviceIntent);
        }
    }
}
