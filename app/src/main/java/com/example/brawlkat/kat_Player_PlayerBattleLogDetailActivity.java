package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.brawlkat.dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class kat_Player_PlayerBattleLogDetailActivity extends kat_Player_PlayerDetailActivity {


    public                  kat_official_playerInfoParser.playerData                    playerData;
    public                  kat_official_playerBattleLogParser.playerBattleData         battleData;

    public                  TextView                                                    battle_log_detail_result;
    public                  ImageView                                                   battle_log_detail_map_image;
    public                  LinearLayout                                                battle_log_detail_players_or_teams;

    private ViewPager2 viewPager2;
    private FragmentStateAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_player_detail_battle_log_detail);

//        battle_log_detail_result = findViewById(R.id.player_player_detail_battle_log_detail_result);
//        battle_log_detail_map_image = findViewById(R.id.player_player_detail_battle_log_detail_map_image);
//        battle_log_detail_players_or_teams = findViewById(R.id.player_player_detail_battle_log_detail_players_or_teams);

        viewPager2 = findViewById(R.id.player_player_detail_battle_log_detail_viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        playerData = (kat_official_playerInfoParser.playerData) intent.getSerializableExtra("playerData");
        battleData = (kat_official_playerBattleLogParser.playerBattleData) intent.getSerializableExtra("battleData");

        adapter = new kat_Player_PlayerBattleLogDetailAdapter(this, playerData, battleData);
        viewPager2.setAdapter(adapter);

        tabLayout = findViewById(R.id.player_player_detail_battle_log_detail_tabLayout);
        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText("Tab " + (position + 1));
                        if(position == 0) tab.setText("플레이어");
                        else if(position == 1) tab.setText("맵 정보");
                    }
                }).attach();

    }



}
