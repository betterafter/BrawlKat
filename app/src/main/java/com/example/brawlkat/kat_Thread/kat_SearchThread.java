package com.example.brawlkat.kat_Thread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.brawlkat.Client;
import com.example.brawlkat.kat_Player_ClubDetailActivity;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_dataparser.kat_clubLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_clubInfoParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_SearchThread extends kat_Player_MainActivity {

    Activity fromActivity;
    Class toClass;
    boolean setData = false;

    SearchThread searchThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public kat_SearchThread(Activity fromActivity, Class toclass){
        this.fromActivity = fromActivity;
        this.toClass = toclass;
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
                if(!client.workDone){
                    if(Client.workThread.isAlive()) {
                        try {
                            Client.workThread.join();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                client.workDone = false;
                sendData.add(client.getAllTypeData().get(0));
                sendData.add(client.getAllTypeData().get(1));
                playerSearch(sendData);
            }

            else if(type.equals("clubs")){

                client.AllTypeInit(tag, type, kat_Player_MainActivity.official);
                if(!client.workDone){
                    if(Client.workThread.isAlive()) {
                        try {
                            Client.workThread.join();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                client.workDone = false;
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
                    if(kat_GetStarlistDataThread.BrawlersArrayList != null)
                        System.out.println(kat_GetStarlistDataThread.BrawlersArrayList.size());

                    if(kat_GetStarlistDataThread.isEmptyListCheckThread.isAlive()){
                        kat_GetStarlistDataThread.isEmptyListCheckThread.join();
                    }

                    if(kat_GetStarlistDataThread.BrawlersArrayList != null)
                        System.out.println(kat_GetStarlistDataThread.BrawlersArrayList.size());
                }

                Intent intent = new Intent(fromActivity, toClass);
                intent.putExtra("playerData", playerData);
                fromActivity.startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void clubSearch(ArrayList<String> sendData){

        // 제대로 가져오지 못했을 경우 알림
        if(sendData.get(0).equals("{none}")){
            Toast toast = Toast.makeText(getApplicationContext(), "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
            toast.show();

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

                Intent intent = new Intent(fromActivity, kat_Player_ClubDetailActivity.class);
                intent.putExtra("clubData", clubData);
                intent.putExtra("clubLogData", clubLogData);

                fromActivity.startActivity(intent);

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void SearchStart(String tag, String type){
        searchThread = new SearchThread(tag, type);
        searchThread.start();

    }

    public void setMyAccount(){
        if(kataMyAccountBase.size() < 1) {
            kataMyAccountBase.insert("player", playerData.getTag(), playerData.getName());
        }
        System.out.println("add my account");
        System.out.println(kataMyAccountBase.size());
        kataMyAccountBase.print();
    }

}
