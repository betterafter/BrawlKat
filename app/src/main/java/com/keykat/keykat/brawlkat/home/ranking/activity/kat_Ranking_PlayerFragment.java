package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.app.Activity;
import android.content.Context;
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
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.splash.activity.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.search.result.player.activity.kat_Player_PlayerDetailActivity;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;
import com.keykat.keykat.brawlkat.util.parser.kat_official_PlayerRankingParser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_PlayerFragment extends Fragment {

    private                         kat_LoadingDialog                           dialog;
    public                          boolean                                     set = false;
    private                         LinearLayout                                player_ranking_player_layout;

    private                         Context                                     mContext;
    private                         Activity                                    activity;

    public kat_Ranking_PlayerFragment(){}

    public kat_Ranking_PlayerFragment(kat_LoadingDialog dialog, Context mContext){
        this.dialog = dialog;
        this.mContext = mContext;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        activity = getActivity();
        dialog = new kat_LoadingDialog(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking_player, container, false);
        player_ranking_player_layout = view.findViewById(R.id.player_ranking_player_layout);

        final Button globalButton = view.findViewById(R.id.player_ranking_player_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_player_mycountry);

        globalButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                globalClick(player_ranking_player_layout, dialog);
            }
        });

        MyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                myCountryClick(player_ranking_player_layout, dialog);
            }
        });

        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        dialog.show();
//        globalClick(player_ranking_player_layout, dialog);
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        dialog.show();
        globalClick(player_ranking_player_layout, dialog);

    }

    public void globalClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread();
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout,
                dialog, databaseChangeThread, "global");
        setUiOnMainView.setPriority(10);
        setUiOnMainView.start();
    }

    public void myCountryClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread();
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout,
                dialog, databaseChangeThread, "else");
        setUiOnMainView.setPriority(9);
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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(databaseChangeThread.isAlive()) {
                        try {
                            databaseChangeThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList;
                    if(type.equals("global")){
                        PlayerRankingArrayList = kat_LoadBeforeMainActivity.PlayerRankingArrayList;
                    }
                    else{
                        PlayerRankingArrayList = kat_LoadBeforeMainActivity.MyPlayerRankingArrayList;
                    }
                    setView(player_ranking_player_layout, PlayerRankingArrayList, dialog);
                }
            });
        }
    }

    private class DatabaseChangeThread extends Thread{
        @Override
        public void run(){
            while(true){
                if(kat_LoadBeforeMainActivity.MyPlayerRankingArrayList != null &&
                kat_LoadBeforeMainActivity.PlayerRankingArrayList != null) {
                    if (kat_LoadBeforeMainActivity.MyPlayerRankingArrayList.size() > 0 &&
                            kat_LoadBeforeMainActivity.PlayerRankingArrayList.size() > 0) {
                        break;
                    }
                }
            }
        }
    }


    public void setView(LinearLayout player_ranking_player_layout,
                        ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList,
                        kat_LoadingDialog dialog){


        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        player_ranking_player_layout.removeAllViews();

        for(int i = 0; i < PlayerRankingArrayList.size(); i++){

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_PlayerRankingParser.playerData playerData = PlayerRankingArrayList.get(i);

            ImageView player_ranking_player_image = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank = itemView.findViewById(R.id.player_Ranking_rank);

            GlideImage(kat_RankingFragment.PlayerImageUrl(playerData.getIconId()),
                    kat_RankingFragment.height / 15,
                    kat_RankingFragment.height / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(playerData.getName());
            player_ranking_player_tag.setText(playerData.getTag());
            player_ranking_player_club.setText(playerData.getClubName());
            player_ranking_player_trophies.setText(playerData.getTrophies());
            player_ranking_player_rank.setText(playerData.getRank());

            String nameColor = "#ffffff";
            if(playerData.getNameColor() != null)
                nameColor = "#" + playerData.getNameColor().substring(2);
            player_ranking_player_name.setTextColor(Color.parseColor(nameColor));

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    kat_LoadingDialog dialog = new kat_LoadingDialog(getActivity());
                    dialog.show();

                    String realTag = playerData.getTag().substring(1);

                    kat_SearchThread kset = new kat_SearchThread(getActivity(), kat_Player_PlayerDetailActivity.class, dialog);
                    kset.SearchStart(realTag, "players", getActivity().getApplicationContext());
                }
            });

            player_ranking_player_layout.addView(itemView);
        }
        if(dialog != null) dialog.dismiss();
    }


    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(mContext)
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
