package com.example.brawlkat;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.brawlkat.dataparser.kat_brawlersParser;
import com.example.brawlkat.dataparser.kat_eventsParser;
import com.example.brawlkat.dataparser.kat_official_playerParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.viewpager2.widget.ViewPager2;


public class kat_Service_EventActivity extends kat_Service_OverdrawActivity {

    //access        type                                name                    init
    private         Context                             context;
    private         kat_Service_OverdrawActivity        overdrawActivity;
    public          Client                              client;
    public          getEventsThread                     eventsThread;
    private         kat_eventsParser                    eventsParser;
    private         kat_brawlersParser                  brawlersParser;
    private         kat_official_playerParser           official_playerParser;
    public          ArrayList<kat_eventsParser.pair>    EventArrayList;
    public          ArrayList<HashMap<String, Object>>  BrawlersArrayList;
    public          ArrayList<String>                   offi_PlayerArrayList;
    private         ViewPager2                          viewPager               = null;
    public          kat_EventAdapter                    eventAdapter;
    public          boolean                             changeRecommendView = false;
    public          String                              playerTag;



    public kat_Service_EventActivity(Context context, kat_Service_OverdrawActivity overdrawActivity){
        super();
        this.context = context;
        this.overdrawActivity = overdrawActivity;
    }

    // map inflater 초기화
    public void init_mapInflater() {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overdrawActivity.mapRecommendView = layoutInflater.inflate(R.layout.service_map_recommend, null);
        overdrawActivity.mapRecommendView.setOnTouchListener(this);
    }

    // 서비스 실행 시에 보여지는 화면
    public void ShowEventsInformation(){

        overdrawActivity.mapRecommend = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );


        if(overdrawActivity.mapWindowManager == null)
            overdrawActivity.mapWindowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        if(overdrawActivity.mapRecommendView.getWindowToken() == null) {

            DisplayMetrics metrics = new DisplayMetrics();
            overdrawActivity.mapWindowManager.getDefaultDisplay().getMetrics(metrics);

            int fixedWidth = Math.max(metrics.heightPixels, metrics.widthPixels);
            int fixedHeight = Math.min(metrics.heightPixels, metrics.widthPixels);

            overdrawActivity.mapRecommend.height = fixedHeight;
            overdrawActivity.mapRecommend.width = fixedWidth / 2;

            overdrawActivity.mapWindowManager.addView(overdrawActivity.mapRecommendView, overdrawActivity.mapRecommend);
        }
    }

    // 서버에서 가져온 api 데이터 불러오고 리스트에 넣기
    public void getCurrentEventsInformation(){

        client = new Client(overdrawActivity);
        client.init();
        eventsThread = new getEventsThread();

        if(!eventsThread.isAlive())
            eventsThread.start();
    }



    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_OUTSIDE){
            onDestroy(); return true;
        }

        return true;
    }





    // starlist.pro api 데이터 받고 저장
    public class getEventsThread extends Thread{

        public void run(){

            try{
                while (true){

                    if(!client.getThread.isAlive()) continue;
                    if(client.getData() == null) continue;

                    ArrayList<String> dataSet = client.getData();
                    System.out.println(dataSet.size());

                    if(dataSet.size() < 2) continue;

                    eventsParser = new kat_eventsParser(dataSet.get(0));
                    brawlersParser = new kat_brawlersParser(dataSet.get(1));

                    EventArrayList = eventsParser.DataParser();
                    BrawlersArrayList = brawlersParser.DataParser();

                    //eventsParser.testPrint(EventArrayList);

                    if(viewPager == null){
                        viewPager = (ViewPager2) overdrawActivity.mapRecommendView.findViewById(R.id.viewPager2);
                        eventAdapter = new kat_EventAdapter(context, EventArrayList, BrawlersArrayList,
                                kat_Service_EventActivity.this);
                    }

                    System.out.println("success to get data'\n");
                    int time = 1000 * 6;
                    sleep(time);
                }
            }
            catch (Exception e){
                if(e instanceof InterruptedException){
                    System.out.println("Interrupt Exception");
                }
                else System.out.println("fail");
            }
        }
    }


    public void Change(){
        while(BrawlersArrayList == null || EventArrayList == null || eventAdapter == null) continue;
//        System.out.println("BrawlersArrayList size : " + BrawlersArrayList.size());
//        System.out.println("EventArrayList size : " + EventArrayList.size());
        viewPager.setAdapter(eventAdapter);
    }



    public void ChangeRecommendViewClick(){

        Button btn = new Button(context);
        LinearLayout buttonGroup = (LinearLayout) overdrawActivity.mapRecommendView.findViewById(R.id.buttonGroup);
        buttonGroup.addView(btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(overdrawActivity.getPlayerTag == null){
                    return;
                }

                client.AllTypeInit(overdrawActivity.getPlayerTag, "players", kat_Player_MainActivity.official);
                System.out.println("get own data button click - player tag : " + overdrawActivity.getPlayerTag);
                GetOffiApiThread offiApiThread = new GetOffiApiThread();
                if(!offiApiThread.isAlive()) offiApiThread.start();
                // 버튼 클릭 시 유저 추천 뷰 <-> 전체 추천 뷰 전환
                if(changeRecommendView) changeRecommendView = false;
                else changeRecommendView = true;
                eventAdapter.refresh();
            }
        });
    }


    private class GetOffiApiThread extends Thread{
        public void run(){
            try{
                while(true){

                    if(client.getAllTypeData() == null) continue;
                    System.out.println("client data not null : " + client.getAllTypeData());
                    official_playerParser = new kat_official_playerParser(client.getAllTypeData().get(0));
                    offi_PlayerArrayList = official_playerParser.DataParser();
                    break;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    // 뷰페이저 옆의 이벤트 선택 버튼
    public void addModeButton(){

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int setWidth = Math.max(width, height);
        LinearLayout buttonGroup = (LinearLayout) overdrawActivity.mapRecommendView.findViewById(R.id.buttonGroup);
        buttonGroup.removeAllViews();

        for(int i = 0; i < EventArrayList.size(); i++) {

            ImageButton btn = new ImageButton(context);

            // 이미지 버튼 스타일링
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5,5,5);
            btn.setLayoutParams(params);

            String gameModeTypeUrl = (String) EventArrayList.get(i).getInfo().get("gamemodeTypeImageUrl");
            String mapType = (String) EventArrayList.get(i).getInfo().get("name");

            Glide.with(context)
                    .load(gameModeTypeUrl).override((setWidth) / 32, (setWidth) / 32)
                    .into(btn);

            // setBackground는 default 스타일이 따로 정해져있어서 커스텀 스타일을 default 스타일이 덮어씌우는 느낌이 된다.
            //btn.setBackgroundColor(Color.BLACK);
            if(mapType.contains("Championship")) {
                btn.setBackgroundResource(R.drawable.championshipbutton);
            }
            else{
                btn.setBackgroundResource(R.drawable.normalbutton);
            }



            final int idx = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(idx, true);
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
    }
}
