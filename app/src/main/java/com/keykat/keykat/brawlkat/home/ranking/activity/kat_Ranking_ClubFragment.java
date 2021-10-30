package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.common.util.KatData;
import com.keykat.keykat.brawlkat.common.util.network.AsyncCoroutine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_ClubFragment extends Fragment {

    private LinearLayout player_ranking_player_layout;

    public kat_Ranking_ClubFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_player_ranking_club, container, false);
        player_ranking_player_layout = view.findViewById(R.id.player_ranking_club_layout);

        final Button globalButton = view.findViewById(R.id.player_ranking_club_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_club_mycountry);

        globalButton.setOnClickListener(v -> globalClick(player_ranking_player_layout));
        MyButton.setOnClickListener(v -> myCountryClick(player_ranking_player_layout));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        globalClick(player_ranking_player_layout);
    }

    private void globalClick(LinearLayout player_ranking_player_layout) {

        KatData.dialog.show();
        AsyncCoroutine.Companion.clubDatabaseChanged(
                requireActivity(),
                player_ranking_player_layout,
                KatData.ClubRankingArrayList
        );
    }

    private void myCountryClick(LinearLayout player_ranking_player_layout) {

        KatData.dialog.show();
        AsyncCoroutine.Companion.clubDatabaseChanged(
                requireActivity(),
                player_ranking_player_layout,
                KatData.MyClubRankingArrayList
        );
    }
}
