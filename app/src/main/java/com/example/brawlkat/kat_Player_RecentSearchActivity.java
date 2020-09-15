package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brawlkat.dataparser.kat_clubLogParser;
import com.example.brawlkat.dataparser.kat_official_clubInfoParser;
import com.example.brawlkat.dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_Player_RecentSearchActivity extends kat_Player_MainActivity {

    private                                     TextInputEditText                               player_detail_user_club_search;
    private                                     String                                          type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_player_club_search);

        type = getIntent().getStringExtra("type");

        player_detail_user_club_search = findViewById(R.id.player_detail_user_club_searchInput);
        player_detail_user_club_search.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    SearchThread st = new SearchThread(player_detail_user_club_search.getText().toString(), type);
                    st.start();
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(this.getClass().getName().equals("com.example.brawlkat.kat_Player_RecentSearchActivity")) recentSearchUpdate();
    }


    private class SearchThread extends Thread{

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
            System.out.println("type : " + type);


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
            Toast toast = Toast.makeText(this.getApplicationContext(), "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
            toast.show();

        }
        // 제대로 가져왔을 경우
        else{
            official_playerInfoParser = new kat_official_playerInfoParser(sendData.get(0));
            official_playerBattleLogParser = new kat_official_playerBattleLogParser(sendData.get(1));

            try {
                playerData = official_playerInfoParser.DataParser();
                if(!client.getAllTypeData().get(1).equals("{none}"))
                    playerBattleDataList = official_playerBattleLogParser.DataParser();

                String type = "players";
                String Tag = playerData.getTag();
                String name = playerData.getName();
                String isAccount = "no";

                katabase.delete(type);
                katabase.insert(type, Tag, name, isAccount);

                Intent intent = new Intent(kat_Player_RecentSearchActivity.this, kat_Player_PlayerDetailActivity.class);
                intent.putExtra("playerData", playerData);

                startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void clubSearch(ArrayList<String> sendData){

        // 제대로 가져오지 못했을 경우 알림
        if(sendData.get(0).equals("{none}")){
            Toast toast = Toast.makeText(this.getApplicationContext(), "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
            toast.show();

        }
        // 제대로 가져왔을 경우
        else{
            official_clubInfoParser = new kat_official_clubInfoParser(sendData.get(0));
            clubLogParser = new kat_clubLogParser(sendData.get(1));
            System.out.println("club log data : " + sendData.get(1));

            try {
                clubData = official_clubInfoParser.DataParser();
                clubLogData = clubLogParser.DataParser();

                String type = "clubs";
                String tag = clubData.getTag();
                String name = clubData.getName();
                String isAccount = "no";

                katabase.delete(type);
                katabase.insert(type, tag, name, isAccount);

                Intent intent = new Intent(kat_Player_RecentSearchActivity.this, kat_Player_ClubDetailActivity.class);
                intent.putExtra("clubData", clubData);
                intent.putExtra("clubLogData", clubLogData);


                startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // 최근 전적 검색 기록 업데이트
    public void recentSearchUpdate(){

        LinearLayout linearLayout = findViewById(R.id.player_detail_recent_search_layout);
        linearLayout.removeAllViews();

        katabase.print();
        ArrayList<ArrayList<String>> resultList = katabase.get(type);

        for(int i = 0; i < 9; i++){

            if(i >= resultList.size()) break;
            recentSearchResultList(linearLayout, layoutInflater, resultList.get(i));
        }

    }

    // 버튼을 클릭했을 때 최근 전적 기록을 갱신함.
    private void recentSearchResultList(LinearLayout linearLayout, LayoutInflater layoutInflater, ArrayList<String> data){

        View view = layoutInflater.inflate(R.layout.player_search_list_item, null);

        TextView tagText = view.findViewById(R.id.player_search_tag);
        TextView nameText = view.findViewById(R.id.player_search_name);

        tagText.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View view) {
                listClick(view);
            }
        });

        nameText.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View view) {
                listClick(view);
            }
        });


        tagText.setText(data.get(1));
        nameText.setText(data.get(2));

        linearLayout.addView(view);
    }


    // 전적 검색 클릭
    public void onUserClubSearchClick(View view){
        SearchThread st = new SearchThread(player_detail_user_club_search.getText().toString(), type);
        st.start();
    }

    // 리스트를 터치했을 때 발생 함수
    private void listClick(View view){
        LinearLayout linearLayout = (LinearLayout) view.getParent();
        String RawTag = ((TextView) linearLayout.getChildAt(1)).getText().toString();
        String newTag = RawTag.substring(1);

        SearchThread st = new SearchThread(newTag, type);
        st.start();
    }



}
