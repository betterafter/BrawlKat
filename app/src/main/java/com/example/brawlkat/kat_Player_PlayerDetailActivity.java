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
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.brawlkat.kat_dataparser.kat_brawlersParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

    private                             Button                                  player_detail_information_button;
    private                             Button                                  player_detail_battle_log_button;


    private                             RequestOptions                          options;

    public                              static int                              height;
    public                              static int                              width;

    private                             AdView                                  playerInformationAdView;
    private                             AdView                                  playerBattleLogAdView;
    private                             AdRequest                               adRequest;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adRequest = new AdRequest.Builder().build();


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

        if(nameColor.equals("#ffffffff")){
            player_detail_tag.setTextColor(Color.parseColor("#000000"));
        }

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
                    kset.SearchStart(newTag, "clubs", getApplicationContext());
                }
            });
        }
        else{
            LinearLayout clubNameLayout = findViewById(R.id.player_detail_clubname_layout);
            clubNameLayout.removeView(player_detail_clubName);
        }
        playerInformationList(iconImage, modeType, modeValue);
        playerBattleLogList();

        // 버튼을 눌렀을 때 플레이어 정보 <-> 배틀 로그가 바뀌게 만들기.........................................//
        final LinearLayout playerInfoLayout = (LinearLayout) findViewById(R.id.player_detail_player_info_layout);
        final LinearLayout playerGetBrawlersLayout = findViewById(R.id.player_detail_get_brawlers_layout);
        final LinearLayout playerBattleLogLayout = findViewById(R.id.player_detail_battlelog_layout);
        final LinearLayout playerWinRateLayout = findViewById(R.id.player_detail_winrate_layout);
        final LinearLayout playerTogetherLayout = findViewById(R.id.play_together_info_layout);

        // 기본 설정은 플레이어 정보부터 보여주기
        final LinearLayout adLayout = findViewById(R.id.player_player_detail_player_Ad);
        final kat_ad ad = new kat_ad(getApplicationContext());
        ad.init();
        ad.build(R.layout.kat_ad_unified_small_banner_layout, adLayout);
        ad.load();

        playerInfoLayout.setVisibility(View.VISIBLE);
        playerGetBrawlersLayout.setVisibility(View.VISIBLE);
        adLayout.setVisibility(View.VISIBLE);

        playerBattleLogLayout.setVisibility(View.GONE);
        playerWinRateLayout.setVisibility(View.GONE);
        playerTogetherLayout.setVisibility(View.GONE);

        player_detail_information_button = findViewById(R.id.player_detail_information_button);
        player_detail_information_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerInfoLayout.setVisibility(View.VISIBLE);
                playerGetBrawlersLayout.setVisibility(View.VISIBLE);
                adLayout.setVisibility(View.VISIBLE);
                ad.load();

                playerBattleLogLayout.setVisibility(View.GONE);
                playerWinRateLayout.setVisibility(View.GONE);
                playerTogetherLayout.setVisibility(View.GONE);
            }
        });

        player_detail_battle_log_button = findViewById(R.id.player_detail_battlelog_button);
        player_detail_battle_log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerInfoLayout.setVisibility(View.GONE);
                playerGetBrawlersLayout.setVisibility(View.GONE);
                adLayout.setVisibility(View.GONE);

                playerBattleLogLayout.setVisibility(View.VISIBLE);
                playerWinRateLayout.setVisibility(View.VISIBLE);
                playerTogetherLayout.setVisibility(View.VISIBLE);

            }
        });
        //..........................................................................................
    }







    // 플레이어의 게임 전체 플레이 타임 정보
    private void playerInformationList(String[] iconImage, String[] modeType, String[] modeValue){

        // 메인 정보 6개 만들기 .........................................................................
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

        // 시즌 보상 & 리셋 트로피 레이아웃 ...............................................................
        View view = layoutInflater.inflate(R.layout.player_player_detail_reward_and_reset_text, null);
        TextView reward_text = view.findViewById(R.id.player_detail_reward);
        TextView after_trophies_text = view.findViewById(R.id.player_detail_after_trophies);

        kat_SeasonRewardsCalculator seasonRewardsCalculator = new kat_SeasonRewardsCalculator(playerData);

        int seasonRewards = seasonRewardsCalculator.SeasonsRewardsCalculator();
        int seasonReset = seasonRewardsCalculator.SeasonsResetTrophiesCalculator();
        int diff = playerData.getTrophies() - seasonReset;
        reward_text.setText(Integer.toString(seasonRewards) + " points");
        after_trophies_text.setText(Integer.toString(seasonReset) + " (-" + diff + ")");

        linearLayout.addView(view);
        //..........................................................................................




        // 가지고 있는 브롤러 정보........................................................................
        playerInformation_ShowPlayerBrawlers();
    }






    private void playerInformation_ShowPlayerBrawlers(){

        LinearLayout linearLayout = findViewById(R.id.player_detail_get_brawlers_layout);
        linearLayout.removeAllViews();
        linearLayout.setGravity(Gravity.CENTER);
        ArrayList<kat_official_playerInfoParser.playerBrawlerData> brawlerData = playerData.getBrawlerData();
        ArrayList<HashMap<String, Object>> BrawlersArrayList = kat_LoadBeforeMainActivity.BrawlersArrayList;

        TextView getBrawlerText = findViewById(R.id.player_detail_player_get_brawler_text);
        getBrawlerText.setText("  획득한 브롤러 " + "(" + brawlerData.size() + "/" + BrawlersArrayList.size() + ")");

        // 유저가 가지고 있는 브롤러 탐색
        for(int i = 0; i < brawlerData.size(); ){

            LinearLayout HorizontalLayout = new LinearLayout(getApplicationContext());
            HorizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            HorizontalLayout.setWeightSum(3);
            HorizontalLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.CENTER;
            HorizontalLayout.setLayoutParams(params);

            // 한 줄에 3개의 브롤러 이미지 보여줄 것.
            for(int k = 0; k < 3; k++){

                // 탐색하다가 유저의 브롤러가 더 이상 없으면 끝
                if(i >= brawlerData.size()) break;
                LayoutInflater layoutInflater
                        = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.player_player_detail_playerinfo_brawlers, null);
                LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                vp.weight = 1;
                vp.setMargins(5,5,5,5);
                view.setBackground(getResources().getDrawable(R.drawable.card_background));

                view.setLayoutParams(vp);

                ImageView brawler_image = view.findViewById(R.id.brawlers_imageView);
                TextView brawler_trophies = view.findViewById(R.id.brawlers_trophies_highestTrophies);
                LinearLayout starPowersList = view.findViewById(R.id.brawlers_starpower_list);
                LinearLayout gadgetsList = view.findViewById(R.id.brawlers_gadgets_list);

                // 현재 탐색 대상 브롤러와 전체 브롤러 비교 및 세팅
                kat_official_playerInfoParser.playerBrawlerData currentBrawler = brawlerData.get(i);
                for(int j = 0; j < BrawlersArrayList.size(); j++){
                    if(currentBrawler.getName().equals(BrawlersArrayList
                            .get(j)
                            .get("name")
                            .toString()
                            .toUpperCase())){
                        GlideImage(BrawlersArrayList.get(j).get("imageUrl").toString(),
                                width / 5,
                                width / 5,
                                brawler_image);
                        brawler_trophies.setText(brawlerData.get(i).getTrophies() + " / " + brawlerData.get(i).getHighestTrophies());
                        ArrayList<kat_brawlersParser.StarPowers> starPowersArrayList
                                = (ArrayList<kat_brawlersParser.StarPowers>) BrawlersArrayList.get(j).get("starPowers");
                        ArrayList<kat_brawlersParser.Gadgets> gadgetsArrayList
                                = (ArrayList<kat_brawlersParser.Gadgets>) BrawlersArrayList.get(j).get("gadgets");

                        for(int x = 0; x < starPowersArrayList.size(); x++){

                            for(int xx = 0; xx < currentBrawler.getStarPowers().size(); xx++){
                                if(starPowersArrayList.get(x).getName().toLowerCase().equals(currentBrawler
                                        .getStarPowers()
                                        .get(xx).getName().toLowerCase())){
                                    ImageView starPower = new ImageView(getApplicationContext());
                                    LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    );
                                    sp.setMargins(5,5,5,5);
                                    starPower.setLayoutParams(sp);
                                    GlideImage(starPowersArrayList.get(x).getImageUrl(), width / 30, width / 30, starPower);
                                    starPowersList.addView(starPower);
                                    break;
                                }
                            }


                        }
                        for(int y = 0; y < gadgetsArrayList.size(); y++){

                            for(int yy = 0; yy < currentBrawler.getGadgets().size(); yy++){
                                if(gadgetsArrayList.get(y).getName().toLowerCase().equals(currentBrawler
                                .getGadgets()
                                .get(yy).getName().toLowerCase())){
                                    ImageView gadget = new ImageView(getApplicationContext());
                                    LinearLayout.LayoutParams gg = new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    );
                                    gg.setMargins(5,5,5,5);
                                    gadget.setLayoutParams(gg);
                                    GlideImage(gadgetsArrayList.get(y).getImageUrl(), width / 30, width / 30, gadget);
                                    gadgetsList.addView(gadget);
                                    break;
                                }
                            }
                        }
                        HorizontalLayout.addView(view);
                        break;
                    }
                }
                i++;
            }

            linearLayout.addView(HorizontalLayout);
        }
    }








    private void playerBattleLogList(){
        if(client.getData().get(1).equals("{none}")
                || (!playerBattleDataListStack.empty() && playerBattleDataListStack.peek() == null)) return;
        playerBattleDataList = playerBattleDataListStack.peek();

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = findViewById(R.id.player_detail_battle_log_layout);
        linearLayout.removeAllViews();

        int victoryCount = 0;
        int loseCount = 0;
        int drawCount = 0;
        int firstCount = 0;
        int GetStarPlayerCount = 0;

        HashMap<String[], Integer> playTogether = new HashMap<>();
        HashMap<String, Integer> playTogetherCount = new HashMap<>();
        HashMap<String, String> playTogetherTag = new HashMap<>();

        //광고 추가 ..................................................................................

        LinearLayout adLayout = new LinearLayout(getApplicationContext());

        final kat_ad ad = new kat_ad(getApplicationContext());
        ad.init();
        ad.build(R.layout.kat_ad_unified_battle_item_like_layout, adLayout);
        ad.load();

        linearLayout.addView(adLayout);

        //..........................................................................................

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
            TextView battleLogStar = view.findViewById(R.id.player_detail_battle_log_list_star);


            String userBrawler = ""; boolean check = false; boolean userFind = false;
            for(int j = 0; j < teams.size(); j++){

                // TeamOrPlayer 가 team 일 때, PlayTeamInfo에서 내 계정 정보 찾기
                if(teams.get(j) instanceof kat_official_playerBattleLogParser.team){

                    // 각 팀의 정보
                    kat_official_playerBattleLogParser.team item = (kat_official_playerBattleLogParser.team)teams.get(j);
                    // 같이 한 플레이어 저장
                    ArrayList<String[]> playTogetherArrayList = new ArrayList<>();

                    // 팀을 전부 탐색하면서 자신이 포함되어 있는 팀의 같이한 플레이어 따로 저장해주기
                    for(int k = 0; k < item.getPlayTeamInfo().size(); k++){
                        kat_official_playerBattleLogParser.playTeamInfo info
                                = item.getPlayTeamInfo().get(k);

                        playTogetherArrayList.add(new String[]{info.getTag(), info.getName()});
                        if(info.getTag().equals(playerData.getTag())){
                            userFind = true;
                            userBrawler = info.getBrawler_name();
                        }
                    }

                    // 자신의 팀을 이미 한번 찾았다면 HashMap에 다시 저장하고 탐색 종료
                    if(userFind){

                        for(int ii = 0; ii < playTogetherArrayList.size(); ii++){

                            if(playTogetherCount.get((playTogetherArrayList.get(ii))[0]) == null){
                                playTogetherCount.put(playTogetherArrayList.get(ii)[0], 1);
                                playTogetherTag.put(playTogetherArrayList.get(ii)[0], playTogetherArrayList.get(ii)[1]);
                            }
                            else{
                                playTogetherCount.put(playTogetherArrayList.get(ii)[0],
                                        playTogetherCount.get(playTogetherArrayList.get(ii)[0]) + 1);
                            }
                        }
                        break;
                    }
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
            // 1등 몇번 했는지 && 스타 플레이어 몇번 받았는지 확인
            if(battleData.getRank() != null && battleData.getRank().equals("1")) firstCount++;
            if(battleData.getStarPlayer() != null && battleData.getStarPlayer().toLowerCase()
                    .equals(playerData.getTag().toLowerCase())) {
                GetStarPlayerCount++;
                battleLogStar.setText("STAR");
            }
            else{
                battleLogStar.setBackgroundColor(getResources().getColor(R.color.transparent));
            }


            // 왼쪽의 승패 여부를 표시하는 텍스트 디자인
            if(battleData.getBattleResult().equals("victory") || (battleData.getBattleTrophyChange() != null
                    && !battleData.getBattleTrophyChange().contains("-"))) {
                victoryCount++;
                battleLogResult.setText("승");
                Drawable drawable = battleLogResult.getBackground();
                drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.winColor));

                Drawable drawable_right = battleLogDetail.getBackground();
                drawable_right.setTint(ContextCompat.getColor(getApplicationContext(), R.color.winColor));
            }

            else if(battleData.getBattleResult().equals("draw") || battleData.getBattleTrophyChange() == null){
                drawCount++;
                battleLogResult.setText("무");
                Drawable drawable = battleLogResult.getBackground();
                drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.drawColor));

                Drawable drawable_right = battleLogDetail.getBackground();
                drawable_right.setTint(ContextCompat.getColor(getApplicationContext(), R.color.drawColor));
            }

            else{
                loseCount++;
                battleLogResult.setText("패");
                Drawable drawable = battleLogResult.getBackground();
                drawable.setTint(ContextCompat.getColor(getApplicationContext(), R.color.loseColor));

                Drawable drawable_right = battleLogDetail.getBackground();
                drawable_right.setTint(ContextCompat.getColor(getApplicationContext(), R.color.loseColor));
            }


            // starlist.pro에서 제공 받은 brawler 정보를 받아와 내가 플레이한 브롤러 이미지 표시하기
            for(int j = 0; j < BrawlersArrayList.size(); j++){
                if(BrawlersArrayList.get(j).get("name").toString().toLowerCase().equals(userBrawler.toLowerCase())){
                    GlideImage(BrawlersArrayList.get(j).get("imageUrl").toString(),
                            width / 10, width / 10, battleLogBrawler);
                }
            }

            battleLogBrawlerName.setText(userBrawler.toLowerCase());


            if(battleData.getBattleTrophyChange() == null)
                battleLogBrawlerTrophy.setText("+0");
            else if(battleData.getBattleTrophyChange().contains("-"))
                battleLogBrawlerTrophy.setText(battleData.getBattleTrophyChange());
            else
                battleLogBrawlerTrophy.setText("+" + battleData.getBattleTrophyChange());

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

        // 전적 기록 - 승률 & 스타 플레이어 횟수 & 1등 횟수 보여주기
        double rate = ((double)victoryCount / (double)(victoryCount + loseCount + drawCount)) * 100;
        String rateString = String.format("%.1f", rate);
        TextView winRateText = findViewById(R.id.winrate);
        winRateText.setText(victoryCount + "승 " + drawCount + "무 " + loseCount + "패 " + "(" + rateString + "%)");

        TextView starPlayerText = findViewById(R.id.starplayer);
        starPlayerText.setText(GetStarPlayerCount + "번");

        TextView firstText = findViewById(R.id.first);
        firstText.setText(firstCount + "번");


        // 같이 한 플레이어 리스트 구현
        LinearLayout playTogetherLayout = findViewById(R.id.play_together_layout);
        playTogetherLayout.removeAllViews();
        Iterator<String> keys = playTogetherCount.keySet().iterator();
        while ( keys.hasNext() ) {
            final String key = keys.next();

            if(playTogetherCount.get(key) >= 2 && !key.equals(playerData.getTag())) {
                View view = layoutInflater.inflate(R.layout.player_player_detail_battle_play_together_item, null);
                TextView play_together_name = view.findViewById(R.id.play_together_name);
                TextView play_together_tag = view.findViewById(R.id.play_together_tag);
                TextView play_together_count = view.findViewById(R.id.play_together_count);

                play_together_name.setText(playTogetherTag.get(key));
                play_together_tag.setText(key);
                play_together_count.setText(playTogetherCount.get(key).toString());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        kat_LoadingDialog dialog = new kat_LoadingDialog(kat_Player_PlayerDetailActivity.this);
                        dialog.show();

                        String realTag = key.substring(1);

                        kat_SearchThread kset = new kat_SearchThread(kat_Player_PlayerDetailActivity.this,
                                kat_Player_PlayerDetailActivity.class, dialog);
                        kset.SearchStart(realTag, "players", getApplicationContext());
                    }
                });

                playTogetherLayout.addView(view);
            }
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
