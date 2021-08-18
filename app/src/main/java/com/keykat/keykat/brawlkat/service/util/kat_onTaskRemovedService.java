package com.keykat.keykat.brawlkat.service.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.keykat.keykat.brawlkat.util.kat_Data;

import androidx.annotation.Nullable;

public class kat_onTaskRemovedService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) { //핸들링 하는 부분
        kat_Data.client.remove();
        stopSelf(); //서비스도 같이 종료
    }
}
