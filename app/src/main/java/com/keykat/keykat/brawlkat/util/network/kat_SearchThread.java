package com.keykat.keykat.brawlkat.util.network;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.keykat.keykat.brawlkat.home.activity.kat_ExceptionActivity;
import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.service.util.kat_NotificationUpdater;
import com.keykat.keykat.brawlkat.util.kat_Data;
import com.keykat.keykat.brawlkat.util.parser.kat_clubLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_clubInfoParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerBattleLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_SearchThread extends AppCompatActivity {

    Activity fromActivity;
    Class toClass;
    kat_LoadingDialog kat_loadingDialog;


    public static boolean SearchDataOnOverdraw = false;

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

            SearchDataOnOverdraw = true;
            kat_Data.playerTag = tag;
            sendData = new ArrayList<>();

            if(type.equals("players")){
                kat_Data.client.AllTypeInit(tag, type, kat_Player_MainActivity.official, context);
                if(kat_Data.client.getAllTypeData().size() <= 0){
                    try {
                        Client.getAllTypeApiThread apiThread = kat_Data.client.apiThread();
                        apiThread.join();
                        sendData.add(kat_Data.client.getAllTypeData().get(0));
                        sendData.add(kat_Data.client.getAllTypeData().get(1));
                        playerSearch(sendData);

                    } catch (Exception e) {
                        e.printStackTrace();
                        if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
                    }
                }
            }

            else if(type.equals("clubs")){

                kat_Data.client.AllTypeInit(tag, type, kat_Player_MainActivity.official, context);
                if(kat_Data.client.getAllTypeData().size() <= 0){
                    try {
                        Client.getAllTypeApiThread apiThread = kat_Data.client.apiThread();
                        apiThread.join();
                        sendData.add(kat_Data.client.getAllTypeData().get(0));
                        sendData.add(kat_Data.client.getAllTypeData().get(1));
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

            Intent errorIntent = new Intent(kat_Player_MainActivity.kat_player_mainActivity.getApplicationContext(),
                    kat_ExceptionActivity.class);
            if(kat_loadingDialog != null) kat_loadingDialog.dismiss();
            kat_Player_MainActivity.kat_player_mainActivity.startActivity(errorIntent);

        }
        // 제대로 가져왔을 경우
        else{
            kat_Data.official_playerInfoParser = new kat_official_playerInfoParser(sendData.get(0));
            kat_Data.official_playerBattleLogParser = new kat_official_playerBattleLogParser(sendData.get(1));


            try {
                // 자신의 플레이어 데이터를 따로 저장. (맵 승률 서비스를 위해 따로 저장하는 리스트)
                // 이 때 notification의 내용도 바꿔준다.
                if(!kat_Data.kataMyAccountBase.getTag().equals("")){
                    if(kat_Data.kataMyAccountBase.getTag()
                            .equals(kat_Data.official_playerInfoParser.DataParser().getTag())) {

                        // 알람창 강제 종료되는 것 방지
                        if(sendData.get(0).length() > 50)
                            kat_Data.eventsPlayerData = kat_Data.official_playerInfoParser.DataParser();
                    }
                }


                kat_Data.playerData = kat_Data.official_playerInfoParser.DataParser();
                System.out.println(kat_Data.playerData.toString());
                System.out.println(kat_Data.client.getAllTypeData().get(1));
                if(!kat_Data.client.getAllTypeData().get(1).equals("{none}")
                && kat_Data.client.getAllTypeData().get(1).length() > 30
                && kat_Data.client.getAllTypeData().get(1) != null) {
                    kat_Data.playerBattleDataList = kat_Data.official_playerBattleLogParser.DataParser();
                    kat_Data.playerBattleDataListStack.add(kat_Data.official_playerBattleLogParser.DataParser());
                }

                String type = "players";
                String Tag = kat_Data.playerData.getTag();
                String name = kat_Data.playerData.getName();
                String isAccount = "NO";

                kat_Data.katabase.delete(type);
                kat_Data.katabase.insert(type, Tag, name, isAccount);

                if(fromActivity == null){
                    // 알림창 업데이트
                    kat_NotificationUpdater updater
                            = new kat_NotificationUpdater(getApplicationContext(), kat_Data.playerData);
                    updater.update();
                    SearchDataOnOverdraw = false;
                    return;
                }

                // "자신의 계정 찾기"에서 넘어왔을 경우
                if(fromActivity.getClass().getName().equals("com.keykat.keykat.brawlkat.home.activity.kat_SearchAccountForSaveActivity")){
                    if(sendData.get(0).length() > 50)
                        kat_Data.eventsPlayerData = kat_Data.playerData;
                    kat_NotificationUpdater updater = new kat_NotificationUpdater(fromActivity.getApplicationContext());
                    updater.update();
                }


                if(kat_Data.kataMyAccountBase.size() < 1) {

                    kat_Data.kataMyAccountBase.insert(
                            "player",
                            kat_Data.playerData.getTag(),
                            kat_Data.playerData.getName()
                    );
                }
                kat_NotificationUpdater updater = new kat_NotificationUpdater(fromActivity.getApplicationContext());
                updater.update();

                ActivityManager manager = (ActivityManager)fromActivity.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                ComponentName componentName= info.get(0).topActivity;
                String topActivityName = componentName.getShortClassName().substring(1);
                System.out.println(topActivityName);
                System.out.println(fromActivity.getClass().getName());

                String com = topActivityName;
                String to = fromActivity.getClass().getName();

                if(fromActivity != null){
                    if(!com.equals(to) && !com.contains(to) && !to.contains(com))
                        return;
                }

                Intent intent = new Intent(fromActivity, toClass);
                intent.putExtra("playerData", kat_Data.playerData);
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
            kat_Data.official_clubInfoParser = new kat_official_clubInfoParser(sendData.get(0));
            kat_Data.clubLogParser = new kat_clubLogParser(sendData.get(1));

            try {
                kat_Data.clubData = kat_Data.official_clubInfoParser.DataParser();
                kat_Data.clubLogData = kat_Data.clubLogParser.DataParser();

                String type = "clubs";
                String tag = kat_Data.clubData.getTag();
                String name = kat_Data.clubData.getName();
                String isAccount = "no";

                kat_Data.katabase.delete(type);
                kat_Data.katabase.insert(type, tag, name, isAccount);

                ActivityManager manager = (ActivityManager)fromActivity.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                ComponentName componentName= info.get(0).topActivity;
                String topActivityName = componentName.getShortClassName().substring(1);
                System.out.println(topActivityName);
                System.out.println(fromActivity.getClass().getName());

                String com = topActivityName;
                String to = fromActivity.getClass().getName();

                if(fromActivity != null){
                    if(!com.equals(to) && !com.contains(to) && !to.contains(com))
                        return;
                }

                Intent intent = new Intent(fromActivity, toClass);
                intent.putExtra("clubData", kat_Data.clubData);
                intent.putExtra("clubLogData", kat_Data.clubLogData);

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
}
