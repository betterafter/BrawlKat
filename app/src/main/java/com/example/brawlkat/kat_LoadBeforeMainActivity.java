package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.brawlkat.kat_Database.kat_database;
import com.example.brawlkat.kat_Database.kat_favoritesDatabase;
import com.example.brawlkat.kat_Database.kat_myAccountDatabase;
import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_dataparser.kat_brawlersParser;
import com.example.brawlkat.kat_dataparser.kat_eventsParser;
import com.example.brawlkat.kat_dataparser.kat_mapsParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_LoadBeforeMainActivity extends AppCompatActivity {

    // database .....................................................................................................//
    public              static kat_database                            katabase;
    public              static kat_favoritesDatabase                   kataFavoritesBase;
    public              static kat_myAccountDatabase                   kataMyAccountBase;




    public              static Client                                  client = new Client();
    public              static HashMap<String, kat_mapsParser.mapData> mapData;

    public              kat_mapsParser                                 mapsParser;
    public              kat_brawlersParser                             brawlersParser;


    public              static ArrayList<kat_eventsParser.pair>        EventArrayList;
    public              static ArrayList<HashMap<String, Object>>      BrawlersArrayList;
    // ..............................................................................................................//


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        if(this.getClass().getName().equals("com.example.brawlkat.kat_LoadBeforeMainActivity")) {
            katabase = new kat_database(getApplicationContext(), "kat", null, 1);
            kataFavoritesBase = new kat_favoritesDatabase(getApplicationContext(), "katfav", null, 1);
            kataMyAccountBase = new kat_myAccountDatabase(getApplicationContext(), "katma", null, 1);

            client.init();

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
