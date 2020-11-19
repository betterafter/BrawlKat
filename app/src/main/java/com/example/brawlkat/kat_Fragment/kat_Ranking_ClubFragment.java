package com.example.brawlkat.kat_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.brawlkat.kat_Player_ClubDetailActivity;
import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_dataparser.kat_official_ClubRankingParser;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_ClubFragment extends Fragment {

    private                         kat_LoadingDialog                           dialog;
    public                          boolean                                     set = false;

    public kat_Ranking_ClubFragment(){}

    public kat_Ranking_ClubFragment(kat_LoadingDialog dialog){
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

        ArrayList<kat_official_ClubRankingParser.clubData> ClubRankingArrayList
                = kat_LoadBeforeMainActivity.ClubRankingArrayList;

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout, ClubRankingArrayList,
                dialog, databaseChangeThread);
        setUiOnMainView.start();
    }

    public void myCountryClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread();
        databaseChangeThread.start();

        ArrayList<kat_official_ClubRankingParser.clubData> ClubRankingArrayList
                = kat_LoadBeforeMainActivity.MyClubRankingArrayList;

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout, ClubRankingArrayList,
                dialog, databaseChangeThread);
        setUiOnMainView.start();

    }


    private class setUiOnMainView extends Thread{

        LinearLayout player_ranking_player_layout;
        ArrayList<kat_official_ClubRankingParser.clubData> ClubRankingArrayList;
        kat_LoadingDialog dialog;
        DatabaseChangeThread databaseChangeThread;

        public setUiOnMainView(LinearLayout player_ranking_player_layout,
                               ArrayList<kat_official_ClubRankingParser.clubData> ClubRankingArrayList,
                               kat_LoadingDialog dialog,
                               DatabaseChangeThread databaseChangeThread){
            this.player_ranking_player_layout = player_ranking_player_layout;
            this.ClubRankingArrayList = ClubRankingArrayList;
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
                    setView(player_ranking_player_layout, ClubRankingArrayList, dialog);
                }
            });
        }
    }

    private class DatabaseChangeThread extends Thread{
        @Override
        public void run(){
            while(true){
                if(kat_LoadBeforeMainActivity.MyClubRankingArrayList != null &&
                        kat_LoadBeforeMainActivity.ClubRankingArrayList != null) {
                    if (kat_LoadBeforeMainActivity.MyClubRankingArrayList.size() > 0 &&
                            kat_LoadBeforeMainActivity.ClubRankingArrayList.size() > 0) {
                        break;
                    }
                }
            }
        }
    }


    public void setView(LinearLayout player_ranking_player_layout,
                        ArrayList<kat_official_ClubRankingParser.clubData> ClubRankingArrayList,
                        kat_LoadingDialog dialog){

        LayoutInflater layoutInflater =
                (LayoutInflater) Objects.requireNonNull(getActivity()).getApplicationContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        player_ranking_player_layout.removeAllViews();

        for(int i = 0; i < ClubRankingArrayList.size(); i++){

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_ClubRankingParser.clubData clubData = ClubRankingArrayList.get(i);

            ImageView player_ranking_player_image = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_memberCount = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank = itemView.findViewById(R.id.player_Ranking_rank);

            LinearLayout.LayoutParams params
                    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1f;
            params.gravity = Gravity.CENTER;
            player_ranking_player_memberCount.setLayoutParams(params);
            player_ranking_player_memberCount.setTextSize(10);

            LinearLayout.LayoutParams params2
                    = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params2.weight = 2f;
            params2.gravity = Gravity.CENTER;
            player_ranking_player_trophies.setLayoutParams(params2);


            GlideImage(kat_RankingFragment.ClubImageUrl(clubData.getBadgeId()),
                    kat_RankingFragment.height / 15,
                    kat_RankingFragment.height / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(clubData.getName());
            player_ranking_player_tag.setText(clubData.getTag());
            player_ranking_player_memberCount.setText(clubData.getMemberCount() + " / 100");
            player_ranking_player_trophies.setText(clubData.getTrophies());
            player_ranking_player_rank.setText(clubData.getRank());

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    kat_LoadingDialog dialog = new kat_LoadingDialog(getActivity());
                    dialog.show();

                    String realTag = clubData.getTag().substring(1);
                    System.out.println("realTag : " + realTag);

                    kat_SearchThread kset = new kat_SearchThread(getActivity(), kat_Player_ClubDetailActivity.class, dialog);
                    kset.SearchStart(realTag, "clubs");
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
