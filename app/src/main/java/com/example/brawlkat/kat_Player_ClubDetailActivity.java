package com.example.brawlkat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.example.brawlkat.dataparser.kat_official_clubInfoParser;

import androidx.annotation.Nullable;

public class kat_Player_ClubDetailActivity extends kat_Player_RecentSearchActivity {


    private                             ImageView                           player_club_icon;
    private                             TextView                            player_club_name;
    private                             TextView                            player_club_tag;
    private                             TextView                            player_club_description;

    private                             RequestOptions                      options;


    private                             int                                     height;
    private                             int                                     width;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_club_detail);

        Intent intent = getIntent();
        clubData = (kat_official_clubInfoParser.clubData) intent.getSerializableExtra("clubData");

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







        System.out.println(clubData.getName());
    }


    @Override
    protected void onStart() {
        super.onStart();

        setData();
    }

    private void setData(){

        String iconUrl = "https://www.starlist.pro/assets/club/" + clubData.getBadgeId() + ".png?v=1";
        GlideImage(iconUrl, width / 5, width / 5, player_club_icon);
        player_club_name.setText(clubData.getName());
        player_club_tag.setText(clubData.getTag());
        player_club_description.setText(clubData.getDescription());

        setClubInformationList();
    }

    private void setClubInformationList(){

        String[] informationIconUrl = new String[]{
                "https://www.starlist.pro/assets/icon/trophy.png",
                "https://www.starlist.pro/assets/icon/Ranking.png",
                "https://www.starlist.pro/assets/icon/Battle-Log.png",
                "https://www.starlist.pro/assets/icon/Battle-Log.png",
                "https://www.starlist.pro/assets/icon/News.png"
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
        for(int i = 0; i < 5; i++){
            View view = layoutInflater.inflate(R.layout.player_club_information, null);
            ImageView icon = view.findViewById(R.id.player_club_info_icon);
            TextView name = view.findViewById(R.id.player_club_info_name);
            TextView value = view.findViewById(R.id.player_club_info_value);

            GlideImage(informationIconUrl[i], width / 20, width / 20, icon);
            name.setText(informationName[i]);
            value.setText(informationValue[i]);

            linearLayout.addView(view);
        }
    }


    private void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }

}
