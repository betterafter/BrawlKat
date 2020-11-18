package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.brawlkat.kat_Database.kat_countryDatabase;
import com.example.brawlkat.kat_Database.kat_database;
import com.example.brawlkat.kat_Database.kat_favoritesDatabase;
import com.example.brawlkat.kat_Database.kat_myAccountDatabase;
import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_dataparser.kat_brawlersParser;
import com.example.brawlkat.kat_dataparser.kat_countryCodeParser;
import com.example.brawlkat.kat_dataparser.kat_eventsParser;
import com.example.brawlkat.kat_dataparser.kat_mapsParser;
import com.example.brawlkat.kat_dataparser.kat_official_BrawlerRankingParser;
import com.example.brawlkat.kat_dataparser.kat_official_ClubRankingParser;
import com.example.brawlkat.kat_dataparser.kat_official_PlayerRankingParser;
import com.example.brawlkat.kat_dataparser.kat_official_PowerPlaySeasonParser;
import com.example.brawlkat.kat_dataparser.kat_official_PowerPlaySeasonRankingParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_LoadBeforeMainActivity extends AppCompatActivity {

    // database .....................................................................................................//
    public    static kat_database                                                                       katabase;
    public    static kat_favoritesDatabase                                                              kataFavoritesBase;
    public    static kat_myAccountDatabase                                                              kataMyAccountBase;
    public    static kat_countryDatabase                                                                kataCountryBase;




    public    static Client                                                                             client = new Client();
    public    static HashMap<String, kat_mapsParser.mapData>                                            mapData;

    public    kat_mapsParser                                                                            mapsParser;
    public    kat_brawlersParser                                                                        brawlersParser;


    public    static ArrayList<kat_eventsParser.pair>                                                   EventArrayList;
    public    static ArrayList<HashMap<String, Object>>                                                 BrawlersArrayList;

    public    static HashMap<String, String>                                                            countryCodeMap;

    public    static ArrayList<kat_official_PlayerRankingParser.playerData>                             PlayerRankingArrayList;
    public    static ArrayList<kat_official_ClubRankingParser.clubData>                                 ClubRankingArrayList;
    public    static ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData>                    BrawlerRankingArrayList;
    public    static ArrayList<kat_official_PowerPlaySeasonParser.powerPlaySeasonsData>                 PowerPlaySeasonArrayList;
    public    static ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData>    PowerPlaySeasonRankingArrayList;

    public    static ArrayList<kat_official_PlayerRankingParser.playerData>                             MyPlayerRankingArrayList;
    public    static ArrayList<kat_official_ClubRankingParser.clubData>                                 MyClubRankingArrayList;
    public    static ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData>                    MyBrawlerRankingArrayList;
    public    static ArrayList<kat_official_PowerPlaySeasonParser.powerPlaySeasonsData>                 MyPowerPlaySeasonArrayList;
    public    static ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData>    MyPowerPlaySeasonRankingArrayList;
    // ..............................................................................................................//


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        if(this.getClass().getName().equals("com.example.brawlkat.kat_LoadBeforeMainActivity")) {

            katabase = new kat_database(getApplicationContext(), "kat", null, 2);
            kataFavoritesBase = new kat_favoritesDatabase(getApplicationContext(), "katfav", null, 4);
            kataMyAccountBase = new kat_myAccountDatabase(getApplicationContext(), "katma", null, 1);
            kataCountryBase = new kat_countryDatabase(getApplicationContext(), "katcountry", null, 1);

            System.out.println("kat favorites database size : " + kataFavoritesBase.size());

            kat_countryCodeParser countryCodeParser = new kat_countryCodeParser(this);
            try {
                countryCodeMap = countryCodeParser.DataParser();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            client.init();

            String countryName = countryCodeMap.get("KR");
            kataCountryBase.insert("KR", countryName);

            kat_LoadBeforeMainActivity.client.RankingInit("global", "", "");
            kat_LoadBeforeMainActivity.client.RankingInit("KR", "", "");

            getMapDataThread mdt = new getMapDataThread();
            if (!mdt.isAlive()) mdt.start();

            move();
        }
    }

    private void move(){

        if(kataMyAccountBase.size() == 1) {
            System.out.println("ready to move");
            kat_SearchThread kset = new kat_SearchThread(this, kat_Player_MainActivity.class);
            String tag = kataMyAccountBase.getTag();
            String realTag = tag.substring(1);
            kset.SearchStart(realTag, "players");
        }
        else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(kat_LoadBeforeMainActivity.this, kat_Player_MainActivity.class);
                    startActivity(intent);
                }
            }, 4000);
        }
    }

    // 맵 데이터 받아오기
    private class getMapDataThread extends Thread{
        public void run(){
            try{
                while(true) {
                    if(client.getData() == null || client.getData().size() <= 2) continue;
                    if (client.getData().get(2) != null) {

                        mapsParser = new kat_mapsParser(client.getData().get(2));
                        mapData = mapsParser.DataParser();

//                        brawlersParser = new kat_brawlersParser(client.getData().get(1));
//                        BrawlersArrayList = brawlersParser.DataParser();

                        int time = 1000 * 60 * 30;
                        sleep(time);
                    }
                }
            }
            catch (Exception e){
                // e.printStackTrace();
            }
        }
    }



}
