package com.keykat.keykat.brawlkat.load.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.database.kat_countryDatabase;
import com.keykat.keykat.brawlkat.util.database.kat_database;
import com.keykat.keykat.brawlkat.util.database.kat_favoritesDatabase;
import com.keykat.keykat.brawlkat.util.database.kat_myAccountDatabase;
import com.keykat.keykat.brawlkat.util.network.AsyncCoroutine;
import com.keykat.keykat.brawlkat.util.network.Client;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;
import com.keykat.keykat.brawlkat.util.parser.kat_countryCodeParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class kat_LoadBeforeMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        KatData.client = new Client(getApplicationContext());
        KatData.currentActivity = this;

        KatData.options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        KatData.SCREEN_HEIGHT = metrics.heightPixels;
        KatData.SCREEN_WIDTH = metrics.widthPixels;


        // 초기 할당이 필요한 리스트들은 여기서 할당해준다./////////////////////////////////////////////////////

        KatData.PlayerRankingArrayList = new ArrayList<>();
        KatData.MyPlayerRankingArrayList = new ArrayList<>();

        KatData.ClubRankingArrayList = new ArrayList<>();
        KatData.MyClubRankingArrayList = new ArrayList<>();

        KatData.BrawlerRankingArrayList = new HashMap<>();
        KatData.MyBrawlerRankingArrayList = new HashMap<>();

        KatData.PowerPlaySeasonArrayList = new ArrayList<>();
        KatData.MyPowerPlaySeasonArrayList = new ArrayList<>();

        KatData.PowerPlaySeasonRankingArrayList = new HashMap<>();
        KatData.MyPowerPlaySeasonRankingArrayList = new HashMap<>();

        KatData.playerBattleDataList = new ArrayList<>();
        KatData.playerBattleDataListStack = new Stack<>();
        ////////////////////////////////////////////////////////////////////////////////////////////


        // 처음 앱 실행할 때 광고를 초기화하기 위해선 인터넷 연결상태를 체크할 필요가 있음.
        ConnectivityManager manager
                = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE
        );
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // 인터넷에 연결되지 않았다면 경고 다이얼로그 표시
        if (networkInfo == null) {
            KatData.serverProblemDialog();
        }
        // 인터넷에 연결되었다면 광고 초기화
        else {
            // 광고 초기화
            MobileAds.initialize(this, initializationStatus -> {
            });
            KatData.adRequest = new AdRequest.Builder().build();
        }

        // client의 getApiThread를 앱이 종료 후에 같이 종료되어 데이터 손실을 막게 해줌
        //startService(new Intent(this, kat_onTaskRemovedService.class));


        // 데이터베이스 모두 초기화////////////////////////////////////////////////////////////////////////
        KatData.katabase
                = new kat_database(
                getApplicationContext(),
                "kat",
                null,
                2
        );

        KatData.kataFavoritesBase
                = new kat_favoritesDatabase(
                getApplicationContext(),
                "katfav",
                null,
                4
        );

        KatData.kataMyAccountBase
                = new kat_myAccountDatabase(
                getApplicationContext(),
                "katma",
                null,
                1
        );

        KatData.kataCountryBase
                = new kat_countryDatabase(
                getApplicationContext(),
                "katcountry",
                null,
                1
        );

        kat_countryCodeParser countryCodeParser
                = new kat_countryCodeParser(this);
        try {
            KatData.countryCodeMap = countryCodeParser.DataParser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String countryName = KatData.countryCodeMap.get("KR");
        if (KatData.kataCountryBase.size() == 0)
            KatData.kataCountryBase.insert("KR", countryName);
        ////////////////////////////////////////////////////////////////////////////////////////////
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        KatData.currentActivity = this;

        try {
            KatData.client.init();
            KatData.client.RankingInit("global", "", "");

            checkThread thread = new checkThread();
            thread.setPriority(10);
            thread.start();

            // 로딩 텍스트 바꾸는 코루틴
            AsyncCoroutine.Companion.changeLoadingText(findViewById(R.id.load_text));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void move() {

        try {
            if (KatData.kataMyAccountBase.size() == 1) {
                kat_SearchThread kat_searchThread
                        = new kat_SearchThread(this, kat_Player_MainActivity.class);
                String tag = KatData.kataMyAccountBase.getTag();
                String realTag = tag.substring(1);
                kat_searchThread.SearchStart(realTag, "players", getApplicationContext());
            } else {
                Intent intent = new Intent(
                        kat_LoadBeforeMainActivity.this,
                        kat_Player_MainActivity.class
                );
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 필수 데이터를 가져오기 전까지 체크 스레드를 계속 돌린다. 여기서 load 화면의 텍스트를 계속 바꿔준다.
    private class checkThread extends Thread {

        public void run() {

            while (true) {
                if (KatData.EventArrayList != null && KatData.BrawlersArrayList != null
                        && KatData.mapData != null) {

                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(kat_LoadBeforeMainActivity.this::move, 0);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
