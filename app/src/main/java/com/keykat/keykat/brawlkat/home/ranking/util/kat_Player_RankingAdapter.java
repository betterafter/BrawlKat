package com.keykat.keykat.brawlkat.home.ranking.util;

import com.keykat.keykat.brawlkat.home.ranking.activity.kat_Ranking_BrawlerFragment;
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_Ranking_ClubFragment;
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_Ranking_PlayerFragment;
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_Ranking_PowerPlayFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class kat_Player_RankingAdapter extends FragmentStateAdapter {


    public kat_Player_RankingAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position == 0) {
            return new kat_Ranking_PlayerFragment();
        }
        else if(position == 1) {
            return new kat_Ranking_ClubFragment();
        }
        else if(position == 2) {
            return new kat_Ranking_BrawlerFragment();
        }
        else if(position == 3) {
            return new kat_Ranking_PowerPlayFragment();
        }
        else return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
