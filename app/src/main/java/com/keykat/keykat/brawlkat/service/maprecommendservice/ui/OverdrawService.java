package com.keykat.keykat.brawlkat.service.maprecommendservice.ui;

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
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.common.Injection;
import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository;
import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendContract;
import com.keykat.keykat.brawlkat.service.model.datasource.MapRecommendDataSource;
import com.keykat.keykat.brawlkat.service.util.ServiceButtonBroadcastReceiver;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class OverdrawService
        extends Service
        implements View.OnTouchListener, MapRecommendContract.MainView {

    // 뷰
    public WindowManager windowManager;
    public WindowManager.LayoutParams layoutParams;
    private RemoteViews contentView;
    private Context context;
    private ConstraintLayout serviceButtonLayout;
    private Button serviceButton;

    // 이벤트 서비스
    private EventService events;
    private final Handler onButtonLongTouchHandler = new Handler();
    private final Runnable onButtonLongTouchRunnable = () -> {
        onDestroy();
        stopSelf();
    };

    // 기타 변수들
    private float mStartingX;
    private float mStartingY;
    private float mWidgetStartingX;
    private float mWidgetStartingY;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        MapRecommendRepository repository
                = Injection.INSTANCE.provideMapRecommendRepository(new MapRecommendDataSource(context));
        events = new EventService(context, repository, this, this);

        initViewProperties();
        setNormalButton();
        initWindowManager();
        initContentView();
        initNotification();

        return START_STICKY;
    }

    public void initViewProperties() {
        serviceButtonLayout = new ConstraintLayout(this);
        serviceButtonLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_service_button_background));
        serviceButton = new Button(this);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int setWidth = Math.min(width, height);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                setWidth / 6,
                setWidth / 6
        );

        serviceButtonLayout.setLayoutParams(params);
        serviceButton.setLayoutParams(params);
    }

    public void initContentView() {
        contentView = new RemoteViews(getPackageName(), R.layout.sub_notification);
        Intent buttonIntent = new Intent(this, ServiceButtonBroadcastReceiver.class);
        buttonIntent.setAction("overdraw.STOP");
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 0, buttonIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 종료버튼과 펜딩 인텐트 연결
        contentView.setOnClickPendingIntent(R.id.service_exit, btPendingIntent);
    }

    public void initNotification() {
        // 종료 버튼을 위한 펜딩 인텐트
        NotificationChannel channel = new NotificationChannel("subChannel", "brawl stars analytics play",
                NotificationManager.IMPORTANCE_LOW);
        // notificationManager 생성
        NotificationManager mNotificationManager
                = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        mNotificationManager.createNotificationChannel(channel);
        // notification 생성
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "subChannel")
                .setSmallIcon(R.drawable.kat_notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.semiBlack))
                .setColorized(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomBigContentView(contentView)
                .setShowWhen(false);
        // id 값은 0보다 큰 양수가 들어가야 한다.
        startForeground(2, notification.build());
    }

    // 메인 버튼 생성
    @SuppressLint("ClickableViewAccessibility")
    public void setNormalButton() {

        serviceButtonLayout.removeAllViews();
        serviceButtonLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_service_button_background));

        serviceButton.setBackground(context.getResources().getDrawable(R.drawable.service_click));
        serviceButton.setOnTouchListener(this);
        serviceButtonLayout.addView(serviceButton);
    }

    public void setLoadingButton() {
        try {
            serviceButtonLayout.removeAllViews();
            serviceButtonLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_service_button_background));

            serviceButton.setBackground(context.getResources().getDrawable(R.drawable.layout_service_button));
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.loading_rotation);
            serviceButton.startAnimation(animation);
            serviceButtonLayout.addView(serviceButton);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 메인 버튼에 연결된 윈도우 매니저 선언
    public void initWindowManager() {

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
        windowManager.addView(serviceButtonLayout, layoutParams);
    }

    // 서비스 종료
    @Override
    public void onDestroy() {
        if (windowManager != null) {
            //서비스 종료시 뷰 제거.
            if (serviceButtonLayout != null && serviceButtonLayout.getWindowToken() != null) {
                windowManager.removeView(serviceButtonLayout);
            }
        }

        if (events != null) {
            events.onDismiss();
            events = null;
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
                windowManager.updateViewLayout(serviceButtonLayout, layoutParams);

                // 버튼 움직임을 의도한 모션을 취할 때는 '3초 누르기' 모션 해제
                if (Math.abs(mWidgetStartingX - layoutParams.x) > 30
                        || Math.abs(mWidgetStartingY - layoutParams.y) > 30) {
                    onButtonLongTouchHandler.removeCallbacks(onButtonLongTouchRunnable);
                }

                return true;

            case MotionEvent.ACTION_UP:

                onButtonLongTouchHandler.removeCallbacks(onButtonLongTouchRunnable);

                if (Math.abs(mWidgetStartingX - layoutParams.x) <= 30
                        && Math.abs(mWidgetStartingY - layoutParams.y) <= 30) {
                    events.showEventsInformation();
                    setLoadingButton();
                    serviceButton.setEnabled(false);
                }

            case MotionEvent.ACTION_OUTSIDE:
                onButtonLongTouchHandler.removeCallbacks(onButtonLongTouchRunnable);
                events.onDismiss();

        }

        return false;
    }

    @Override
    public void setServiceButtonEnable() {
        setNormalButton();
        serviceButton.setEnabled(true);
        serviceButton.clearAnimation();
    }
}
