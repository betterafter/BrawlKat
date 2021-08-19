package com.keykat.keykat.brawlkat.load.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.activity.kat_ExceptionActivity;
import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;
import com.keykat.keykat.brawlkat.service.util.kat_onTaskRemovedService;
import com.keykat.keykat.brawlkat.util.database.kat_countryDatabase;
import com.keykat.keykat.brawlkat.util.database.kat_database;
import com.keykat.keykat.brawlkat.util.database.kat_favoritesDatabase;
import com.keykat.keykat.brawlkat.util.database.kat_myAccountDatabase;
import com.keykat.keykat.brawlkat.util.database.kat_settingDatabase;
import com.keykat.keykat.brawlkat.util.kat_Data;
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

        kat_Data.client = new Client(getApplicationContext());

        kat_Data.options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        kat_Data.SCREEN_HEIGHT = metrics.heightPixels;
        kat_Data.SCREEN_WIDTH = metrics.widthPixels;



        // 초기 할당이 필요한 리스트들은 여기서 할당해준다./////////////////////////////////////////////////////

        kat_Data.BrawlerRankingArrayList = new HashMap<>();
        kat_Data.PowerPlaySeasonRankingArrayList = new HashMap<>();
        kat_Data.MyPowerPlaySeasonRankingArrayList= new HashMap<>();
        kat_Data.MyBrawlerRankingArrayList = new HashMap<>();

        kat_Data.playerBattleDataList = new ArrayList<>();
        kat_Data.playerBattleDataListStack = new Stack<>();



        ////////////////////////////////////////////////////////////////////////////////////////////




        ConnectivityManager manager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo == null){
            Intent errorIntent = new Intent(this, kat_ExceptionActivity.class);
            errorIntent.putExtra("which", "kat_LoadBeforeMainActivity");
            errorIntent.putExtra("cause", "error.INTERNET");
            startActivity(errorIntent);

            finish();
        }

        // client의 getApiThread를 앱이 종료 후에 같이 종료되어 데이터 손실을 막게 해줌
        startService(new Intent(this, kat_onTaskRemovedService.class));
        // 초기화
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        // 데이터베이스 모두 초기화////////////////////////////////////////////////////////////////////////
        kat_Data.katabase = new kat_database(getApplicationContext(), "kat", null, 2);
        kat_Data.kataFavoritesBase = new kat_favoritesDatabase(getApplicationContext(), "katfav", null, 4);
        kat_Data.kataMyAccountBase = new kat_myAccountDatabase(getApplicationContext(), "katma", null, 1);
        kat_Data.kataCountryBase = new kat_countryDatabase(getApplicationContext(), "katcountry", null, 1);
        kat_Data.kataSettingBase = new kat_settingDatabase(getApplicationContext(), "kataSetting", null, 2);

        kat_Data.kataSettingBase.init();

        kat_countryCodeParser countryCodeParser = new kat_countryCodeParser(this);
        try {
            kat_Data.countryCodeMap = countryCodeParser.DataParser();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String countryName = kat_Data.countryCodeMap.get("KR");
        if(kat_Data.kataCountryBase.size() == 0)
            kat_Data.kataCountryBase.insert("KR", countryName);

        kat_Data.client.RankingInit("global", "", "");
        ////////////////////////////////////////////////////////////////////////////////////////////

        kat_Data.adRequest = new AdRequest.Builder().build();

    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkThread thread = new checkThread();
        thread.start();

        try{
            kat_Data.client.init();
            thread.join();
            move();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void move(){

        try {

            if (kat_Data.kataMyAccountBase.size() == 1) {
                kat_SearchThread kset = new kat_SearchThread(this, kat_Player_MainActivity.class);
                String tag = kat_Data.kataMyAccountBase.getTag();
                String realTag = tag.substring(1);
                kset.SearchStart(realTag, "players", getApplicationContext());
            } else {
                Intent intent = new Intent(kat_LoadBeforeMainActivity.this, kat_Player_MainActivity.class);
                startActivity(intent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // 필수 데이터를 가져오기 전까지 체크 스레드를 계속 돌린다. 여기서 load 화면의 텍스트를 계속 바꿔준다.
    private class checkThread extends Thread{
        public void run(){
            while(true){
                if(kat_Data.EventArrayList != null && kat_Data.BrawlersArrayList != null
                && kat_Data.mapData != null) {
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
