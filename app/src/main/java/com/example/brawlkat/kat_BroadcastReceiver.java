package com.example.brawlkat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

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
