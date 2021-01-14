package com.example.brawlkat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_broadcast_receiver.kat_ActionBroadcastReceiver;
import com.example.brawlkat.kat_broadcast_receiver.kat_ButtonBroadcastReceiver;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

public class kat_Service_OverdrawActivity extends Service implements View.OnTouchListener {

    // 윈도우 매니저
    public      WindowManager                   windowManager;
    public      WindowManager                   mapWindowManager;
    public      WindowManager.LayoutParams      layoutParams;
    public      WindowManager.LayoutParams      mapRecommend;
    public      Context                         context;


    // 뷰
    private     Button                          btn;
    public      ConstraintLayout                constraintLayout;
    public      View                            mapRecommendView;
    public      LayoutInflater                  layoutInflater;
    private     kat_Service_EventActivity       events;
    public      buttonLongClickToExitThread     buttonThread;


    // 기타 변수들
    private     float                           mStartingX, mStartingY, mWidgetStartingX, mWidgetStartingY;
    public      boolean                         ServiceButtonTouched = false;
    public      int                             ServiceButtonTouchedCase = 0;
    public      static String                   getPlayerTag;

    public      boolean                         unbindCall = false;


    private     BrawlStarsPlayCheckThread       checkThread;
    private     timeCountThread                 timeThread;
    private     boolean                         isCheckThreadStart;
    private     int                             timeCount = 0;


    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId )
    {

        if(!kat_LoadBeforeMainActivity.client.isGetApiThreadAlive())
            kat_LoadBeforeMainActivity.client.init();

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.sub_notification);

        checkThread = new BrawlStarsPlayCheckThread(context);
        checkThread.start();

        timeCountThread timeCountThread = new timeCountThread();
        timeCountThread.start();

        isCheckThreadStart = true;

        // 종료 버튼을 위한 펜딩 인텐트
        Intent buttonIntent = new Intent(this, kat_ButtonBroadcastReceiver.class);
        buttonIntent.setAction("overdraw.STOP");
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 종료버튼과 펜딩 인텐트 연결
        contentView.setOnClickPendingIntent(R.id.service_exit, btPendingIntent);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("subChannel", "brawl stars analytics play",
                    NotificationManager.IMPORTANCE_LOW);


            mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "subChannel")
                    .setSmallIcon(R.drawable.kat_notification_icon)
                    .setColor(getResources().getColor(R.color.semiBlack))
                    .setColorized(true)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomBigContentView(contentView)
                    .setShowWhen(false);

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager.notify(2, notification.build());
            startForeground(2, notification.build());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        init_Inflater();
        init_windowManager();

        // EventActivity 선언 및 뷰 생성
        events = new kat_Service_EventActivity(context, this);
        events.init_mapInflater();
        events.getCurrentEventsInformation();

        // 메인 버튼 클릭 스레드 실행
        buttonThread = new buttonLongClickToExitThread();
        buttonThread.start();
    }



    // 메인 버튼 생성
    public void init_Inflater(){

        constraintLayout = new ConstraintLayout(this);

        btn = new Button(this);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int setWidth = Math.min(width, height);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                setWidth / 5,
                setWidth / 5
        );

        btn.setBackground(context.getResources().getDrawable(R.drawable.service_click));
        btn.setLayoutParams(params);

        btn.setOnTouchListener(this);

        constraintLayout.addView(btn);
    }

    // 메인 버튼에 연결된 윈도우 매니저 선언
    public void init_windowManager(){

        // FLAG_NOT_FOCUSABLE : 현재 윈도우에 포커스가 집중되어 네비게이션 바 같은 시스템 ui가 현재 서비스 윈도우의 상태에
        // 종속되므로 해당 플래그로 포커스를 없애줘야함.
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(constraintLayout, layoutParams);
    }

    // 서비스 종료
    @Override
    public void onDestroy() {
        if(windowManager != null) {
            //서비스 종료시 뷰 제거.
            if(constraintLayout != null && constraintLayout.getWindowToken() != null)
                windowManager.removeView(constraintLayout);
        }

        unbindCall = true;
        kat_Player_MainActivity.isServiceStart = false;
        setNotification();

        kat_ActionBroadcastReceiver forGetPackageName = new kat_ActionBroadcastReceiver();
        if(!forGetPackageName.getTopPackageName(context).equals("com.example.brawlkat")){
            kat_LoadBeforeMainActivity.client.remove();
        }


        isCheckThreadStart = false;
        checkThread = null;
        timeThread = null;

        super.onDestroy();
    }



    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        switch (ev.getAction()) {


            case MotionEvent.ACTION_DOWN:

                buttonThread.stopLongClickAction = false;

                ServiceButtonTouched = true;
                mStartingX = ev.getRawX();
                mStartingY = ev.getRawY();

                mWidgetStartingX = layoutParams.x;
                mWidgetStartingY = layoutParams.y;
                return false;
                
            case MotionEvent.ACTION_MOVE:

                float deltaX = mStartingX - ev.getRawX();
                float deltaY = mStartingY - ev.getRawY();
                layoutParams.x = (int) (mWidgetStartingX - deltaX);
                layoutParams.y = (int) (mWidgetStartingY - deltaY);
                windowManager.updateViewLayout(constraintLayout, layoutParams);

                // 버튼 움직임을 의도한 모션을 취할 때는 '3초 누르기' 모션 해제
                if(Math.abs(mWidgetStartingX - layoutParams.x) > 30 || Math.abs(mWidgetStartingY - layoutParams.y) > 30){
                    buttonThread.stopLongClickAction = true;
                }
                // 그렇지 않으면 '3초 누르기' 모션 유지
                else{
                    buttonThread.stopLongClickAction = false;
                }

                return true;

            case MotionEvent.ACTION_UP:

                buttonThread.stopLongClickAction = true;
                if(timeCount > 5){
                    kat_SearchThread searchThread = new kat_SearchThread();
                    searchThread.SearchStart(getPlayerTag, "players", context);
                    timeCount = 0;
                }
                if(ServiceButtonTouchedCase != 3){

                    if(Math.abs(mWidgetStartingX - layoutParams.x) > 30 || Math.abs(mWidgetStartingY - layoutParams.y) > 30){
                        ServiceButtonTouchedCase = 1;
                    }

                    // 움직이지 않고 제자리 터치 할 경우
                    else {
                        ServiceButtonTouchedCase = 2;
                        events.ShowEventsInformation();
                        events.Change();
                        events.addModeButton();
                        events.ChangeRecommendViewClick();

                        setNotification();
                    }
                }


            case MotionEvent.ACTION_OUTSIDE:
                ServiceButtonTouchedCase = 0;
        }

        return false;
    }

    // 버튼을 3초 이상 누르고 있으면 서비스 종료
    private class buttonLongClickToExitThread extends Thread{

        public int stopCount;
        public boolean stopLongClickAction = true;

        public void run(){

            try{
                stopCount = 0; stopLongClickAction = true;

                while(true){

                    if(stopCount >= 3){
                        stopCount = 0; onDestroy();
                        stopSelf();

                        break;
                    }

                    stopCount++;
                    if(stopLongClickAction){
                        stopCount = 0; continue;
                    }
                    sleep(1000);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    // notification 업데이트
    private void setNotification(){

        kat_NotificationUpdater updater = new kat_NotificationUpdater(context);
        updater.update();
    }


    private class timeCountThread extends Thread{
        public void run(){
            while(isCheckThreadStart){
                try {
                    timeCount++;
                    sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }



    private class BrawlStarsPlayCheckThread extends Thread{

        Context context;

        public BrawlStarsPlayCheckThread(Context context){
            this.context = context;
        }

        public void run(){
            while(isCheckThreadStart){

                try {
                    // 브롤스타즈가 실행되고 서비스가 아직 실행되지 않았다면
                    if(getTopPackageName(context).toLowerCase().contains("brawlstar")) {
                        setNotification();
                    }
                    sleep(1000 * 60 * 5);
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
