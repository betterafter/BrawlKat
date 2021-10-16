package com.keykat.keykat.brawlkat.service.maprecommendservice.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository;
import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendContract;
import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendViewPagerPresenter;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;


public class kat_Service_EventService implements MapRecommendContract.ViewpagerView {

    private View mapRecommendView;
    private LinearLayout mapRecommendButtonGroup;
    private WindowManager windowManager;
    private final View.OnTouchListener touchListener;
    private int fixedWidth;
    private int fixedHeight;

    private kat_EventAdapter eventAdapter;


    private MapRecommendViewPagerPresenter presenter;
    private MapRecommendContract.MainView mainView;


    private final Context context;
    public getEventsThread eventsThread;

    public ArrayList<kat_eventsParser.pair> EventArrayList;
    public ArrayList<HashMap<String, Object>> BrawlersArrayList;

    private ViewPager2 viewPager = null;
    public boolean isPlayerRecommend = false;

    private static long mLastClickTime = 0;
    public boolean isEventThreadStart = true;


    public kat_Service_EventService(Context context,
                                    MapRecommendRepository repository,
                                    MapRecommendContract.MainView mainView,
                                    View.OnTouchListener touchListener) {
        super();
        this.context = context;
        this.mainView = mainView;
        this.touchListener = touchListener;

        this.presenter = new MapRecommendViewPagerPresenter(
                repository, this, eventAdapter
        );

        init_mapInflater();
    }

    // map inflater 초기화
    @SuppressLint("InflateParams")
    public void init_mapInflater() {
        LayoutInflater layoutInflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mapRecommendView = layoutInflater.inflate(R.layout.service_map_recommend, null);
        mapRecommendView.setOnTouchListener(touchListener);
        mapRecommendButtonGroup = mapRecommendView.findViewById(R.id.buttonGroup);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        fixedWidth = Math.max(metrics.heightPixels, metrics.widthPixels);
        fixedHeight = Math.min(metrics.heightPixels, metrics.widthPixels);
    }

    // 서비스 실행 시에 보여지는 화면
    public void ShowEventsInformation() {
        WindowManager.LayoutParams mapRecommendLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );

        if (mapRecommendView.getWindowToken() == null) {
            mapRecommendLayoutParams.height = fixedHeight;
            mapRecommendLayoutParams.width = fixedWidth / 2;
            windowManager.addView(mapRecommendView, mapRecommendLayoutParams);
        }
    }

    // 서버에서 가져온 api 데이터 불러오고 리스트에 넣기
    public void getCurrentEventsInformation() {

        EventArrayList = KatData.EventArrayList;
        BrawlersArrayList = KatData.BrawlersArrayList;

        eventsThread = new getEventsThread();
        isEventThreadStart = true;
        if (!eventsThread.isAlive())
            eventsThread.start();
    }

    // starlist.pro api 데이터 받고 저장
    public class getEventsThread extends Thread {

        public void run() {

            try {
                while (isEventThreadStart) {
                    if (!KatData.isForegroundServiceStart) break;

                    if (viewPager == null) {
                        viewPager = mapRecommendView.findViewById(R.id.viewPager2);
                        eventAdapter = new kat_EventAdapter(
                                context,
                                EventArrayList,
                                BrawlersArrayList
                        );
                    }

                    int time = 1000 * 60;
                    sleep(time);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void Change() {
        viewPager.setAdapter(eventAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void ChangeRecommendViewClick() {

        final Button btn = new Button(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(5, 5, 5, 5);

        btn.setLayoutParams(params);

        btn.setBackgroundColor(context.getResources().getColor(R.color.semiBlack));
        btn.setText("my");
        btn.setAllCaps(false);
        if (KatData.kataMyAccountBase.getTag().equals("")) {
            btn.setTextColor(context.getResources().getColor(R.color.gray));
            btn.setEnabled(false);
        } else {
            btn.setTextColor(context.getResources().getColor(R.color.Color1));
            btn.setEnabled(true);
        }
        Typeface typeface = ResourcesCompat.getFont(context, R.font.lilita_one);
        btn.setTypeface(typeface);
        btn.setTextSize(12);
        mapRecommendButtonGroup.addView(btn);


        btn.setOnClickListener(view -> {

//            if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
//                return;
//            }
//
//            if (KatData.playerTag == null) {
//                mLastClickTime = SystemClock.elapsedRealtime();
//                return;
//            }

            System.out.println(isPlayerRecommend);
            isPlayerRecommend = !isPlayerRecommend;
            if (isPlayerRecommend) {
                presenter.setOnPlayerRecommendClicked();
            } else {
                presenter.setOnAllRecommendClicked();
            }

            int length = ((ViewGroup) btn.getParent()).getWidth();

            // 이미지 버튼 스타일링
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    length - 10,
                    length - 10
            );
            params1.setMargins(5, 5, 5, 5);
            btn.setLayoutParams(params1);

            mLastClickTime = SystemClock.elapsedRealtime();
        });
    }

    @Override
    public void setOnPlayerRecommendButtonClick() {
        Button changeButton = (Button) mapRecommendButtonGroup.getChildAt(mapRecommendButtonGroup.getChildCount() - 1);
        changeButton.setText("My");
        changeButton.setTextSize(12);
        changeButton.setBackground(null);
    }

    @Override
    public void setOnAllRecommendButtonClick() {
        Button changeButton = (Button) mapRecommendButtonGroup.getChildAt(mapRecommendButtonGroup.getChildCount() - 1);
        changeButton.setText("All");
        changeButton.setTextSize(10);
        changeButton.setBackground(null);
    }


    // 뷰페이저 옆의 이벤트 선택 버튼
    public void addModeButton() {

        LinearLayout buttonGroup = mapRecommendView.findViewById(R.id.buttonGroup);
        buttonGroup.removeAllViews();

        for (int i = 0; i < EventArrayList.size(); i++) {

            ImageButton btn = new ImageButton(context);

            // 이미지 버튼 스타일링
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            btn.setLayoutParams(params);

            String gameModeTypeUrl
                    = (String) EventArrayList.get(i).getInfo().get("gamemodeTypeImageUrl");
            String mapType = (String) EventArrayList.get(i).getInfo().get("name");

            Glide.with(context)
                    .load(gameModeTypeUrl)
                    .override(fixedWidth / 32, fixedWidth / 32)
                    .into(btn);

            // setBackground는 default 스타일이 따로 정해져있어서 커스텀 스타일을 default 스타일이 덮어씌우는 느낌이 된다.
            //btn.setBackgroundColor(Color.BLACK);
            if (mapType != null && mapType.contains("Championship")) {
                btn.setBackgroundResource(R.drawable.championshipbutton);
            } else {
                btn.setBackgroundResource(R.drawable.normalbutton);
            }


            final int idx = i;
            btn.setOnClickListener(view -> viewPager.setCurrentItem(idx, true));

            buttonGroup.addView(btn);
        }
    }

    public void onDismiss() {
        isEventThreadStart = false;

        if (eventsThread != null) {
            eventsThread = null;
        }

        if (windowManager != null) {
            if (mapRecommendView != null) {
                if (mapRecommendView.getWindowToken() != null) {
                    windowManager.removeView(mapRecommendView);
                }
            }
        }
    }
}
