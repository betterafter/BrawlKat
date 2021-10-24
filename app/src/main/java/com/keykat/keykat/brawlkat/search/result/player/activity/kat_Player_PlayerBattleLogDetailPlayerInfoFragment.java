package com.keykat.keykat.brawlkat.search.result.player.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerBattleLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class kat_Player_PlayerBattleLogDetailPlayerInfoFragment extends Fragment {

    private final kat_official_playerInfoParser.playerData playerData;
    private final kat_official_playerBattleLogParser.playerBattleData battleData;

    private ArrayList<HashMap<String, Object>> BrawlersArrayList;

    private int[] colorArray;
    private int[] colorArray2;


    public kat_Player_PlayerBattleLogDetailPlayerInfoFragment(kat_official_playerInfoParser.playerData playerData,
                                                              kat_official_playerBattleLogParser.playerBattleData battleData) {

        this.playerData = playerData;
        this.battleData = battleData;
    }

    public static kat_Player_PlayerBattleLogDetailPlayerInfoFragment newInstance
            (kat_official_playerInfoParser.playerData playerData,
             kat_official_playerBattleLogParser.playerBattleData battleData) {

        return new kat_Player_PlayerBattleLogDetailPlayerInfoFragment(playerData, battleData);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BrawlersArrayList = KatData.BrawlersArrayList;
        colorArray = new int[]{
                R.color.winColor, R.color.loseColor, R.color.drawColor, 0, 0, 0, 0, 0, 0, 0
        };

        colorArray2 = new int[]{
                R.color.Color1, R.color.Color2, R.color.Color3, R.color.Color4, R.color.Color5,
                R.color.Color6, R.color.Color7, R.color.Color8, R.color.Color9, R.color.Color10
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
        if (TeamOrPlayer.get(0) instanceof kat_official_playerBattleLogParser.team
                && TeamOrPlayer.size() == 2) {

            // top에 복사 후 정렬
            ArrayList<kat_official_playerBattleLogParser.team> top = new ArrayList<>();
            for (int i = 0; i < TeamOrPlayer.size(); i++) {
                top.add((kat_official_playerBattleLogParser.team) TeamOrPlayer.get(i));
            }
            top.sort(comparator);

            // 각 팀을 linearlayout에 묶어서 화면에 표현
            for (int i = 0; i < top.size(); i++) {

                ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams =
                        (top.get(i)).getPlayTeamInfo();
                Team_resultSetLinearLayout(teams, i, linearLayout, "3vs3");
            }
        }

        // 3 vs 3 모드가 아닌 팀 게임일 때 : 순서대로 출력하고 내 팀만 찾기
        else if (TeamOrPlayer.get(0) instanceof kat_official_playerBattleLogParser.team
                && TeamOrPlayer.size() > 2) {

            // 각 팀을 linearlayout에 묶어서 화면에 표현
            for (int i = 0; i < TeamOrPlayer.size(); i++) {

                ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams =
                        ((kat_official_playerBattleLogParser.team) TeamOrPlayer.get(i)).getPlayTeamInfo();
                if (battleData.getEventMode().equals("duoShowdown")) {
                    Team_result_duoShowDown_SetLinearLayout(teams, i, linearLayout, "duoShowDown");
                } else Team_resultSetLinearLayout(teams, i, linearLayout, "not3vs3");
            }
        } else if (TeamOrPlayer.get(0) instanceof kat_official_playerBattleLogParser.player) {

            for (int i = 0; i < TeamOrPlayer.size(); i++) {

                ArrayList<kat_official_playerBattleLogParser.playTeamInfo> players =
                        ((kat_official_playerBattleLogParser.player) TeamOrPlayer.get(i)).getPlayTeamInfo();

                Player_resultSetLinearLayout(players, i, linearLayout, "solo");
            }
        }

        return view;
    }

    private void Team_resultSetLinearLayout(ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams,
                                            int i, LinearLayout linearLayout, String type) {

        LinearLayout innerLayout = new LinearLayout(requireActivity().getApplicationContext());
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setBackground(ContextCompat.getDrawable(requireActivity().getApplicationContext(), R.drawable.card));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(20, 10, 20, 10);
        innerLayout.setPadding(15, 15, 15, 15);
        innerLayout.setLayoutParams(layoutParams);

        battleResultText_3vs3(layoutParams, innerLayout, i);

        for (int j = 0; j < teams.size(); j++) {

            kat_official_playerBattleLogParser.playTeamInfo playerInfo = teams.get(j);
            innerLayout.addView(playerItem(playerInfo, i));
        }

        linearLayout.addView(innerLayout);
    }

    private void Team_result_duoShowDown_SetLinearLayout(ArrayList<kat_official_playerBattleLogParser.playTeamInfo> teams,
                                                         int i, LinearLayout linearLayout, String type) {

        for (int j = 0; j < teams.size(); j += 2) {

            LinearLayout innerLayout = new LinearLayout(requireActivity().getApplicationContext());
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.card));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 10, 0, 10);
            innerLayout.setPadding(0, 15, 0, 15);
            innerLayout.setLayoutParams(layoutParams);

            battleResultText_showDown(layoutParams, innerLayout, i);

            for (int k = j; k < j + 2; k++) {
                kat_official_playerBattleLogParser.playTeamInfo playerInfo = teams.get(j + k);
                innerLayout.addView(playerItem(playerInfo, i));
            }

            linearLayout.addView(innerLayout);
        }
    }

    private void Player_resultSetLinearLayout(ArrayList<kat_official_playerBattleLogParser.playTeamInfo> players,
                                              int i, LinearLayout linearLayout, String type) {

        for (int j = 0; j < players.size(); j++) {

            LinearLayout innerLayout = new LinearLayout(requireActivity().getApplicationContext());
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.card));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(20, 10, 20, 10);
            innerLayout.setPadding(15, 15, 15, 15);
            innerLayout.setLayoutParams(layoutParams);


            if (battleData.getEventMode() != null && battleData.getEventMode().equals("soloShowdown"))
                battleResultText_showDown(layoutParams, innerLayout, j);
            else
                battleResultText_event(layoutParams, innerLayout, j);

            kat_official_playerBattleLogParser.playTeamInfo playerInfo = players.get(j);
            innerLayout.addView(playerItem(playerInfo, i));

            linearLayout.addView(innerLayout);
        }
    }

    private void battleResultText_3vs3(LinearLayout.LayoutParams layoutParams, LinearLayout innerLayout, int i) {

        LinearLayout.LayoutParams TextViewLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        TextViewLayoutParams.setMargins(10, 10, 10, 10);

        TextView tv = new TextView(requireActivity().getApplicationContext());
        tv.setTextSize(24);
        tv.setBackground(getResources().getDrawable(R.drawable.card_bottom_line));
        tv.setLayoutParams(TextViewLayoutParams);
        tv.setPadding(20, 10, 0, 20);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);


        if (battleData.getBattleResult() == null) {
            tv.setText("무승부");
            tv.setTextColor(getResources().getColor(colorArray[2]));
        } else if (battleData.getBattleResult().equals("victory")) {
            if (i == 0) {
                tv.setText("승리");
                tv.setTextColor(getResources().getColor(colorArray[0]));
            } else {
                tv.setText("패배");
                tv.setTextColor(getResources().getColor(colorArray[1]));
            }
        } else {
            if (i == 0) {
                tv.setText("패배");
                tv.setTextColor(getResources().getColor(colorArray[1]));
            } else {
                tv.setText("승리");
                tv.setTextColor(getResources().getColor(colorArray[0]));
            }
        }
        innerLayout.addView(tv);
    }

    // 각 플레이어를 표시하는 레이아웃 디자인 (innerlayout 디자인)
    private void battleResultText_showDown(LinearLayout.LayoutParams layoutParams, LinearLayout innerLayout, int i) {

        LinearLayout.LayoutParams TextViewLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        TextView tv = new TextView(requireActivity().getApplicationContext());
        tv.setTextSize(24);
        tv.setBackground(getResources().getDrawable(R.drawable.card_bottom_line));
        tv.setLayoutParams(TextViewLayoutParams);
        tv.setPadding(20, 10, 0, 20);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

        tv.setText(i + 1 + "등");
        tv.setTextColor(getResources().getColor(colorArray2[i]));

        innerLayout.addView(tv);
    }

    private void battleResultText_event(LinearLayout.LayoutParams layoutParams, LinearLayout innerLayout, int i) {

        LinearLayout.LayoutParams TextViewLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView tv = new TextView(requireActivity().getApplicationContext());
        tv.setTextSize(24);
        tv.setBackground(getResources().getDrawable(R.drawable.card_bottom_line));
        tv.setLayoutParams(TextViewLayoutParams);
        tv.setPadding(20, 10, 0, 20);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

        tv.setText("승리");
        tv.setTextColor(getResources().getColor(colorArray2[0]));

        innerLayout.addView(tv);
    }


    // 플레이어 상세 정보릂 표시하는 아이템 뷰
    private View playerItem(final kat_official_playerBattleLogParser.playTeamInfo playerInfo,
                            int i) {
        LayoutInflater layoutInflater =
                (LayoutInflater) requireActivity().getApplicationContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(10, 0, 0, 0);

        View v = layoutInflater.inflate(R.layout.player_player_detail_battle_log_detail_player_profile, null);
        ImageView brawler_image = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_brawler_image);
        TextView player_name = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_player_name);
        TextView brawler_level = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_brawler_level);
        TextView player_trophy = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_player_trophy);
        TextView player_tag = v.findViewById(R.id.player_player_detail_battle_log_detail_player_profile_player_tag);
        ImageView player_star = v.findViewById(R.id.player_player_detail_battle_log_star);

        v.setLayoutParams(layoutParams);

        for (int k = 0; k < BrawlersArrayList.size(); k++) {
            if (BrawlersArrayList.get(k).get("name").toString().toLowerCase().equals(playerInfo.getBrawler_name().toLowerCase())) {
                KatData.glideImage(
                        requireActivity().getApplicationContext(),
                        Objects.requireNonNull(BrawlersArrayList.get(k).get("imageUrl")).toString(),
                        KatData.SCREEN_WIDTH.intValue() / 10,
                        KatData.SCREEN_WIDTH.intValue() / 10,
                        brawler_image);
                break;
            }
        }
        player_name.setText(playerInfo.getName());
        if (battleData.getBattleResult().equals("draw"))
            player_name.setTextColor(getResources().getColor(R.color.drawColor));
        else if (battleData.getBattleResult().equals("victory")) {
            if (i == 0) player_name.setTextColor(getResources().getColor(R.color.winColor));
            else if (i == 1) player_name.setTextColor(getResources().getColor(R.color.loseColor));
        } else if (battleData.getBattleResult().equals("defeat")) {
            if (i == 0) player_name.setTextColor(getResources().getColor(R.color.loseColor));
            else if (i == 1) player_name.setTextColor(getResources().getColor(R.color.winColor));
        } else {
            if (i == 0) player_name.setTextColor(getResources().getColor(R.color.winColor));
            else if (i == 1) player_name.setTextColor(getResources().getColor(R.color.loseColor));
        }

        if (battleData.getStarPlayer() != null) {
            if (playerInfo.getTag().equals(battleData.getStarPlayer())) {
                Drawable drawable = getActivity().getDrawable(R.drawable.round_star_24_yellow);
                player_star.setImageDrawable(drawable);
            }
        }

        brawler_level.setText(playerInfo.getBrawler_power());
        player_trophy.setText(playerInfo.getBrawler_trophies());
        player_tag.setText(playerInfo.getTag());

        v.setOnClickListener(v1 -> {
            if (KatData.dialog != null) KatData.dialog.show();

            String realTag = playerInfo.getTag().substring(1);

            kat_SearchThread kset = new kat_SearchThread(getActivity(), kat_Player_PlayerDetailActivity.class);
            kset.SearchStart(realTag, "players", requireActivity().getApplicationContext());
        });

        return v;
    }


    Comparator<kat_official_playerBattleLogParser.team> comparator = (t1, t2) -> {
        if (isMyTeam(t2)) return 1;
        else return -1;
    };

    boolean isMyTeam(kat_official_playerBattleLogParser.team team) {
        for (int i = 0; i < team.getPlayTeamInfo().size(); i++) {
            if (team.getPlayTeamInfo().get(i).getTag().equals(playerData.getTag())) return true;
        }
        return false;
    }
}
