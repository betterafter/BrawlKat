package com.example.brawlkat;

import com.example.brawlkat.kat_dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;
import com.example.brawlkat.kat_Fragment.kat_Player_PlayerBattleLogDetailMapFragment;
import com.example.brawlkat.kat_Fragment.kat_Player_PlayerBattleLogDetailPlayerInfoFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class kat_Player_PlayerBattleLogDetailAdapter extends FragmentStateAdapter {

    private                 kat_official_playerInfoParser.playerData                            playerData;
    private                 kat_official_playerBattleLogParser.playerBattleData                 battleData;

    public kat_Player_PlayerBattleLogDetailAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public kat_Player_PlayerBattleLogDetailAdapter(@NonNull FragmentActivity fragmentActivity,
                                                   kat_official_playerInfoParser.playerData playerData,
                                                   kat_official_playerBattleLogParser.playerBattleData battleData){
        super(fragmentActivity);
        this.playerData = playerData;
        this.battleData = battleData;
    }

    public kat_Player_PlayerBattleLogDetailAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public kat_Player_PlayerBattleLogDetailAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position == 0) return kat_Player_PlayerBattleLogDetailPlayerInfoFragment.newInstance(playerData, battleData);
        else if(position == 1) return kat_Player_PlayerBattleLogDetailMapFragment.newInstance(playerData, battleData);
        else return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
