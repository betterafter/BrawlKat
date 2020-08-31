package com.example.brawlkat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.dataparser.kat_brawlersParser;
import com.example.brawlkat.dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;

public class kat_Player_DetailActivity extends kat_Player_RecentSearchActivity {

    private                             ImageView                               playerIcon;
    private                             ImageView                               player_detail_trophies_icon;



    private                             TextView                                player_detail_name;
    private                             TextView                                player_detail_tag;
    private                             TextView                                player_detail_trophies;
    private                             TextView                                player_detail_level;




    private                             RequestOptions                          options;

    private                             int                                     height;
    private                             int                                     width;



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

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;
    }

    protected void onStart(){
        super.onStart();

        setData();
    }

    // player_detail 레이아웃에 데이터 바인드
    private void setData(){


        String url_profile = "https://www.starlist.pro/assets/profile/" + playerData.getIconId() + ".png?v=1";
        String url_icon_trophies = "https://www.starlist.pro/assets/icon/trophy.png";

        String[] iconImage = new String[]{
                "https://www.starlist.pro/assets/icon/Power-Play.png",
                "https://www.starlist.pro/assets/icon/3v3.png",
                "https://www.starlist.pro/assets/gamemode/Showdown.png?v=2",
                "https://www.starlist.pro/assets/gamemode/Duo-Showdown.png?v=2",
                "https://www.starlist.pro/assets/gamemode/Robo-Rumble.png?v=2",
                "https://www.starlist.pro/assets/gamemode/Big-Game.png?v=2"
        };

        String[] modeType = new String[]{
                "Power Play",
                "3 vs 3",
                "Solo Showdown",
                "Duo Showdown",
                "Robo Rumble",
                "Big Game"
        };

        String[] modeValue = new String[]{
                Integer.toString(playerData.getPowerPlayPoint()),
                Integer.toString(playerData.get_3vs3()),
                Integer.toString(playerData.get_solo()),
                Integer.toString(playerData.get_duo()),
                Integer.toString(playerData.get_event()),
                Integer.toString(playerData.get_bigBrawler())
        };


        GlideImage(url_profile, width / 5, width / 5, playerIcon);

        player_detail_tag.setText(playerData.getTag());
        player_detail_name.setText(playerData.getName());
        player_detail_trophies.setText(playerData.getTrophies() + " / " + playerData.getHighestTrophies());
        player_detail_level.setText(Integer.toString(playerData.getExpLevel()));

        GlideImage(url_icon_trophies, width / 20, width / 20, player_detail_trophies_icon);

        playerInformationList(iconImage, modeType, modeValue);
        playerBattleLogList();
    }

    // 플레이어의 게임 전체 플레이 타임 정보
    private void playerInformationList(String[] iconImage, String[] modeType, String[] modeValue){

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.player_detail_player_info_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0; i < 6; ){

            // 행을 2개로 나누어 정렬하도록 한다.
            LinearLayout lin = new LinearLayout(getApplicationContext());
            lin.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lin.setGravity(Gravity.LEFT);
            lin.setWeightSum(2);
            lin.setLayoutParams(params);

            for(int j = 0; j < 2; j++){

                // 내부 레이아웃 아이템을 같은 간격으로 정렬
                View view = layoutInflater.inflate(R.layout.player_detail_playinformation, null);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                p.weight = 1;
                view.setLayoutParams(p);

                // 각 아이템 값 선언
                ImageView icon = view.findViewById(R.id.player_detail_game_info_icon);
                GlideImage(iconImage[i], width / 20, width / 20, icon);

                TextView type = view.findViewById(R.id.player_detail_game_info_modeType);
                type.setText(modeType[i]);

                TextView value = view.findViewById(R.id.player_detail_game_info_modeValue);
                value.setText(modeValue[i]);

                lin.addView(view);
                i++;
            }
            linearLayout.addView(lin);
        }
    }

    private void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    private void playerBattleLogList(){
        if(client.getdata().get(1).equals("{none}") || playerBattleDataList == null) return;

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = findViewById(R.id.player_detail_battle_log_layout);

        kat_brawlersParser bp = new kat_brawlersParser(client.getdata().get(1));
        ArrayList<HashMap<String, Object>> BrawlersArrayList = new ArrayList<>();
        try{
            BrawlersArrayList = bp.DataParser();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        for(int i = 0; i < playerBattleDataList.size(); i++){

            kat_official_playerBattleLogParser.playerBattleData battleData = playerBattleDataList.get(i);

            ArrayList<kat_official_playerBattleLogParser.team> teams = battleData.getTeams();

            View view = layoutInflater.inflate(R.layout.player_detail_battle_log_list, null);

            TextView battleLogResult = view.findViewById(R.id.player_detail_battle_log_result);
            ImageView battleLogBrawler = view.findViewById(R.id.player_detail_battle_log_brawler);
            TextView battleLogBrawlerName = view.findViewById(R.id.player_detail_battle_log_brawler_name);
            TextView battleLogBrawlerTrophy = view.findViewById(R.id.player_detail_battle_log_brawler_trophy);
            ImageView battleLogEventIcon = view.findViewById(R.id.player_detail_battle_log_event_icon);
            TextView battleLogEventName = view.findViewById(R.id.player_detail_battle_log_event_name);
            TextView battleLogTime = view.findViewById(R.id.player_detail_battle_log_time);
            TextView battleLogEventMap = view.findViewById(R.id.player_detail_battle_log_event_map);

            String userBrawler = ""; boolean check = false;
            for(int j = 0; j < teams.size(); j++){
                for(int k = 0; k < teams.get(j).getPlayTeamInfos().size(); k++){
                    kat_official_playerBattleLogParser.playTeamInfo info
                            = teams.get(j).getPlayTeamInfos().get(k);

                    if(info.getTag().equals(playerData.getTag())){
                        userBrawler = info.getBrawler_name(); check = true; break;
                    }
                }
                if(check) break;
            }

            battleLogResult.setText(battleData.getBattleResult());
            for(int j = 0; j < BrawlersArrayList.size(); j++){
                if(BrawlersArrayList.get(j).get("name").toString().toLowerCase().equals(userBrawler.toLowerCase())){
                    GlideImage(BrawlersArrayList.get(j).get("imageUrl").toString(), width / 15, width / 15, battleLogBrawler);
                }
            }

            battleLogBrawlerName.setText(userBrawler);
            kat_official_playerInfoParser.playerBrawlerData brawlerData = null;

            for(int j = 0; j < playerData.getBrawlerData().size(); j++){
                if(playerData.getBrawlerData().get(j).getName().toLowerCase().equals(userBrawler.toLowerCase())){
                    brawlerData = playerData.getBrawlerData().get(j);
                    break;
                }
            }

            battleLogBrawlerTrophy.setText(Integer.toString(brawlerData.getTrophies()));

            System.out.println(mapData.get(battleData.getEventId()).getGameModeIconUrl());

            GlideImage(mapData.get(battleData.getEventId()).getGameModeIconUrl(), width / 20, width / 20, battleLogEventIcon);
            battleLogTime.setText(battleData.getBattleTime());
            battleLogEventName.setText(mapData.get(battleData.getEventId()).getName());
            battleLogEventMap.setText("click");

            linearLayout.addView(view);
        }





    }





}
