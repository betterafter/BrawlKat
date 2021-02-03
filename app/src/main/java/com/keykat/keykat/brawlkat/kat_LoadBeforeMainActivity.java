package com.keykat.keykat.brawlkat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.keykat.keykat.brawlkat.kat_Database.kat_countryDatabase;
import com.keykat.keykat.brawlkat.kat_Database.kat_database;
import com.keykat.keykat.brawlkat.kat_Database.kat_favoritesDatabase;
import com.keykat.keykat.brawlkat.kat_Database.kat_myAccountDatabase;
import com.keykat.keykat.brawlkat.kat_Database.kat_settingDatabase;
import com.keykat.keykat.brawlkat.kat_Thread.kat_SearchThread;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_brawlersParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_countryCodeParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_eventsParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_mapsParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_BrawlerRankingParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_ClubRankingParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_PlayerRankingParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_PowerPlaySeasonParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_PowerPlaySeasonRankingParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_playerInfoParser;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class kat_LoadBeforeMainActivity extends AppCompatActivity {

    // database .....................................................................................................//
    public    static kat_database                                                                       katabase;
    public    static kat_favoritesDatabase                                                              kataFavoritesBase;
    public    static kat_myAccountDatabase                                                              kataMyAccountBase;
    public    static kat_countryDatabase                                                                kataCountryBase;
    public    static kat_settingDatabase                                                                kataSettingBase;

    // ..............................................................................................................//



    public    static Client                                                                             client = new Client();
    public    static HashMap<String, kat_mapsParser.mapData>                                            mapData;

    public    kat_mapsParser                                                                            mapsParser;
    public    kat_brawlersParser                                                                        brawlersParser;

    public    static kat_official_playerInfoParser.playerData                                           eventsPlayerData;

    public    static ArrayList<kat_eventsParser.pair>                                                   EventArrayList;
    public    static ArrayList<HashMap<String, Object>>                                                 BrawlersArrayList;

    public    static HashMap<String, String>                                                            countryCodeMap;

    public    static ArrayList<kat_official_PlayerRankingParser.playerData>                             PlayerRankingArrayList;
    public    static ArrayList<kat_official_ClubRankingParser.clubData>                                 ClubRankingArrayList;
    public    static HashMap<String, ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData>>   BrawlerRankingArrayList = new HashMap<>();
    public    static ArrayList<kat_official_PowerPlaySeasonParser.powerPlaySeasonsData>                 PowerPlaySeasonArrayList;
    public    static HashMap<String, ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData>> PowerPlaySeasonRankingArrayList = new HashMap<>();

    public    static ArrayList<kat_official_PlayerRankingParser.playerData>                             MyPlayerRankingArrayList;
    public    static ArrayList<kat_official_ClubRankingParser.clubData>                                 MyClubRankingArrayList;
    public    static HashMap<String, HashMap<String, ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData>>> MyBrawlerRankingArrayList = new HashMap<>();
    public    static ArrayList<kat_official_PowerPlaySeasonParser.powerPlaySeasonsData>                 MyPowerPlaySeasonArrayList;
    public    static HashMap<String, HashMap<String, ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData>>> MyPowerPlaySeasonRankingArrayList = new HashMap<>();

    private   kat_LoadingDialog                                                                         dialog;

    // ..............................................................................................................//

    public   static final int                                                                           TYPE_WIFI = 1;
    public   static final int                                                                           TYPE_MOBILE = 2;
    public   static final int                                                                           TYPE_NOT_CONNECTED = 3;

    public   static final String ApiRootUrl = "https://api.brawlify.com";
    public   static final String CdnRootUrl = "https://cdn.brawlify.com";
    public   static final String WebRootUrl = "https://brawlify.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

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


        if(this.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity")) {


            // client의 getApiThread를 앱이 종료 후에 같이 종료되어 데이터 손실을 막게 해줌
            startService(new Intent(this, kat_onTaskRemovedService.class));
            // 초기화
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });


            katabase = new kat_database(getApplicationContext(), "kat", null, 2);
            kataFavoritesBase = new kat_favoritesDatabase(getApplicationContext(), "katfav", null, 4);
            kataMyAccountBase = new kat_myAccountDatabase(getApplicationContext(), "katma", null, 1);
            kataCountryBase = new kat_countryDatabase(getApplicationContext(), "katcountry", null, 1);
            kataSettingBase = new kat_settingDatabase(getApplicationContext(), "kataSetting", null, 2);

            kataSettingBase.init();

            kat_countryCodeParser countryCodeParser = new kat_countryCodeParser(this);
            try {
                countryCodeMap = countryCodeParser.DataParser();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            client.init();

            String countryName = countryCodeMap.get("KR");
            if(kataCountryBase.size() == 0)
                kataCountryBase.insert("KR", countryName);

            kat_LoadBeforeMainActivity.client.RankingInit("global", "", "");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity")){
            move();
        }
    }

    private void move(){

        if(kataMyAccountBase.size() == 1) {
            kat_SearchThread kset = new kat_SearchThread(this, kat_Player_MainActivity.class);
            String tag = kataMyAccountBase.getTag();
            String realTag = tag.substring(1);
            kset.SearchStart(realTag, "players", getApplicationContext());
        }
        else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(kat_LoadBeforeMainActivity.this, kat_Player_MainActivity.class);
                    startActivity(intent);
                }
            }, 3000);
        }
    }




    @Override
    public void onBackPressed() {
        if(this.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity")){
            ActivityCompat.finishAffinity(this);
            finishAffinity();
        }
        else super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity")){
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity")){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity")){
            finish();
        }
    }
}
