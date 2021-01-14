package com.keykat.keykat.brawlkat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class kat_onTaskRemovedService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) { //핸들링 하는 부분
        kat_LoadBeforeMainActivity.client.remove();
        stopSelf(); //서비스도 같이 종료
    }
}
