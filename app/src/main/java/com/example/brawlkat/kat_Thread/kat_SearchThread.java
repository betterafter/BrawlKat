package com.example.brawlkat.kat_Thread;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.brawlkat.Client;
import com.example.brawlkat.R;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_LoadingDialog;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_SeasonRewardsCalculator;
import com.example.brawlkat.kat_Service_BrawlStarsNotifActivity;
import com.example.brawlkat.kat_broadcast_receiver.kat_ButtonBroadcastReceiver;
import com.example.brawlkat.kat_dataparser.kat_clubLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_clubInfoParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;

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

        public SearchThread(String tag, String type){
            this.tag = tag;
            this.type = type;
        }

        public void run(){

            playerTag = tag;
            sendData = new ArrayList<>();

            if(type.equals("players")){
                client.AllTypeInit(tag, type, kat_Player_MainActivity.official);
                if(client.getAllTypeData().size() <= 0){
                    try {
                        Client.getAllTypeApiThread apiThread = client.apiThread();
                        apiThread.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sendData.add(client.getAllTypeData().get(0));
                sendData.add(client.getAllTypeData().get(1));
                playerSearch(sendData);

            }

            else if(type.equals("clubs")){

                client.AllTypeInit(tag, type, kat_Player_MainActivity.official);
                if(client.getAllTypeData().size() <= 0){
                    try {
                        Client.getAllTypeApiThread apiThread = client.apiThread();
                        apiThread.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                sendData.add(client.getAllTypeData().get(0));
                sendData.add(client.getAllTypeData().get(1));
                clubSearch(sendData);
            }
        }
    }


    public void playerSearch(ArrayList<String> sendData){

        // 제대로 가져오지 못했을 경우 알림
        if(sendData.get(0).equals("{none}")){
//            Toast toast = Toast.makeText(fromActivity.getApplicationContext(),
//                    "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
//            toast.show();
            System.out.println("something wrong....");
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

                if(fromActivity.getClass().getName().equals("com.example.brawlkat.kat_SearchAccountForSaveActivity")){
                    setMyAccount();
                }

                // 2020.10.26
                // activity.startActivity(intent) 와
                // startActivity(intent) 의 차이점은 뭘까?

                if(fromActivity.getClass().getName().equals("com.example.brawlkat.kat_LoadBeforeMainActivity")){

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
//            Toast toast = Toast.makeText(getApplicationContext(), "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
//            toast.show();
            System.out.println("something wrong on clubSearch...");
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


    public void SearchStart(String tag, String type){
        SearchThread searchThread = new SearchThread(tag, type);
        searchThread.start();

    }

    public void setMyAccount(){
        if(kataMyAccountBase.size() < 1) {

            Context context = kat_Player_MainActivity.kat_player_mainActivity.getApplicationContext();

            kataMyAccountBase.insert("player", playerData.getTag(), playerData.getName());
            kat_LoadBeforeMainActivity.eventsPlayerData = playerData;

            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.main_notification);

            kat_official_playerInfoParser.playerData playerData
                    = kat_LoadBeforeMainActivity.eventsPlayerData;

            kat_SeasonRewardsCalculator seasonRewardsCalculator
                    = new kat_SeasonRewardsCalculator(playerData);
            int seasonRewards = seasonRewardsCalculator.SeasonsRewardsCalculator();

            contentView.setTextViewText(R.id.title, playerData.getName());
            contentView.setTextViewText(R.id.explain_text, " after season end");
            contentView.setTextViewText(R.id.text, seasonRewards + " points");

            // 인텐트 등록
            Intent homeIntent = new Intent(context, kat_ButtonBroadcastReceiver.class);
            homeIntent.setAction("main.HOME");

            Intent analyticsIntent = new Intent(context, kat_ButtonBroadcastReceiver.class);
            analyticsIntent.setAction("main.ANALYTICS");


            PendingIntent HomePendingIntent = PendingIntent.getBroadcast(context, 0, homeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // 종료버튼과 펜딩 인텐트 연결
            PendingIntent AnalyticsPendingIntent = PendingIntent.getBroadcast(context, 0, analyticsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            contentView.setOnClickPendingIntent(R.id.main_home, HomePendingIntent);
            contentView.setOnClickPendingIntent(R.id.main_analytics, AnalyticsPendingIntent);

            kat_Service_BrawlStarsNotifActivity.notification.setCustomContentView(contentView);
            kat_Service_BrawlStarsNotifActivity.mNotificationManager.notify(1,
                    kat_Service_BrawlStarsNotifActivity.notification.build());
        }

    }

}
