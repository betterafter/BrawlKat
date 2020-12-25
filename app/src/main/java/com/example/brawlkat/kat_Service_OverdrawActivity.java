package com.example.brawlkat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

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


    public      final IBinder                   binder = new LocalBinder();
    public      boolean                         unbindCall = false;


    // 스레드와 메인 액티비티 연결을 위한 바인더 선언
    public class LocalBinder extends Binder{
        kat_Service_OverdrawActivity getService(){
            return kat_Service_OverdrawActivity.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0){
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId )
    {
//        // QQQ: 두번 이상 호출되지 않도록 조치해야 할 것 같다.
//        Intent clsIntent = new Intent(this, kat_Player_MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clsIntent, 0);
//
//        NotificationCompat.Builder clsBuilder;
//        if( Build.VERSION.SDK_INT >= 26 )
//        {
//            String CHANNEL_ID = "channel_id";
//            NotificationChannel clsChannel = new NotificationChannel( CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT );
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel( clsChannel );
//
//            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID );
//        }
//        else
//        {
//            clsBuilder = new NotificationCompat.Builder(this );
//        }
//
//        // QQQ: notification 에 보여줄 타이틀, 내용을 수정한다.
//        clsBuilder.setSmallIcon( R.drawable.logo)
//                .setContentTitle( "서비스 앱" ).setContentText( "서비스 앱" )
//                .setContentIntent( pendingIntent );
//
//        // foreground 서비스로 실행한다.
//        startForeground( 1, clsBuilder.build() );
//
//        // QQQ: 쓰레드 등을 실행하여서 서비스에 적합한 로직을 구현한다.

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "brawl stars play",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, "channel")
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                240,
                240
        );

        btn.setBackground(context.getResources().getDrawable(R.drawable.service_click));

        btn.setText("Bk");
        btn.setTextColor(context.getResources().getColor(R.color.Color1));
        Typeface typeface = ResourcesCompat.getFont(context, R.font.lilita_one);
        btn.setTypeface(typeface);
        btn.setTextSize(28);

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
        // 서비스 종료 시 모든 스레드 종료
        events.eventsThread.interrupt();
        events.client.getThread.interrupt();
        buttonThread.interrupt();

        unbindCall = true;

        super.onDestroy();
    }



    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_DOWN) System.out.println("DOWN");
        else if(ev.getAction() == MotionEvent.ACTION_UP) System.out.println("UP");
        else if(ev.getAction() == MotionEvent.ACTION_OUTSIDE) System.out.println("OUTSIDE");
        else if(ev.getAction() == MotionEvent.ACTION_MOVE) System.out.println("MOVE");

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
                if(Math.abs(mWidgetStartingX - layoutParams.x) > 10 || Math.abs(mWidgetStartingY - layoutParams.y) > 10){
                    buttonThread.stopLongClickAction = true;
                }
                // 그렇지 않으면 '3초 누르기' 모션 유지
                else{
                    buttonThread.stopLongClickAction = false;
                }

                return true;

            case MotionEvent.ACTION_UP:

                buttonThread.stopLongClickAction = true;
                if(ServiceButtonTouchedCase != 3){

                    if(Math.abs(mWidgetStartingX - layoutParams.x) > 10 || Math.abs(mWidgetStartingY - layoutParams.y) > 10){
                        ServiceButtonTouchedCase = 1;
                    }

                    // 움직이지 않고 제자리 터치 할 경우
                    else {
                        ServiceButtonTouchedCase = 2;
                        events.ShowEventsInformation();
                        events.Change();
                        events.addModeButton();
                        events.ChangeRecommendViewClick();
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
                        stopCount = 0; onDestroy(); this.interrupt();
                        kat_Player_MainActivity.isServiceStart = false;
                        stopSelf();

                        break;
                    }

                    stopCount++;
                    if(stopLongClickAction){
                        stopCount = 0; continue;
                    }
                    System.out.println(stopCount);
                    sleep(1000);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
