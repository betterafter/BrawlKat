package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brawlkat.dataparser.kat_official_playerInfoParser;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_Player_RecentSearchActivity extends kat_Player_MainActivity {

    private                                     TextInputEditText                               player_detail_user_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_club_search);

        player_detail_user_search = findViewById(R.id.player_detail_user_searchInput);
        player_detail_user_search.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    Search(player_detail_user_search.getText().toString());
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


    public void Search(String tag){

        playerTag = tag;
       // playerTag = player_detail_user_search.getText().toString();

        client.offi_init(playerTag);

        // official api 데이터를 완전히 가져올 때까지 기다린다.
        while(client.getOffidata() == null || client.getOffidata().size() <= 0);

        // 제대로 가져오지 못했을 경우 알림
        if(client.getOffidata().get(0).equals("none")){
            Toast toast = Toast.makeText(this.getApplicationContext(), "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
            toast.show();

            client.offidataRemove();
        }

        // 제대로 가져왔을 경우
        else{
            official_playerInfoParser = new kat_official_playerInfoParser(client.getOffidata().get(0));
            client.offidataRemove();
            try {

                playerData = null;
                playerData = official_playerInfoParser.DataParser();
                while(playerData == null);


                String type = "player";
                String Tag = playerData.getTag();
                String name = playerData.getName();
                String isAccount = "no";

                katabase.delete();
                katabase.insert(type, Tag, name, isAccount);
                recentSearchUpdate();

                Intent intent = new Intent(kat_Player_RecentSearchActivity.this, kat_Player_DetailActivity.class);
                intent.putExtra("playerData", playerData);
                startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void recentSearchUpdate(){

        LinearLayout linearLayout = findViewById(R.id.player_detail_recent_search_layout);
        linearLayout.removeAllViews();

        ArrayList<ArrayList<String>> resultList = katabase.get();

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
    public void onUserSearchClick(View view){
        Search(player_detail_user_search.getText().toString());
    }

    // 리스트를 터치했을 때 발생 함수
    private void listClick(View view){
        LinearLayout linearLayout = (LinearLayout) view.getParent();
        String RawTag = ((TextView) linearLayout.getChildAt(1)).getText().toString();
        String newTag = RawTag.substring(1);

        System.out.println(newTag);
        Search(newTag);
    }



}
