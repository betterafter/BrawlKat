package com.keykat.keykat.brawlkat.service.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import com.keykat.keykat.brawlkat.load.activity.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.service.maprecommendservice.ui.OverdrawService;
import com.keykat.keykat.brawlkat.util.KatData;

public class ServiceButtonBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case "main.HOME":
                Intent homeIntent = new Intent(context, kat_LoadBeforeMainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(homeIntent);
                break;
            case "main.ANALYTICS":

                if (Settings.canDrawOverlays(context)) {
                    Intent serviceIntent = new Intent(context, OverdrawService.class);
                    context.stopService(serviceIntent);
                    context.startService(serviceIntent);
                    KatData.isForegroundServiceStart = true;
                } else {
                    Toast.makeText(context, "다른 앱 위에 그리기 권한을 먼저 허용해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case "overdraw.STOP":
                Intent serviceIntent = new Intent(context, OverdrawService.class);
                context.stopService(serviceIntent);
                break;
        }
    }
}
