package com.example.brawlkat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class kat_OverdrawActivity extends Service implements View.OnTouchListener {

    public      WindowManager                   windowManager;
    public      WindowManager                   mapWindowManager;
    public      WindowManager.LayoutParams      layoutParams;
    public      WindowManager.LayoutParams      mapRecommend;

    private     ImageButton                     btn;
    public      ImageButton                     getUserInformationButton;
    public      ImageButton                     getEventsInformationButton;

    public      View                            mapRecommendView;

    private     kat_EventActivity               events;

    private     float                           mStartingX, mStartingY, mWidgetStartingX, mWidgetStartingY;

    public      LayoutInflater                  layoutInflater;

    public      Context                         context;
    public      boolean                         ServiceButtonTouched = false;
    public      int                             ServiceButtonTouchedCase = 0;




    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init_Inflater();
        init_windowManager();

        context = getApplicationContext();

        events = new kat_EventActivity(context, this);
        events.init_mapInflater();



        btn.setOnTouchListener(this);
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){

                if(ServiceButtonTouchedCase == 2){
                    events.ShowEventsInformation();
                    events.getCurrentEventsInformation();
                    events.Change();
                    events.addModeButton();
                }
            }
        });
    }

    public void init_Inflater(){

        btn = new ImageButton(this);
        btn.setImageResource(R.drawable.logo);
        btn.setBackgroundColor(Color.TRANSPARENT);
    }

    public void init_windowManager(){

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(btn, layoutParams);
    }



    @Override
    public void onDestroy() {
        if(windowManager != null) {        //서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
            if(btn != null) windowManager.removeView(btn);
        }
        if(mapWindowManager != null){
            if(mapRecommendView != null) mapWindowManager.removeView(mapRecommendView);
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        ServiceButtonTouchedCase = 0;

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:

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
                windowManager.updateViewLayout(btn, layoutParams);
                return true;

            case MotionEvent.ACTION_UP:

                if(mWidgetStartingX != layoutParams.x || mWidgetStartingY != layoutParams.y)
                    ServiceButtonTouchedCase = 1;
                else
                    ServiceButtonTouchedCase = 2;


        }
        return false;
    }
}
