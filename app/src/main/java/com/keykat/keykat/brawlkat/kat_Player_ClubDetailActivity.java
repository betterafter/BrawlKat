package com.keykat.keykat.brawlkat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.keykat.keykat.brawlkat.kat_Thread.kat_SearchThread;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_clubLogParser;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_clubInfoParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class kat_Player_ClubDetailActivity extends kat_Player_RecentSearchActivity {


    private                             ImageView                           player_club_icon;
    private                             TextView                            player_club_name;
    private                             TextView                            player_club_tag;
    private                             TextView                            player_club_description;

    private                             RequestOptions                      options;


    private                             int                                 height;
    private                             int                                 width;

    private                             int[]                               colorArray2;
    private                             Client                              client = kat_Player_MainActivity.client;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_club_detail);

        LinearLayout adLayout = findViewById(R.id.adView2);

        final kat_ad ad = new kat_ad(getApplicationContext());
        ad.init();
        ad.build(R.layout.kat_ad_unified_small_banner_layout, adLayout);
        ad.load();

        Intent intent = getIntent();
        clubData = (kat_official_clubInfoParser.clubData) intent.getSerializableExtra("clubData");
        clubLogData = (kat_clubLogParser.clubLogData) intent.getSerializableExtra("clubLogData");

        player_club_icon = findViewById(R.id.player_club_icon);
        player_club_name = findViewById(R.id.player_club_name);
        player_club_tag = findViewById(R.id.player_club_tag);
        player_club_description = findViewById(R.id.player_club_description);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        colorArray2 = new int[]{
                R.color.Color1,  R.color.Color3, R.color.Color4,
                R.color.Color6, R.color.Color7
        };
    }


    @Override
    protected void onStart() {
        super.onStart();

        setData();
    }

    private void setData(){

        String iconUrl = kat_LoadBeforeMainActivity.WebRootUrl + "/assets/club/" + clubData.getBadgeId() + ".png?v=1";
        GlideImageWithRoundCorner(iconUrl, width / 7, width / 7, player_club_icon);
        player_club_name.setText(clubData.getName());
        player_club_tag.setText(clubData.getTag());
        player_club_description.setText(clubData.getDescription());


        setClubInformationList();
        setClubMemberList();
    }

    private void setClubInformationList(){

        String[] informationIconUrl = new String[]{
                kat_LoadBeforeMainActivity.WebRootUrl + "/assets/icon/trophy.png",
                kat_LoadBeforeMainActivity.WebRootUrl + "/assets/icon/Ranking.png",
                kat_LoadBeforeMainActivity.WebRootUrl + "/assets/icon/Battle-Log.png",
                kat_LoadBeforeMainActivity.WebRootUrl + "/assets/icon/Battle-Log.png",
                kat_LoadBeforeMainActivity.WebRootUrl + "/assets/icon/News.png"
        };

        String[] informationName = new String[]{
                "Trophies", "Required Trophies", "Trophy Range", "Average Trophy", "Members"
        };

        String[] informationValue = new String[]{
                Integer.toString(clubData.getTrophies()),
                Integer.toString(clubData.getRequiredTrophies()),
                clubData.getTrophyRange(),
                Integer.toString(clubData.getAverageTrophy()),
                clubData.getMemberDatas().size()  + " / " + "100"
        };

        LinearLayout linearLayout = findViewById(R.id.player_club_clubInformation);
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearLayout.removeAllViews();

        TextView tv = new TextView(getApplicationContext());
        tv.setText("클럽 정보");
        tv.setBackgroundResource(R.drawable.card_bottom_line);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv.setTextColor(getResources().getColor(R.color.colorWhite));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setPadding(30, 30, 30, 30);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(layoutParams);
        linearLayout.addView(tv);

        String[] membersType = new String[4];
        int[] membersValue = new int[4];
        HashMap<String, Integer> roles = clubData.getMembersRole();
        Iterator<String> iter = roles.keySet().iterator();

        int idx = 0;
        while(iter.hasNext()){
            String key = (String) iter.next();
            int value = roles.get(key);

            membersType[idx] = key;
            membersValue[idx] = value;

            idx++;
        }


        for(int i = 0; i < 5; i++){

            // 내부 레이아웃 아이템을 같은 간격으로 정렬
            View view = layoutInflater.inflate(R.layout.club_detail_clubinformation, null);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            p.weight = 1;
            view.setLayoutParams(p);

            // 각 아이템 값 선언
            ImageView icon = view.findViewById(R.id.club_detail_game_info_icon);
            GlideImageWithRoundCorner(informationIconUrl[i], width / 20, width / 20, icon);

            TextView name = view.findViewById(R.id.club_detail_game_info_modeType);
            name.setText(informationName[i]);

            TextView value = view.findViewById(R.id.club_detail_game_info_modeValue);
            value.setText(informationValue[i]);
            value.setTypeface(tv.getTypeface(), Typeface.BOLD);
            value.setTextColor(getResources().getColor(colorArray2[i]));

            if(i == 4){
                LinearLayout member_summary_layout = view.findViewById(R.id.member_summary);
                for(int j = 0; j < 4; j++){
                    View v = layoutInflater.inflate(R.layout.player_club_member_information, null);
                    TextView club_member_type = v.findViewById(R.id.player_club_member_type);
                    TextView club_member_value = v.findViewById(R.id.player_club_member_value);

                    club_member_type.setText(membersType[j]);
                    club_member_value.setText(Integer.toString(membersValue[j]));

                    member_summary_layout.addView(v);
                }

            }

            linearLayout.addView(view);
        }
    }

    public void setClubMemberList(){

        LinearLayout linearLayout = findViewById(R.id.player_club_members);
        linearLayout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ArrayList<kat_official_clubInfoParser.clubMemberData> memberData = clubData.getMemberDatas();
        for(int i = 0; i < memberData.size(); i++){
            View view = inflater.inflate(R.layout.player_club_detail_members_item, null);
            TextView club_member_rank = view.findViewById(R.id.player_club_detail_members_ranknum);
            ImageView club_member_icon = view.findViewById(R.id.player_club_detail_members_icon);
            TextView club_member_name = view.findViewById(R.id.player_club_detail_members_name);
            TextView club_member_trophy = view.findViewById(R.id.player_club_detail_members_trophy);
            TextView club_member_role = view.findViewById(R.id.player_club_detail_members_role);

            String iconUrl = kat_LoadBeforeMainActivity.WebRootUrl + "/assets/profile-low/" +
                    memberData.get(i).getIconId() + ".png?v=1";
            GlideImage(iconUrl, width / 10, width / 10, club_member_icon);
            club_member_rank.setText(Integer.toString(i + 1));
            club_member_name.setText(memberData.get(i).getName());
            club_member_trophy.setText(Integer.toString(memberData.get(i).getTrophies()));
            club_member_role.setText(memberData.get(i).getRole());

            String nameColor = memberData.get(i).getNameColor();
            nameColor = nameColor.replace("0x", "#");
            club_member_name.setTextColor(Color.parseColor(nameColor));

            club_member_trophy.setTextColor(getResources().getColor(R.color.trophiesYellow));
            if(memberData.get(i).getRole().equals("member")){
                club_member_role.setTextColor(getResources().getColor(R.color.colorWhite));
            }
            else if(memberData.get(i).getRole().equals("senior")){
                club_member_role.setTextColor(getResources().getColor(R.color.Color4));
            }
            else if(memberData.get(i).getRole().equals("vicePresident")){
                club_member_role.setTextColor(getResources().getColor(R.color.Color3));
            }
            else if(memberData.get(i).getRole().equals("president")){
                club_member_role.setTextColor(getResources().getColor(R.color.Color1));
            }

            final int idx = i;
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    kat_LoadingDialog dialog = new kat_LoadingDialog(kat_Player_ClubDetailActivity.this);
                    dialog.show();

                    String realTag = memberData.get(idx).getTag().substring(1);

                    kat_SearchThread kset = new kat_SearchThread(kat_Player_ClubDetailActivity.this,
                            kat_Player_PlayerDetailActivity.class, dialog);
                    kset.SearchStart(realTag, "players", getApplicationContext());
                }
            });
            linearLayout.addView(view);
        }
    }


    public void setClubLog(){

        LinearLayout linearLayout = findViewById(R.id.player_club_members);
        linearLayout.removeAllViews();

        if(clubLogData.getStatus().equals("forbidden")){

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 50, 0, 0);

            TextView forbiddenText = new TextView(getApplicationContext());
            TextView forbiddenText2 = new TextView(getApplicationContext());

            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.lilita_one);
            forbiddenText.setTypeface(typeface);
            forbiddenText.setGravity(Gravity.CENTER);
            forbiddenText2.setGravity(Gravity.CENTER);

            forbiddenText.setText("This club is not tracked. \n");
            forbiddenText2.setText(Html.fromHtml("You can toggle it by going to " +
                    "<font color=\"#2a7de2\"><b>" + kat_LoadBeforeMainActivity.WebRootUrl + "/stats/clublog/JJQP98G0</b></font>" +
                    " and enabling tracking. <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>"));

            forbiddenText.setTextSize(24);
            forbiddenText.setTextColor(getResources().getColor(R.color.colorWhite));
            forbiddenText.setLayoutParams(params);

            forbiddenText2.setTextSize(18);
            forbiddenText2.setTextColor(getResources().getColor(R.color.colorWhite));

            linearLayout.addView(forbiddenText);
            linearLayout.addView(forbiddenText2);
            return;
        }

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ArrayList<Object> cld = clubLogData.getHistoryData();
        for(int i = 0; i < cld.size(); i++){

            View view = inflater.inflate(R.layout.player_club_detail_club_log, null);
            TextView type = view.findViewById(R.id.player_club_detail_club_log_type);
            TextView change = view.findViewById(R.id.player_club_detail_club_log_change);
            TextView time = view.findViewById(R.id.player_club_detail_club_log_time);

            if(cld.get(i) instanceof kat_clubLogParser.joinData){
                kat_clubLogParser.joinData data = (kat_clubLogParser.joinData) cld.get(i);
                type.setText("Changing Club Member");
                if(data.isJoin()) {
                    Drawable drawable = type.getBackground();
                    drawable.setTint(getResources().getColor(R.color.Color12));
                    change.setText(data.getPlayerName() + " 님이 클럽에 가입했습니다.");
                }
                else {
                    Drawable drawable = type.getBackground();
                    drawable.setTint(getResources().getColor(R.color.Color11));
                    change.setText(data.getPlayerName() + " 님이 클럽을 탈퇴했습니다.");
                }

                time.setText(data.getTimeFormat());
            }

            else if(cld.get(i) instanceof kat_clubLogParser.settingData){
                kat_clubLogParser.settingData data = (kat_clubLogParser.settingData) cld.get(i);
                type.setText("Changing Club Status");
                Drawable drawable = type.getBackground();
                if(data.getType().equals("requirement")){
                    drawable.setTint(getResources().getColor(R.color.Color1));
                    type.setText("Changing Club Status [Requirement]");
                    change.setText(data.getOldType() + " 에서 " + data.getNewType() + " 으로 변경되었습니다.");
                }
                else if(data.getType().equals("status")){
                    drawable.setTint(getResources().getColor(R.color.Color2));
                    type.setText("Changing Club Status [Status]");
                    change.setText(data.getOldType() + " 에서 " + data.getNewType() + " 으로 변경되었습니다.");
                }
                else if(data.getType().equals("description")){
                    drawable.setTint(getResources().getColor(R.color.Color3));
                    type.setText("Changing Club Status [Description]");
                    change.setText("before : " + data.getOldType() + '\n' + "after : " + data.getNewType());
                }
                time.setText(data.getTimeFormat());
            }

            else if(cld.get(i) instanceof kat_clubLogParser.promoteData){
                kat_clubLogParser.promoteData data = (kat_clubLogParser.promoteData) cld.get(i);
                Drawable drawable = type.getBackground();
                drawable.setTint(getResources().getColor(R.color.Color8));
                type.setText("Changing Member's Role");
                change.setText(data.getPlayerName() + " 님의 역할이 " + data.getOldRole() + " 에서 " + data.getNewRole() + " 로 변경되었습니다.");

                time.setText(data.getTimeFormat());
            }

            linearLayout.addView(view);
        }
    }

    public void onsetClubMemberListClick(View view){
        setClubMemberList();
    }

    public void onsetClubLogClick(View view){
        setClubLog();
    }

    private void GlideImage(String url, int width, int height, ImageView view){

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
