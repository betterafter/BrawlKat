package com.example.brawlkat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.brawlkat.dataparser.kat_clubLogParser;
import com.example.brawlkat.dataparser.kat_official_clubInfoParser;
import com.example.brawlkat.dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_SearchThread extends kat_Player_MainActivity {

    Activity fromActivity;
    Activity toActivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public kat_SearchThread(Activity fromActivity){
        this.fromActivity = fromActivity;
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

                while(!client.workDone){
                    System.out.println("client wait");
                    if(client.workDone){
                        client.workDone = false;
                        sendData.add(client.getAllTypeData().get(0));
                        sendData.add(client.getAllTypeData().get(1));
                        playerSearch(sendData);

                        break;
                    }
                }
            }

            else if(type.equals("clubs")){

                client.AllTypeInit(tag, type, kat_Player_MainActivity.official);
                while(!client.workDone){
                    System.out.println("client wait");

                    if(client.workDone){
                        client.workDone = false;
                        sendData.add(client.getAllTypeData().get(0));
                        sendData.add(client.getAllTypeData().get(1));
                        clubSearch(sendData);

                        break;
                    }
                }
            }
        }
    }


    public void playerSearch(ArrayList<String> sendData){

        // 제대로 가져오지 못했을 경우 알림
        if(sendData.get(0).equals("{none}")){
            Toast toast = Toast.makeText(getApplicationContext(), "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
            toast.show();

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

                // 2020.10.26
                // activity.startActivity(intent) 와
                // startActivity(intent) 의 차이점은 뭘까?
                Intent intent = new Intent(fromActivity, kat_Player_PlayerDetailActivity.class);
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
        SearchThread searchThread = new SearchThread(tag, type);
        searchThread.start();
    }

}
