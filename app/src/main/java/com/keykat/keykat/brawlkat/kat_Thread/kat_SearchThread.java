package com.keykat.keykat.brawlkat.kat_Thread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.keykat.keykat.brawlkat.Client;
import com.keykat.keykat.brawlkat.kat_ExceptionActivity;
import com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.kat_NotificationUpdater;
import com.keykat.keykat.brawlkat.kat_Player_MainActivity;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_clubLogParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_clubInfoParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_playerBattleLogParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_SearchThread extends kat_Player_MainActivity {

    Activity fromActivity;
    Class toClass;
    kat_LoadingDialog kat_loadingDialog;


    boolean setData = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public kat_SearchThread(){};

    public kat_SearchThread(Activity fromActivity, Class toclass){
        this.fromActivity = fromActivity;
        this.toClass = toclass;
    }

    public kat_SearchThread(Activity fromActivity, Class toclass, kat_LoadingDialog kat_loadingDialog){
        this.fromActivity = fromActivity;
        this.toClass = toclass;
        this.kat_loadingDialog = kat_loadingDialog;
    }


    public class SearchThread extends Thread{

        String tag;
        String type;
        ArrayList<String> sendData;
        Context context;

        public SearchThread(String tag, String type, Context context){
            this.tag = tag;
            this.type = type;
            this.context = context;
        }

        public void run(){

            playerTag = tag;
            sendData = new ArrayList<>();

            if(type.equals("players")){
                client.AllTypeInit(tag, type, kat_Player_MainActivity.official, context);
                if(client.getAllTypeData().size() <= 0){
                    try {
                        Client.getAllTypeApiThread apiThread = client.apiThread();
                        apiThread.join();
                        sendData.add(client.getAllTypeData().get(0));
                        sendData.add(client.getAllTypeData().get(1));
                        playerSearch(sendData);

                    } catch (Exception e) {
                        e.printStackTrace();
                        if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
                    }
                }
            }

            else if(type.equals("clubs")){

                client.AllTypeInit(tag, type, kat_Player_MainActivity.official, context);
                if(client.getAllTypeData().size() <= 0){
                    try {
                        Client.getAllTypeApiThread apiThread = client.apiThread();
                        apiThread.join();
                        sendData.add(client.getAllTypeData().get(0));
                        sendData.add(client.getAllTypeData().get(1));
                        clubSearch(sendData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
                    }

                }
            }
        }
    }


    public void playerSearch(ArrayList<String> sendData){

        // 제대로 가져오지 못했을 경우 알림
        if(sendData.get(0).equals("{none}")){

            if(fromActivity.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_Service_OverdrawActivity")){
                return;
            }

            Intent errorIntent = new Intent(kat_Player_MainActivity.kat_player_mainActivity.getApplicationContext(),
                    kat_ExceptionActivity.class);
            if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
            kat_Player_MainActivity.kat_player_mainActivity.startActivity(errorIntent);

        }
        // 제대로 가져왔을 경우
        else{
            official_playerInfoParser = new kat_official_playerInfoParser(sendData.get(0));
            official_playerBattleLogParser = new kat_official_playerBattleLogParser(sendData.get(1));


            try {
                // 자신의 플레이어 데이터를 따로 저장. (맵 승률 서비스를 위해 따로 저장하는 리스트)
                // 이 때 notification의 내용도 바꿔준다.
                if(!kat_LoadBeforeMainActivity.kataMyAccountBase.getTag().equals("")){
                    if(kat_LoadBeforeMainActivity.kataMyAccountBase.getTag()
                            .equals(official_playerInfoParser.DataParser().getTag())) {
                        kat_LoadBeforeMainActivity.eventsPlayerData = official_playerInfoParser.DataParser();
                    }
                }


                playerData = official_playerInfoParser.DataParser();
                if(!client.getAllTypeData().get(1).equals("{none}")) {
                    playerBattleDataList = official_playerBattleLogParser.DataParser();
                    playerBattleDataListStack.add(official_playerBattleLogParser.DataParser());
                }

                String type = "players";
                String Tag = playerData.getTag();
                String name = playerData.getName();
                String isAccount = "NO";

                katabase.delete(type);
                katabase.insert(type, Tag, name, isAccount);


                if(fromActivity == null){
                    // 알림창 업데이트
                    kat_LoadBeforeMainActivity.eventsPlayerData = playerData;
                    kat_NotificationUpdater updater
                            = new kat_NotificationUpdater(kat_player_mainActivity.getApplicationContext());
                    updater.update();
                    return;
                }

                if(fromActivity.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_SearchAccountForSaveActivity")){
                    setMyAccount();
                }

                // 2020.10.26
                // activity.startActivity(intent) 와
                // startActivity(intent) 의 차이점은 뭘까?

                if(fromActivity.getClass().getName().equals("com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity")){

                    client.getFirstInitThread().join();
                }

                Intent intent = new Intent(fromActivity, toClass);
                intent.putExtra("playerData", playerData);
                fromActivity.startActivity(intent);

                if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void clubSearch(ArrayList<String> sendData){

        // 제대로 가져오지 못했을 경우 알림
        if(sendData.get(0).equals("{none}")){

            Intent errorIntent = new Intent(kat_Player_MainActivity.kat_player_mainActivity.getApplicationContext(),
                    kat_ExceptionActivity.class);
            if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
            kat_Player_MainActivity.kat_player_mainActivity.startActivity(errorIntent);
        }
        // 제대로 가져왔을 경우
        else{
            official_clubInfoParser = new kat_official_clubInfoParser(sendData.get(0));
            clubLogParser = new kat_clubLogParser(sendData.get(1));

            try {
                clubData = official_clubInfoParser.DataParser();
                clubLogData = clubLogParser.DataParser();

                String type = "clubs";
                String tag = clubData.getTag();
                String name = clubData.getName();
                String isAccount = "no";

                katabase.delete(type);
                katabase.insert(type, tag, name, isAccount);

                Intent intent = new Intent(fromActivity, toClass);
                intent.putExtra("clubData", clubData);
                intent.putExtra("clubLogData", clubLogData);

                fromActivity.startActivity(intent);

                if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void SearchStart(String tag, String type, Context context){
        SearchThread searchThread = new SearchThread(tag, type, context);
        searchThread.start();

    }

    public void setMyAccount(){
        if(kataMyAccountBase.size() < 1) {

            kataMyAccountBase.insert("player", playerData.getTag(), playerData.getName());

            kat_NotificationUpdater updater = new kat_NotificationUpdater(kat_player_mainActivity.getApplicationContext());
            updater.update();
        }
    }
}