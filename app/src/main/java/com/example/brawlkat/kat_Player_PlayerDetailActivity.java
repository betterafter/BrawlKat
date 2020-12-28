package com.example.brawlkat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.example.brawlkat.kat_Fragment.kat_FavoritesFragment;
import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class kat_Player_PlayerDetailActivity extends kat_Player_RecentSearchActivity {

    private                             ImageView                               playerIcon;
    private                             ImageView                               player_detail_trophies_icon;
    private                             ImageView                               player_detail_favorites;


    private                             TextView                                player_detail_name;
    private                             TextView                                player_detail_tag;
    private                             TextView                                player_detail_trophies;
    private                             TextView                                player_detail_level;
    private                             TextView                                player_detail_clubName;



    private                             RequestOptions                          options;

    public                              static int                              height;
    public                              static int                              width;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_player_detail);

        playerIcon = findViewById(R.id.player_detail_image);
        player_detail_name = findViewById(R.id.player_detail_name);
        player_detail_tag = findViewById(R.id.player_detail_tag);
        player_detail_trophies_icon = findViewById(R.id.player_detail_trophies_image);
        player_detail_trophies = findViewById(R.id.player_detail_trophies);
        player_detail_level = findViewById(R.id.player_detail_level_icon_text);
        player_detail_clubName = findViewById(R.id.player_detail_clubname);
        player_detail_favorites = findViewById(R.id.player_detail_favorites);

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

        if(this.getClass().getName().equals("com.example.brawlkat.kat_Player_PlayerDetailActivity")) {
            Intent intent = getIntent();
            playerData = (kat_official_playerInfoParser.playerData) intent.getSerializableExtra("playerData");
            setData();

            if (!kataFavoritesBase.isFavorites(playerData.getTag())) {
                player_detail_favorites.setBackground(getResources().getDrawable(R.drawable.round_star_border_24));
            }
            else {
                player_detail_favorites.setBackground(getResources().getDrawable(R.drawable.round_star_24));
            }
        }
    }

    // player_player_detail 레이아웃에 데이터 바인드
    private void setData(){

        String url_profile = "";
        if(playerData.getIconId() != null)
            url_profile = "https://www.starlist.pro/assets/profile/" + playerData.getIconId() + ".png?v=1";
        else
            url_profile = "https://www.starlist.pro/assets/profile/" + "28000000" + ".png?v=1";
        String url_icon_trophies = "https://www.starlist.pro/assets/icon/trophy.png";
        String url_icon_define_club = "https://cdn.starlist.pro/club/8000006.png?v=1";

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


        GlideImageWithRoundCorner(url_profile, width / 5, width / 5, playerIcon);

        player_detail_tag.setText(playerData.getTag());
        player_detail_name.setText(playerData.getName());
        player_detail_trophies.setText(playerData.getTrophies() + " / " + playerData.getHighestTrophies());
        player_detail_level.setText(Integer.toString(playerData.getExpLevel()));

        String nameColor = playerData.getNameColor().replace("0x", "#");
        player_detail_name.setTextColor(Color.parseColor(nameColor));
        player_detail_tag.getBackground().setTint(Color.parseColor(nameColor));

        GlideImage(url_icon_trophies, width / 30, width / 30, player_detail_trophies_icon);

        if(playerData.getClub().getId() != null && playerData.getClub().getName() != null) {
            player_detail_clubName.setText(playerData.getClub().getName());
            player_detail_clubName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    kat_LoadingDialog dialog = new kat_LoadingDialog(kat_Player_PlayerDetailActivity.this);
                    dialog.show();

                    String RawTag = playerData.getClub().getId();
                    String newTag = RawTag.substring(1);

                    kat_SearchThread kset = new kat_SearchThread(kat_Player_PlayerDetailActivity.this,
                            kat_Player_ClubDetailActivity.class, dialog);
                    kset.SearchStart(newTag, "clubs");
                }
            });
        }
        else{
            LinearLayout clubNameLayout = findViewById(R.id.player_detail_clubname_layout);
            clubNameLayout.removeView(player_detail_clubName);
        }

        playerInformationList(iconImage, modeType, modeValue);
        playerBattleLogList();
    }

    // 플레이어의 게임 전체 플레이 타임 정보
    private void playerInformationList(String[] iconImage, String[] modeType, String[] modeValue){

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.player_detail_player_info_layout);
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearLayout.removeAllViews();

        for(int i = 0; i < 6; ){

            // 행을 2개로 나누어 정렬하도록 한다.
            LinearLayout lin = new LinearLayout(getApplicationContext());
            lin.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lin.setGravity(Gravity.CENTER);
            lin.setWeightSum(3);
            lin.setLayoutParams(params);

            for(int j = 0; j < 3; j++){

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

    private void playerBattleLogList(){
        if(client.getData().get(1).equals("{none}")
                || (!playerBattleDataListStack.empty() && playerBattleDataListStack.peek() == null)) return;
        playerBattleDataList = playerBattleDataListStack.peek();

        //BrawlersArrayList = kat_LoadBeforeMainActivity.BrawlersArrayList;

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = findViewById(R.id.player_detail_battle_log_layout);
        linearLayout.removeAllViews();

        for(int i = 0; i < playerBattleDataList.size(); i++){

            final kat_official_playerBattleLogParser.playerBattleData battleData = playerBattleDataList.get(i);

            ArrayList<Object> teams = battleData.getTeamOrPlayer();

            View view = layoutInflater.inflate(R.layout.player_detail_battle_log_list, null);

            TextView battleLogResult = view.findViewById(R.id.player_detail_battle_log_result);
            ImageView battleLogBrawler = view.findViewById(R.id.player_detail_battle_log_brawler);
            TextView battleLogBrawlerName = view.findViewById(R.id.player_detail_battle_log_brawler_name);
            TextView battleLogBrawlerTrophy = view.findViewById(R.id.player_detail_battle_log_brawler_trophy);
            ImageView battleLogEventIcon = view.findViewById(R.id.player_detail_battle_log_event_icon);
            TextView battleLogEventName = view.findViewById(R.id.player_detail_battle_log_event_name);
            TextView battleLogTime = view.findViewById(R.id.player_detail_battle_log_time);
            ImageView battleLogDetail = view.findViewById(R.id.player_detail_battle_log_show_detail);


            String userBrawler = ""; boolean check = false;
            for(int j = 0; j < teams.size(); j++){

                // TeamOrPlayer 가 team 일 때, PlayTeamInfo에서 내 계정 정보 찾기
                if(teams.get(j) instanceof kat_official_playerBattleLogParser.team){

                    kat_official_playerBattleLogParser.team item = (kat_official_playerBattleLogParser.team)teams.get(j);

                    for(int k = 0; k < item.getPlayTeamInfo().size(); k++){
                        kat_official_playerBattleLogParser.playTeamInfo info
                                = item.getPlayTeamInfo().get(k);

                        if(info.getTag().equals(playerData.getTag())){
                            userBrawler = info.getBrawler_name(); check = true; break;
                        }
                    }
                    if(check) break;
                }

                // TeamOrPlayer 가 player 일 때, PlayTeamInfo에서 내 계정 정보 찾기
                else if(teams.get(j) instanceof kat_official_playerBattleLogParser.player){

                    kat_official_playerBattleLogParser.player item = (kat_official_playerBattleLogParser.player) teams.get(j);

                    for(int k = 0; k < item.getPlayTeamInfo().size(); k++){
                        kat_official_playerBattleLogParser.playTeamInfo info
                                = item.getPlayTeamInfo().get(k);

                        if(info.getTag().equals(playerData.getTag())){
                            userBrawler = info.getBrawler_name(); check = true; break;
                        }
                    }
                    if(check) break;
                }
            }

            // 왼쪽의 승패 여부를 표시하는 텍스트 디자인
            if(battleData.getBattleResult().equals("victory") || (battleData.getBattleTrophyChange() != null
                    && !battleData.getBattleTrophyChange().contains("-"))) {
                battleLogResult.setText("승");
                Drawable drawable = battleLogResult.getBackground();
                drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.winColor));

                Drawable drawable_right = battleLogDetail.getBackground();
                drawable_right.setTint(ContextCompat.getColor(getApplicationContext(), R.color.winColor));
            }

            else if(battleData.getBattleResult().equals("draw") || battleData.getBattleTrophyChange() == null){
                battleLogResult.setText("무");
                Drawable drawable = battleLogResult.getBackground();
                drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.drawColor));

                Drawable drawable_right = battleLogDetail.getBackground();
                drawable_right.setTint(ContextCompat.getColor(getApplicationContext(), R.color.drawColor));
            }

            else{
                battleLogResult.setText("패");
                Drawable drawable = battleLogResult.getBackground();
                drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.loseColor));

                Drawable drawable_right = battleLogDetail.getBackground();
                drawable_right.setTint(ContextCompat.getColor(getApplicationContext(), R.color.loseColor));
            }


            // starlist.pro에서 제공 받은 brawler 정보를 받아와 내가 플레이한 브롤러 이미지 표시하기
            System.out.println(BrawlersArrayList.size());
            for(int j = 0; j < BrawlersArrayList.size(); j++){
                if(BrawlersArrayList.get(j).get("name").toString().toLowerCase().equals(userBrawler.toLowerCase())){
                    System.out.println(BrawlersArrayList.get(j).get("name").toString());
                    GlideImage(BrawlersArrayList.get(j).get("imageUrl").toString(),
                            width / 10, width / 10, battleLogBrawler);
                }
            }

            battleLogBrawlerName.setText(userBrawler.toLowerCase());
            kat_official_playerInfoParser.playerBrawlerData brawlerData = null;

            for(int j = 0; j < playerData.getBrawlerData().size(); j++){
                if(playerData.getBrawlerData().get(j).getName().toLowerCase().equals(userBrawler.toLowerCase())){
                    brawlerData = playerData.getBrawlerData().get(j);
                    break;
                }
            }

            if(battleData.getBattleTrophyChange() == null)
                battleLogBrawlerTrophy.setText("+0");
            else if(battleData.getBattleTrophyChange().contains("-"))
                battleLogBrawlerTrophy.setText(battleData.getBattleTrophyChange());
            else
                battleLogBrawlerTrophy.setText("+" + battleData.getBattleTrophyChange());

            System.out.println(mapData.get(battleData.getEventId()));

            System.out.println("mapdata size : " + kat_LoadBeforeMainActivity.mapData.size());
            System.out.println("battleData.getEventId  : " + battleData.getEventId());
            if(!battleData.getEventId().equals("0") && mapData.get(battleData.getEventId()) != null){
                GlideImage(mapData.get(battleData.getEventId()).getGameModeIconUrl(),
                        width / 20,
                        width / 20,
                        battleLogEventIcon);
            }


            String time = battleData.getBattleTime();
            String year = time.substring(0, 4);
            String month = time.substring(4, 6);
            String day = time.substring(6, 8);
            String hour = time.substring(9, 11);
            String min = time.substring(11, 13);
            String sec = time.substring(13, 15);
            battleLogTime.setText(year + "." + month + "." + day + "   " + hour + "시 " + min + "분 " + sec + "초");

            if(!battleData.getEventId().equals("0") && mapData.get(battleData.getEventId()) != null)
                battleLogEventName.setText(mapData.get(battleData.getEventId()).getName());

            else
                battleLogEventName.setText("custom map");

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(kat_Player_PlayerDetailActivity.this,
                            kat_Player_PlayerBattleLogDetailActivity.class);
                    intent.putExtra("playerData", playerData);
                    intent.putExtra("battleData", battleData);
                    startActivity(intent);
                }
            });
            linearLayout.addView(view);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(this.getClass().getName().equals("com.example.brawlkat.kat_Player_PlayerDetailActivity"))
            playerBattleDataListStack.pop();
    }

    public void onFavoritesClick(View view){
        if(!kataFavoritesBase.isFavorites(playerData.getTag())) {

            String tag = "", name = "", trophies = "", highestTrophies = "", iconId = "", level = "";
            if(playerData.getTag() != null) tag = playerData.getTag();
            if(playerData.getName() != null) name = playerData.getName();
            if(playerData.getTrophies() != 0) trophies = Integer.toString(playerData.getTrophies());
            if(playerData.getHighestTrophies() != 0) highestTrophies = Integer.toString(playerData.getHighestTrophies());
            if(playerData.getIconId() != null) iconId = playerData.getIconId();
            if(playerData.getExpLevel() != 0) level = Integer.toString(playerData.getExpLevel());

            kataFavoritesBase.insert(
                    "players", tag, name, trophies, highestTrophies, iconId, level
            );
            view.setBackground(getResources().getDrawable(R.drawable.round_star_24));
        }
        else {
            kataFavoritesBase.delete(playerData.getTag());
            view.setBackground(getResources().getDrawable(R.drawable.round_star_border_24));
        }

        for(Fragment fragment : kat_player_mainActivity.getSupportFragmentManager().getFragments()){
            System.out.println(fragment);
            if(fragment instanceof kat_FavoritesFragment){
                ((kat_FavoritesFragment) fragment).refresh();
            }
        }

    }


    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    public void GlideImageWithRoundCorner(String url, int width, int height, ImageView view){
        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .apply(new RequestOptions().circleCrop().circleCrop())
                .override(width, height)
                .into(view);
    }
}
