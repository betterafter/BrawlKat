package com.example.brawlkat.kat_Fragment;

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
import com.example.brawlkat.R;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_LoadingDialog;
import com.example.brawlkat.kat_Player_PlayerDetailActivity;
import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_dataparser.kat_official_PlayerRankingParser;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_PlayerFragment extends Fragment {

    private                         kat_LoadingDialog                           dialog;
    public                          boolean                                     set = false;

    public kat_Ranking_PlayerFragment(){}

    public kat_Ranking_PlayerFragment(kat_LoadingDialog dialog){
        this.dialog = dialog;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking_player, container, false);
        final LinearLayout player_ranking_player_layout = view.findViewById(R.id.player_ranking_player_layout);

        dialog.show();
        globalClick(player_ranking_player_layout, dialog);

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

    public void globalClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread();
        databaseChangeThread.start();

        ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList
                = kat_LoadBeforeMainActivity.PlayerRankingArrayList;

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout, PlayerRankingArrayList,
                dialog, databaseChangeThread);
        setUiOnMainView.start();
    }

    public void myCountryClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread();
        databaseChangeThread.start();

        ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList
                = kat_LoadBeforeMainActivity.MyPlayerRankingArrayList;

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout, PlayerRankingArrayList,
                dialog, databaseChangeThread);
        setUiOnMainView.start();

    }


    private class setUiOnMainView extends Thread{

        LinearLayout player_ranking_player_layout;
        ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList;
        kat_LoadingDialog dialog;
        DatabaseChangeThread databaseChangeThread;

        public setUiOnMainView(LinearLayout player_ranking_player_layout,
                               ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList,
                               kat_LoadingDialog dialog,
                               DatabaseChangeThread databaseChangeThread){
            this.player_ranking_player_layout = player_ranking_player_layout;
            this.PlayerRankingArrayList = PlayerRankingArrayList;
            this.dialog = dialog;
            this.databaseChangeThread = databaseChangeThread;
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
                (LayoutInflater) Objects.requireNonNull(getActivity()).getApplicationContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
