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
import com.keykat.keykat.brawlkat.common.model.datasource.SharedPreferenceManager;
import com.keykat.keykat.brawlkat.common.util.parser.kat_eventsParser;
import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository;
import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendContract;
import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendViewPagerPresenter;
import com.keykat.keykat.brawlkat.service.model.data.NotificationData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;


public class EventService implements MapRecommendContract.ViewpagerView {

    private View mapRecommendView;
    private LinearLayout mapRecommendButtonGroup;
    private WindowManager windowManager;
    private final View.OnTouchListener touchListener;
    private EventAdapter eventAdapter;
    private final MapRecommendViewPagerPresenter presenter;
    private ViewPager2 viewPager = null;
    private final Context context;

    public ArrayList<kat_eventsParser.pair> eventArrayList = new ArrayList<>();
    public ArrayList<HashMap<String, Object>> brawlersArrayList = new ArrayList<>();

    private int fixedWidth;
    private int fixedHeight;
    public boolean isPlayerRecommend = false;

    public EventService(Context context,
                        MapRecommendRepository repository,
                        MapRecommendContract.MainView mainView,
                        View.OnTouchListener touchListener) {
        super();
        this.context = context;
        this.touchListener = touchListener;

        initMapInflater();
        this.presenter = new MapRecommendViewPagerPresenter(
                repository, this, eventAdapter, mainView
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
            presenter.getMapRecommendData();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            kat_eventsParser.pair event = eventArrayList.get(i);

            ImageButton eventButton = new ImageButton(context);

            // 이미지 버튼 스타일링
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            eventButton.setLayoutParams(params);
            eventButton.setBackgroundResource(R.drawable.normalbutton);

            if(event.getInfo() != null) {
                if (event.getInfo().get("gameModeTypeImageUrl") != null) {
                    String gameModeTypeUrl = (String) event.getInfo().get("gameModeTypeImageUrl");
                    Glide.with(context)
                            .load(gameModeTypeUrl)
                            .override(fixedWidth / 32, fixedWidth / 32)
                            .into(eventButton);
                }
            }

            final int idx = i;
            eventButton.setOnClickListener(view -> viewPager.setCurrentItem(idx, true));

            buttonGroup.addView(eventButton);
        }
    }

    @SuppressLint("SetTextI18n")
    public void changeRecommendViewClick() {

        final Button recommendButton = new Button(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(5, 5, 5, 5);

        recommendButton.setLayoutParams(params);

        recommendButton.setBackgroundColor(ContextCompat.getColor(context, R.color.semiBlack));
        recommendButton.setText(context.getString(R.string.playerChange));
        recommendButton.setAllCaps(false);

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);

        if (Objects.equals(sharedPreferenceManager.getAccount(), "")) {
            recommendButton.setTextColor(ContextCompat.getColor(context, R.color.gray));
            recommendButton.setEnabled(false);
        } else {
            recommendButton.setTextColor(ContextCompat.getColor(context, R.color.Color1));
            recommendButton.setEnabled(true);
        }
        Typeface typeface = ResourcesCompat.getFont(context, R.font.lilita_one);
        recommendButton.setTypeface(typeface);
        recommendButton.setTextSize(context.getResources().getInteger(R.integer.map_recommend_service_button_text_size));

        mapRecommendButtonGroup.addView(recommendButton);

        recommendButton.setOnClickListener(view -> {
            isPlayerRecommend = !isPlayerRecommend;
            if (isPlayerRecommend) {
                presenter.setOnPlayerRecommendClicked();
            } else {
                presenter.setOnAllRecommendClicked();
            }

            int length = ((ViewGroup) recommendButton.getParent()).getWidth();

            // 이미지 버튼 스타일링
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    length - 10,
                    length - 10
            );
            params1.setMargins(5, 5, 5, 5);
            recommendButton.setLayoutParams(params1);
        });
    }

    private void setWindowManager() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams mapRecommendLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        if (mapRecommendView.getWindowToken() != null) {
            windowManager.removeView(mapRecommendView);
        }

        mapRecommendLayoutParams.height = fixedHeight;
        mapRecommendLayoutParams.width = fixedWidth / 2;
        windowManager.addView(mapRecommendView, mapRecommendLayoutParams);

    }

    @Override
    public void updateMapRecommendData(@NonNull NotificationData notificationData) {
        this.eventArrayList = notificationData.getEventArrayList();
        this.brawlersArrayList = notificationData.getBrawlerArrayList();
        try {
            setWindowManager();
            addModeButton();
            changeRecommendViewClick();
        } catch (Exception e) {
            e.printStackTrace();
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
}
