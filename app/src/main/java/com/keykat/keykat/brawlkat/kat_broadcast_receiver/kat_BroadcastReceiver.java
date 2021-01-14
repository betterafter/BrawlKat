package com.keykat.keykat.brawlkat.kat_broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.kat_Service_BrawlStarsNotifActivity;

public class kat_BroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            if(kat_LoadBeforeMainActivity.kataSettingBase.getData("ForegroundService") == 1){

                Intent serviceIntent = new Intent(context, kat_Service_BrawlStarsNotifActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                }
                else{
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
