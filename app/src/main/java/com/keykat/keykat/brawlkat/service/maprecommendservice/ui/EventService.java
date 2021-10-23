package com.keykat.keykat.brawlkat.service.maprecommendservice.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
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
import com.keykat.keykat.brawlkat.service.model.data.NotificationData;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;


public class EventService implements MapRecommendContract.ViewpagerView {

    private View mapRecommendView;
    private LinearLayout mapRecommendButtonGroup;
    private WindowManager windowManager;
    private final View.OnTouchListener touchListener;
    private int fixedWidth;
    private int fixedHeight;

    private EventAdapter eventAdapter;

    private final MapRecommendViewPagerPresenter presenter;

    private final Context context;

    public ArrayList<kat_eventsParser.pair> eventArrayList = new ArrayList<>();
    public ArrayList<HashMap<String, Object>> brawlersArrayList = new ArrayList<>();

    private ViewPager2 viewPager = null;
    public boolean isPlayerRecommend = false;

    public EventService(Context context,
                        MapRecommendRepository repository,
                        View.OnTouchListener touchListener) {
        super();
        this.context = context;
        this.touchListener = touchListener;

        initMapInflater();
        this.presenter = new MapRecommendViewPagerPresenter(
                repository, this, eventAdapter
        );
    }

    // map inflater 초기화
    @SuppressLint("InflateParams")
    public void initMapInflater() {
        LayoutInflater layoutInflater
                = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mapRecommendView = layoutInflater.inflate(R.layout.service_map_recommend, null);
        mapRecommendView.setOnTouchListener(touchListener);
        mapRecommendButtonGroup = mapRecommendView.findViewById(R.id.buttonGroup);

        initAdapter();

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        fixedWidth = Math.max(metrics.heightPixels, metrics.widthPixels);
        fixedHeight = Math.min(metrics.heightPixels, metrics.widthPixels);
    }

    private void initAdapter() {
        eventAdapter = new EventAdapter(context, eventArrayList, brawlersArrayList);
        viewPager = mapRecommendView.findViewById(R.id.viewPager2);
        viewPager.setAdapter(eventAdapter);
    }

    // 서비스 실행 시에 보여지는 화면
    public void showEventsInformation() {
        try {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams mapRecommendLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
            );
            if(mapRecommendView.getWindowToken() != null) {
                windowManager.removeView(mapRecommendView);
            }

            mapRecommendLayoutParams.height = fixedHeight;
            mapRecommendLayoutParams.width = fixedWidth / 2;
            windowManager.addView(mapRecommendView, mapRecommendLayoutParams);

            presenter.getMapRecommendData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void changeRecommendViewClick() {

        final Button btn = new Button(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(5, 5, 5, 5);

        btn.setLayoutParams(params);

        btn.setBackgroundColor(context.getResources().getColor(R.color.semiBlack));
        btn.setText(context.getString(R.string.playerChange));
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
        btn.setTextSize(context.getResources().getInteger(R.integer.map_recommend_service_button_text_size));
        mapRecommendButtonGroup.addView(btn);


        btn.setOnClickListener(view -> {

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
        });
    }

    @Override
    public void setOnPlayerRecommendButtonClick() {
        Button changeButton = (Button) mapRecommendButtonGroup.getChildAt(mapRecommendButtonGroup.getChildCount() - 1);
        changeButton.setText(context.getString(R.string.playerChange));
        changeButton.setTextSize(context.getResources().getInteger(R.integer.map_recommend_service_button_text_size));
    }

    @Override
    public void setOnAllRecommendButtonClick() {
        Button changeButton = (Button) mapRecommendButtonGroup.getChildAt(mapRecommendButtonGroup.getChildCount() - 1);
        changeButton.setText(context.getString(R.string.allChange));
        changeButton.setTextSize(context.getResources().getInteger(R.integer.map_recommend_service_button_text_size));
    }


    // 뷰페이저 옆의 이벤트 선택 버튼
    public void addModeButton() {

        LinearLayout buttonGroup = mapRecommendView.findViewById(R.id.buttonGroup);
        buttonGroup.removeAllViews();

        for (int i = 0; i < eventArrayList.size(); i++) {

            ImageButton btn = new ImageButton(context);

            // 이미지 버튼 스타일링
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            btn.setLayoutParams(params);

            String gameModeTypeUrl
                    = (String) eventArrayList.get(i).getInfo().get("gamemodeTypeImageUrl");
            String mapType = (String) eventArrayList.get(i).getInfo().get("name");

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
        if (windowManager != null) {
            if (mapRecommendView != null) {
                if (mapRecommendView.getWindowToken() != null) {
                    windowManager.removeView(mapRecommendView);
                }
            }
        }
    }

    @Override
    public void updateMapRecommendData(@NonNull NotificationData notificationData) {
        this.eventArrayList = notificationData.getEventArrayList();
        this.brawlersArrayList = notificationData.getBrawlerArrayList();
        addModeButton();
        changeRecommendViewClick();
    }
}
