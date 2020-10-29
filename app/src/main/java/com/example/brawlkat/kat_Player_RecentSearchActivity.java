package com.example.brawlkat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
                    kat_SearchThread kset = new kat_SearchThread(kat_Player_RecentSearchActivity.this);
                    kset.SearchStart(player_detail_user_club_search.getText().toString(), type);
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

        kat_SearchThread kset = new kat_SearchThread(kat_Player_RecentSearchActivity.this);
        kset.SearchStart(player_detail_user_club_search.getText().toString(), type);
    }

    // 리스트를 터치했을 때 발생 함수
    private void listClick(View view){
        LinearLayout linearLayout = (LinearLayout) view.getParent();
        String RawTag = ((TextView) linearLayout.getChildAt(1)).getText().toString();
        String newTag = RawTag.substring(1);

        kat_SearchThread kset = new kat_SearchThread(kat_Player_RecentSearchActivity.this);
        kset.SearchStart(newTag, type);
    }

}
