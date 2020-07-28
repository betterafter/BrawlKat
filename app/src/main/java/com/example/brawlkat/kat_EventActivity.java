package com.example.brawlkat;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.viewpager2.widget.ViewPager2;


public class kat_EventActivity extends kat_OverdrawActivity {

    //access        type                                name                    init
    private         Context                             context;
    private         kat_OverdrawActivity                overdrawActivity;
    private         Client                              client;
    private         getEventsThread                     eventsThread;
    private         kat_eventsParser                    eventsParser;
    private         kat_brawlersParser                  brawlersParser;
    private         View[]                              eventView           = new View[20];
    private         ArrayList<kat_eventsParser.pair>    EventArrayList;
    private         ArrayList<HashMap<String, Object>>  BrawlersArrayList;
    private         ViewPager2                          viewPager;


    public kat_EventActivity(Context context, kat_OverdrawActivity overdrawActivity){
        super();
        this.context = context;
        this.overdrawActivity = overdrawActivity;
    }

    // map inflater 초기화
    public void init_mapInflater() {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overdrawActivity.mapRecommendView = layoutInflater.inflate(R.layout.map_recommend, null);
        overdrawActivity.mapRecommendView.setOnTouchListener(this);
    }

    // 서비스 실행 시에 보여지는 화면
    public void ShowEventsInformation(){
        if(overdrawActivity.mapWindowManager == null){
            overdrawActivity.mapRecommend = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
            );

            overdrawActivity.mapWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        }
//        if(overdrawActivity.mapRecommendView.getWindowToken() != null) {
//            overdrawActivity.mapWindowManager.removeView(overdrawActivity.mapRecommendView);
//        }

//        else {
//            overdrawActivity.mapWindowManager.addView(overdrawActivity.mapRecommendView, overdrawActivity.mapRecommend);
//        }
        if(overdrawActivity.mapRecommendView.getWindowToken() == null) {
            overdrawActivity.mapWindowManager.addView(overdrawActivity.mapRecommendView, overdrawActivity.mapRecommend);
        }
    }


    public void getCurrentEventsInformation(){

        client = new Client();
        eventsThread = new getEventsThread();
        if(!eventsThread.isAlive())
            eventsThread.start();

    }



    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        System.out.println(ev.getAction());

        if(ev.getAction() == MotionEvent.ACTION_OUTSIDE){

            System.out.println(overdrawActivity.layoutParams.x);
            System.out.println(overdrawActivity.layoutParams.y);
            System.out.println(ev.getX());
            System.out.println(ev.getY());


            while(overdrawActivity.ServiceButtonTouched) continue;
            if(overdrawActivity.ServiceButtonTouchedCase != 0){
                onDestroy(); return true;
            }
        }

        return true;
    }



    private class getEventsThread extends Thread{

        public void run(){
            ArrayList<String> dataSet = client.getdata("events", "brawlers");
            eventsParser = new kat_eventsParser(dataSet.get(0));
            brawlersParser = new kat_brawlersParser(dataSet.get(1));

            try{
                EventArrayList = eventsParser.DataParser();
                BrawlersArrayList = brawlersParser.DataParser();

                //eventsParser.testPrint(EventArrayList);

                System.out.println("success to get data '\n'");


            }
            catch (Exception e){
                System.out.println("fail");
            }
        }
    }


    public void Change(){

        while (EventArrayList == null || BrawlersArrayList == null) continue;

        viewPager = (ViewPager2) overdrawActivity.mapRecommendView.findViewById(R.id.viewPager2);
        kat_EventAdapter eventAdapter = new kat_EventAdapter(context, EventArrayList, BrawlersArrayList);
        viewPager.setAdapter(eventAdapter);
    }

    public void addModeButton(){

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        LinearLayout buttonGroup = (LinearLayout) overdrawActivity.mapRecommendView.findViewById(R.id.buttonGroup);
        buttonGroup.removeAllViews();

        for(int i = 0; i < EventArrayList.size(); i++){
            ImageButton btn = new ImageButton(context);
            String gameModeTypeUrl = (String) EventArrayList.get(i).getInfo().get("gamemodeTypeImageUrl");
            Glide.with(context).load(gameModeTypeUrl).override((width / 3) / 7,(width / 3) / 7).into(btn);

            final int idx = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(idx, false);
                }
            });

            buttonGroup.addView(btn);
        }
    }




    @Override
    public void onDestroy() {

        if(overdrawActivity.mapWindowManager != null){
            if(overdrawActivity.mapRecommendView != null)
                overdrawActivity.mapWindowManager.removeView(overdrawActivity.mapRecommendView);
        }
        super.onDestroy();
    }
}
