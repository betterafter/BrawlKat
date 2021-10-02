package com.keykat.keykat.brawlkat.service.activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.service.util.kat_ButtonBroadcastReceiver;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

public class kat_Service_OverdrawActivity extends Service implements View.OnTouchListener {

    // 윈도우 매니저
    public WindowManager windowManager;
    public WindowManager mapWindowManager;
    public WindowManager.LayoutParams layoutParams;
    public WindowManager.LayoutParams mapRecommend;
    public Context context;


    // 뷰
    private Button btn;
    public ConstraintLayout constraintLayout;
    public View mapRecommendView;
    public LayoutInflater layoutInflater;
    private kat_Service_EventActivity events;


    // 기타 변수들
    private float mStartingX, mStartingY, mWidgetStartingX, mWidgetStartingY;
    public boolean ServiceButtonTouched = false;
    public static String getPlayerTag;

    public boolean unbindCall = false;

    private timeCountThread timeThread;
    private boolean isCheckThreadStart;
    private int timeCount = 0;

    private kat_SearchThread searchThread;

    private final Handler onButtonLongTouchHandler = new Handler();
    private final Runnable onButtonLongTouchRunnable = () -> {
        onDestroy();
        stopSelf();
    };

    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        searchThread = new kat_SearchThread();


        init_Inflater();
        init_windowManager();

        // EventActivity 선언 및 뷰 생성
        events = new kat_Service_EventActivity(context, this);
        events.init_mapInflater();
        events.getCurrentEventsInformation();


        if (!KatData.client.isGetApiThreadAlive())
            KatData.client.init();

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.sub_notification);

        timeThread = new timeCountThread();
        timeThread.start();

        isCheckThreadStart = true;

        // 종료 버튼을 위한 펜딩 인텐트
        Intent buttonIntent = new Intent(this, kat_ButtonBroadcastReceiver.class);
        buttonIntent.setAction("overdraw.STOP");
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 종료버튼과 펜딩 인텐트 연결
        contentView.setOnClickPendingIntent(R.id.service_exit, btPendingIntent);


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
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    // 메인 버튼 생성
    public void init_Inflater() {

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
    public void init_windowManager() {

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
        if (windowManager != null) {
            //서비스 종료시 뷰 제거.
            if (constraintLayout != null && constraintLayout.getWindowToken() != null)
                windowManager.removeView(constraintLayout);
        }

        unbindCall = true;
        KatData.isForegroundServiceStart = false;
        setNotification();

        isCheckThreadStart = false;
        timeThread = null;

        if (events != null) {
            events.isEventThreadStart = false;
            events.eventsThread = null;

            events = null;
        }
        try {
            KatData.client.remove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        super.onDestroy();
        stopSelf();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        switch (ev.getAction()) {


            case MotionEvent.ACTION_DOWN:

                onButtonLongTouchHandler.postDelayed(onButtonLongTouchRunnable, 2000);

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
                if (Math.abs(mWidgetStartingX - layoutParams.x) > 30
                        || Math.abs(mWidgetStartingY - layoutParams.y) > 30) {
                    onButtonLongTouchHandler.removeCallbacks(onButtonLongTouchRunnable);
                }

                return true;

            case MotionEvent.ACTION_UP:

                onButtonLongTouchHandler.removeCallbacks(onButtonLongTouchRunnable);

                if (timeCount > 30) {
                    kat_SearchThread searchThread = new kat_SearchThread();
                    searchThread.SearchStart(getPlayerTag, "players", context);
                    setNotification();
                    timeCount = 0;
                }

                if (Math.abs(mWidgetStartingX - layoutParams.x) <= 30
                        && Math.abs(mWidgetStartingY - layoutParams.y) <= 30) {
                    events.ShowEventsInformation();
                    events.Change();
                    events.addModeButton();
                    events.ChangeRecommendViewClick();
                }

            case MotionEvent.ACTION_OUTSIDE:
                onButtonLongTouchHandler.removeCallbacks(onButtonLongTouchRunnable);

        }

        return false;
    }

    // notification 업데이트
    private void setNotification() {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (!kat_SearchThread.SearchDataOnOverdraw)
                searchThread.SearchStart(getPlayerTag, "players", this);
        });
    }
    
    private class timeCountThread extends Thread {
        public void run() {
            while (isCheckThreadStart) {
                try {
                    timeCount++;
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
