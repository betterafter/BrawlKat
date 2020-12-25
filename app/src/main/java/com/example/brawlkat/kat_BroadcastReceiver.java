package com.example.brawlkat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class kat_BroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){

            Intent i = new Intent(context, kat_Service_BrawlStarsNotifActivity.class);
            context.startService(i);
            System.out.println("service start");
            Toast myToast = Toast.makeText(context,"service start", Toast.LENGTH_SHORT);
            myToast.show();
        }
    }
}
