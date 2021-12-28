package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.common.util.KatData;
import com.keykat.keykat.brawlkat.common.util.network.AsyncCoroutine;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_BrawlerFragment extends Fragment {

    private                         String                                      initId;
    private                         String                                      initName;
    private                         LinearLayout                                player_ranking_brawler_layout;
    private                         Button                                      button;

    private                         Context                                     mContext;

    public kat_Ranking_BrawlerFragment(){};

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initId = Objects.requireNonNull(KatData.BrawlersArrayList.get(0).get("id")).toString();
        initName = Objects.requireNonNull(KatData.BrawlersArrayList.get(0).get("name")).toString();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_ranking_brawler, container, false);
        player_ranking_brawler_layout = view.findViewById(R.id.player_ranking_brawler_layout);

        globalClick(player_ranking_brawler_layout);

        button = view.findViewById(R.id.player_ranking_brawler_select);
        button.setText(initName);
        button.setOnClickListener(view1 -> {

            Intent intent = new Intent(getActivity().getApplicationContext(), kat_BrawlerSelectionPopUpActivity.class);
            startActivityForResult(intent, 1011);
        });

        final Button globalButton = view.findViewById(R.id.player_ranking_brawler_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_brawler_mycountry);

        globalButton.setOnClickListener(v -> globalClick(player_ranking_brawler_layout));
        MyButton.setOnClickListener(v -> myCountryClick(player_ranking_brawler_layout));

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1011){
            if(resultCode == 1113){
                //데이터 받기
                initId = data.getStringExtra("BrawlerId");
                initName = data.getStringExtra("BrawlerName");
                button.setText(initName);

                globalClick(player_ranking_brawler_layout);
            }
        }
    }


    public void globalClick(LinearLayout player_ranking_brawler_layout){

        KatData.dialog.show();
        if(!KatData.BrawlerRankingArrayList.containsKey(initId)){
            KatData.client.RankingInit("global", initId, "Brawler");
        }

        AsyncCoroutine.Companion.brawlerGlobalDatabaseChanged(
                requireActivity(),
                player_ranking_brawler_layout,
                initId
        );
    }

    public void myCountryClick(LinearLayout player_ranking_brawler_layout){

        String countryCode = KatData.kataCountryBase.getCountryCode();
        if(!KatData.MyBrawlerRankingArrayList.containsKey(countryCode) ||
                (KatData.MyBrawlerRankingArrayList.containsKey(countryCode) &&
                        !KatData.MyBrawlerRankingArrayList.get(countryCode).containsKey(initId))){

            KatData.client.RankingInit(countryCode, initId, "Brawler");
        }

        KatData.dialog.show();
        AsyncCoroutine.Companion.brawlerDatabaseChanged(
                requireActivity(),
                player_ranking_brawler_layout,
                initId
        );
    }
}
