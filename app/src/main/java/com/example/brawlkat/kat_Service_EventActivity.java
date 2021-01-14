package com.example.brawlkat;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
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
import com.example.brawlkat.kat_adapter.kat_EventAdapter;
import com.example.brawlkat.kat_dataparser.kat_brawlersParser;
import com.example.brawlkat.kat_dataparser.kat_eventsParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.core.content.res.ResourcesCompat;
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

    private         static long                         mLastClickTime = 0;


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

        EventArrayList = kat_LoadBeforeMainActivity.EventArrayList;
        BrawlersArrayList = kat_LoadBeforeMainActivity.BrawlersArrayList;

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
                    if(!kat_Player_MainActivity.isServiceStart) break;

                    if(viewPager == null){
                        viewPager = (ViewPager2) overdrawActivity.mapRecommendView.findViewById(R.id.viewPager2);
                        eventAdapter = new kat_EventAdapter(context, EventArrayList, BrawlersArrayList,
                                kat_Service_EventActivity.this);
                    }

                    int time = 1000 * 60;
                    sleep(time);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void Change(){
        viewPager.setAdapter(eventAdapter);
    }

    public void ChangeRecommendViewClick(){

        final kat_LoadingDialog kat_loadingDialog = new kat_LoadingDialog(context);

        final Button btn = new Button(context);
        LinearLayout buttonGroup = (LinearLayout) overdrawActivity.mapRecommendView.findViewById(R.id.buttonGroup);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(5, 5,5,5);

        btn.setLayoutParams(params);

        btn.setBackgroundColor(context.getResources().getColor(R.color.semiBlack));
        btn.setText("my");
        btn.setAllCaps(false);
        if(kat_LoadBeforeMainActivity.kataMyAccountBase.getTag().equals("")) {
            btn.setTextColor(context.getResources().getColor(R.color.gray));
            btn.setEnabled(false);
        }
        else {
            btn.setTextColor(context.getResources().getColor(R.color.Color1));
            btn.setEnabled(true);
        }
        Typeface typeface = ResourcesCompat.getFont(context, R.font.lilita_one);
        btn.setTypeface(typeface);
        btn.setTextSize(12);
        buttonGroup.addView(btn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (SystemClock.elapsedRealtime() - mLastClickTime < 1500){
                    return;
                }

                if(overdrawActivity.getPlayerTag == null){
                    mLastClickTime = SystemClock.elapsedRealtime();
                    return;
                }

                Player_NonPlayer_ViewChangeThread viewChangeThread = new Player_NonPlayer_ViewChangeThread(btn);
                viewChangeThread.start();

                int length = ((ViewGroup)btn.getParent()).getWidth();

                    // 이미지 버튼 스타일링
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            length - 10,
                            length - 10
                    );
                    params.setMargins(5, 5,5,5);
                    btn.setLayoutParams(params);

                Drawable drawable = context.getResources().getDrawable(R.drawable.round_rotate_right_24_small);
                btn.setBackground(drawable);
                btn.setText("");

                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
    }

    private class Player_NonPlayer_ViewChangeThread extends Thread{

        Handler viewChangeHandler = new Handler();
        Button btn;

        public Player_NonPlayer_ViewChangeThread(Button btn){
            this.btn = btn;
        }

        public void run(){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {

                        // 버튼 클릭 시 유저 추천 뷰 <-> 전체 추천 뷰 전환
                        if(changeRecommendView) {
                            changeRecommendView = false;
                            btn.setText("My");
                            btn.setTextSize(12);
                            btn.setBackground(null);
                        }
                        else {
                            changeRecommendView = true;
                            btn.setText("All");
                            btn.setTextSize(10);
                            btn.setBackground(null);
                        }
                        eventAdapter.refresh();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            viewChangeHandler.post(runnable);
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

        client = null;

        if(overdrawActivity.mapWindowManager != null){
            if(overdrawActivity.mapRecommendView != null)
                overdrawActivity.mapWindowManager.removeView(overdrawActivity.mapRecommendView);
        }
    }
}
