package com.keykat.keykat.brawlkat.service.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.keykat.keykat.brawlkat.load.activity.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.service.maprecommendservice.ui.kat_Service_OverdrawService;
import com.keykat.keykat.brawlkat.util.KatData;

public class kat_ButtonBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("main.HOME")){
            Intent homeIntent = new Intent(context, kat_LoadBeforeMainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(homeIntent);
        }
        else if(intent.getAction().equals("main.ANALYTICS")){

            if(Settings.canDrawOverlays(context)) {
                Intent serviceIntent = new Intent(context, kat_Service_OverdrawService.class);
                context.stopService(serviceIntent);
                context.startService(serviceIntent);
                KatData.isForegroundServiceStart = true;
            } else {
                Toast.makeText(context, "다른 앱 위에 그리기 권한을 먼저 허용해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(intent.getAction().equals("overdraw.STOP")){
            Intent serviceIntent = new Intent(context, kat_Service_OverdrawService.class);
            context.stopService(serviceIntent);
        }
    }
}
