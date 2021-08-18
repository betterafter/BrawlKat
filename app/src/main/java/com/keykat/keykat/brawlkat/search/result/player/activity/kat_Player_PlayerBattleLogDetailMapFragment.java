package com.keykat.keykat.brawlkat.search.result.player.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.util.kat_Data;
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerBattleLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Player_PlayerBattleLogDetailMapFragment extends Fragment {

    private final kat_official_playerBattleLogParser.playerBattleData                     battleData;

    public kat_Player_PlayerBattleLogDetailMapFragment(kat_official_playerBattleLogParser.playerBattleData battleData){
        this.battleData = battleData;
    }

    public static kat_Player_PlayerBattleLogDetailMapFragment newInstance(kat_official_playerInfoParser.playerData playerData,
                                                                          kat_official_playerBattleLogParser.playerBattleData battleData){

        kat_Player_PlayerBattleLogDetailMapFragment fragment
                = new kat_Player_PlayerBattleLogDetailMapFragment(battleData);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_player_detail_battle_log_detail_map, container, false);
        ImageView mapImage = view.findViewById(R.id.player_player_detail_battle_log_detail_map_image);

        HashMap<String, kat_mapsParser.mapData> mapData = kat_Data.mapData;

        Iterator<String> iterator = mapData.keySet().iterator();
        while(iterator.hasNext()){

            if(iterator.next().equals(battleData.getEventId())){

                kat_mapsParser.mapData data = mapData.get(battleData.getEventId());

                kat_Data.GlideImage(
                        Objects.requireNonNull(getActivity()).getApplicationContext(),
                        data.getMapImageUrl(),
                        kat_Data.SCREEN_WIDTH.intValue(),
                        kat_Data.SCREEN_HEIGHT.intValue(),
                        mapImage
                );

                break;
            }
        }


        return view;
    }
}
