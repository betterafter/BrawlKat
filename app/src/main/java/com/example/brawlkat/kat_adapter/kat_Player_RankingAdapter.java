package com.example.brawlkat.kat_adapter;

import android.content.Context;

import com.example.brawlkat.kat_Fragment.kat_Ranking_BrawlerFragment;
import com.example.brawlkat.kat_Fragment.kat_Ranking_ClubFragment;
import com.example.brawlkat.kat_Fragment.kat_Ranking_PlayerFragment;
import com.example.brawlkat.kat_Fragment.kat_Ranking_PowerPlayFragment;
import com.example.brawlkat.kat_LoadingDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class kat_Player_RankingAdapter extends FragmentStateAdapter {

    kat_LoadingDialog dialog;
    private Context mContext;

    public kat_Player_RankingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public kat_Player_RankingAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public kat_Player_RankingAdapter(@NonNull Fragment fragment, kat_LoadingDialog dialog, Context mContext) {
        super(fragment);
        this.dialog = dialog;
        this.mContext = mContext;
    }

    public kat_Player_RankingAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) return new kat_Ranking_PlayerFragment(dialog, mContext);
        else if(position == 1) return new kat_Ranking_ClubFragment(dialog, mContext);
        else if(position == 2) return new kat_Ranking_BrawlerFragment(dialog, mContext);
        else if(position == 3) return new kat_Ranking_PowerPlayFragment(dialog, mContext);
        else return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
