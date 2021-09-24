package com.keykat.keykat.brawlkat.home.ranking.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_RankingFragment;
import com.keykat.keykat.brawlkat.search.result.club.activity.kat_Player_ClubDetailActivity;
import com.keykat.keykat.brawlkat.search.result.player.activity.kat_Player_PlayerDetailActivity;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;
import com.keykat.keykat.brawlkat.util.parser.kat_official_BrawlerRankingParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_ClubRankingParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_PlayerRankingParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_PowerPlaySeasonRankingParser;

import java.util.ArrayList;

public class setRankingView {

    private LayoutInflater layoutInflater;

    public setRankingView(Activity activity) {
        layoutInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
    }

    // 플레이어 랭킹 뷰
    public void playerSetView(
            Activity activity,
            LinearLayout player_ranking_player_layout,
            ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList) {

        player_ranking_player_layout.removeAllViews();

        for (int i = 0; i < PlayerRankingArrayList.size(); i++) {

            @SuppressLint("InflateParams")
            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_PlayerRankingParser.playerData playerData
                    = PlayerRankingArrayList.get(i);

            ImageView player_ranking_player_image
                    = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name
                    = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag
                    = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club
                    = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies
                    = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank
                    = itemView.findViewById(R.id.player_Ranking_rank);

            KatData.glideImage(
                    activity.getApplicationContext(),
                    kat_RankingFragment.PlayerImageUrl(playerData.getIconId()),
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(playerData.getName());
            player_ranking_player_tag.setText(playerData.getTag());
            player_ranking_player_club.setText(playerData.getClubName());
            player_ranking_player_trophies.setText(playerData.getTrophies());
            player_ranking_player_rank.setText(playerData.getRank());

            String nameColor = "#ffffff";
            if (playerData.getNameColor() != null)
                nameColor = "#" + playerData.getNameColor().substring(2);
            player_ranking_player_name.setTextColor(Color.parseColor(nameColor));

            itemView.setOnClickListener(v -> {

                KatData.dialog.show();
                String realTag = playerData.getTag().substring(1);

                kat_SearchThread kat_searchThread
                        = new kat_SearchThread(activity, kat_Player_PlayerDetailActivity.class);
                kat_searchThread.SearchStart(
                        realTag,
                        "players",
                        activity.getApplicationContext()
                );
            });

            player_ranking_player_layout.addView(itemView);
        }
    }


    // 클럽 랭킹 뷰
    public void clubSetView(
            Activity activity,
            LinearLayout player_ranking_player_layout,
            ArrayList<kat_official_ClubRankingParser.clubData> ClubRankingArrayList) {

        player_ranking_player_layout.removeAllViews();

        for (int i = 0; i < ClubRankingArrayList.size(); i++) {

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_ClubRankingParser.clubData clubData = ClubRankingArrayList.get(i);

            ImageView player_ranking_player_image
                    = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name
                    = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag
                    = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_memberCount
                    = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies
                    = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank
                    = itemView.findViewById(R.id.player_Ranking_rank);

            LinearLayout.LayoutParams params
                    = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.weight = 1f;
            params.gravity = Gravity.CENTER;
            player_ranking_player_memberCount.setLayoutParams(params);
            player_ranking_player_memberCount.setTextSize(10);

            LinearLayout.LayoutParams params2
                    = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params2.weight = 2f;
            params2.gravity = Gravity.CENTER;
            player_ranking_player_trophies.setLayoutParams(params2);


            KatData.glideImage(
                    activity.getApplicationContext(),
                    kat_RankingFragment.ClubImageUrl(clubData.getBadgeId()),
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(clubData.getName());
            player_ranking_player_tag.setText(clubData.getTag());
            player_ranking_player_memberCount.setText(clubData.getMemberCount() + " / 100");
            player_ranking_player_trophies.setText(clubData.getTrophies());
            player_ranking_player_rank.setText(clubData.getRank());

            itemView.setOnClickListener(v -> {

                KatData.dialog.show();
                String realTag = clubData.getTag().substring(1);

                kat_SearchThread kset = new kat_SearchThread(
                        activity,
                        kat_Player_ClubDetailActivity.class
                );
                kset.SearchStart(realTag, "clubs", activity.getApplicationContext());
            });

            player_ranking_player_layout.addView(itemView);
        }
    }

    // 브롤러 랭킹 뷰
    public void brawlerSetView(
            Activity activity,
            LinearLayout player_ranking_player_layout,
            ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData> BrawlerRankingArrayList
    ) {

        player_ranking_player_layout.removeAllViews();

        for (int i = 0; i < BrawlerRankingArrayList.size(); i++) {

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_BrawlerRankingParser.brawlerRankingData brawlerRankingData
                    = BrawlerRankingArrayList.get(i);

            ImageView player_ranking_player_image
                    = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name
                    = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag
                    = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club
                    = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies
                    = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank
                    = itemView.findViewById(R.id.player_Ranking_rank);

            KatData.glideImage(
                    activity.getApplicationContext(),
                    kat_RankingFragment.PlayerImageUrl(brawlerRankingData.getIconId()),
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(brawlerRankingData.getName());
            player_ranking_player_tag.setText(brawlerRankingData.getTag());
            player_ranking_player_club.setText(brawlerRankingData.getClubName());
            player_ranking_player_trophies.setText(brawlerRankingData.getTrophies());
            player_ranking_player_rank.setText(brawlerRankingData.getRank());

            String nameColor = "#ffffff";
            if (brawlerRankingData.getNameColor() != null)
                nameColor = "#" + brawlerRankingData.getNameColor().substring(2);
            player_ranking_player_name.setTextColor(Color.parseColor(nameColor));

            itemView.setOnClickListener(v -> {

                KatData.dialog.show();
                String realTag = brawlerRankingData.getTag().substring(1);

                kat_SearchThread kat_searchThread
                        = new kat_SearchThread(activity, kat_Player_PlayerDetailActivity.class);
                kat_searchThread.SearchStart(
                        realTag,
                        "players",
                        activity.getApplicationContext()
                );
            });

            player_ranking_player_layout.addView(itemView);
        }
    }


    // 파워플레이 뷰
    public void powerPlaySetView(
            Activity activity,
            LinearLayout player_ranking_player_layout,
            ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData>
                    PowerPlayRankingArrayList
    ) {

        player_ranking_player_layout.removeAllViews();

        for (int i = 0; i < PowerPlayRankingArrayList.size(); i++) {

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData
                    powerPlaySeasonRankingData = PowerPlayRankingArrayList.get(i);

            ImageView player_ranking_player_image
                    = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name
                    = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag
                    = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club
                    = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies
                    = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank
                    = itemView.findViewById(R.id.player_Ranking_rank);

            KatData.glideImage(
                    activity.getApplicationContext(),
                    kat_RankingFragment.PlayerImageUrl(powerPlaySeasonRankingData.getIconId()),
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    KatData.SCREEN_HEIGHT.intValue() / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(powerPlaySeasonRankingData.getName());
            player_ranking_player_tag.setText(powerPlaySeasonRankingData.getTag());
            player_ranking_player_club.setText(powerPlaySeasonRankingData.getClubName());
            player_ranking_player_trophies.setText(powerPlaySeasonRankingData.getTrophies());
            player_ranking_player_rank.setText(powerPlaySeasonRankingData.getRank());

            String nameColor = "#ffffff";
            if (powerPlaySeasonRankingData.getNameColor() != null)
                nameColor = "#" + powerPlaySeasonRankingData.getNameColor().substring(2);
            player_ranking_player_name.setTextColor(Color.parseColor(nameColor));

            itemView.setOnClickListener(v -> {

                KatData.dialog.show();
                String realTag = powerPlaySeasonRankingData.getTag().substring(1);

                kat_SearchThread kset = new kat_SearchThread(
                        activity,
                        kat_Player_PlayerDetailActivity.class
                );
                kset.SearchStart(realTag, "players", activity.getApplicationContext());
            });

            player_ranking_player_layout.addView(itemView);
        }
    }
}
