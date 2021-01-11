package com.example.brawlkat.kat_Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.Client;
import com.example.brawlkat.R;
import com.example.brawlkat.kat_Database.kat_myAccountDatabase;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_LoadingDialog;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_Player_PlayerDetailActivity;
import com.example.brawlkat.kat_Player_RecentSearchActivity;
import com.example.brawlkat.kat_SearchAccountForSaveActivity;
import com.example.brawlkat.kat_Service_BrawlStarsNotifActivity;
import com.example.brawlkat.kat_Thread.kat_SearchThread;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_SearchFragment extends Fragment {


    private             kat_Player_MainActivity                                                 kat_player_mainActivity;
    private             Client                                                                  client;

    private             boolean                                                                 touchOutsideOfMyAccount = true;
    private             LinearLayout                                                            inputMyAccount;
    private             kat_official_playerInfoParser.playerData                                playerData;
    private             ArrayList<HashMap<String, Object>>                                      BrawlerArrayList;

    private             RequestOptions                                                          options;
    public              static int                                                              height;
    public              static int                                                              width;


    public kat_SearchFragment(kat_Player_MainActivity kat_player_mainActivity){
        this.kat_player_mainActivity = kat_player_mainActivity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = kat_player_mainActivity.client;

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

        final LinearLayout player_main_inputMyAccount = (LinearLayout) view.findViewById(R.id.player_main_inputMyAccount);


        // 내 계정 뷰 보여주기......................................................................................................//
        kat_myAccountDatabase kataMyAccountBase = kat_player_mainActivity.kataMyAccountBase;
        if(kataMyAccountBase.size() == 1){

            final View tempView = player_main_inputMyAccount.getChildAt(0);
            final Drawable tempDrawable = player_main_inputMyAccount.getBackground();

            playerData = kat_Player_MainActivity.MyPlayerData;

            // 현재 있는 뷰 일단 지워주기
            player_main_inputMyAccount.removeAllViews();
            player_main_inputMyAccount.setBackground(null);

            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View accountView = layoutInflater.inflate(R.layout.player_my_account, null);
            accountView.setClickable(true);

            // 뷰 가져오기 .....................................
            ImageView playerIcon = accountView.findViewById(R.id.player_my_account_image);
            TextView player_my_account_name = accountView.findViewById(R.id.player_my_account_name);
            TextView player_my_account_tag = accountView.findViewById(R.id.player_my_account_tag);
            ImageView player_my_account_trophies_icon = accountView.findViewById(R.id.player_my_account_trophies_image);
            TextView player_my_account_trophies = accountView.findViewById(R.id.player_my_account_trophies);
            TextView player_my_account_level = accountView.findViewById(R.id.player_my_account_level_icon_text);
            ImageView player_my_account_close = accountView.findViewById(R.id.player_my_account_close);
            // ..............................................


            // 플레이어의 모스트 3 브롤러 가져오기. 다 똑같으면 이름 순으로
            ArrayList<kat_official_playerInfoParser.playerBrawlerData> brawlerData
                    = new ArrayList<>();
            if(kat_Player_MainActivity.MyPlayerData != null) {
                brawlerData = kat_Player_MainActivity.MyPlayerData.getBrawlerData();
            }
            Collections.sort(brawlerData, new brawlerSort());

            BrawlerArrayList = kat_LoadBeforeMainActivity.BrawlersArrayList;

            FrameLayout brawler1 = accountView.findViewById(R.id.brawler1);
            FrameLayout brawler2 = accountView.findViewById(R.id.brawler2);
            FrameLayout brawler3 = accountView.findViewById(R.id.brawler3);

            findBrawler(brawler1, kat_Player_MainActivity.MyPlayerData, BrawlerArrayList, 0);
            findBrawler(brawler2, kat_Player_MainActivity.MyPlayerData, BrawlerArrayList, 1);
            findBrawler(brawler3, kat_Player_MainActivity.MyPlayerData, BrawlerArrayList, 2);



            // 이미지 링크 선언
            String url_profile = "";
            if(kat_Player_MainActivity.MyPlayerData != null){
                url_profile = "https://www.starlist.pro/assets/profile/" +
                        kat_Player_MainActivity.MyPlayerData.getIconId() + ".png?v=1";
            }
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

            player_my_account_close.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent){

                    player_main_inputMyAccount.removeAllViews();
                    player_main_inputMyAccount.addView(tempView);
                    player_main_inputMyAccount.setBackground(tempDrawable);

                    kat_player_mainActivity.kataMyAccountBase.delete(playerData.getTag());
                    kat_LoadBeforeMainActivity.eventsPlayerData = null;

                    RemoteViews contentView = new RemoteViews(getActivity().getPackageName(), R.layout.main_notification);

                    kat_Service_BrawlStarsNotifActivity.notification.setCustomContentView(contentView);
                    kat_Service_BrawlStarsNotifActivity.mNotificationManager.notify(1,
                            kat_Service_BrawlStarsNotifActivity.notification.build());

                    return true;
                }
            });


            accountView.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent){
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP) {

                        kat_LoadingDialog dialog = new kat_LoadingDialog(getActivity());
                        dialog.show();

                        String RawTag = playerData.getTag();
                        String newTag = RawTag.substring(1);

                        kat_SearchThread kset = new kat_SearchThread(getActivity(),
                                kat_Player_PlayerDetailActivity.class, dialog);
                        kset.SearchStart(newTag, "players");

                    }
                    return false;
                }
            });

            // 부모 뷰와 크기 맞추기
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            ;
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

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(getActivity(), kat_SearchAccountForSaveActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        return view;
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

    class brawlerSort implements Comparator<kat_official_playerInfoParser.playerBrawlerData> {

        @Override
        public int compare(kat_official_playerInfoParser.playerBrawlerData t1,
                           kat_official_playerInfoParser.playerBrawlerData t2) {


            if(t1.getTrophies() < t2.getTrophies()) return 1;
            else if(t1.getTrophies() > t2.getTrophies()) return -1;
            return 0;
        }
    }


    public void findBrawler(FrameLayout frameLayout,
                            kat_official_playerInfoParser.playerData playerData,
                            ArrayList<HashMap<String, Object>> BrawlerArrayList,
                            int i){
        if(playerData == null) return;
        ArrayList<kat_official_playerInfoParser.playerBrawlerData> brawlerData = playerData.getBrawlerData();
        if(brawlerData.size() - 1 < i) return;
        for(int j = 0; j <BrawlerArrayList.size(); j++){

            if(brawlerData.get(i).getId().equals(BrawlerArrayList.get(j).get("id").toString())){
                ImageView imageView = (ImageView) frameLayout.getChildAt(0);
                TextView textView = (TextView) frameLayout.getChildAt(1);

                GlideImageWithRoundCorner(BrawlerArrayList.get(j).get("imageUrl").toString(), width / 7, height / 7, imageView);
                textView.setText(Integer.toString(brawlerData.get(i).getTrophies()));

                break;
            }
        }
    }


}
