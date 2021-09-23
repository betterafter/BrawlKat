package com.keykat.keykat.brawlkat.service.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.keykat.keykat.brawlkat.util.KatData;

import androidx.annotation.Nullable;

public class kat_onTaskRemovedService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) { //핸들링 하는 부분
        try {
            KatData.client.remove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopSelf(); //서비스도 같이 종료
    }
}
