package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.AsyncCoroutine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_PlayerFragment extends Fragment {

    private                         LinearLayout                                player_ranking_player_layout;

    private                         Context                                     mContext;
    private                         Activity                                    activity;

    public kat_Ranking_PlayerFragment(){}


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        activity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_ranking_player, container, false);
        player_ranking_player_layout = view.findViewById(R.id.player_ranking_player_layout);

        final Button globalButton = view.findViewById(R.id.player_ranking_player_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_player_mycountry);

        globalButton.setOnClickListener(v -> globalClick(player_ranking_player_layout));
        MyButton.setOnClickListener(v -> myCountryClick(player_ranking_player_layout));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        globalClick(player_ranking_player_layout);

    }

    public void globalClick(LinearLayout player_ranking_player_layout){
        KatData.dialog.show();
        AsyncCoroutine.Companion.playerDatabaseChanged(
                requireActivity(),
                player_ranking_player_layout,
                KatData.PlayerRankingArrayList
        );
    }

    public void myCountryClick(LinearLayout player_ranking_player_layout){
        KatData.dialog.show();
        AsyncCoroutine.Companion.playerDatabaseChanged(
                requireActivity(),
                player_ranking_player_layout,
                KatData.MyPlayerRankingArrayList
        );
    }
}
