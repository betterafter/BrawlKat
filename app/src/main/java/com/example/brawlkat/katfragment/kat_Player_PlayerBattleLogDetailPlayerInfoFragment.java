package com.example.brawlkat.katfragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.Client;
import com.example.brawlkat.R;
import com.example.brawlkat.dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_Player_PlayerDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class kat_Player_PlayerBattleLogDetailPlayerInfoFragment extends Fragment {

    private                 RequestOptions                                                          options;
    private                 kat_official_playerInfoParser.playerData                                playerData;
    private                 kat_official_playerBattleLogParser.playerBattleData                     battleData;

    private                 ArrayList<HashMap<String, Object>>                                      BrawlersArrayList;

    private                 int                                                                     width;
    private                 int                                                                     height;
    private                 int[]                                                                   colorArray;
    private                 int[]                                                                   colorArray2;
    private                 String[]                                                                winComment;
    private                 String[]                                                                loseComment;
    private                 String[]                                                                drawComment;
    private                 Client                                                                  client = kat_Player_MainActivity.client;


    public kat_Player_PlayerBattleLogDetailPlayerInfoFragment(kat_official_playerInfoParser.playerData playerData,
                                                              kat_official_playerBattleLogParser.playerBattleData battleData){

        this.playerData = playerData;
        this.battleData = battleData;
    }

    public static kat_Player_PlayerBattleLogDetailPlayerInfoFragment newInstance(kat_official_playerInfoParser.playerData playerData,
                                                                                 kat_official_playerBattleLogParser.playerBattleData battleData){

        kat_Player_PlayerBattleLogDetailPlayerInfoFragment fragment
                = new kat_Player_PlayerBattleLogDetailPlayerInfoFragment(playerData, battleData);

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

        BrawlersArrayList = kat_Player_MainActivity.BrawlersArrayList;
        colorArray = new int[]{
                R.color.winColor, R.color.loseColor, R.color.drawColor, 0,0,0,0,0,0,0
        };

        colorArray2 = new int[]{
                R.color.Color1, R.color.Color2, R.color.Color3, R.color.Color4, R.color.Color5,
                R.color.Color6, R.color.Color7, R.color.Color8, R.color.Color9, R.color.Color10
        };

        winComment = new String[]{
                "축하해요! 이겼어요!", "승리를 축하해요!", "승리하셨군요!", "당신이 이길 줄 알았어요!",
                "이 기세를 살려 연승을!", "실력이 뛰어나시군요! 또 이겼어요!", "이것이 너와 나의 차이다"
        };
        loseComment = new String[]{
                "이번에는 물러가지만 다음엔 어림 없어요!", "패배할 수도 있죠.", "다음엔 꼭 이겨봐요",
                "항상 이길 수는 없는 법이죠.", "정말 아쉬워요. 다음엔 이길거예요.", "졌지만 잘 싸웠다.",
                "운으로 게임하네"
        };
        drawComment = new String[]{
                "지는 것보단 낫죠.", "이걸 비기네", "무승부예요!", "이길 수 있었는데...", "질 뻔 했는데 다행이다",
                "좋은 승부였다.", "이걸?"
        };

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player_player_detail_battle_log_detail_player_info, container, false);
        LinearLayout linearLayout = view.findViewById(R.id.player_player_detail_battle_log_detail_player_info_playerlist);
        linearLayout.removeAllViews();

        ArrayList<Object> TeamOrPlayer = battleData.getTeamOrPlayer();

        // 전투 전적이 3 vs 3 기록일 때 : 배열리스트를 복사해서 team 클래스 객체로 넣어주고 내 팀이 가장 앞으로 오게 정렬한 후 처리
        if(TeamOrPlayer.get(0) instanceof kat_official_playerBattleLogParser.team
        && TeamOrPlayer.size() == 2){

            // top에 복사 후 정렬
            ArrayList<kat_official_playerBattleLogParser.team> top = new ArrayList<>();
            for(int i = 0; i < TeamOrPlayer.size(); i++){
                top.add((kat_official_playerBattleLogParser.team) TeamOrPlayer.get(i));
            }
            Collections.sort(top, comparator);

            // 각 팀을 linearlayout에 묶어서 화면에 표현
            for(int i = 0; i < top.size(); i++){

                ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams =
                        ((kat_official_playerBattleLogParser.team) top.get(i)).getPlayTeamInfo();
                Team_resultSetLinearLayout(teams, i, linearLayout, "3vs3");
            }
        }

        // 3 vs 3 모드가 아닌 팀 게임일 때 : 순서대로 출력하고 내 팀만 찾기
        else if(TeamOrPlayer.get(0) instanceof kat_official_playerBattleLogParser.team
                && TeamOrPlayer.size() > 2){

            // 각 팀을 linearlayout에 묶어서 화면에 표현
            for(int i = 0; i < TeamOrPlayer.size(); i++){

                ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams =
                        ((kat_official_playerBattleLogParser.team) TeamOrPlayer.get(i)).getPlayTeamInfo();
                if(battleData.getEventMode().equals("duoShowDown")){
                    Team_result_duoShowDown_SetLinearLayout(teams, i, linearLayout, "duoShowDown");
                }
                else Team_resultSetLinearLayout(teams, i, linearLayout, "not3vs3");
            }
        }

        else if(TeamOrPlayer.get(0) instanceof kat_official_playerBattleLogParser.player){

            for(int i = 0; i < TeamOrPlayer.size(); i++){

                ArrayList<kat_official_playerBattleLogParser.playTeamInfo> players =
                        ((kat_official_playerBattleLogParser.player) TeamOrPlayer.get(i)).getPlayTeamInfo();

                Player_resultSetLinearLayout(players, i, linearLayout, "solo");
            }
        }

        return view;
    }

    private void Team_resultSetLinearLayout(ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams,
                                            int i, LinearLayout linearLayout, String type){

        LinearLayout innerLayout = new LinearLayout(Objects.requireNonNull(getActivity()).getApplicationContext());
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.card));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 10, 20, 10);
        innerLayout.setPadding(15, 15, 15, 15);
        innerLayout.setLayoutParams(layoutParams);

        battleResultText_3vs3(layoutParams, innerLayout, i);

        for(int j = 0; j < teams.size(); j++){

            kat_official_playerBattleLogParser.playTeamInfo playerInfo = teams.get(j);
            innerLayout.addView(playerItem(playerInfo, i));
        }

        linearLayout.addView(innerLayout);
    }

    private void Team_result_duoShowDown_SetLinearLayout(ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams,
                                            int i, LinearLayout linearLayout, String type){

        for(int j = 0; j < teams.size(); j++){

            LinearLayout innerLayout = new LinearLayout(Objects.requireNonNull(getActivity()).getApplicationContext());
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.card));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(20, 10, 20, 10);
            innerLayout.setPadding(15, 15, 15, 15);
            innerLayout.setLayoutParams(layoutParams);

            battleResultText_showDown(layoutParams, innerLayout, i);

            kat_official_playerBattleLogParser.playTeamInfo playerInfo = teams.get(j);
            innerLayout.addView(playerItem(playerInfo, i));

            linearLayout.addView(innerLayout);
        }
    }


    private void Player_resultSetLinearLayout(ArrayList<kat_official_playerBattleLogParser.playTeamInfo> players,
                                              int i, LinearLayout linearLayout, String type){

        for(int j = 0; j < players.size(); j++){

            LinearLayout innerLayout = new LinearLayout(Objects.requireNonNull(getActivity()).getApplicationContext());
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.card));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(20, 10, 20, 10);
            innerLayout.setPadding(15, 15, 15, 15);
            innerLayout.setLayoutParams(layoutParams);

            System.out.println(battleData.getEventMode());

            if(battleData.getEventMode().equals("soloShowdown"))
                battleResultText_showDown(layoutParams, innerLayout, j);
            else
                battleResultText_event(layoutParams, innerLayout, j);

            kat_official_playerBattleLogParser.playTeamInfo playerInfo = players.get(j);
            innerLayout.addView(playerItem(playerInfo, i));

            linearLayout.addView(innerLayout);
        }
    }


    private void battleResultText_3vs3(LinearLayout.LayoutParams layoutParams, LinearLayout innerLayout, int i){
        TextView tv = new TextView(Objects.requireNonNull(getActivity()).getApplicationContext());
        tv.setTextSize(24);
        tv.setLayoutParams(layoutParams);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

        System.out.println(battleData.getBattleResult());

        if(battleData.getBattleResult() == null){
            tv.setText("무승부");
            tv.setTextColor(getResources().getColor(colorArray[2]));
        }
        else if(battleData.getBattleResult().equals("victory")){
            if(i == 0) {
                tv.setText("승리");
                tv.setTextColor(getResources().getColor(colorArray[0]));
            }
            else{
                tv.setText("패배");
                tv.setTextColor(getResources().getColor(colorArray[1]));
            }
        }
        else{
            if(i == 0) {
                tv.setText("패배");
                tv.setTextColor(getResources().getColor(colorArray[1]));
            }
            else{
                tv.setText("승리");
                tv.setTextColor(getResources().getColor(colorArray[0]));
            }
        }
        innerLayout.addView(tv);
    }

    private void battleResultText_showDown(LinearLayout.LayoutParams layoutParams, LinearLayout innerLayout, int i){
        TextView tv = new TextView(Objects.requireNonNull(getActivity()).getApplicationContext());
        tv.setTextSize(24);
        tv.setLayoutParams(layoutParams);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

        tv.setText(i + 1 + "등");
        tv.setTextColor(getResources().getColor(colorArray2[i]));

        innerLayout.addView(tv);
    }

    private void battleResultText_event(LinearLayout.LayoutParams layoutParams, LinearLayout innerLayout, int i){
        TextView tv = new TextView(Objects.requireNonNull(getActivity()).getApplicationContext());
        tv.setTextSize(24);
        tv.setLayoutParams(layoutParams);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

        tv.setText("승리");
        tv.setTextColor(getResources().getColor(colorArray2[0]));

        innerLayout.addView(tv);
    }






    // 플레이어 상세 정보릂 표시하는 아이템 뷰
    private View playerItem(final kat_official_playerBattleLogParser.playTeamInfo playerInfo,
                            int i){
        LayoutInflater layoutInflater =
                (LayoutInflater) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.player_player_detail_battle_log_detail_player_profile, null);
        ImageView brawler_image = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_brawler_image);
        TextView player_name = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_player_name);
        TextView brawler_level = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_brawler_level);
        TextView player_trophy = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_player_trophy);
        TextView player_tag = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_player_tag);


        for(int k = 0; k < BrawlersArrayList.size(); k++){

            if(BrawlersArrayList.get(k).get("name").toString().toLowerCase().equals(playerInfo.getBrawler_name().toLowerCase())){
                System.out.println(BrawlersArrayList.get(k).get("imageUrl").toString());
                GlideImage(
                        BrawlersArrayList.get(k).get("imageUrl").toString(),
                        width / 10,
                        width / 10,
                        brawler_image);
                break;
            }
        }
        player_name.setText(playerInfo.getName());
        if(battleData.getBattleResult().equals("draw")) player_name.setTextColor(getResources().getColor(R.color.drawColor));
        else if(i == 0) player_name.setTextColor(getResources().getColor(R.color.winColor));
        else if(i == 1) player_name.setTextColor(getResources().getColor(R.color.loseColor));
        brawler_level.setText(playerInfo.getBrawler_power());
        player_trophy.setText(playerInfo.getBrawler_trophies());
        player_tag.setText(playerInfo.getTag());

        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("tag : " + playerInfo.getTag());
                String realTag = playerInfo.getTag().substring(1);
                System.out.println("realTag : " + realTag);
                SearchThread st = new SearchThread(realTag, "players");
                st.start();
            }
        });

        return v;
    }

    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(this)
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    Comparator<kat_official_playerBattleLogParser.team> comparator = new Comparator<kat_official_playerBattleLogParser.team>() {
        @Override
        public int compare(kat_official_playerBattleLogParser.team t1, kat_official_playerBattleLogParser.team t2) {
            if(isMyTeam(t2)) return 1;
            else return -1;
        }
    };

    boolean isMyTeam(kat_official_playerBattleLogParser.team team){
        for(int i = 0; i < team.getPlayTeamInfo().size(); i++){
            if(team.getPlayTeamInfo().get(i).getTag().equals(playerData.getTag())) return true;
        }
        return false;
    }

    public class SearchThread extends Thread{

        String tag;
        String type;
        ArrayList<String> sendData;

        public SearchThread(String tag, String type){
            this.tag = tag;
            this.type = type;
        }

        public void run(){

            sendData = new ArrayList<>();
            System.out.println("type : " + type);

            client.AllTypeInit(tag, type, kat_Player_MainActivity.official);

            while(!client.workDone){
                System.out.println("client wait");
                if(client.workDone){
                    client.workDone = false;
                    sendData.add(client.getAllTypeData().get(0));
                    sendData.add(client.getAllTypeData().get(1));
                    playerSearch(sendData);

                    break;
                }
            }
        }
    }


    public void playerSearch(ArrayList<String> sendData){

        // 제대로 가져오지 못했을 경우 알림
        if(sendData.get(0).equals("{none}")){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    "잘못된 태그 형식 또는 존재하지 않는 태그입니다.", Toast.LENGTH_SHORT);
            toast.show();

        }
        // 제대로 가져왔을 경우
        else{

            kat_official_playerInfoParser official_playerInfoParser = new kat_official_playerInfoParser(sendData.get(0));
            kat_official_playerBattleLogParser official_playerBattleLogParser = new kat_official_playerBattleLogParser(sendData.get(1));

            try {
                playerData = official_playerInfoParser.DataParser();
                if(!client.getAllTypeData().get(1).equals("{none}")) {
                    kat_Player_MainActivity.playerBattleDataList = official_playerBattleLogParser.DataParser();
                    kat_Player_MainActivity.playerBattleDataListStack.add(official_playerBattleLogParser.DataParser());
                }

                Intent intent = new Intent(getActivity().getApplicationContext(), kat_Player_PlayerDetailActivity.class);
                intent.putExtra("playerData", playerData);

                startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
