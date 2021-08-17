package com.keykat.keykat.brawlkat.search.result.player.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerBattleLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;
import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;

import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Player_PlayerBattleLogDetailMapFragment extends Fragment {

    private                 RequestOptions                                                          options;
    private                 kat_official_playerInfoParser.playerData                                playerData;
    private                 kat_official_playerBattleLogParser.playerBattleData                     battleData;

    private                 int                                                                     width;
    private                 int                                                                     height;

    public kat_Player_PlayerBattleLogDetailMapFragment(kat_official_playerInfoParser.playerData playerData,
                                                       kat_official_playerBattleLogParser.playerBattleData battleData){
        this.playerData = playerData;
        this.battleData = battleData;
    }

    public static kat_Player_PlayerBattleLogDetailMapFragment newInstance(kat_official_playerInfoParser.playerData playerData,
                                                                          kat_official_playerBattleLogParser.playerBattleData battleData){

        kat_Player_PlayerBattleLogDetailMapFragment fragment = new kat_Player_PlayerBattleLogDetailMapFragment(playerData, battleData);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        width = kat_Player_PlayerDetailActivity.width;
        height = kat_Player_PlayerDetailActivity.height;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_player_detail_battle_log_detail_map, container, false);
        ImageView mapImage = view.findViewById(R.id.player_player_detail_battle_log_detail_map_image);

        HashMap<String, kat_mapsParser.mapData> mapData = kat_Player_MainActivity.mapData;

        Iterator<String> iterator = mapData.keySet().iterator();
        while(iterator.hasNext()){

            if(iterator.next().equals(battleData.getEventId())){

                kat_mapsParser.mapData data = mapData.get(battleData.getEventId());

                GlideImage(data.getMapImageUrl(), width, height, mapImage);

                break;
            }
        }


        return view;
    }

    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(this)
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }
}
