package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;

import androidx.annotation.Nullable;

public class kat_Player_DetailActivity extends kat_Player_RecentSearchActivity {

    private                             ImageView                               playerIcon;
    private                             ImageView                               player_detail_trophies_icon;



    private                             TextView                                player_detail_name;
    private                             TextView                                player_detail_tag;
    private                             TextView                                player_detail_trophies;
    private                             TextView                                player_detail_level;


    private                             RequestOptions                          options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_detail);

        Intent intent = getIntent();
        playerData = (kat_official_playerInfoParser.playerData) intent.getSerializableExtra("playerData");

        playerIcon = findViewById(R.id.player_detail_image);
        player_detail_name = findViewById(R.id.player_detail_name);
        player_detail_tag = findViewById(R.id.player_detail_tag);
        player_detail_trophies_icon = findViewById(R.id.player_detail_trophies_image);
        player_detail_trophies = findViewById(R.id.player_detail_trophies);
        player_detail_level = findViewById(R.id.player_detail_level_icon_text);


        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);


        System.out.println(playerData.getName());
    }

    protected void onStart(){
        super.onStart();

        setData();
    }


    private void setData(){
        String url_profile = "https://www.starlist.pro/assets/profile/" + playerData.getIconId() + ".png?v=1";
        String url_icon_trophies = "https://www.starlist.pro/assets/icon/trophy.png";
        String url_icon_powerPlay = "https://www.starlist.pro/assets/icon/Power-Play.png";
        String url_icon_3vs3 = "https://www.starlist.pro/assets/icon/3v3.png";
        String url_icon_solo = "https://www.starlist.pro/assets/gamemode/Showdown.png?v=2";
        String url_icon_duo = "https://www.starlist.pro/assets/gamemode/Duo-Showdown.png?v=2";
        String url_icon_robo = "https://www.starlist.pro/assets/gamemode/Robo-Rumble.png?v=2";
        String url_icon_bigGame = "https://www.starlist.pro/assets/gamemode/Big-Game.png?v=2";



        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        GildeImage(url_profile, width / 5, width / 5, playerIcon);

        player_detail_tag.setText(playerData.getTag());
        player_detail_name.setText(playerData.getName());
        player_detail_trophies.setText(playerData.getTrophies() + " / " + playerData.getHighestTrophies());
        player_detail_level.setText(Integer.toString(playerData.getExpLevel()));


        GildeImage(url_icon_trophies, width / 20, width / 20, player_detail_trophies_icon);



    }


    private void GildeImage(String url, int width, int height, ImageView view){

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }





}
