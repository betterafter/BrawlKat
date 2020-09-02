package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;

import com.example.brawlkat.dataparser.kat_official_clubInfoParser;

import androidx.annotation.Nullable;

public class kat_Player_ClubDetailActivity extends kat_Player_RecentSearchActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_club_detail);

        Intent intent = getIntent();
        clubData = (kat_official_clubInfoParser.clubData) intent.getSerializableExtra("clubData");
        System.out.println(clubData.getName());
    }
}
