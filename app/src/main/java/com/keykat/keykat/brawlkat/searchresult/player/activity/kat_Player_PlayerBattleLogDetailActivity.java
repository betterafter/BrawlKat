package com.keykat.keykat.brawlkat.searchresult.player.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.searchresult.player.util.kat_Player_PlayerBattleLogDetailAdapter;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerBattleLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class kat_Player_PlayerBattleLogDetailActivity extends AppCompatActivity {


    public kat_official_playerInfoParser.playerData playerData;
    public kat_official_playerBattleLogParser.playerBattleData battleData;


    private ViewPager2 viewPager2;
    private FragmentStateAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_player_detail_battle_log_detail);

        viewPager2 = findViewById(R.id.player_player_detail_battle_log_detail_viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        playerData
                = (kat_official_playerInfoParser.playerData) intent.getSerializableExtra(
                "playerData"
        );
        battleData
                = (kat_official_playerBattleLogParser.playerBattleData) intent.getSerializableExtra(
                "battleData"
        );

        adapter
                = new kat_Player_PlayerBattleLogDetailAdapter(
                this,
                playerData,
                battleData
        );

        viewPager2.setAdapter(adapter);

        tabLayout = findViewById(R.id.player_player_detail_battle_log_detail_tabLayout);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    tab.setText("Tab " + (position + 1));
                    if (position == 0) tab.setText("플레이어");
                    else if (position == 1) tab.setText("맵 정보");
                }).attach();

    }

    @Override
    protected void onResume() {
        super.onResume();
        KatData.currentActivity = this;
    }
}
