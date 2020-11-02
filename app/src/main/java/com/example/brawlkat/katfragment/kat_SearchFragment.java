package com.example.brawlkat.katfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.example.brawlkat.R;
import com.example.brawlkat.database.kat_myAccountDatabase;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_Player_RecentSearchActivity;
import com.example.brawlkat.kat_SearchAccountForSaveActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_SearchFragment extends Fragment {


    private             kat_Player_MainActivity                                                 kat_player_mainActivity;
    private             boolean                                                                 touchOutsideOfMyAccount = true;
    private             LinearLayout                                                            inputMyAccount;
    private             kat_official_playerInfoParser.playerData                                playerData;

    private             RequestOptions                                                          options;
    public              static int                                                              height;
    public              static int                                                              width;


    public kat_SearchFragment(kat_Player_MainActivity kat_player_mainActivity){
        this.kat_player_mainActivity = kat_player_mainActivity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.player_main, container, false);

        LinearLayout player_user_search_layout = (LinearLayout) view.findViewById(R.id.player_user_searchInput_layout);
        LinearLayout player_club_search_layout = (LinearLayout) view.findViewById(R.id.player_club_searchInput_layout);

        LinearLayout player_main_inputMyAccount = (LinearLayout) view.findViewById(R.id.player_main_inputMyAccount);


        // 내 계정 뷰 보여주기......................................................................................................//
        kat_myAccountDatabase kataMyAccountBase = kat_player_mainActivity.kataMyAccountBase;
        if(kataMyAccountBase.size() == 1){

            // 현재 있는 뷰 일단 지워주기
            player_main_inputMyAccount.removeAllViews();
            player_main_inputMyAccount.setBackground(null);

            player_main_inputMyAccount.setClickable(false);
            player_main_inputMyAccount.setEnabled(false);
            player_main_inputMyAccount.setFocusable(false);

            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View accountView = layoutInflater.inflate(R.layout.player_my_account, null);

            // 뷰 가져오기
            ImageView playerIcon = accountView.findViewById(R.id.player_my_account_image);
            TextView player_my_account_name = accountView.findViewById(R.id.player_my_account_name);
            TextView player_my_account_tag = accountView.findViewById(R.id.player_my_account_tag);
            ImageView player_my_account_trophies_icon = accountView.findViewById(R.id.player_my_account_trophies_image);
            TextView player_my_account_trophies = accountView.findViewById(R.id.player_my_account_trophies);
            TextView player_my_account_level = accountView.findViewById(R.id.player_my_account_level_icon_text);

            // 이미지 링크 선언
            String url_profile = "https://www.starlist.pro/assets/profile/" + playerData.getIconId() + ".png?v=1";
            String url_icon_trophies = "https://www.starlist.pro/assets/icon/trophy.png";

            // 이미지 세팅
            GlideImageWithRoundCorner(url_profile, width / 5, width / 5, playerIcon);
            GlideImage(url_icon_trophies, width / 20, width / 20, player_my_account_trophies_icon);

            // 텍스트 세팅
            player_my_account_tag.setText(playerData.getTag());
            player_my_account_name.setText(playerData.getName());
            player_my_account_trophies.setText(playerData.getTrophies() + " / " + playerData.getHighestTrophies());
            player_my_account_level.setText(Integer.toString(playerData.getExpLevel()));

            player_main_inputMyAccount.addView(accountView);



            // 부모 뷰와 크기 맞추기
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            accountView.setLayoutParams(params);
        }
        // ..................................................................................................................//

        player_user_search_layout.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(kat_player_mainActivity, kat_Player_RecentSearchActivity.class);
                    intent.putExtra("type", "players");
                    kat_player_mainActivity.startActivity(intent);
                }

                return false;
            }
        });

        player_club_search_layout.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(kat_player_mainActivity, kat_Player_RecentSearchActivity.class);
                    intent.putExtra("type", "clubs");
                    kat_player_mainActivity.startActivity(intent);
                }
                return false;
            }
        });

        player_main_inputMyAccount.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){

                Intent intent = new Intent(getActivity(), kat_SearchAccountForSaveActivity.class);
                startActivity(intent);
                return false;
            }
        });

        return view;
    }


    public void getPlayerData(kat_official_playerInfoParser.playerData playerData){
        this.playerData = playerData;
    }

    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getActivity().getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    public void GlideImageWithRoundCorner(String url, int width, int height, ImageView view){
        Glide.with(getActivity().getApplicationContext())
                .applyDefaultRequestOptions(options)
                .load(url)
                .apply(new RequestOptions().circleCrop().circleCrop())
                .override(width, height)
                .into(view);
    }

}
