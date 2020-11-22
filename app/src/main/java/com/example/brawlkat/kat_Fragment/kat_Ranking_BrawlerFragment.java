package com.example.brawlkat.kat_Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.Client;
import com.example.brawlkat.R;
import com.example.brawlkat.kat_BrawlerSelectionPopUpActivity;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_LoadingDialog;
import com.example.brawlkat.kat_Player_PlayerDetailActivity;
import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_dataparser.kat_official_BrawlerRankingParser;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_BrawlerFragment extends Fragment {

    private                         kat_LoadingDialog                           dialog;
    private                         String                                      initId;
    private                         LinearLayout                                player_ranking_brawler_layout;

    public kat_Ranking_BrawlerFragment(kat_LoadingDialog dialog){
        this.dialog = dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initId = kat_LoadBeforeMainActivity.BrawlersArrayList.get(0).get("id").toString();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking_brawler, container, false);
        player_ranking_brawler_layout = view.findViewById(R.id.player_ranking_brawler_layout);

        dialog.show();
        globalClick(player_ranking_brawler_layout, dialog);

        Button button = view.findViewById(R.id.player_ranking_brawler_select);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity().getApplicationContext(), kat_BrawlerSelectionPopUpActivity.class);
                startActivityForResult(intent, 1011);
            }
        });

        final Button globalButton = view.findViewById(R.id.player_ranking_brawler_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_brawler_mycountry);

        globalButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                globalClick(player_ranking_brawler_layout, dialog);
            }
        });

        MyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                myCountryClick(player_ranking_brawler_layout, dialog);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1011){
            if(resultCode == 1113){
                //데이터 받기
                initId = data.getStringExtra("BrawlerId");
                dialog.show();
                globalClick(player_ranking_brawler_layout, dialog);
            }
        }
    }


    public void globalClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread();
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView
                = new setUiOnMainView(player_ranking_player_layout, dialog, databaseChangeThread, "global");
        setUiOnMainView.start();
    }

    public void myCountryClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread();
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout, dialog, databaseChangeThread,
                kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode());
        setUiOnMainView.start();

    }


    private class setUiOnMainView extends Thread{

        LinearLayout player_ranking_player_layout;
        kat_LoadingDialog dialog;
        DatabaseChangeThread databaseChangeThread;
        String type;

        public setUiOnMainView(LinearLayout player_ranking_player_layout,
                               kat_LoadingDialog dialog,
                               DatabaseChangeThread databaseChangeThread,
                               String type){
            this.player_ranking_player_layout = player_ranking_player_layout;
            this.dialog = dialog;
            this.databaseChangeThread = databaseChangeThread;
            this.type = type;
        }

        public void run(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(databaseChangeThread.isAlive()) {
                        try {
                            databaseChangeThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(type.equals("global")) {
                        setView(player_ranking_player_layout, kat_LoadBeforeMainActivity.BrawlerRankingArrayList.get(initId), dialog);
                    }

                    else {
                        ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData> MyBrawlerRankingArrayList
                                = kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList
                                .get(kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode())
                                .get(initId);

                        setView(player_ranking_player_layout, MyBrawlerRankingArrayList, dialog);
                    }
                }
            });
        }
    }

    private class DatabaseChangeThread extends Thread{

        Client.getRankingApiThread rankingApiThread[] = new Client.getRankingApiThread[2];

        @Override
        public void run(){

            if(!kat_LoadBeforeMainActivity.BrawlerRankingArrayList.containsKey(initId) ||

                    !kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList
                            .containsKey(kat_LoadBeforeMainActivity
                                    .kataCountryBase
                                    .getCountryCode())

                    || (kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList
                    .containsKey(kat_LoadBeforeMainActivity
                            .kataCountryBase
                            .getCountryCode())
                    &&
                            !kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList
                            .get(kat_LoadBeforeMainActivity
                            .kataCountryBase
                            .getCountryCode())
                            .containsKey(initId))
            ){

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rankingApiThread[0]
                                = kat_LoadBeforeMainActivity.client.RankingResearch(
                                "global", initId, "Brawler"
                        );
                        rankingApiThread[1]
                                = kat_LoadBeforeMainActivity.client.RankingResearch(
                                kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode(),
                                initId, "Brawler"
                        );

                        while (true) {
                            try {
                                rankingApiThread[0].start();
                                rankingApiThread[1].start();

                                rankingApiThread[0].join();
                                rankingApiThread[1].join();

                                break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }


    public void setView(LinearLayout player_ranking_player_layout,
                        ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData> BrawlerRankingArrayList,
                        kat_LoadingDialog dialog){

        LayoutInflater layoutInflater =
                (LayoutInflater) Objects.requireNonNull(getActivity()).getApplicationContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        player_ranking_player_layout.removeAllViews();

        for(int i = 0; i < BrawlerRankingArrayList.size(); i++){

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_BrawlerRankingParser.brawlerRankingData brawlerRankingData = BrawlerRankingArrayList.get(i);

            ImageView player_ranking_player_image = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank = itemView.findViewById(R.id.player_Ranking_rank);

            GlideImage(kat_RankingFragment.PlayerImageUrl(brawlerRankingData.getIconId()),
                    kat_RankingFragment.height / 15,
                    kat_RankingFragment.height / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(brawlerRankingData.getName());
            player_ranking_player_tag.setText(brawlerRankingData.getTag());
            player_ranking_player_club.setText(brawlerRankingData.getClubName());
            player_ranking_player_trophies.setText(brawlerRankingData.getTrophies());
            player_ranking_player_rank.setText(brawlerRankingData.getRank());

            String nameColor = "#ffffff";
            if(brawlerRankingData.getNameColor() != null)
                nameColor = "#" + brawlerRankingData.getNameColor().substring(2);
            player_ranking_player_name.setTextColor(Color.parseColor(nameColor));

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    kat_LoadingDialog dialog = new kat_LoadingDialog(getActivity());
                    dialog.show();

                    String realTag = brawlerRankingData.getTag().substring(1);

                    kat_SearchThread kset = new kat_SearchThread(getActivity(), kat_Player_PlayerDetailActivity.class, dialog);
                    kset.SearchStart(realTag, "players");
                }
            });

            player_ranking_player_layout.addView(itemView);
        }
        if(dialog != null) dialog.dismiss();
    }


    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getActivity().getApplicationContext())
                .applyDefaultRequestOptions(kat_RankingFragment.options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    public void GlideImageWithRoundCorner(String url, int width, int height, ImageView view){
        Glide.with(getActivity().getApplicationContext())
                .applyDefaultRequestOptions(kat_RankingFragment.options)
                .load(url)
                .apply(new RequestOptions().circleCrop().circleCrop())
                .override(width, height)
                .into(view);


    }

}
